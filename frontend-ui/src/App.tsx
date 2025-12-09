import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import theme from './theme';

import { AuthProvider, useAuth } from './context/AuthContext';
import { CartProvider } from './context/CartContext';
import { NotificationProvider } from './context/NotificationContext';
import ProtectedRoute from './components/ProtectedRoute';
import Layout from './components/Layout';

// Placeholder Components
const NotFoundPage: React.FC = () => (
  <div style={{ textAlign: 'center', marginTop: '50px' }}>
    <h1>404</h1>
    <p>Page not found</p>
  </div>
);

// Actual Page Components
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import EventsPage from './pages/EventsPage';
import EventDetailPage from './pages/EventDetailPage';
import CartPage from './pages/CartPage';
import CheckoutPage from './pages/CheckoutPage';
import OrderConfirmationPage from './pages/OrderConfirmationPage';
import OrderDetailsPage from './pages/OrderDetailsPage';
import MyTicketsPage from './pages/MyTicketsPage';
import ProfilePage from './pages/ProfilePage'; // Import ProfilePage
import EventFormPage from './pages/EventFormPage';
import OrganizerOrdersPage from './pages/OrganizerOrdersPage';
import OrganizerReportsPage from './pages/OrganizerReportsPage';
import CheckInPage from './pages/CheckInPage';
import AdminSupportPage from './pages/AdminSupportPage';
import AdminContentManagementPage from './pages/AdminContentManagementPage';
import OrganizerDashboardPage from './pages/OrganizerDashboardPage';
import AdminDashboardPage from './pages/AdminDashboardPage';
import { CircularProgress, Box } from '@mui/material';

const AppContent: React.FC = () => {
  const { loading } = useAuth();

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Layout>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/events" element={<EventsPage />} />
        <Route path="/events/:id" element={<EventDetailPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/cart" element={<CartPage />} />

        {/* Protected Routes */}
        <Route element={<ProtectedRoute />}>
          <Route path="/profile" element={<ProfilePage />} /> {/* New protected route for ProfilePage */}
          <Route path="/my-tickets" element={<MyTicketsPage />} />
          <Route path="/my-orders/:id" element={<OrderDetailsPage />} />
          <Route path="/checkout" element={<CheckoutPage />} />
          <Route path="/order-confirmation/:id" element={<OrderConfirmationPage />} />
        </Route>
        <Route element={<ProtectedRoute allowedRoles={['ROLE_ORGANIZER']} />}>
          <Route path="/organizer/dashboard" element={<OrganizerDashboardPage />} />
          <Route path="/organizer/events/new" element={<EventFormPage />} />
          <Route path="/organizer/events/edit/:id" element={<EventFormPage />} />
          <Route path="/organizer/orders" element={<OrganizerOrdersPage />} />
          <Route path="/organizer/reports" element={<OrganizerReportsPage />} />
        </Route>
        <Route element={<ProtectedRoute allowedRoles={['ROLE_STAFF', 'ROLE_ORGANIZER', 'ROLE_ADMIN']} />}>
          <Route path="/staff/checkin" element={<CheckInPage />} />
        </Route>
        <Route element={<ProtectedRoute allowedRoles={['ROLE_ADMIN']} />}>
          <Route path="/admin/dashboard" element={<AdminDashboardPage />} />
          <Route path="/admin/support" element={<AdminSupportPage />} />
          <Route path="/admin/content" element={<AdminContentManagementPage />} />
        </Route>

        {/* Catch-all route */}
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </Layout>
  );
};

const App: React.FC = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Router>
        <AuthProvider>
          <CartProvider>
            <NotificationProvider>
              <AppContent />
            </NotificationProvider>
          </CartProvider>
        </AuthProvider>
      </Router>
    </ThemeProvider>
  );
};

export default App;
