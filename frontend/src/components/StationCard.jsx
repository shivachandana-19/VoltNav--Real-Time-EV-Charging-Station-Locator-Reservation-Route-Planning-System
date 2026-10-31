import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Zap, MapPin, Clock, Gauge, ArrowRight, Navigation } from 'lucide-react';

export default function StationCard({ station, onNavigateToMap }) {
  const navigate = useNavigate();

  const isAvailable = (station.availableChargers || 0) > 0 && station.status === 'ACTIVE';

  return (
    <div className="card" style={{
      display: 'flex',
      flexDirection: 'column',
      justifyContent: 'space-between',
      position: 'relative',
      overflow: 'hidden',
      borderTop: isAvailable ? '4px solid #10b981' : '4px solid #94a3b8'
    }}>
      {/* Top Header */}
      <div>
        <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', gap: '10px', marginBottom: '8px' }}>
          <div>
            <h3 style={{ fontSize: '1.15rem', fontWeight: '700', color: 'var(--text-main)' }}>
              {station.name}
            </h3>
            <div style={{ display: 'flex', alignItems: 'center', gap: '5px', color: 'var(--text-muted)', fontSize: '0.825rem', marginTop: '2px' }}>
              <MapPin size={14} color="#10b981" />
              <span>{station.locationName}, {station.cityName}</span>
            </div>
          </div>
          <span className={`badge ${isAvailable ? 'badge-success' : 'badge-danger'}`}>
            {station.status === 'ACTIVE' ? (isAvailable ? 'Available' : 'Full / Busy') : 'Inactive'}
          </span>
        </div>

        <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '14px', lineHeight: '1.4' }}>
          {station.completeAddress}
        </p>

        {/* Charger Availability Pill & Specs Grid */}
        <div style={{
          backgroundColor: '#f8fafc',
          borderRadius: '10px',
          padding: '12px',
          display: 'grid',
          gridTemplateColumns: '1fr 1fr',
          gap: '10px',
          marginBottom: '16px',
          border: '1px solid var(--border-light)'
        }}>
          <div>
            <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)', textTransform: 'uppercase', fontWeight: '600' }}>Available Chargers</div>
            <div style={{ fontSize: '1.1rem', fontWeight: '800', color: isAvailable ? '#059669' : '#dc2626', display: 'flex', alignItems: 'center', gap: '4px' }}>
              <Zap size={16} />
              <span>{station.availableChargers || 0} / {station.totalChargers || 4}</span>
            </div>
          </div>

          <div>
            <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)', textTransform: 'uppercase', fontWeight: '600' }}>Charging Speed</div>
            <div style={{ fontSize: '0.95rem', fontWeight: '700', color: 'var(--text-main)', display: 'flex', alignItems: 'center', gap: '4px' }}>
              <Gauge size={16} color="#06b6d4" />
              <span>{station.chargingSpeed}</span>
            </div>
          </div>

          <div style={{ gridColumn: 'span 2' }}>
            <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)', textTransform: 'uppercase', fontWeight: '600' }}>Connector Support</div>
            <div style={{ fontSize: '0.85rem', fontWeight: '600', color: '#334155', marginTop: '2px' }}>
              {station.chargerType}
            </div>
          </div>

          <div>
            <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)', textTransform: 'uppercase', fontWeight: '600' }}>Operating Hours</div>
            <div style={{ fontSize: '0.85rem', fontWeight: '600', color: 'var(--text-main)', display: 'flex', alignItems: 'center', gap: '4px' }}>
              <Clock size={14} color="#64748b" />
              <span>{station.operatingHours}</span>
            </div>
          </div>

          <div>
            <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)', textTransform: 'uppercase', fontWeight: '600' }}>Tariff Rate</div>
            <div style={{ fontSize: '0.9rem', fontWeight: '700', color: '#047857' }}>
              ₹{station.pricingPerKwh || '18.50'} / kWh
            </div>
          </div>
        </div>
      </div>

      {/* Action Buttons */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1.2fr', gap: '8px' }}>
        <button
          onClick={() => {
            if (onNavigateToMap) {
              onNavigateToMap(station);
            } else {
              navigate(`/map?stationId=${station.id}`);
            }
          }}
          className="btn btn-secondary btn-sm"
          style={{ width: '100%', gap: '4px' }}
        >
          <Navigation size={14} />
          Route & Map
        </button>

        <button
          onClick={() => navigate(`/book/${station.id}`)}
          disabled={station.status !== 'ACTIVE'}
          className="btn btn-primary btn-sm"
          style={{ width: '100%', gap: '4px' }}
        >
          <span>Reserve Slot</span>
          <ArrowRight size={14} />
        </button>
      </div>
    </div>
  );
}
