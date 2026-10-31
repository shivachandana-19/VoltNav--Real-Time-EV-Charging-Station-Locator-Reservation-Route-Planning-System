import React from 'react';
import { Link } from 'react-router-dom';
import { QrCode, Bell, Activity } from 'lucide-react';

export default function AdminHeader({ title, subtitle, actionButton }) {
  const today = new Date().toLocaleDateString('en-US', {
    weekday: 'short',
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  });

  return (
    <header style={{
      backgroundColor: '#ffffff',
      borderBottom: '1px solid var(--border-light)',
      padding: '18px 32px',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      gap: '20px'
    }}>
      <div>
        <h1 style={{ fontSize: '1.45rem', fontWeight: '800', color: 'var(--text-main)' }}>{title}</h1>
        {subtitle && <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginTop: '2px' }}>{subtitle}</p>}
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
        {/* Status Pill */}
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '6px',
          padding: '6px 12px',
          backgroundColor: '#ecfdf5',
          borderRadius: '9999px',
          border: '1px solid #a7f3d0',
          fontSize: '0.8rem',
          color: '#065f46',
          fontWeight: '600'
        }}>
          <Activity size={14} color="#10b981" />
          <span>VoltNav Online</span>
          <span style={{ color: '#94a3b8', margin: '0 4px' }}>|</span>
          <span style={{ fontWeight: '500', color: '#64748b' }}>{today}</span>
        </div>

        {actionButton}

        <Link
          to="/admin/verify-qr"
          className="btn btn-primary btn-sm"
          style={{ gap: '6px' }}
        >
          <QrCode size={16} />
          Scan QR Code
        </Link>
      </div>
    </header>
  );
}
