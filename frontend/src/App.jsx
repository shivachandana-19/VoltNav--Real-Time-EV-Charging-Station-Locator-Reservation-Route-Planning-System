import { useEffect, useState } from "react";
import { QRCodeCanvas } from "qrcode.react";
import "./index.css";

const INITIAL_STATIONS = [
  { id: 1, name: "Central City Charging Hub", location: "City Center", power: 60, connector: "CCS2", price: 18, status: "Available" },
  { id: 2, name: "Green Energy Station", location: "Green Park", power: 30, connector: "Type 2", price: 15, status: "Available" },
];

const readStorage = (key, fallback) => {
  try {
    const saved = localStorage.getItem(key);
    return saved ? JSON.parse(saved) : fallback;
  } catch {
    return fallback;
  }
};

export default function App() {
  const [page, setPage] = useState("welcome");
  const [authMode, setAuthMode] = useState("login");
  const [role, setRole] = useState("USER");
  const [currentUser, setCurrentUser] = useState(null);
  const [users, setUsers] = useState(() => readStorage("voltnav_users", []));
  const [stations, setStations] = useState(() => readStorage("voltnav_stations", INITIAL_STATIONS));
  const [bookings, setBookings] = useState(() => readStorage("voltnav_bookings", []));
  const [search, setSearch] = useState("");
  const [message, setMessage] = useState("");
  const [selectedStation, setSelectedStation] = useState(null);
  const [confirmedBooking, setConfirmedBooking] = useState(null);

  const [auth, setAuth] = useState({ name: "", email: "", password: "" });
  const [stationForm, setStationForm] = useState({ name: "", location: "", power: "60", connector: "CCS2", price: "18" });
  const [bookingForm, setBookingForm] = useState({ date: "", time: "", duration: "1" });

  useEffect(() => localStorage.setItem("voltnav_users", JSON.stringify(users)), [users]);
  useEffect(() => localStorage.setItem("voltnav_stations", JSON.stringify(stations)), [stations]);
  useEffect(() => localStorage.setItem("voltnav_bookings", JSON.stringify(bookings)), [bookings]);

  function notify(text) {
    setMessage(text);
  }

  function handleAuthSubmit(event) {
    event.preventDefault();
    setMessage("");
    const email = auth.email.trim().toLowerCase();

    if (authMode === "register") {
      if (!auth.name.trim()) return notify("Please enter your full name.");
      if (auth.password.length < 8) return notify("Password must contain at least 8 characters.");
      if (users.some((user) => user.email?.trim().toLowerCase() === email)) {
        return notify("An account with this email already exists.");
      }
      if (role === "ADMIN") {
        return notify("Admin registration is disabled. An administrator must create or authorize admin accounts.");
      }

      const newUser = { id: Date.now(), name: auth.name.trim(), email, password: auth.password, role: "USER" };
      setUsers((previous) => [...previous, newUser]);
      setCurrentUser(newUser);
      setPage("dashboard");
      notify("Account created successfully!");
      return;
    }

    const matchedUser = users.find(
      (user) => user.email?.trim().toLowerCase() === email && user.password === auth.password
    );

    // Demo-only administrator credentials. Replace with backend authentication for production.
    const demoAdmin = email === "admin@voltnav.com" && auth.password === "Admin@123" && role === "ADMIN";

    if (demoAdmin) {
      setCurrentUser({ id: "demo-admin", name: "VoltNav Administrator", email, role: "ADMIN" });
      setPage("dashboard");
      notify("Demo admin signed in.");
      return;
    }

    if (matchedUser && String(matchedUser.role || "USER").toUpperCase() === role) {
      setCurrentUser({ ...matchedUser, role: String(matchedUser.role || "USER").toUpperCase() });
      setPage("dashboard");
      notify("Login successful!");
    } else {
      notify("Invalid credentials or account type.");
    }
  }

  function logout() {
    setCurrentUser(null);
    setPage("welcome");
    setAuth({ name: "", email: "", password: "" });
    setSelectedStation(null);
    setConfirmedBooking(null);
    setMessage("");
  }

  function addStation(event) {
    event.preventDefault();
    if (!stationForm.name.trim() || !stationForm.location.trim()) return notify("Enter the station name and location.");

    const power = Number(stationForm.power);
    const price = Number(stationForm.price);
    if (power <= 0 || price < 0 || !Number.isFinite(power + price)) return notify("Enter valid power and price values.");

    setStations((previous) => [...previous, {
      id: Date.now(),
      name: stationForm.name.trim(),
      location: stationForm.location.trim(),
      power,
      connector: stationForm.connector,
      price,
      status: "Available",
    }]);
    setStationForm({ name: "", location: "", power: "60", connector: "CCS2", price: "18" });
    notify("Charging station added.");
  }

  function deleteStation(id) {
    setStations((previous) => previous.filter((station) => station.id !== id));
    notify("Station removed from this demo.");
  }

  function createBooking(event) {
    event.preventDefault();

    if (!selectedStation || !currentUser) {
      notify("Please sign in and select a charging station.");
      return;
    }

    const { date, time, duration } = bookingForm;
    if (!date || !time) {
      notify("Please select a booking date and start time.");
      return;
    }

    const start = new Date(`${date}T${time}`);
    const hours = Number(duration);
    if (Number.isNaN(start.getTime()) || start <= new Date()) {
      notify("Please select a valid future date and time.");
      return;
    }
    if (!Number.isFinite(hours) || hours <= 0 || hours > 12) {
      notify("Choose a duration between 1 and 12 hours.");
      return;
    }

    const end = new Date(start.getTime() + hours * 60 * 60 * 1000);
    const conflict = bookings.some((booking) => {
      if (
        String(booking.stationId) !== String(selectedStation.id) ||
        booking.date !== date ||
        booking.status !== "Booked"
      ) return false;

      const existingStart = new Date(`${booking.date}T${booking.time}`);
      const existingEnd = new Date(existingStart.getTime() + Number(booking.duration) * 60 * 60 * 1000);
      return start < existingEnd && end > existingStart;
    });

    if (conflict) {
      notify("This time slot is already booked. Please choose another time.");
      return;
    }

    const bookingId = globalThis.crypto?.randomUUID
      ? globalThis.crypto.randomUUID()
      : `VN-${Date.now()}-${Math.random().toString(36).slice(2, 8).toUpperCase()}`;

    const newBooking = {
      id: bookingId,
      userId: currentUser.id,
      userEmail: currentUser.email,
      stationId: selectedStation.id,
      stationName: selectedStation.name,
      location: selectedStation.location,
      date,
      time,
      duration: hours,
      status: "Booked",
      createdAt: new Date().toISOString(),
    };

    setBookings((previous) => [...previous, newBooking]);
    setConfirmedBooking(newBooking);
    setSelectedStation(null);
    setPage("dashboard");
    setBookingForm({ date: "", time: "", duration: "1" });
    notify("Booking confirmed! Your QR code is ready.");
  }

  function cancelBooking(id) {
    setBookings((previous) => previous.map((booking) =>
      booking.id === id ? { ...booking, status: "Cancelled" } : booking
    ));
    if (confirmedBooking?.id === id) setConfirmedBooking(null);
    notify("Demo booking cancelled.");
  }

  const filteredStations = stations.filter((station) =>
    `${station.name} ${station.location} ${station.connector}`.toLowerCase().includes(search.toLowerCase())
  );
  const myBookings = bookings.filter((booking) => booking.userId === currentUser?.id);
  const isAdmin = currentUser?.role === "ADMIN";

  if (page === "welcome") {
    return (
      <div className="app">
        <header className="navbar">
          <a className="brand" href="#home"><span className="brand-icon">⚡</span> VoltNav</a>
          <nav><a href="#home">Home</a><a href="#features">Features</a></nav>
          <button className="primary-button" onClick={() => { setAuthMode("login"); setRole("USER"); setPage("auth"); setMessage(""); }}>Sign in</button>
        </header>
        <main>
          <section className="hero" id="home">
            <div className="hero-content">
              <span className="eyebrow">SMARTER ELECTRIC MOBILITY</span>
              <h1>Charge your journey.<br /><span>Power your future.</span></h1>
              <p>Discover EV charging stations, explore charging options, and plan your next charging stop with VoltNav.</p>
              <div className="auth-actions">
                <button className="primary-button" onClick={() => { setAuthMode("register"); setRole("USER"); setPage("auth"); setMessage(""); }}>Create user account →</button>
                <button className="secondary-button" onClick={() => { setAuthMode("login"); setRole("ADMIN"); setPage("auth"); setMessage(""); }}>Admin sign in</button>
              </div>
              <div className="hero-stats">
                <div><strong>{stations.length}</strong><span>Stations in demo</span></div>
                <div><strong>24/7</strong><span>Explore anytime</span></div>
              </div>
            </div>
            <div className="hero-visual"><div className="glow" /><div className="bolt">ϟ</div><div className="charging-card"><span className="car-icon">🚘</span><div><strong>Your next charge starts here</strong><p>Explore EV charging with VoltNav</p></div></div></div>
          </section>
          <section className="stations-section" id="features">
            <div className="section-heading"><div><span className="eyebrow">EXPLORE VOLTNAV</span><h2>Built for every journey</h2><p>Create an account to explore stations and manage your demo bookings.</p></div></div>
            <div className="station-grid">
              {[
                ["⌖", "Find charging stations", "Explore station locations and connector details."],
                ["▣", "Manage reservations", "Choose a station, date, time, and duration."],
                ["⚙", "Admin management", "Add charging stations and review demo bookings."],
              ].map(([icon, title, description]) => <article className="station-card" key={title}><div className="station-icon">{icon}</div><h3>{title}</h3><p className="location">{description}</p></article>)}
            </div>
          </section>
        </main>
        <footer><a className="brand footer-brand" href="#home"><span className="brand-icon">⚡</span> VoltNav</a><p>Smarter charging. Better journeys.</p><span>© {new Date().getFullYear()} VoltNav</span></footer>
      </div>
    );
  }

  if (page === "auth") {
    return (
      <div className="auth-page">
        <button className="back-link" onClick={() => { setPage("welcome"); setMessage(""); }}>← Back to VoltNav</button>
        <div className="auth-card">
          <a className="brand auth-brand" href="#home"><span className="brand-icon">⚡</span> VoltNav</a>
          <span className="eyebrow">{role === "ADMIN" ? "ADMIN PORTAL" : "EV CHARGING PLATFORM"}</span>
          <h1>{authMode === "register" ? "Create your account" : "Welcome back"}</h1>
          <p className="auth-description">{authMode === "register" ? "Register to discover charging stations and manage bookings." : "Sign in to continue to your VoltNav dashboard."}</p>
          <div className="role-switch">
            <button className={role === "USER" ? "role-active" : ""} onClick={() => { setRole("USER"); setMessage(""); }}>User</button>
            <button className={role === "ADMIN" ? "role-active" : ""} onClick={() => { setRole("ADMIN"); setAuthMode("login"); setMessage(""); }}>Admin</button>
          </div>
          <form className="app-form" onSubmit={handleAuthSubmit}>
            {authMode === "register" && <label>Full name<input required value={auth.name} onChange={(event) => setAuth({ ...auth, name: event.target.value })} placeholder="Enter your full name" /></label>}
            <label>Email address<input required type="email" value={auth.email} onChange={(event) => setAuth({ ...auth, email: event.target.value })} placeholder="you@example.com" autoComplete="email" /></label>
            <label>Password<input required type="password" value={auth.password} onChange={(event) => setAuth({ ...auth, password: event.target.value })} placeholder="Enter your password" autoComplete={authMode === "register" ? "new-password" : "current-password"} /></label>
            <button className="primary-button form-submit" type="submit">{authMode === "register" ? "Create account" : "Sign in"} →</button>
          </form>
          {message && <p className="notice">{message}</p>}
          {role === "ADMIN" && authMode === "login" && <div className="demo-credentials"><strong>Local UI demo credentials</strong><span>Email: admin@voltnav.com</span><span>Password: Admin@123</span><small>Demo only. Replace this with secure backend authentication.</small></div>}
          {role === "USER" && <p className="auth-toggle">{authMode === "login" ? "New to VoltNav?" : "Already registered?"} <button onClick={() => { setAuthMode(authMode === "login" ? "register" : "login"); setMessage(""); }}>{authMode === "login" ? "Create an account" : "Sign in"}</button></p>}
        </div>
      </div>
    );
  }

  return (
    <div className="app dashboard-app">
      <header className="navbar">
        <a className="brand" href="#dashboard"><span className="brand-icon">⚡</span> VoltNav</a>
        <span className="role-badge">{isAdmin ? "ADMIN DASHBOARD" : "USER DASHBOARD"}</span>
        <button className="secondary-button" onClick={logout}>Sign out</button>
      </header>

      <main className="dashboard-main">
        <section className="dashboard-welcome"><span className="eyebrow">{isAdmin ? "STATION MANAGEMENT" : "YOUR EV JOURNEY"}</span><h1>Hello, {currentUser?.name || "VoltNav user"}.</h1><p>{isAdmin ? "Manage charging infrastructure and monitor reservations." : "Find a charging station and manage your reservations."}</p></section>
        {message && <div className="notice dashboard-notice">{message}</div>}

        <div className="summary-grid">
          <article className="summary-card"><span>Total stations</span><strong>{stations.length}</strong></article>
          <article className="summary-card"><span>Available stations</span><strong>{stations.filter((station) => station.status === "Available").length}</strong></article>
          <article className="summary-card"><span>{isAdmin ? "All reservations" : "Your reservations"}</span><strong>{isAdmin ? bookings.length : myBookings.length}</strong></article>
          {isAdmin && <article className="summary-card"><span>Registered users</span><strong>{users.length}</strong></article>}
        </div>

        {isAdmin && <section className="dashboard-panel">
          <div className="panel-heading"><div><span className="eyebrow">ADMIN TOOLS</span><h2>Add charging station</h2></div></div>
          <form className="app-form station-form" onSubmit={addStation}>
            <label>Station name<input required value={stationForm.name} onChange={(event) => setStationForm({ ...stationForm, name: event.target.value })} placeholder="e.g. Hyderabad EV Hub" /></label>
            <label>Location<input required value={stationForm.location} onChange={(event) => setStationForm({ ...stationForm, location: event.target.value })} placeholder="Area or address" /></label>
            <label>Power (kW)<input required type="number" min="1" value={stationForm.power} onChange={(event) => setStationForm({ ...stationForm, power: event.target.value })} /></label>
            <label>Connector<select value={stationForm.connector} onChange={(event) => setStationForm({ ...stationForm, connector: event.target.value })}><option>CCS2</option><option>Type 2</option><option>CHAdeMO</option><option>Bharat AC-001</option><option>Bharat DC-001</option></select></label>
            <label>Price per kWh (₹)<input required type="number" min="0" step="0.01" value={stationForm.price} onChange={(event) => setStationForm({ ...stationForm, price: event.target.value })} /></label>
            <button className="primary-button form-submit" type="submit">+ Add station</button>
          </form>
        </section>}

        <section className="dashboard-panel">
          <div className="panel-heading"><div><span className="eyebrow">CHARGING NETWORK</span><h2>{isAdmin ? "Manage stations" : "Find a station"}</h2></div></div>
          <div className="search-box"><span>⌕</span><input type="search" value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Search by name, location, or connector..." /></div>
          <div className="station-grid">
            {filteredStations.map((station) => <article className="station-card" key={station.id}>
              <div className="station-card-top"><div className="station-icon">⚡</div><span className="status available">{station.status}</span></div>
              <h3>{station.name}</h3><p className="location">⌖ {station.location}</p>
              <div className="station-details"><div><span>Power</span><strong>{station.power} kW</strong></div><div><span>Connector</span><strong>{station.connector}</strong></div><div><span>Rate</span><strong>₹{station.price}/kWh</strong></div></div>
              {isAdmin ? <button className="station-button" onClick={() => deleteStation(station.id)}>Remove station</button> : <button className="station-button" onClick={() => { setSelectedStation(station); setBookingForm({ date: "", time: "", duration: "1" }); setPage("booking"); setMessage(""); }}>Book charging →</button>}
            </article>)}
          </div>
          {filteredStations.length === 0 && <p className="location">No matching charging stations found.</p>}
        </section>

        <section className="dashboard-panel">
          <div className="panel-heading"><div><span className="eyebrow">BOOKING MANAGEMENT</span><h2>{isAdmin ? "All reservations" : "My reservations"}</h2></div></div>
          {(isAdmin ? bookings : myBookings).length === 0 ? <p className="location">No reservations yet.</p> : <div className="booking-list">
            {(isAdmin ? bookings : myBookings).map((booking) => <article className="booking-row" key={booking.id}>
              <div className="station-icon">▣</div><div className="booking-info"><strong>{booking.stationName}</strong><span>{booking.date} at {booking.time}</span><span>{booking.duration} hour(s) · {booking.userEmail}</span></div>
              <span className={`status ${booking.status === "Booked" ? "available" : "occupied"}`}>{booking.status}</span>
              {booking.status === "Booked" && !isAdmin && <button className="small-action" onClick={() => cancelBooking(booking.id)}>Cancel</button>}
            </article>)}
          </div>}
        </section>

        {isAdmin && <section className="dashboard-panel"><div className="panel-heading"><div><span className="eyebrow">USER MANAGEMENT</span><h2>Registered users</h2></div></div>
          {users.length === 0 ? <p className="location">No locally registered users yet.</p> : <div className="booking-list">{users.map((user) => <article className="booking-row" key={user.id}><div className="station-icon">♙</div><div className="booking-info"><strong>{user.name}</strong><span>{user.email}</span></div><span className="status available">User</span></article>)}</div>}
        </section>}

        <p className="demo-disclaimer">Prototype mode: data is saved in this browser only. Live bookings, secure authentication, real-time availability, and database storage require backend API integration.</p>
      </main>

      {page === "booking" && selectedStation && <div className="modal-backdrop">
        <section className="station-modal" role="dialog" aria-modal="true" aria-labelledby="booking-title">
          <button className="close-button" type="button" onClick={() => { setPage("dashboard"); setSelectedStation(null); }} aria-label="Close booking form">×</button>
          <span className="eyebrow">RESERVE A CHARGING SLOT</span><h2 id="booking-title">{selectedStation.name}</h2><p className="location">{selectedStation.location}</p>
          <form className="app-form" onSubmit={createBooking}>
            <label>Booking date<input type="date" required min={new Date().toLocaleDateString("en-CA")} value={bookingForm.date} onChange={(event) => setBookingForm({ ...bookingForm, date: event.target.value })} /></label>
            <label>Start time<input type="time" required value={bookingForm.time} onChange={(event) => setBookingForm({ ...bookingForm, time: event.target.value })} /></label>
            <label>Duration<select value={bookingForm.duration} onChange={(event) => setBookingForm({ ...bookingForm, duration: event.target.value })}>{[1, 2, 3, 4, 5, 6, 8, 12].map((hours) => <option key={hours} value={hours}>{hours} hour(s)</option>)}</select></label>
            <p className="demo-note">This demo checks overlapping reservations saved in this browser. Production availability must be checked by the backend.</p>
            <button className="primary-button form-submit" type="submit">Confirm booking and generate QR</button>
          </form>
        </section>
      </div>}

      {confirmedBooking && <div className="modal-backdrop qr-backdrop">
        <section className="station-modal qr-modal" role="dialog" aria-modal="true" aria-labelledby="qr-title">
          <button className="close-button" type="button" onClick={() => setConfirmedBooking(null)} aria-label="Close QR code">×</button>
          <span className="eyebrow">BOOKING CONFIRMED</span><h2 id="qr-title">Your charging slot is reserved!</h2>
          <p className="location">Show this QR code when you arrive at the charging station.</p>
          <div className="qr-code-container"><QRCodeCanvas value={JSON.stringify({ bookingId: confirmedBooking.id, stationId: confirmedBooking.stationId, stationName: confirmedBooking.stationName, userEmail: confirmedBooking.userEmail, date: confirmedBooking.date, time: confirmedBooking.time, duration: confirmedBooking.duration })} size={220} level="H" includeMargin /></div>
          <div className="qr-booking-details">
            <p><strong>Booking ID:</strong><span>{confirmedBooking.id}</span></p>
            <p><strong>Station:</strong><span>{confirmedBooking.stationName}</span></p>
            <p><strong>Date:</strong><span>{confirmedBooking.date}</span></p>
            <p><strong>Start time:</strong><span>{confirmedBooking.time}</span></p>
            <p><strong>Duration:</strong><span>{confirmedBooking.duration} hour(s)</span></p>
            <p><strong>Status:</strong><span>{confirmedBooking.status}</span></p>
          </div>
          <button className="primary-button form-submit" onClick={() => window.print()}>Print booking / Save as PDF</button>
          <button className="secondary-button form-submit" onClick={() => setConfirmedBooking(null)}>Return to dashboard</button>
        </section>
      </div>}

      <footer><a className="brand footer-brand" href="#dashboard"><span className="brand-icon">⚡</span> VoltNav</a><p>Smarter charging. Better journeys.</p><span>© {new Date().getFullYear()} VoltNav</span></footer>
    </div>
  );
}
