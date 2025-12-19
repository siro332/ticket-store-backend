import React, { useEffect, useState } from 'react';
import { Container, Typography, CircularProgress, Grid, Card, CardContent, Box, Alert } from '@mui/material';
import { QRCodeCanvas } from 'qrcode.react';
import { useAuth } from '../context/AuthContext';
import { useNotification } from '../context/NotificationContext';
import { TicketsService } from '../api/services/TicketsService';
import type { TicketResponse } from '../api/models/TicketResponse';
import { motion } from 'framer-motion';

const MyTicketsPage: React.FC = () => {
  const { user } = useAuth();
  const { showNotification } = useNotification();
  const [tickets, setTickets] = useState<TicketResponse[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const fetchMyTickets = async () => {
      if (!user?.id) {
        showNotification('User not authenticated.', 'error');
        setLoading(false);
        return;
      }

      setLoading(true);
      try {
        const fetchedTickets = await TicketsService.getApiTicketsUser(user.id);
        setTickets(fetchedTickets);
      } catch (err: any) {
        showNotification(err.message || 'Failed to fetch your tickets.', 'error');
        console.error("Failed to fetch tickets:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchMyTickets();
  }, [user, showNotification]);

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 700, mb: 4 }}>
        My Active Tickets
      </Typography>

      {tickets.length > 0 ? (
        <Grid container spacing={3}>
          {tickets.map((ticket, index) => (
            <Grid item xs={12} sm={6} md={4} key={ticket.id}>
              <Card 
                component={motion.div}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.1 }}
              >
                <CardContent sx={{ textAlign: 'center' }}>
                  <Typography variant="h6" gutterBottom>
                    Ticket #{ticket.id}
                  </Typography>
                  <Box sx={{ my: 2 }}>
                    <QRCodeCanvas value={ticket.ticketCode || 'N/A'} size={128} />
                  </Box>
                  <Typography variant="body2" color="text.secondary">
                    Event ID: {ticket.eventId}
                  </Typography>
                  <Typography variant="body1">
                    {ticket.attendeeName}
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      ) : (
        <Alert severity="info" sx={{ mt: 4 }}>
          You don't have any active tickets.
        </Alert>
      )}
    </Container>
  );
};

export default MyTicketsPage;
