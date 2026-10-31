import React from 'react';
import { NavLink, Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  LayoutDashboard,
  Building2,
  MapPin,
  Zap,
  Clock,
  CalendarCheck,
  Users,
  QrCode,
  BarChart3,
  LogOut,
  ExternalLink,
  ShieldCheck
} from 'lucide-react';

export default function AdminSidebar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/admin/login');
  };

  const navItems = [
    { name: 'Dashboard', path: '/admin/dashboard', icon: LayoutDashboard },
    { name: 'Manage Cities', path: '/admin/cities', icon: Building2 },
    { name: 'Manage Locations', path: '/admin/locations', icon: MapPin },
    { name: 'Charging Stations', path: '/admin/stations', icon: Zap },
    { name: 'Charging Slots', path: '/admin/slots', icon: Clock },
    { name: 'Manage Bookings', path: '/admin/bookings', icon: CalendarCheck },
    { name: 'Registered Users', path: '/admin/users', icon: Users },
    { name: 'Verify QR Code', path: '/admin/verify-qr', icon: QrCode },
    { name: 'Analytics & Reports', path: '/admin/reports', icon: BarChart3 }
  ];

  return (
    <aside style={{
      width: '260px',
      backgroundColor: '#0f172a',
      color: '#ffffff',
      height: '100vh',
      position: 'sticky',
      top: 0,
      display: 'flex',
      flexDirection: 'column',
      borderRight: '1px solid #1e293b',
      userSelect: 'none'
    }}>
      {/* Brand Header */}
      <div style={{
        padding: '24px 20px',
        borderBottom: '1px solid #1e293b',
        display: 'flex',
        alignItems: 'center',
        gap: '12px'
      }}>
        <div style={{
          background: 'linear-gradient(135deg, #10b981, #06b6d4)',
          color: '#ffffff',
          width: '40px',
          height: '40px',
          borderRadius: '10px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          boxShadow: '0 0 15px rgba(16, 185, 129, 0.4)'
        }}>
          <ShieldCheck size={24} />
        </div>
        <div>
          <div style={{ fontSize: '1.25rem', fontWeight: '800', letterSpacing: '-0.02em' }}>
            Volt<span style={{ color: '#10b981' }}>Nav</span>
          </div>
          <div style={{ fontSize: '0.7rem', color: '#38bdf8', fontWeight: '600', textTransform: 'uppercase', letterSpacing: '0.08em' }}>
            Admin Portal
          </div>
        </div>
      </div>

      {/* Navigation Links */}
      <div style={{ flex: 1, padding: '16px 12px', overflowY: 'auto' }}>
        <div style={{ fontSize: '0.7rem', color: '#64748b', textTransform: 'uppercase', fontWeight: '700', padding: '0 12px 8px', letterSpacing: '0.05em' }}>
          Operations & Infrastructure
        </div>
        <nav style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.path}
                to={item.path}
                style={({ isActive }) => ({
                  display: 'flex',
                  alignItems: 'center',
                  gap: '12px',
                  padding: '10px 14px',
                  borderRadius: '8px',
                  fontSize: '0.9rem',
                  fontWeight: isActive ? '600' : '500',
                  color: isActive ? '#10b981' : '#94a3b8',
                  backgroundColor: isActive ? 'rgba(16, 185, 129, 0.12)' : 'transparent',
                  borderLeft: isActive ? '3px solid #10b981' : '3px solid transparent',
                  transition: 'all 0.15s ease'
                })}
              >
                <Icon size={18} />
                <span>{item.name}</span>
              </NavLink>
            );
          })}
        </nav>
      </div>

      {/* User Portal Link & Footer */}
      <div style={{
        padding: '16px',
        borderTop: '1px solid #1e293b',
        backgroundColor: '#090d16'
      }}>
        <Link
          to="/"
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            padding: '8px 12px',
            borderRadius: '6px',
            backgroundColor: '#1e293b',
            color: '#cbd5e1',
            fontSize: '0.8rem',
            marginBottom: '12px'
          }}
        >
          <span>View Public EV Portal</span>
          <ExternalLink size={14} />
        </Link>

        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div style={{ overflow: 'hidden' }}>
            <div style={{ fontSize: '0.85rem', fontWeight: '600', color: '#ffffff', whiteSpace: 'nowrap', textOverflow: 'ellipsis' }}>
              {user?.name || 'Administrator'}
            </div>
            <div style={{ fontSize: '0.725rem', color: '#64748b', whiteSpace: 'nowrap', textOverflow: 'ellipsis' }}>
              {user?.email || 'admin@voltnav.com'}
            </div>
          </div>
          <button
            onClick={handleLogout}
            style={{
              padding: '6px',
              borderRadius: '6px',
              color: '#ef4444',
              backgroundColor: 'rgba(239, 68, 68, 0.1)'
            }}
            title="Log out"
          >
            <LogOut size={16} />
          </button>
        </div>
      </div>
    </aside>
  );
}
