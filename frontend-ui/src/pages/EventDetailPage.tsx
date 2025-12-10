import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Container, Grid, Card, CardContent, CardMedia, Typography, Button, TextField, Select, MenuItem, Chip, Box, List, ListItem, ListItemText, CircularProgress } from '@mui/material';
import { EventsService } from '../api/services/EventsService';
import type { Event } from '../api/models/Event';
import type { TicketType } from '../api/models/TicketType';
import type { Seat } from '../api/models/Seat';
import { useCart } from '../context/CartContext';
import { useNotification } from '../context/NotificationContext';
import { motion } from 'framer-motion';

const EventDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const eventId = id ? parseInt(id) : undefined;
  const navigate = useNavigate();
  const { addToCart } = useCart();
  const { showNotification } = useNotification();

  const [event, setEvent] = useState<Event | null>(null);
  const [ticketTypes, setTicketTypes] = useState<TicketType[]>([]);
  const [seats, setSeats] = useState<Seat[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [selectedQuantities, setSelectedQuantities] = useState<{ [key: number]: number }>({});
  const [selectedSeat, setSelectedSeat] = useState<number | string>('');

  useEffect(() => {
    const fetchEventDetails = async () => {
      if (!eventId) {
        showNotification('Invalid event ID.', 'error');
        setLoading(false);
        return;
      }

      setLoading(true);
      try {
        const fetchedEvent = await EventsService.getApiEvents1(eventId);
        setEvent(fetchedEvent);

        const fetchedTicketTypes = await EventsService.getApiEventsTicketTypes(eventId);
        setTicketTypes(fetchedTicketTypes);

        const initialQuantities: { [key: number]: number } = {};
        fetchedTicketTypes.forEach(type => {
          initialQuantities[type.id] = 1;
        });
        setSelectedQuantities(initialQuantities);

        // Always fetch seats, the seats.length will determine if seating is relevant
        const fetchedSeats = await EventsService.getApiEventsSeats(eventId);
        setSeats(fetchedSeats.filter(seat => seat.isAvailable && !seat.locked));
        
      } catch (err: any) {
        showNotification(err.message || 'Failed to fetch event details.', 'error');
        console.error("Failed to fetch event details:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchEventDetails();
  }, [eventId, showNotification]);

  const handleQuantityChange = (ticketTypeId: number, quantity: number) => {
    setSelectedQuantities(prev => ({
      ...prev,
      [ticketTypeId]: Math.max(1, quantity),
    }));
  };

  const handleAddToCart = (ticketType: TicketType, seatId?: number) => {
    if (!event) return;

    const quantity = selectedQuantities[ticketType.id];
    if (!quantity || quantity <= 0) {
      showNotification('Please select a valid quantity.', 'warning');
      return;
    }

    // Only enforce seat selection if there are actual seats available for the event
    if (seats.length > 0 && !selectedSeat) { // Use selectedSeat state instead of passed seatId for check
      showNotification('Please select a seat.', 'warning');
      return;
    }

    addToCart(
      {
        ticketTypeId: ticketType.id,
        ticketTypeName: ticketType.name,
        price: ticketType.price,
        eventId: event.id!,
        eventName: event.name!,
        seatId: seats.length > 0 ? (selectedSeat as number) : undefined, // Only assign seatId if seats exist
      },
      quantity
    );
    showNotification(`${quantity} x ${ticketType.name} added to cart!`, 'success');
    navigate('/cart');
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (!event) {
    return <Container sx={{ mt: 5, textAlign: 'center' }}><Typography variant="h5">Event not found or an error occurred.</Typography></Container>;
  }

  // Determine if seating options should be displayed based on actual available seats
  const hasSeatingOptions = seats.length > 0;

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Grid container spacing={4}>
        <Grid item xs={12} md={8}>
          <Card component={motion.div} initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.5 }}>
            {event.coverImage && (
              <CardMedia
                component="img"
                height="400"
                image={event.coverImage}
                alt={event.name}
              />
            )}
            <CardContent>
              <Typography variant="h3" component="h1" gutterBottom>
                {event.name}
              </Typography>
              <Typography variant="subtitle1" color="text.secondary" gutterBottom>
                {event.category} | {event.venue?.name}, {event.venue?.city}
              </Typography>
              <Typography variant="body1" paragraph sx={{ mt: 2 }}>
                {event.description}
              </Typography>
              
              <List>
                <ListItem>
                  <ListItemText primary="Starts" secondary={new Date(event.startTime).toLocaleString()} />
                </ListItem>
                <ListItem>
                  <ListItemText primary="Ends" secondary={new Date(event.endTime).toLocaleString()} />
                </ListItem>
                {event.venue && (
                  <ListItem>
                    <ListItemText primary="Venue" secondary={`${event.venue.name}, ${event.venue.address}, ${event.venue.city} (Capacity: ${event.venue.capacity})`} />
                  </ListItem>
                )}
                <ListItem>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <Typography variant="body2">Status:</Typography>
                    <Chip label={event.status} color={event.status === 'PUBLISHED' ? 'success' : 'warning'} size="small" />
                  </Box>
                </ListItem>
                {event.refundEnabled && (
                  <ListItem>
                    <ListItemText 
                      primary="Refund Policy" 
                      secondary={`Refunds enabled up to ${event.refundDeadlineHours} hours before start. ${event.refundFeePercent * 100}% fee applies.`} 
                    />
                  </ListItem>
                )}
              </List>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card sx={{ mb: 3 }} component={motion.div} initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} transition={{ duration: 0.5, delay: 0.2 }}>
            <CardContent>
              <Typography variant="h5" gutterBottom>Ticket Types</Typography>
              <List>
                {ticketTypes.length > 0 ? (
                  ticketTypes.map((type) => (
                    <ListItem key={type.id} divider sx={{ flexDirection: 'column', alignItems: 'flex-start' }}>
                      <Box sx={{ width: '100%', display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                        <Typography variant="subtitle1" fontWeight="bold">{type.name}</Typography>
                        <Typography variant="subtitle1">${type.price.toFixed(2)}</Typography>
                      </Box>
                      <Typography variant="caption" color="text.secondary" display="block" sx={{ mb: 2 }}>
                        Available: {type.quota} | Limit: {type.purchaseLimit}
                      </Typography>
                      <Box sx={{ width: '100%', display: 'flex', gap: 1 }}>
                        <TextField
                          type="number"
                          label="Qty"
                          size="small"
                          inputProps={{ min: 1, max: type.purchaseLimit }}
                          value={selectedQuantities[type.id] || 1}
                          onChange={(e) => handleQuantityChange(type.id, parseInt(e.target.value))}
                          sx={{ width: '80px' }}
                        />
                        <Button
                          variant="contained"
                          fullWidth
                          onClick={() => handleAddToCart(type)}
                          disabled={(selectedQuantities[type.id] || 0) <= 0 || (selectedQuantities[type.id] || 0) > (type.quota || 9999)}
                        >
                          Add to Cart
                        </Button>
                      </Box>
                    </ListItem>
                  ))
                ) : (
                  <ListItem><ListItemText primary="No ticket types available." /></ListItem>
                )}
              </List>
            </CardContent>
          </Card>

          {hasSeatingOptions && (
            <Card component={motion.div} initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} transition={{ duration: 0.5, delay: 0.4 }}>
              <CardContent>
                <Typography variant="h5" gutterBottom>Available Seats</Typography>
                {seats.length > 0 ? (
                  <Box>
                    <Grid container spacing={1}>
                      {seats.map((seat) => (
                        <Grid item xs={2} key={seat.id}>
                          <Button
                            variant={selectedSeat === seat.id ? 'contained' : 'outlined'}
                            fullWidth
                            onClick={() => setSelectedSeat(seat.id!)}
                            disabled={!seat.isAvailable}
                          >
                            {seat.seatNumber}
                          </Button>
                        </Grid>
                      ))}
                    </Grid>
                    <Button
                      variant="contained"
                      fullWidth
                      disabled={!selectedSeat}
                      onClick={() => {
                        if (typeof selectedSeat === 'number') {
                          const seatObj = seats.find(s => s.id === selectedSeat);
                          const ticketTypeForSeat = ticketTypes.find(tt => tt.id === seatObj?.ticketType?.id);
                          if (ticketTypeForSeat) {
                            handleAddToCart(ticketTypeForSeat, selectedSeat);
                          } else {
                            showNotification('Could not determine ticket type for selected seat.', 'error');
                          }
                        }
                      }}
                      sx={{ mt: 2 }}
                    >
                      Add Selected Seat
                    </Button>
                  </Box>
                ) : (
                  <Typography variant="body2" color="text.secondary">No available seats for this event.</Typography>
                )}
              </CardContent>
            </Card>
          )}
        </Grid>
      </Grid>
    </Container>
  );
};

export default EventDetailPage;
