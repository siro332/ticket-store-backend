import React, { useEffect, useState } from 'react';
import { Container, Grid, Card, CardContent, CardMedia, Button, Typography, Box, CircularProgress, CardActions, Alert } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import { EventsService } from '../api/services/EventsService';
import type { Event } from '../api/models/Event';
import { useAuth } from '../context/AuthContext';
import { useNotification } from '../context/NotificationContext';
import AddIcon from '@mui/icons-material/Add';
import AssessmentIcon from '@mui/icons-material/Assessment';
import ListAltIcon from '@mui/icons-material/ListAlt';
import { motion } from 'framer-motion';

const OrganizerDashboardPage: React.FC = () => {
  const { user } = useAuth();
  const { showNotification } = useNotification();
  const [events, setEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    fetchOrganizerEvents();
  }, [user, showNotification]);

  const fetchOrganizerEvents = async () => {
    if (!user?.id) {
      showNotification('User not authenticated.', 'error');
      setLoading(false);
      return;
    }

    setLoading(true);
    try {
      const response = await EventsService.getApiEventsSearch();
      const allEvents = response.content || [];
      const organizerEvents = allEvents.filter(event => event.organizerId === user.id);
      setEvents(organizerEvents);
    } catch (err: any) {
      const errorMessage = err.body?.message || err.response?.data?.message || err.message || 'Failed to fetch your events.';
      showNotification(errorMessage, 'error');
      console.error("Failed to fetch organizer events:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteEvent = async (eventId: number) => {
    if (!window.confirm('Are you sure you want to delete this event? This action cannot be undone.')) {
      return;
    }
    try {
      await EventsService.deleteApiEvents(eventId);
      showNotification('Event deleted successfully!', 'success');
      fetchOrganizerEvents();
    } catch (err: any) {
      const errorMessage = err.body?.message || err.response?.data?.message || err.message || 'Failed to delete event.';
      showNotification(errorMessage, 'error');
      console.error("Failed to delete event:", err);
    }
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
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Typography variant="h4" fontWeight={700}>Organizer Dashboard</Typography>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button component={RouterLink} to="/organizer/events/new" variant="contained" startIcon={<AddIcon />}>
            New Event
          </Button>
          <Button component={RouterLink} to="/organizer/orders" variant="outlined" startIcon={<ListAltIcon />}>
            Orders
          </Button>
          <Button component={RouterLink} to="/organizer/reports" variant="outlined" color="success" startIcon={<AssessmentIcon />}>
            Reports
          </Button>
        </Box>
      </Box>

      {events.length === 0 ? (
        <Container maxWidth="sm" sx={{ textAlign: 'center', mt: 8 }}>
          <Typography variant="h6" color="text.secondary" gutterBottom>
            You haven't created any events yet.
          </Typography>
          <Button component={RouterLink} to="/organizer/events/new" variant="contained" sx={{ mt: 2 }}>
            Create Your First Event
          </Button>
        </Container>
      ) : (
        <Grid container spacing={4}>
          {events.map((event, index) => (
            <Grid item xs={12} sm={6} md={4} key={event.id}>
              <Card 
                component={motion.div}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.1 }}
                sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}
              >
                {event.coverImage && (
                  <CardMedia
                    component="img"
                    height="140"
                    image={event.coverImage}
                    alt={event.name}
                  />
                )}
                <CardContent sx={{ flexGrow: 1 }}>
                  <Typography gutterBottom variant="h6" component="div" noWrap>
                    {event.name}
                  </Typography>
                  <Typography variant="body2" color="text.secondary" gutterBottom>
                    {event.category}
                  </Typography>
                  <Typography variant="caption" display="block" color="text.secondary">
                    {new Date(event.startTime).toLocaleDateString()} - {new Date(event.endTime).toLocaleDateString()}
                  </Typography>
                </CardContent>
                <CardActions sx={{ justifyContent: 'space-between', px: 2, pb: 2 }}>
                  <Box>
                    <Button size="small" component={RouterLink} to={`/organizer/events/edit/${event.id}`}>Edit</Button>
                    <Button size="small" component={RouterLink} to={`/events/${event.id}`}>View</Button>
                  </Box>
                  <Button size="small" color="error" onClick={() => handleDeleteEvent(event.id!)}>Delete</Button>
                </CardActions>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Container>
  );
};

export default OrganizerDashboardPage;
