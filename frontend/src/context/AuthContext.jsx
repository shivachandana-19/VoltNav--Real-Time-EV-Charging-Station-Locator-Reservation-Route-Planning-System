import React, { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('voltnav_token') || null);
  const [role, setRole] = useState(localStorage.getItem('voltnav_role') || null);
  const [loading, setLoading] = useState(true);
  const [selectedCity, setSelectedCity] = useState(
    JSON.parse(localStorage.getItem('voltnav_selected_city') || 'null')
  );

  useEffect(() => {
    const storedToken = localStorage.getItem('voltnav_token');
    const storedRole = localStorage.getItem('voltnav_role');
    const storedName = localStorage.getItem('voltnav_name');
    const storedEmail = localStorage.getItem('voltnav_email');
    const storedId = localStorage.getItem('voltnav_id');

    if (storedToken && storedRole) {
      setUser({
        id: storedId,
        name: storedName,
        email: storedEmail,
        role: storedRole
      });
      setToken(storedToken);
      setRole(storedRole);
    }
    setLoading(false);
  }, []);

  const login = (authData) => {
    localStorage.setItem('voltnav_token', authData.token);
    localStorage.setItem('voltnav_role', authData.role);
    localStorage.setItem('voltnav_name', authData.name);
    localStorage.setItem('voltnav_email', authData.email);
    localStorage.setItem('voltnav_id', authData.id);

    setUser({
      id: authData.id,
      name: authData.name,
      email: authData.email,
      role: authData.role
    });
    setToken(authData.token);
    setRole(authData.role);
  };

  const logout = () => {
    localStorage.removeItem('voltnav_token');
    localStorage.removeItem('voltnav_role');
    localStorage.removeItem('voltnav_name');
    localStorage.removeItem('voltnav_email');
    localStorage.removeItem('voltnav_id');

    setUser(null);
    setToken(null);
    setRole(null);
  };

  const updateSelectedCity = (city) => {
    setSelectedCity(city);
    localStorage.setItem('voltnav_selected_city', JSON.stringify(city));
  };

  const isAuthenticated = !!token;
  const isAdmin = role === 'ROLE_ADMIN';
  const isUser = role === 'ROLE_USER';

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        role,
        isAuthenticated,
        isAdmin,
        isUser,
        loading,
        selectedCity,
        setSelectedCity: updateSelectedCity,
        login,
        logout
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
