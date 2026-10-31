package com.voltnav.service.impl;

import com.voltnav.dto.BookingRequest;
import com.voltnav.dto.BookingResponse;
import com.voltnav.entity.*;
import com.voltnav.exception.BadRequestException;
import com.voltnav.exception.ResourceNotFoundException;
import com.voltnav.exception.UnauthorizedException;
import com.voltnav.repository.BookingRepository;
import com.voltnav.repository.ChargingSlotRepository;
import com.voltnav.repository.ChargingStationRepository;
import com.voltnav.repository.UserRepository;
import com.voltnav.service.BookingService;
import com.voltnav.util.AppConstants;
import com.voltnav.util.QRCodeGenerator;
import com.voltnav.websocket.SlotUpdateMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ChargingSlotRepository slotRepository;
    private final ChargingStationRepository stationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              ChargingSlotRepository slotRepository,
                              ChargingStationRepository stationRepository,
                              UserRepository userRepository,
                              SimpMessagingTemplate messagingTemplate) {
        this.bookingRepository = bookingRepository;
        this.slotRepository = slotRepository;
        this.stationRepository = stationRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    @Transactional
    public synchronized BookingResponse createBooking(BookingRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new BadRequestException("Your account is blocked. You cannot make reservations.");
        }

        ChargingSlot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found with id: " + request.getSlotId()));

        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new BadRequestException("Slot " + slot.getSlotNumber() + " is already booked or unavailable.");
        }

        ChargingStation station = slot.getStation();

        // Mark slot as booked
        slot.setStatus(SlotStatus.BOOKED);
        slotRepository.save(slot);

        // Update station available chargers count
        long availableSlots = slotRepository.countByStationIdAndStatus(station.getId(), SlotStatus.AVAILABLE);
        station.setAvailableChargers(Math.max(0, (int) availableSlots));
        stationRepository.save(station);

        // Generate unique booking ID
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uniquePart = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        String bookingId = "VN-" + datePart + "-" + uniquePart;

        // Generate secure QR Token
        String qrToken = "VNQR-" + UUID.randomUUID().toString().replace("-", "").toUpperCase();

        // QR Code Payload
        String qrPayload = String.format("{\"app\":\"VoltNav\",\"bookingId\":\"%s\",\"token\":\"%s\",\"station\":\"%s\",\"date\":\"%s\",\"time\":\"%s - %s\"}",
                bookingId, qrToken, station.getName(), slot.getSlotDate(), slot.getStartTime(), slot.getEndTime());

        // Generate QR Code image (ZXing 300x300)
        String qrCodeBase64 = QRCodeGenerator.generateQRCodeBase64(qrPayload, 300, 300);

        Booking booking = new Booking();
        booking.setBookingId(bookingId);
        booking.setUser(user);
        booking.setStation(station);
        booking.setSlot(slot);
        booking.setBookingDate(slot.getSlotDate());
        booking.setStartTime(slot.getStartTime());
        booking.setEndTime(slot.getEndTime());
        booking.setTotalAmount(slot.getPrice());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setQrToken(qrToken);
        booking.setQrCodeImage(qrCodeBase64);
        booking.setVehicleNumber(request.getVehicleNumber());

        Booking savedBooking = bookingRepository.save(booking);

        // Broadcast real-time slot update via WebSocket
        try {
            SlotUpdateMessage msg = new SlotUpdateMessage(
                    station.getId(),
                    slot.getId(),
                    slot.getSlotNumber(),
                    SlotStatus.BOOKED.name(),
                    (int) availableSlots,
                    station.getTotalChargers()
            );
            messagingTemplate.convertAndSend(AppConstants.WS_TOPIC_SLOTS, msg);
        } catch (Exception ignored) {
        }

        return mapToResponse(savedBooking);
    }

    @Override
    public List<BookingResponse> getUserBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        return bookingRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponse> getAllBookings(BookingStatus status, Long cityId, Long stationId, LocalDate bookingDate, String query) {
        return bookingRepository.searchBookings(status, cityId, stationId, bookingDate, query)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        return mapToResponse(booking);
    }

    @Override
    public BookingResponse getBookingByBookingId(String bookingId) {
        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        return mapToResponse(booking);
    }

    @Override
    @Transactional
    public synchronized BookingResponse cancelBooking(Long id, String currentUserEmail, boolean isAdmin) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        // If not admin, user can only cancel their own booking
        if (!isAdmin && !booking.getUser().getEmail().equalsIgnoreCase(currentUserEmail)) {
            throw new BadRequestException("You can only cancel your own bookings.");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled.");
        }

        if (booking.getStatus() == BookingStatus.CHECKED_IN || booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel an already checked-in or completed booking.");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking updatedBooking = bookingRepository.save(booking);

        // Free up the slot
        ChargingSlot slot = booking.getSlot();
        if (slot != null) {
            slot.setStatus(SlotStatus.AVAILABLE);
            slotRepository.save(slot);

            ChargingStation station = booking.getStation();
            long availableSlots = slotRepository.countByStationIdAndStatus(station.getId(), SlotStatus.AVAILABLE);
            station.setAvailableChargers((int) availableSlots);
            stationRepository.save(station);

            // Broadcast slot released over WebSocket
            try {
                SlotUpdateMessage msg = new SlotUpdateMessage(
                        station.getId(),
                        slot.getId(),
                        slot.getSlotNumber(),
                        SlotStatus.AVAILABLE.name(),
                        (int) availableSlots,
                        station.getTotalChargers()
                );
                messagingTemplate.convertAndSend(AppConstants.WS_TOPIC_SLOTS, msg);
            } catch (Exception ignored) {
            }
        }

        return mapToResponse(updatedBooking);
    }

    private BookingResponse mapToResponse(Booking booking) {
        BookingResponse res = new BookingResponse();
        res.setId(booking.getId());
        res.setBookingId(booking.getBookingId());
        res.setUserId(booking.getUser().getId());
        res.setUserName(booking.getUser().getFullName());
        res.setUserEmail(booking.getUser().getEmail());

        if (booking.getStation() != null) {
            res.setStationId(booking.getStation().getId());
            res.setStationName(booking.getStation().getName());
            res.setStationAddress(booking.getStation().getCompleteAddress());
            res.setStationLatitude(booking.getStation().getLatitude());
            res.setStationLongitude(booking.getStation().getLongitude());
            if (booking.getStation().getLocation() != null) {
                res.setLocationName(booking.getStation().getLocation().getName());
                if (booking.getStation().getLocation().getCity() != null) {
                    res.setCityName(booking.getStation().getLocation().getCity().getName());
                }
            }
        }

        if (booking.getSlot() != null) {
            res.setSlotId(booking.getSlot().getId());
            res.setSlotNumber(booking.getSlot().getSlotNumber());
        }

        res.setBookingDate(booking.getBookingDate());
        res.setStartTime(booking.getStartTime());
        res.setEndTime(booking.getEndTime());
        res.setTotalAmount(booking.getTotalAmount());
        res.setStatus(booking.getStatus());
        res.setQrToken(booking.getQrToken());
        res.setQrCodeImage(booking.getQrCodeImage());
        res.setVehicleNumber(booking.getVehicleNumber());
        res.setCreatedAt(booking.getCreatedAt());

        return res;
    }
}
