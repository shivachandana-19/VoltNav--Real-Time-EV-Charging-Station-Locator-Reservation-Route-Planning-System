import React from 'react';
import { Download, X, CheckCircle2, ShieldCheck, MapPin, Clock } from 'lucide-react';

export default function QRCodeModal({ booking, onClose }) {
  if (!booking) return null;

  const handleDownload = () => {
    if (!booking.qrCodeImage) return;
    const link = document.createElement('a');
    link.href = booking.qrCodeImage;
    link.download = `VoltNav-${booking.bookingId}-QR.png`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '440px', textAlign: 'center', padding: '28px 24px' }}>
        <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
          <button onClick={onClose} style={{ color: '#94a3b8' }}>
            <X size={20} />
          </button>
        </div>

        <div style={{
          display: 'inline-flex',
          alignItems: 'center',
          gap: '6px',
          backgroundColor: '#ecfdf5',
          color: '#065f46',
          padding: '6px 14px',
          borderRadius: '9999px',
          fontSize: '0.8rem',
          fontWeight: '700',
          marginBottom: '12px',
          textTransform: 'uppercase'
        }}>
          <ShieldCheck size={16} color="#10b981" />
          <span>Confirmed Reservation Pass</span>
        </div>

        <h3 style={{ fontSize: '1.35rem', fontWeight: '800', color: '#0f172a', marginBottom: '4px' }}>
          {booking.stationName}
        </h3>
        <p style={{ fontSize: '0.825rem', color: '#64748b', marginBottom: '20px' }}>
          Booking ID: <strong style={{ color: '#0f172a' }}>{booking.bookingId}</strong>
        </p>

        {/* QR Code Container */}
        <div style={{
          display: 'inline-block',
          padding: '16px',
          backgroundColor: '#ffffff',
          borderRadius: '16px',
          border: '2px dashed #10b981',
          boxShadow: '0 10px 15px -3px rgba(16, 185, 129, 0.1)',
          marginBottom: '20px'
        }}>
          {booking.qrCodeImage ? (
            <img
              src={booking.qrCodeImage}
              alt="VoltNav Booking QR Code"
              style={{ width: '220px', height: '220px', display: 'block', borderRadius: '8px' }}
            />
          ) : (
            <div style={{ width: '220px', height: '220px', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#94a3b8' }}>
              QR Code unavailable
            </div>
          )}
        </div>

        {/* Details Card */}
        <div style={{
          backgroundColor: '#f8fafc',
          borderRadius: '12px',
          padding: '14px',
          textAlign: 'left',
          fontSize: '0.85rem',
          border: '1px solid #e2e8f0',
          marginBottom: '20px',
          display: 'flex',
          flexDirection: 'column',
          gap: '8px'
        }}>
          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <span style={{ color: '#64748b' }}>Date & Timing:</span>
            <span style={{ fontWeight: '600', color: '#0f172a' }}>{booking.bookingDate} • {booking.startTime} - {booking.endTime}</span>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <span style={{ color: '#64748b' }}>Reserved Bay:</span>
            <span style={{ fontWeight: '600', color: '#0f172a' }}>{booking.slotNumber || 'Bay 1'}</span>
          </div>
          {booking.vehicleNumber && (
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: '#64748b' }}>Vehicle:</span>
              <span style={{ fontWeight: '600', color: '#0f172a' }}>{booking.vehicleNumber}</span>
            </div>
          )}
          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <span style={{ color: '#64748b' }}>Status:</span>
            <span className={`badge ${booking.status === 'CONFIRMED' ? 'badge-success' : 'badge-purple'}`} style={{ padding: '2px 8px', fontSize: '0.7rem' }}>
              {booking.status}
            </span>
          </div>
        </div>

        <button
          onClick={handleDownload}
          className="btn btn-primary"
          style={{ width: '100%', padding: '12px', marginBottom: '8px' }}
        >
          <Download size={18} />
          Download QR Pass (PNG)
        </button>

        <p style={{ fontSize: '0.75rem', color: '#94a3b8' }}>
          Present this QR pass to the station operator or kiosk upon arrival to activate the charger.
        </p>
      </div>
    </div>
  );
}
