import axios from 'axios';

const API_BASE_URL = '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Request interceptor to attach JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('voltnav_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Token expired or invalid
      const currentPath = window.location.pathname;
      if (!currentPath.includes('/login') && !currentPath.includes('/register')) {
        // clear local storage and redirect if needed
      }
    }
    return Promise.reject(error);
  }
);

/* ==================== AUTH APIS ==================== */
export const authApi = {
  loginUser: (email, password) => api.post('/auth/user/login', { email, password }),
  registerUser: (data) => api.post('/auth/user/register', data),
  loginAdmin: (email, password) => api.post('/auth/admin/login', { email, password }),
  registerAdmin: (data) => api.post('/auth/admin/register', data)
};

/* ==================== CITY APIS ==================== */
export const cityApi = {
  getAll: () => api.get('/cities'),
  getById: (id) => api.get(`/cities/${id}`),
  search: (query) => api.get(`/cities/search?query=${encodeURIComponent(query || '')}`),
  create: (data) => api.post('/cities', data),
  update: (id, data) => api.put(`/cities/${id}`, data),
  delete: (id) => api.delete(`/cities/${id}`)
};

/* ==================== LOCATION APIS ==================== */
export const locationApi = {
  getAll: () => api.get('/locations'),
  getById: (id) => api.get(`/locations/${id}`),
  getByCity: (cityId) => api.get(`/locations/city/${cityId}`),
  search: (query) => api.get(`/locations/search?query=${encodeURIComponent(query || '')}`),
  create: (data) => api.post('/locations', data),
  update: (id, data) => api.put(`/locations/${id}`, data),
  delete: (id) => api.delete(`/locations/${id}`)
};

/* ==================== STATION APIS ==================== */
export const stationApi = {
  getAll: () => api.get('/stations'),
  getById: (id) => api.get(`/stations/${id}`),
  getByCity: (cityId) => api.get(`/stations/city/${cityId}`),
  getByLocation: (locationId) => api.get(`/stations/location/${locationId}`),
  search: (params) => api.get('/stations/search', { params }),
  create: (data) => api.post('/stations', data),
  update: (id, data) => api.put(`/stations/${id}`, data),
  delete: (id) => api.delete(`/stations/${id}`)
};

/* ==================== CHARGING SLOT APIS ==================== */
export const slotApi = {
  getByStation: (stationId, date) => api.get(`/slots/station/${stationId}`, { params: { date } }),
  getAvailableByStation: (stationId, date) => api.get(`/slots/station/${stationId}/available`, { params: { date } }),
  create: (data) => api.post('/slots', data),
  bulkGenerate: (data) => api.post('/slots/bulk', data),
  update: (id, data) => api.put(`/slots/${id}`, data),
  updateStatus: (id, status) => api.patch(`/slots/${id}/status?status=${status}`),
  delete: (id) => api.delete(`/slots/${id}`)
};

/* ==================== BOOKING APIS ==================== */
export const bookingApi = {
  create: (data) => api.post('/bookings', data),
  getMyBookings: () => api.get('/bookings/my'),
  getById: (id) => api.get(`/bookings/${id}`),
  getByReference: (bookingId) => api.get(`/bookings/reference/${bookingId}`),
  cancel: (id) => api.post(`/bookings/${id}/cancel`)
};

/* ==================== ADMIN APIS ==================== */
export const adminApi = {
  getDashboard: () => api.get('/admin/dashboard'),
  getBookings: (params) => api.get('/admin/bookings', { params }),
  getUsers: () => api.get('/admin/users'),
  getUserById: (id) => api.get(`/admin/users/${id}`),
  toggleUserStatus: (id) => api.patch(`/admin/users/${id}/toggle-status`),
  verifyQrCode: (data) => api.post('/admin/qr/verify', data),
  checkQrToken: (token) => api.get(`/admin/qr/check/${encodeURIComponent(token)}`),
  getReports: () => api.get('/admin/reports')
};

/* ==================== USER PROFILE APIS ==================== */
export const userApi = {
  getDashboard: () => api.get('/user/dashboard'),
  getProfile: () => api.get('/user/profile'),
  updateProfile: (data) => api.put('/user/profile', data),
  changePassword: (data) => api.post('/user/change-password', data)
};

export default api;
