import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { cityApi } from '../services/api';
import { Zap, MapPin, Calendar, User, LogOut, ChevronDown, Compass, Shield } from 'lucide-react';

export default function Navbar() {
  const { user, isAuthenticated, isAdmin, isUser, logout, selectedCity, setSelectedCity } = useAuth();
  const [cities, setCities] = useState([]);
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    cityApi.getAll()
      .then((res) => {
        setCities(res.data);
        if (!selectedCity && res.data.length > 0) {
          setSelectedCity(res.data[0]);
        }
      })
      .catch((err) => console.error('Failed to load cities in navbar', err));
  }, []);

  const handleCityChange = (e) => {
    const cityId = Number(e.target.value);
    const found = cities.find((c) => c.id === cityId);
    if (found) {
      setSelectedCity(found);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header style={{
      backgroundColor: '#0f172a',
      color: '#ffffff',
      borderBottom: '1px solid #1e293b',
      position: 'sticky',
      top: 0,
      zIndex: 100
    }}>
      <div style={{
        maxWidth: '1280px',
        margin: '0 auto',
        padding: '0 20px',
        height: '70px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        gap: '20px'
      }}>
        {/* Logo */}
        <Link to="/" style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <div style={{
            background: 'linear-gradient(135deg, #10b981, #06b6d4)',
            color: '#ffffff',
            width: '38px',
            height: '38px',
            borderRadius: '10px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            boxShadow: '0 0 15px rgba(16, 185, 129, 0.4)'
          }}>
            <Zap size={22} fill="white" />
          </div>
          <div>
            <span style={{ fontSize: '1.4rem', fontWeight: '800', letterSpacing: '-0.03em', color: '#ffffff' }}>Volt<span style={{ color: '#10b981' }}>Nav</span></span>
            <div style={{ fontSize: '0.65rem', color: '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.08em', marginTop: '-3px' }}>Smart EV Navigator</div>
          </div>
        </Link>

        {/* City Selector Pill */}
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '8px',
          backgroundColor: '#1e293b',
          padding: '6px 14px',
          borderRadius: '9999px',
          border: '1px solid #334155'
        }}>
          <MapPin size={16} color="#10b981" />
          <span style={{ fontSize: '0.8rem', color: '#94a3b8' }}>City:</span>
          <select
            value={selectedCity?.id || ''}
            onChange={handleCityChange}
            style={{
              backgroundColor: 'transparent',
              color: '#ffffff',
              border: 'none',
              fontWeight: '600',
              fontSize: '0.9rem',
              cursor: 'pointer'
            }}
          >
            {cities.map((city) => (
              <option key={city.id} value={city.id} style={{ backgroundColor: '#1e293b', color: '#ffffff' }}>
                {city.name}
              </option>
            ))}
          </select>
        </div>

        {/* Navigation Links */}
        <nav style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <Link
            to="/stations"
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              padding: '8px 14px',
              borderRadius: '8px',
              fontSize: '0.9rem',
              fontWeight: '500',
              color: location.pathname === '/stations' ? '#10b981' : '#cbd5e1',
              backgroundColor: location.pathname === '/stations' ? 'rgba(16, 185, 129, 0.1)' : 'transparent'
            }}
          >
            <Compass size={18} />
            Stations
          </Link>

          <Link
            to="/map"
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              padding: '8px 14px',
              borderRadius: '8px',
              fontSize: '0.9rem',
              fontWeight: '500',
              color: location.pathname === '/map' ? '#10b981' : '#cbd5e1',
              backgroundColor: location.pathname === '/map' ? 'rgba(16, 185, 129, 0.1)' : 'transparent'
            }}
          >
            <MapPin size={18} />
            Interactive Map
          </Link>

          {isAuthenticated && isUser && (
            <Link
              to="/my-bookings"
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '6px',
                padding: '8px 14px',
                borderRadius: '8px',
                fontSize: '0.9rem',
                fontWeight: '500',
                color: location.pathname === '/my-bookings' ? '#10b981' : '#cbd5e1',
                backgroundColor: location.pathname === '/my-bookings' ? 'rgba(16, 185, 129, 0.1)' : 'transparent'
              }}
            >
              <Calendar size={18} />
              My Bookings
            </Link>
          )}

          {isAdmin && (
            <Link
              to="/admin/dashboard"
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '6px',
                padding: '8px 14px',
                borderRadius: '8px',
                fontSize: '0.9rem',
                fontWeight: '600',
                color: '#38bdf8',
                backgroundColor: 'rgba(56, 189, 248, 0.1)'
              }}
            >
              <Shield size={18} />
              Admin Console
            </Link>
          )}
        </nav>

        {/* Auth / Profile Area */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          {isAuthenticated ? (
            <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
              <Link
                to={isAdmin ? '/admin/dashboard' : '/profile'}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px',
                  backgroundColor: '#1e293b',
                  padding: '6px 12px',
                  borderRadius: '8px',
                  border: '1px solid #334155'
                }}
              >
                <div style={{
                  width: '28px',
                  height: '28px',
                  borderRadius: '50%',
                  backgroundColor: '#10b981',
                  color: '#ffffff',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontWeight: '700',
                  fontSize: '0.8rem'
                }}>
                  {user?.name ? user.name.charAt(0).toUpperCase() : 'U'}
                </div>
                <span style={{ fontSize: '0.85rem', fontWeight: '600' }}>{user?.name}</span>
              </Link>
              <button
                onClick={handleLogout}
                className="btn btn-secondary btn-sm"
                style={{ backgroundColor: '#1e293b', color: '#f87171', border: '1px solid #334155' }}
                title="Log out"
              >
                <LogOut size={16} />
              </button>
            </div>
          ) : (
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <Link to="/login" className="btn btn-secondary btn-sm" style={{ backgroundColor: '#1e293b', color: '#ffffff', border: '1px solid #334155' }}>
                Sign In
              </Link>
              <Link to="/register" className="btn btn-primary btn-sm">
                Register
              </Link>
              <Link to="/admin/login" style={{ fontSize: '0.75rem', color: '#94a3b8', marginLeft: '6px', textDecoration: 'underline' }}>
                Admin
              </Link>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}
