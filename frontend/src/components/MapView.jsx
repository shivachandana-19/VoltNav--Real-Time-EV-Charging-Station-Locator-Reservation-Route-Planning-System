import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { Navigation, Zap, Clock, Gauge, ArrowRight, X, AlertCircle } from 'lucide-react';
import axios from 'axios';

// Fix Leaflet's default icon path issues in bundlers
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png'
});

export default function MapView({
  stations = [],
  selectedStation = null,
  onStationSelect,
  initialCenter = [19.0760, 72.8777],
  initialZoom = 12
}) {
  const mapContainerRef = useRef(null);
  const mapInstanceRef = useRef(null);
  const markersLayerRef = useRef(null);
  const routeLayerRef = useRef(null);
  const userMarkerRef = useRef(null);

  const [userLocation, setUserLocation] = useState(null);
  const [activeStation, setActiveStation] = useState(selectedStation);
  const [routeInfo, setRouteInfo] = useState(null);
  const [loadingRoute, setLoadingRoute] = useState(false);
  const [routeError, setRouteError] = useState(null);

  const navigate = useNavigate();

  // Initialize Map
  useEffect(() => {
    if (!mapContainerRef.current) return;

    if (!mapInstanceRef.current) {
      const map = L.map(mapContainerRef.current, {
        center: initialCenter,
        zoom: initialZoom,
        zoomControl: true
      });

      // OpenStreetMap standard tile layer
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors | VoltNav'
      }).addTo(map);

      markersLayerRef.current = L.layerGroup().addTo(map);
      routeLayerRef.current = L.layerGroup().addTo(map);
      mapInstanceRef.current = map;
    }

    // Request user geolocation
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          const lat = pos.coords.latitude;
          const lng = pos.coords.longitude;
          setUserLocation([lat, lng]);
        },
        (err) => {
          console.warn('Geolocation permission denied or unavailable, using city default center', err);
          // Fallback user location is slightly offset from map center so routing can be demonstrated immediately!
          setUserLocation([initialCenter[0] - 0.02, initialCenter[1] - 0.02]);
        },
        { enableHighAccuracy: true, timeout: 10000 }
      );
    } else {
      setUserLocation([initialCenter[0] - 0.02, initialCenter[1] - 0.02]);
    }

    return () => {
      if (mapInstanceRef.current) {
        mapInstanceRef.current.remove();
        mapInstanceRef.current = null;
      }
    };
  }, []);

  // Recenter map when initialCenter changes
  useEffect(() => {
    if (mapInstanceRef.current && initialCenter) {
      mapInstanceRef.current.setView(initialCenter, initialZoom);
    }
  }, [initialCenter, initialZoom]);

  // Update User Marker
  useEffect(() => {
    if (!mapInstanceRef.current || !userLocation) return;

    if (userMarkerRef.current) {
      userMarkerRef.current.remove();
    }

    const userIcon = L.divIcon({
      className: 'custom-user-marker',
      html: `<div class="custom-user-pin" title="Your Current Location">
               <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="3"></circle><path d="M12 2v3m0 14v3M2 12h3m14 0h3"></path></svg>
             </div>`,
      iconSize: [34, 34],
      iconAnchor: [17, 17]
    });

    userMarkerRef.current = L.marker(userLocation, { icon: userIcon })
      .bindPopup('<b>You are here</b><br>Origin point for EV route navigation')
      .addTo(mapInstanceRef.current);
  }, [userLocation]);

  // Sync selected station prop
  useEffect(() => {
    if (selectedStation) {
      setActiveStation(selectedStation);
      calculateRoute(selectedStation);
    }
  }, [selectedStation]);

  // Render Station Markers
  useEffect(() => {
    if (!mapInstanceRef.current || !markersLayerRef.current) return;

    markersLayerRef.current.clearLayers();

    stations.forEach((station) => {
      if (!station.latitude || !station.longitude) return;

      const isAvailable = (station.availableChargers || 0) > 0 && station.status === 'ACTIVE';
      const isSelected = activeStation && activeStation.id === station.id;

      const markerHtml = `
        <div class="custom-station-pin" style="background-color: ${isSelected ? '#0284c7' : isAvailable ? '#10b981' : '#64748b'}; width: ${isSelected ? '42px' : '36px'}; height: ${isSelected ? '42px' : '36px'};">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"></polygon>
          </svg>
        </div>
      `;

      const customIcon = L.divIcon({
        className: 'custom-station-marker',
        html: markerHtml,
        iconSize: isSelected ? [42, 42] : [36, 36],
        iconAnchor: isSelected ? [21, 42] : [18, 36],
        popupAnchor: [0, -36]
      });

      const marker = L.marker([station.latitude, station.longitude], { icon: customIcon });

      const popupContent = `
        <div style="min-width: 220px; font-family: Inter, sans-serif;">
          <h4 style="font-size: 1rem; font-weight: 700; margin-bottom: 4px; color: #0f172a;">${station.name}</h4>
          <p style="font-size: 0.8rem; color: #64748b; margin-bottom: 8px;">${station.completeAddress}</p>
          <div style="display: flex; gap: 6px; margin-bottom: 8px;">
            <span style="background: ${isAvailable ? '#ecfdf5' : '#fee2e2'}; color: ${isAvailable ? '#065f46' : '#991b1b'}; padding: 2px 8px; border-radius: 9999px; font-size: 0.75rem; font-weight: 600;">
              ${station.availableChargers || 0} / ${station.totalChargers || 4} Available
            </span>
            <span style="background: #f1f5f9; color: #334155; padding: 2px 8px; border-radius: 9999px; font-size: 0.75rem; font-weight: 600;">
              ${station.chargingSpeed}
            </span>
          </div>
          <div style="font-size: 0.8rem; font-weight: 600; color: #047857; margin-bottom: 10px;">
            ₹${station.pricingPerKwh || '18.50'} / kWh • ${station.operatingHours}
          </div>
          <button id="btn-route-${station.id}" style="width: 100%; background: #10b981; color: white; border: none; padding: 7px 12px; border-radius: 6px; font-weight: 600; cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 5px;">
            Calculate Shortest Route
          </button>
        </div>
      `;

      marker.bindPopup(popupContent);

      marker.on('click', () => {
        setActiveStation(station);
        if (onStationSelect) onStationSelect(station);
      });

      marker.on('popupopen', () => {
        const btn = document.getElementById(`btn-route-${station.id}`);
        if (btn) {
          btn.onclick = () => {
            setActiveStation(station);
            calculateRoute(station);
          };
        }
      });

      markersLayerRef.current.addLayer(marker);
    });
  }, [stations, activeStation]);

  // Calculate Shortest Driving Route via OSRM
  const calculateRoute = async (targetStation) => {
    if (!targetStation || !targetStation.latitude || !targetStation.longitude) return;

    const startLoc = userLocation || initialCenter;
    const endLoc = [targetStation.latitude, targetStation.longitude];

    setLoadingRoute(true);
    setRouteError(null);

    try {
      // OSRM driving routing service: coordinates order is {lng},{lat}
      const osrmUrl = `https://router.project-osrm.org/route/v1/driving/${startLoc[1]},${startLoc[0]};${endLoc[1]},${endLoc[0]}?overview=full&geometries=geojson&steps=true`;

      const response = await axios.get(osrmUrl);

      if (response.data && response.data.routes && response.data.routes.length > 0) {
        const route = response.data.routes[0];
        const distanceKm = (route.distance / 1000).toFixed(1);
        const durationMins = Math.round(route.duration / 60);

        setRouteInfo({
          distanceKm,
          durationMins,
          durationFormatted: durationMins >= 60 ? `${Math.floor(durationMins / 60)}h ${durationMins % 60}m` : `${durationMins} mins`,
          station: targetStation
        });

        // Draw Polyline on map
        if (routeLayerRef.current) {
          routeLayerRef.current.clearLayers();

          const coordinates = route.geometry.coordinates.map((coord) => [coord[1], coord[0]]);

          // Outer shadow polyline
          const shadowPolyline = L.polyline(coordinates, {
            color: '#0369a1',
            weight: 8,
            opacity: 0.5,
            lineJoin: 'round'
          });

          // Inner bright electric polyline
          const mainPolyline = L.polyline(coordinates, {
            color: '#06b6d4',
            weight: 5,
            opacity: 0.95,
            lineJoin: 'round'
          });

          routeLayerRef.current.addLayer(shadowPolyline);
          routeLayerRef.current.addLayer(mainPolyline);

          // Fit map bounds to show complete route smoothly
          if (mapInstanceRef.current) {
            mapInstanceRef.current.fitBounds(mainPolyline.getBounds(), {
              padding: [50, 50]
            });
          }
        }
      } else {
        setRouteError('No driving route found to this charging station.');
      }
    } catch (err) {
      console.error('OSRM route calculation error', err);
      // Fallback: draw direct straight driving guide if public router times out
      if (routeLayerRef.current && mapInstanceRef.current) {
        routeLayerRef.current.clearLayers();
        const coords = [startLoc, endLoc];
        const line = L.polyline(coords, { color: '#10b981', weight: 4, dashArray: '8, 8' });
        routeLayerRef.current.addLayer(line);
        mapInstanceRef.current.fitBounds(line.getBounds(), { padding: [50, 50] });

        // Calculate approximate Euclidean distance
        const R = 6371;
        const dLat = (endLoc[0] - startLoc[0]) * Math.PI / 180;
        const dLon = (endLoc[1] - startLoc[1]) * Math.PI / 180;
        const a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                  Math.cos(startLoc[0] * Math.PI / 180) * Math.cos(endLoc[0] * Math.PI / 180) *
                  Math.sin(dLon/2) * Math.sin(dLon/2);
        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        const dist = (R * c).toFixed(1);
        const mins = Math.round(dist * 2.5);

        setRouteInfo({
          distanceKm: dist,
          durationMins: mins,
          durationFormatted: `${mins} mins`,
          station: targetStation
        });
      }
    } finally {
      setLoadingRoute(false);
    }
  };

  const clearRoute = () => {
    if (routeLayerRef.current) {
      routeLayerRef.current.clearLayers();
    }
    setRouteInfo(null);
  };

  return (
    <div style={{ position: 'relative', width: '100%', height: '100%', minHeight: '520px', borderRadius: '16px', overflow: 'hidden' }}>
      {/* Map Leaflet Container */}
      <div ref={mapContainerRef} style={{ width: '100%', height: '100%', zIndex: 1 }} />

      {/* Floating Route Info Drawer / Panel */}
      {routeInfo && (
        <div style={{
          position: 'absolute',
          bottom: '24px',
          left: '24px',
          right: '24px',
          maxWidth: '520px',
          zIndex: 10,
          backgroundColor: '#ffffff',
          borderRadius: '16px',
          boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.2), 0 8px 10px -6px rgba(0, 0, 0, 0.1)',
          padding: '20px',
          border: '1px solid #e2e8f0',
          backdropFilter: 'blur(8px)'
        }}>
          <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', marginBottom: '12px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <div style={{
                background: 'linear-gradient(135deg, #0284c7, #06b6d4)',
                color: 'white',
                padding: '10px',
                borderRadius: '12px'
              }}>
                <Navigation size={22} />
              </div>
              <div>
                <h4 style={{ fontSize: '1.1rem', fontWeight: '800', color: '#0f172a' }}>
                  {routeInfo.station.name}
                </h4>
                <div style={{ fontSize: '0.8rem', color: '#64748b' }}>
                  Shortest driving route via OSRM
                </div>
              </div>
            </div>
            <button
              onClick={clearRoute}
              style={{ padding: '6px', color: '#94a3b8', borderRadius: '50%' }}
              title="Close navigation"
            >
              <X size={20} />
            </button>
          </div>

          {/* Metric Badges */}
          <div style={{
            display: 'grid',
            gridTemplateColumns: '1fr 1fr 1fr',
            gap: '10px',
            backgroundColor: '#f8fafc',
            borderRadius: '10px',
            padding: '12px',
            marginBottom: '16px',
            border: '1px solid #e2e8f0'
          }}>
            <div>
              <div style={{ fontSize: '0.7rem', color: '#64748b', textTransform: 'uppercase', fontWeight: '600' }}>Distance</div>
              <div style={{ fontSize: '1.25rem', fontWeight: '800', color: '#0284c7' }}>
                {routeInfo.distanceKm} <span style={{ fontSize: '0.8rem', fontWeight: '600' }}>km</span>
              </div>
            </div>

            <div>
              <div style={{ fontSize: '0.7rem', color: '#64748b', textTransform: 'uppercase', fontWeight: '600' }}>Est. Travel Time</div>
              <div style={{ fontSize: '1.25rem', fontWeight: '800', color: '#0f172a' }}>
                {routeInfo.durationFormatted}
              </div>
            </div>

            <div>
              <div style={{ fontSize: '0.7rem', color: '#64748b', textTransform: 'uppercase', fontWeight: '600' }}>Chargers Available</div>
              <div style={{ fontSize: '1.25rem', fontWeight: '800', color: '#059669' }}>
                {routeInfo.station.availableChargers} / {routeInfo.station.totalChargers}
              </div>
            </div>
          </div>

          {/* Quick Book Slot CTA */}
          <div style={{ display: 'flex', gap: '10px' }}>
            <button
              onClick={() => navigate(`/book/${routeInfo.station.id}`)}
              className="btn btn-primary"
              style={{ flex: 1, padding: '12px' }}
            >
              <Zap size={18} />
              Reserve Charging Slot Now
            </button>
            <button
              onClick={clearRoute}
              className="btn btn-secondary"
              style={{ padding: '12px 18px' }}
            >
              Done
            </button>
          </div>
        </div>
      )}

      {/* Loading Overlay */}
      {loadingRoute && (
        <div style={{
          position: 'absolute',
          top: '20px',
          right: '20px',
          zIndex: 10,
          backgroundColor: '#0f172a',
          color: 'white',
          padding: '10px 18px',
          borderRadius: '9999px',
          display: 'flex',
          alignItems: 'center',
          gap: '10px',
          boxShadow: '0 10px 15px -3px rgba(0,0,0,0.3)',
          fontSize: '0.85rem',
          fontWeight: '600'
        }}>
          <Navigation size={16} className="animate-spin" />
          <span>Calculating Shortest Route...</span>
        </div>
      )}

      {/* Route Error Notification */}
      {routeError && (
        <div style={{
          position: 'absolute',
          top: '20px',
          right: '20px',
          zIndex: 10,
          backgroundColor: '#fee2e2',
          color: '#991b1b',
          border: '1px solid #fecaca',
          padding: '10px 18px',
          borderRadius: '8px',
          display: 'flex',
          alignItems: 'center',
          gap: '10px',
          fontSize: '0.85rem'
        }}>
          <AlertCircle size={16} />
          <span>{routeError}</span>
        </div>
      )}
    </div>
  );
}
