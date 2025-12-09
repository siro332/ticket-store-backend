import React, { useState, useEffect, useCallback } from 'react';
import { Container, Grid, Card, CardContent, Button, TextField, MenuItem, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Chip, Box, CircularProgress, Typography, Alert } from '@mui/material';
import { EventsService } from '../api/services/EventsService';
import { TicketsService } from '../api/services/TicketsService';
import type { Event } from '../api/models/Event';
import type { CheckInLogDto } from '../api/models/CheckInLogDto';
import type { TicketResponse } from '../api/models/TicketResponse';
import { useAuth } from '../context/AuthContext';
import { useNotification } from '../context/NotificationContext';
import QrCodeScannerIcon from '@mui/icons-material/QrCodeScanner';
import HistoryIcon from '@mui/icons-material/History';
import RefreshIcon from '@mui/icons-material/Refresh';

const CheckInPage: React.FC = () => {
  const { user } = useAuth();
  const { showNotification } = useNotification();
  const [loading, setLoading] = useState<boolean>(true);

  const [availableEvents, setAvailableEvents] = useState<Event[]>([]);
  const [selectedEventId, setSelectedEventId] = useState<string>('');
  const [currentEvent, setCurrentEvent] = useState<Event | null>(null);

  const [ticketCodeInput, setTicketCodeInput] = useState<string>('');

  const [checkInLogs, setCheckInLogs] = useState<CheckInLogDto[]>([]);
  const [attendeeTickets, setAttendeeTickets] = useState<TicketResponse[]>([]);

  useEffect(() => {
    if (!user?.id) {
      showNotification('User not authenticated.', 'error');
      setLoading(false);
      return;
    }
    fetchManagedEvents();
  }, [user, showNotification]);

  useEffect(() => {
    if (selectedEventId) {
      const event = availableEvents.find(e => e.id === parseInt(selectedEventId));
      setCurrentEvent(event || null);
      if (event) {
        fetchEventData(event.id!);
      }
    } else {
      setCurrentEvent(null);
      setCheckInLogs([]);
      setAttendeeTickets([]);
    }
  }, [selectedEventId, availableEvents]);

  const fetchManagedEvents = async () => {
    setLoading(true);
    try {
      const response = await EventsService.getApiEventsSearch();
      const allEvents = response.content || [];
      setAvailableEvents(allEvents.filter(event => event.status === 'PUBLISHED'));
    } catch (err: any) {
      const errorMessage = err.body?.message || err.response?.data?.message || err.message || 'Failed to fetch events.';
      showNotification(errorMessage, 'error');
      console.error("Failed to fetch events:", err);
    } finally {
      setLoading(false);
    }
  };

  const fetchEventData = useCallback(async (eventId: number) => {
    setLoading(true);
    try {
      const logs = await TicketsService.getApiTicketsEventCheckInLogs(eventId);
      setCheckInLogs(logs);

      const tickets = await TicketsService.getApiTicketsEvent(eventId);
      setAttendeeTickets(tickets);
    } catch (err: any) {
      const errorMessage = err.body?.message || err.response?.data?.message || err.message || 'Failed to fetch event data.';
      showNotification(errorMessage, 'error');
      console.error("Failed to fetch event data:", err);
    } finally {
      setLoading(false);
    }
  }, [showNotification]);

  const handleTicketScan = async (ticketCode: string, isRescan: boolean = false) => {
    if (!selectedEventId || !ticketCode) {
      showNotification('Please select an event and enter a ticket code.', 'warning');
      return;
    }

    setLoading(true);
    try {
      const scannedTicket = await TicketsService.postApiTicketsScan(ticketCode, `Gate ${selectedEventId}-A`, `Device-${user?.id}`);
      showNotification(`Ticket ${scannedTicket.ticketCode} for ${scannedTicket.attendeeName} checked in successfully!`, 'success');
      if (!isRescan) { // Only clear input if it was a manual entry scan
        setTicketCodeInput('');
      }
      fetchEventData(parseInt(selectedEventId));
    } catch (err: any) {
      const errorMessage = err.body?.message || err.response?.data?.message || err.message || 'Check-in failed.';
      showNotification(errorMessage, 'error');
      console.error("Check-in failed:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleManualCheckIn = async (ticketCode: string) => {
    if (!selectedEventId) {
      showNotification('Please select an event first.', 'warning');
      return;
    }

    if (!window.confirm(`Manually check in ticket ${ticketCode}?`)) {
      return;
    }

    setLoading(true);
    try {
      const scannedTicket = await TicketsService.postApiTicketsScan(ticketCode, `Manual Gate ${selectedEventId}`, `Device-${user?.id}`);
      showNotification(`Ticket ${scannedTicket.ticketCode} for ${scannedTicket.attendeeName} manually checked in successfully!`, 'success');
      fetchEventData(parseInt(selectedEventId));
    } catch (err: any) {
      const errorMessage = err.body?.message || err.response?.data?.message || err.message || 'Manual check-in failed.';
      showNotification(errorMessage, 'error');
      console.error("Manual check-in failed:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleExportAttendees = () => {
    if (!currentEvent) {
      showNotification('Please select an event first.', 'warning');
      return;
    }
    const csvContent = "data:text/csv;charset=utf-8,"
      + "Ticket Code,Attendee Name,Attendee Email,Status,Check-in Time\n"
      + attendeeTickets.map(t => 
          `${t.ticketCode},${t.attendeeName},${t.attendeeEmail},${t.status},${checkInLogs.find(log => log.ticket?.ticketCode === t.ticketCode)?.checkInTime || ''}`
        ).join("\n");
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement("a");
    link.setAttribute("href", encodedUri);
    link.setAttribute("download", `attendees_${currentEvent.name?.replace(/\s/g, '_')}_${selectedEventId}.csv`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    showNotification('Attendee list exported!', 'success');
  };

  if (loading && !currentEvent) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom fontWeight={700}>Check-in Operations</Typography>
      
      <Card sx={{ mb: 4 }}>
        <CardContent>
          <TextField
            select
            label="Select Event"
            fullWidth
            value={selectedEventId}
            onChange={(e) => setSelectedEventId(e.target.value)}
          >
            <MenuItem value="">-- Select an Event --</MenuItem>
            {availableEvents.map(event => (
              <MenuItem key={event.id} value={event.id}>{event.name}</MenuItem>
            ))}
          </TextField>
        </CardContent>
      </Card>

      {currentEvent && (
        <Grid container spacing={4}>
          <Grid item xs={12} md={6}>
            <Card sx={{ mb: 4 }}>
              <CardContent>
                <Typography variant="h6" gutterBottom>Check-in for {currentEvent.name}</Typography>
                <Box component="form" onSubmit={(e) => { e.preventDefault(); handleTicketScan(ticketCodeInput); }} sx={{ mb: 2 }}>
                  <TextField
                    fullWidth
                    label="Enter Ticket Code (or scan QR)"
                    value={ticketCodeInput}
                    onChange={(e) => setTicketCodeInput(e.target.value)}
                    required
                    sx={{ mb: 2 }}
                  />
                  <Button
                    type="submit"
                    variant="contained"
                    fullWidth
                    disabled={loading}
                    startIcon={<QrCodeScannerIcon />}
                    size="large"
                  >
                    {loading ? <CircularProgress size={24} color="inherit" /> : 'Scan / Check-in'}
                  </Button>
                </Box>
                <Button variant="outlined" fullWidth onClick={handleExportAttendees} startIcon={<HistoryIcon />}>
                  Export Attendee List (CSV)
                </Button>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} md={6}>
            <Card sx={{ mb: 4 }}>
              <CardContent>
                <Typography variant="h6" gutterBottom>Check-in History</Typography>
                {checkInLogs.length === 0 ? (
                  <Alert severity="info">No check-in logs available for this event yet.</Alert>
                ) : (
                  <TableContainer component={Paper}>
                    <Table size="small">
                      <TableHead>
                        <TableRow>
                          <TableCell>Ticket Code</TableCell>
                          <TableCell>Time</TableCell>
                          <TableCell>Action</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {checkInLogs.map(log => (
                          <TableRow key={log.id}>
                            <TableCell>{log.ticket?.ticketCode}</TableCell>
                            <TableCell>{new Date(log.checkInTime).toLocaleTimeString()}</TableCell>
                            <TableCell>
                              <Button
                                variant="outlined"
                                size="small"
                                onClick={() => handleTicketScan(log.ticket?.ticketCode || '', true)}
                                disabled={loading}
                                startIcon={<RefreshIcon />}
                              >
                                Scan Again
                              </Button>
                            </TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </TableContainer>
                )}
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}

      {currentEvent && (
        <>
          <Typography variant="h5" gutterBottom fontWeight={700} sx={{ mt: 4 }}>All Event Tickets</Typography>
          {attendeeTickets.length === 0 ? (
            <Alert severity="info">No tickets sold for this event yet.</Alert>
          ) : (
            <TableContainer component={Paper}>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Ticket Code</TableCell>
                    <TableCell>Attendee Name</TableCell>
                    <TableCell>Email</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell>Check-in Time</TableCell>
                    <TableCell>Action</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {attendeeTickets.map(ticket => {
                    const log = checkInLogs.find(l => l.ticket?.ticketCode === ticket.ticketCode);
                    const isCheckedIn = ticket.status === 'SCANNED';
                    return (
                      <TableRow key={ticket.id}>
                        <TableCell>{ticket.ticketCode}</TableCell>
                        <TableCell>{ticket.attendeeName}</TableCell>
                        <TableCell>{ticket.attendeeEmail}</TableCell>
                        <TableCell>
                          <Chip 
                            label={isCheckedIn ? 'Checked In' : 'Pending'}
                            color={isCheckedIn ? 'success' : 'default'}
                            size="small" 
                          />
                        </TableCell>
                        <TableCell>{log?.checkInTime ? new Date(log.checkInTime).toLocaleString() : '-'}</TableCell>
                        <TableCell>
                          {!isCheckedIn && (
                            <Button
                              variant="outlined"
                              size="small"
                              onClick={() => handleManualCheckIn(ticket.ticketCode!)}
                              disabled={loading}
                            >
                              Manual
                            </Button>
                          )}
                        </TableCell>
                      </TableRow>
                    );
                  })}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </>
      )}
    </Container>
  );
};

export default CheckInPage;