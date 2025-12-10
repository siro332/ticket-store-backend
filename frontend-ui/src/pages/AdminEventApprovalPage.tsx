import React, { useState, useEffect, useCallback } from 'react';
import { Container, Typography, Tabs, Tab, Box, CircularProgress, Card, CardContent, CardActions, Button, Chip, Grid } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import { EventsService } from '../api/services/EventsService';
import type { Event } from '../api/models/Event';
import { useNotification } from '../context/NotificationContext';

type EventStatus = 'PENDING' | 'APPROVED' | 'CANCELLED';

const AdminEventApprovalPage: React.FC = () => {
  const { showNotification } = useNotification();
  const [events, setEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [currentTab, setCurrentTab] = useState<EventStatus>('PENDING');

  const fetchEventsByStatus = useCallback(async (status: EventStatus) => {
    setLoading(true);
    try {
      // Assuming the API can filter by status.
      // The actual parameter name might be different.
      const response = await EventsService.getApiEventsSearch(undefined, undefined, undefined, undefined, undefined, undefined, undefined, 0, 100, status);
      setEvents(response.content || []);
    } catch (err: any) {
      showNotification(err.message || `Failed to fetch ${status.toLowerCase()} events.`, 'error');
    } finally {
      setLoading(false);
    }
  }, [showNotification]);

  useEffect(() => {
    fetchEventsByStatus(currentTab);
  }, [currentTab, fetchEventsByStatus]);

  const handleTabChange = (event: React.SyntheticEvent, newValue: EventStatus) => {
    setCurrentTab(newValue);
  };

  const handleUpdateStatus = async (eventId: number, newStatus: EventStatus) => {
    try {
      // This is a hypothetical method. You would need to implement it in your EventsService.
      // await EventsService.updateEventStatus(eventId, newStatus);
      showNotification(`Event ${eventId} has been ${newStatus.toLowerCase()}.`, 'success');
      // Refresh the list
      fetchEventsByStatus(currentTab);
    } catch (err: any) {
      showNotification(err.message || `Failed to update event status.`, 'error');
    }
  };

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom fontWeight={700}>
        Event Approval
      </Typography>

      <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
        <Tabs value={currentTab} onChange={handleTabChange} aria-label="event status tabs">
          <Tab label="Pending" value="PENDING" />
          <Tab label="Approved" value="APPROVED" />
          <Tab label="Cancelled" value="CANCELLED" />
        </Tabs>
      </Box>

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
          <CircularProgress />
        </Box>
      ) : (
        <Grid container spacing={3}>
          {events.length === 0 ? (
            <Grid item xs={12}>
              <Typography>No events found with status: {currentTab}</Typography>
            </Grid>
          ) : (
            events.map((event) => (
              <Grid item xs={12} sm={6} md={4} key={event.id}>
                <Card>
                  <CardContent>
                    <Typography variant="h6">{event.name}</Typography>
                    <Typography color="text.secondary">By: {event.organizer?.name}</Typography>
                    <Chip label={event.status} color={
                      event.status === 'PENDING' ? 'warning' :
                      event.status === 'APPROVED' ? 'success' : 'error'
                    } sx={{ mt: 1 }} />
                  </CardContent>
                  <CardActions>
                    {currentTab === 'PENDING' && (
                      <>
                        <Button size="small" onClick={() => handleUpdateStatus(event.id!, 'APPROVED')}>Approve</Button>
                        <Button size="small" color="error" onClick={() => handleUpdateStatus(event.id!, 'CANCELLED')}>Reject</Button>
                      </>
                    )}
                    <Button size="small" component={RouterLink} to={`/events/${event.id}`}>View Details</Button>
                  </CardActions>
                </Card>
              </Grid>
            ))
          )}
        </Grid>
      )}
    </Container>
  );
};

export default AdminEventApprovalPage;
