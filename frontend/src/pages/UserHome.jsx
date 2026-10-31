import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { stationApi, bookingApi, cityApi } from '../services/api';
import wsService from '../services/websocket';
import StationCard from '../components/StationCard';
import {
  Zap,
  MapPin,
  Calendar,
  Navigation,
  Shield,
  ArrowRight,
  Clock,
  Sparkles,
  CheckCircle,
  Activity
} from 'lucide-react';

export default function UserHome() {
  const { user, isAuthenticated, selectedCity, setSelectedCity } = useAuth();
  const [stations, setStations] = useState([]);
  const [cities, setCities] = useState([]);
  const [myBookings, setMyBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  // Load cities
  useEffect(() => {
    cityApi.getAll().then((res) => {
      setCities(res.data);
      if (!selectedCity && res.data.length > 0) {
        setSelectedCity(res.data[0]);
      }
    });
  }, []);

  // Load stations for selected city
  useEffect(() => {
    if (!selectedCity) return;
    setLoading(true);
    stationApi.getByCity(selectedCity.id)
      .then((res) => {
        setStations(res.data);
      })
      .catch((err) => console.error('Failed to load stations', err))
      .finally(() => setLoading(false));
  }, [selectedCity]);

  // Load user's bookings if authenticated
  useEffect(() => {
    if (isAuthenticated) {
      bookingApi.getMyBookings()
        .then((res) => setMyBookings(res.data))
        .catch((err) => console.error('Failed to load user bookings', err));
    }
  }, [isAuthenticated]);

  // Real-time WebSocket updates for slot/charger availability
  useEffect(() => {
    const unsubscribe = wsService.subscribeToSlots((update) => {
      setStations((prevStations) =>
        prevStations.map((st) => {
          if (st.id === update.stationId) {
            return {
              ...st,
              availableChargers: update.availableChargers
            };
          }
          return st;
        })
      );
    });

    return () => {
      if (unsubscribe) unsubscribe();
    };
  }, []);

  const upcomingBookings = myBookings.filter((b) => b.status === 'CONFIRMED');
  const availableStationsCount = stations.filter((s) => (s.availableChargers || 0) > 0 && s.status === 'ACTIVE').length;

  return (
    <div style={{ minHeight: 'calc(100vh - 70px)', paddingBottom: '60px' }}>
      {/* Hero Banner Section */}
      <section style={{
        background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)',
        color: '#ffffff',
        padding: '60px 20px',
        borderBottom: '1px solid #334155',
        position: 'relative',
        overflow: 'hidden'
      }}>
        {/* Glow orb background decorative effects */}
        <div style={{
          position: 'absolute',
          top: '-80px',
          right: '5%',
          width: '350px',
          height: '350px',
          borderRadius: '50%',
          background: 'radial-gradient(circle, rgba(16, 185, 129, 0.25) 0%, transparent 70%)',
          pointerEvents: 'none'
        }} />

        <div style={{ maxWidth: '1280px', margin: '0 auto' }}>
          <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 0.8fr', gap: '40px', alignItems: 'center' }}>
            <div>
              <div style={{
                display: 'inline-flex',
                alignItems: 'center',
                gap: '8px',
                padding: '6px 14px',
                borderRadius: '9999px',
                backgroundColor: 'rgba(16, 185, 129, 0.15)',
                border: '1px solid rgba(16, 185, 129, 0.3)',
                color: '#34d399',
                fontSize: '0.85rem',
                fontWeight: '600',
                marginBottom: '18px'
              }}>
                <Sparkles size={16} />
                <span>Next-Gen Smart EV Mobility & Route Navigation</span>
              </div>

              <h1 style={{ fontSize: '2.8rem', fontWeight: '900', color: '#ffffff', lineHeight: '1.15', marginBottom: '16px' }}>
                Charge Smarter.<br />
                <span style={{ color: '#10b981' }}>Navigate Faster.</span>
              </h1>

              <p style={{ fontSize: '1.05rem', color: '#94a3b8', lineHeight: '1.6', maxWidth: '580px', marginBottom: '28px' }}>
                Locate high-speed EV chargers in real-time across {selectedCity?.name || 'your city'}, reserve your dedicated charging bay, and get turn-by-turn shortest driving routes with instant QR code check-in.
              </p>

              {/* Action Buttons */}
              <div style={{ display: 'flex', gap: '14px', flexWrap: 'wrap' }}>
                <Link to="/map" className="btn btn-primary btn-lg" style={{ gap: '10px' }}>
                  <Navigation size={20} />
                  <span>Interactive Live Map & Route</span>
                </Link>

                <Link to="/stations" className="btn btn-secondary btn-lg" style={{ backgroundColor: '#1e293b', color: '#ffffff', border: '1px solid #475569' }}>
                  <span>Browse All Stations</span>
                  <ArrowRight size={18} />
                </Link>
              </div>
            </div>

            {/* Quick City Status Card */}
            <div className="card" style={{
              backgroundColor: 'rgba(30, 41, 59, 0.85)',
              borderColor: '#334155',
              backdropFilter: 'blur(12px)',
              padding: '28px',
              color: '#ffffff'
            }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '20px' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                  <MapPin size={24} color="#10b981" />
                  <div>
                    <h3 style={{ fontSize: '1.2rem', color: '#ffffff' }}>{selectedCity?.name || 'Selected City'}</h3>
                    <div style={{ fontSize: '0.8rem', color: '#94a3b8' }}>{selectedCity?.state || 'India'}</div>
                  </div>
                </div>

                <span className="badge badge-success" style={{ gap: '6px' }}>
                  <Activity size={12} />
                  Live Sync
                </span>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '20px' }}>
                <div style={{ backgroundColor: '#0f172a', padding: '14px', borderRadius: '10px', border: '1px solid #334155' }}>
                  <div style={{ fontSize: '0.75rem', color: '#94a3b8', textTransform: 'uppercase' }}>Charging Stations</div>
                  <div style={{ fontSize: '1.6rem', fontWeight: '800', color: '#ffffff', marginTop: '4px' }}>
                    {stations.length}
                  </div>
                </div>

                <div style={{ backgroundColor: '#0f172a', padding: '14px', borderRadius: '10px', border: '1px solid #334155' }}>
                  <div style={{ fontSize: '0.75rem', color: '#94a3b8', textTransform: 'uppercase' }}>Available With Slots</div>
                  <div style={{ fontSize: '1.6rem', fontWeight: '800', color: '#10b981', marginTop: '4px' }}>
                    {availableStationsCount}
                  </div>
                </div>
              </div>

              <div style={{ fontSize: '0.85rem', color: '#cbd5e1', lineHeight: '1.5' }}>
                {selectedCity?.description || 'Explore verified high-speed charging hubs equipped with Type 2 AC and CCS2 DC fast charging capabilities.'}
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Main Content Area */}
      <div style={{ maxWidth: '1280px', margin: '40px auto 0', padding: '0 20px' }}>
        {/* User's Upcoming Bookings Ribbon (If logged in and has active bookings) */}
        {isAuthenticated && upcomingBookings.length > 0 && (
          <div style={{
            backgroundColor: '#ffffff',
            borderRadius: '16px',
            border: '1.5px solid #10b981',
            padding: '20px 24px',
            boxShadow: 'var(--shadow-md)',
            marginBottom: '36px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            flexWrap: 'wrap',
            gap: '16px'
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
              <div style={{
                width: '48px',
                height: '48px',
                borderRadius: '12px',
                backgroundColor: '#ecfdf5',
                color: '#10b981',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }}>
                <Calendar size={24} />
              </div>
              <div>
                <div style={{ fontSize: '0.75rem', fontWeight: '700', color: '#059669', textTransform: 'uppercase' }}>Upcoming Reservation</div>
                <h4 style={{ fontSize: '1.15rem', fontWeight: '800', color: '#0f172a' }}>
                  {upcomingBookings[0].stationName}
                </h4>
                <div style={{ fontSize: '0.825rem', color: '#64748b' }}>
                  {upcomingBookings[0].bookingDate} • {upcomingBookings[0].startTime} - {upcomingBookings[0].endTime} • Bay: {upcomingBookings[0].slotNumber || 'Bay 1'}
                </div>
              </div>
            </div>

            <div style={{ display: 'flex', gap: '10px' }}>
              <Link
                to={`/booking-confirmation/${upcomingBookings[0].bookingId}`}
                className="btn btn-primary btn-sm"
              >
                View QR Pass
              </Link>
              <Link to="/my-bookings" className="btn btn-secondary btn-sm">
                Manage Bookings ({upcomingBookings.length})
              </Link>
            </div>
          </div>
        )}

        {/* Section: Nearby Charging Stations */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '24px' }}>
          <div>
            <h2 style={{ fontSize: '1.75rem', fontWeight: '800', color: '#0f172a' }}>
              Available Charging Stations in {selectedCity?.name}
            </h2>
            <p style={{ fontSize: '0.9rem', color: '#64748b', marginTop: '2px' }}>
              Live real-time charger bay statuses updated automatically
            </p>
          </div>

          <Link to="/stations" className="btn btn-outline btn-sm">
            <span>View All ({stations.length})</span>
            <ArrowRight size={16} />
          </Link>
        </div>

        {/* Stations Grid */}
        {loading ? (
          <div style={{ textAlign: 'center', padding: '60px 0', color: '#64748b' }}>
            <Zap size={36} color="#10b981" className="animate-spin" style={{ margin: '0 auto 12px' }} />
            <p>Fetching real-time charging stations...</p>
          </div>
        ) : stations.length === 0 ? (
          <div className="card" style={{ textAlign: 'center', padding: '50px 20px' }}>
            <MapPin size={42} color="#94a3b8" style={{ margin: '0 auto 12px' }} />
            <h3 style={{ fontSize: '1.2rem', color: '#0f172a' }}>No charging stations listed in this city yet</h3>
            <p style={{ color: '#64748b', fontSize: '0.9rem', marginTop: '4px' }}>
              Select another city or check back soon as new charging corridors are added.
            </p>
          </div>
        ) : (
          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fill, minmax(340px, 1fr))',
            gap: '24px'
          }}>
            {stations.slice(0, 6).map((station) => (
              <StationCard
                key={station.id}
                station={station}
                onNavigateToMap={(st) => navigate(`/map?stationId=${st.id}`)}
              />
            ))}
          </div>
        )}

        {/* Bottom Feature Strip */}
        <div style={{
          marginTop: '60px',
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))',
          gap: '20px'
        }}>
          <div className="card" style={{ display: 'flex', gap: '14px', alignItems: 'flex-start' }}>
            <div style={{ backgroundColor: '#ecfdf5', color: '#10b981', padding: '12px', borderRadius: '12px' }}>
              <Clock size={24} />
            </div>
            <div>
              <h4 style={{ fontSize: '1rem', fontWeight: '700', marginBottom: '4px' }}>Real-Time Availability</h4>
              <p style={{ fontSize: '0.825rem', color: '#64748b' }}>WebSocket synchronized slot updates eliminate queues and wait times.</p>
            </div>
          </div>

          <div className="card" style={{ display: 'flex', gap: '14px', alignItems: 'flex-start' }}>
            <div style={{ backgroundColor: '#e0f2fe', color: '#0284c7', padding: '12px', borderRadius: '12px' }}>
              <Navigation size={24} />
            </div>
            <div>
              <h4 style={{ fontSize: '1rem', fontWeight: '700', marginBottom: '4px' }}>OSRM Shortest Route</h4>
              <p style={{ fontSize: '0.825rem', color: '#64748b' }}>Calculate accurate driving distances and estimated arrival times.</p>
            </div>
          </div>

          <div className="card" style={{ display: 'flex', gap: '14px', alignItems: 'flex-start' }}>
            <div style={{ backgroundColor: '#fef3c7', color: '#d97706', padding: '12px', borderRadius: '12px' }}>
              <Shield size={24} />
            </div>
            <div>
              <h4 style={{ fontSize: '1rem', fontWeight: '700', marginBottom: '4px' }}>Instant QR Code Pass</h4>
              <p style={{ fontSize: '0.825rem', color: '#64748b' }}>Tamper-proof digital tokens for seamless check-in verification.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
