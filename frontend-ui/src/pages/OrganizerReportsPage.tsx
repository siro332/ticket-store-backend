import React, { useState, useEffect } from 'react';
import { Container, Grid, Card, CardContent, Typography, Box, CircularProgress, TextField, MenuItem } from '@mui/material';
import { EventsService } from '../api/services/EventsService';
import { ReportingService } from '../api/services/ReportingService';
import type { Event } from '../api/models/Event';
import { useAuth } from '../context/AuthContext';
import { useNotification } from '../context/NotificationContext';
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';
import ConfirmationNumberIcon from '@mui/icons-material/ConfirmationNumber';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

const OrganizerReportsPage: React.FC = () => {
  const { user } = useAuth();
  const { showNotification } = useNotification();
  const [loading, setLoading] = useState<boolean>(true);

  const [events, setEvents] = useState<Event[]>([]);
  const [selectedEventId, setSelectedEventId] = useState<string>('');

  const [totalRevenue, setTotalRevenue] = useState<number | null>(null);
  const [eventRevenue, setEventRevenue] = useState<number | null>(null);
  const [totalTicketsSold, setTotalTicketsSold] = useState<number | null>(null);
  const [eventTicketsSold, setEventTicketsSold] = useState<number | null>(null);
  const [chartData, setChartData] = useState<{ name: string; revenue: number }[]>([]);

  useEffect(() => {
    if (!user?.id) {
      showNotification('User not authenticated.', 'error');
      setLoading(false);
      return;
    }
    fetchOrganizerEvents();
  }, [user, showNotification]);

  useEffect(() => {
    fetchReports();
  }, [events, selectedEventId, showNotification]);

  const fetchOrganizerEvents = async () => {
    setLoading(true);
    try {
      const response = await EventsService.getApiEventsSearch();
      const allEvents = response.content || [];
      const organizerManagedEvents = allEvents.filter(event => event.organizerId === user?.id);
      setEvents(organizerManagedEvents);
    } catch (err: any) {
      const errorMessage = err.body?.message || err.response?.data?.message || err.message || 'Failed to fetch your events.';
      showNotification(errorMessage, 'error');
      console.error("Failed to fetch organizer events:", err);
    } finally {
      setLoading(false);
    }
  };

  const fetchReports = async () => {
    if (events.length === 0) {
      if (!loading) {
        showNotification('No events available to generate reports.', 'info');
      }
      setTotalRevenue(null);
      setEventRevenue(null);
      setTotalTicketsSold(null);
      setEventTicketsSold(null);
      setChartData([]);
      return;
    }

    try {
      let calculatedTotalRevenue = 0;
      let calculatedTotalTicketsSold = 0;
      const newChartData: { name: string; revenue: number }[] = [];

      const eventsToReport = events.filter(event => event.organizerId === user?.id);

      for (const event of eventsToReport) {
        const revenue = await ReportingService.getApiReportsRevenueEvent(event.id!);
        calculatedTotalRevenue += revenue;
        const ticketsSold = await ReportingService.getApiReportsTicketsEventSold(event.id!);
        calculatedTotalTicketsSold += ticketsSold;
        newChartData.push({ name: event.name!, revenue });
      }
      setTotalRevenue(calculatedTotalRevenue);
      setTotalTicketsSold(calculatedTotalTicketsSold);
      setChartData(newChartData);

      if (selectedEventId) {
        const parsedEventId = parseInt(selectedEventId);
        const revenue = await ReportingService.getApiReportsRevenueEvent(parsedEventId);
        setEventRevenue(revenue);
        const ticketsSold = await ReportingService.getApiReportsTicketsEventSold(parsedEventId);
        setEventTicketsSold(ticketsSold);
      } else {
        setEventRevenue(null);
        setEventTicketsSold(null);
      }
    } catch (err: any) {
      const errorMessage = err.body?.message || err.response?.data?.message || err.message || 'Failed to fetch reports.';
      showNotification(errorMessage, 'error');
      console.error("Failed to fetch reports:", err);
    } finally {
      // Optional: Set loading to false if this was the only loading indicator
    }
  };

  const handleEventSelectChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSelectedEventId(e.target.value);
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom fontWeight={700}>Reports & Analytics</Typography>
      
      <Card sx={{ mb: 4, p: 2 }}>
        <TextField
          select
          label="View Reports For"
          fullWidth
          value={selectedEventId}
          onChange={handleEventSelectChange}
        >
          <MenuItem value="">All My Events</MenuItem>
          {events.map(event => (
            <MenuItem key={event.id} value={event.id}>{event.name}</MenuItem>
          ))}
        </TextField>
      </Card>

      <Grid container spacing={4}>
        <Grid item xs={12} md={6}>
          <Card sx={{ height: '100%', bgcolor: 'primary.light', color: 'primary.contrastText' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <AttachMoneyIcon sx={{ fontSize: 40, mr: 1 }} />
                <Typography variant="h6">Total Revenue (All Events)</Typography>
              </Box>
              <Typography variant="h3" fontWeight="bold">
                ${totalRevenue !== null ? totalRevenue.toFixed(2) : '0.00'}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={6}>
          <Card sx={{ height: '100%', bgcolor: 'secondary.light', color: 'secondary.contrastText' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <ConfirmationNumberIcon sx={{ fontSize: 40, mr: 1 }} />
                <Typography variant="h6">Total Tickets Sold (All Events)</Typography>
              </Box>
              <Typography variant="h3" fontWeight="bold">
                {totalTicketsSold !== null ? totalTicketsSold : '0'}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        {selectedEventId && (
          <>
            <Grid item xs={12} md={6}>
              <Card sx={{ height: '100%' }}>
                <CardContent>
                  <Typography variant="subtitle1" color="text.secondary" gutterBottom>
                    Revenue for {events.find(e => e.id === parseInt(selectedEventId))?.name}
                  </Typography>
                  <Typography variant="h4" color="primary.main" fontWeight="bold">
                    ${eventRevenue !== null ? eventRevenue.toFixed(2) : '0.00'}
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} md={6}>
              <Card sx={{ height: '100%' }}>
                <CardContent>
                  <Typography variant="subtitle1" color="text.secondary" gutterBottom>
                    Tickets Sold for {events.find(e => e.id === parseInt(selectedEventId))?.name}
                  </Typography>
                  <Typography variant="h4" color="secondary.main" fontWeight="bold">
                    {eventTicketsSold !== null ? eventTicketsSold : '0'}
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          </>
        )}
      </Grid>

      <Card sx={{ mt: 4 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>Revenue by Event</Typography>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="revenue" fill="#8884d8" />
            </BarChart>
          </ResponsiveContainer>
        </CardContent>
      </Card>
    </Container>
  );
};

export default OrganizerReportsPage;
