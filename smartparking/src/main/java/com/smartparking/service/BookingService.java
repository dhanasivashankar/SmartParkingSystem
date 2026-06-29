package com.smartparking.service;

import com.smartparking.entity.Booking;
import com.smartparking.entity.ParkingSlot;
import com.smartparking.entity.User;
import com.smartparking.repository.BookingRepository;
import com.smartparking.repository.ParkingSlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ParkingSlotRepository parkingSlotRepository;

    public BookingService(BookingRepository bookingRepository,
                          ParkingSlotRepository parkingSlotRepository) {
        this.bookingRepository = bookingRepository;
        this.parkingSlotRepository = parkingSlotRepository;
    }

    // Book Parking Slot
    public Booking bookSlot(Integer slotId, User user) {

        ParkingSlot slot = parkingSlotRepository.findById(slotId).orElse(null);

        if (slot == null || !"AVAILABLE".equalsIgnoreCase(slot.getStatus())) {
            return null;
        }

        Booking booking = new Booking();

        booking.setUser(user);
        booking.setParkingSlot(slot);
        booking.setStartTime(LocalDateTime.now());
        booking.setBookingStatus("ACTIVE");
        booking.setTotalAmount(0.0);

        booking = bookingRepository.save(booking);

        slot.setStatus("OCCUPIED");
        parkingSlotRepository.save(slot);

        return booking;
    }

    // User Booking History
    public List<Booking> getBookings(User user) {
        return bookingRepository.findByUser(user);
    }

    // Cancel Booking (Optional)
    public void cancelBooking(Integer bookingId) {

        Booking booking = bookingRepository.findByBookingId(bookingId).orElse(null);

        if (booking == null) {
            return;
        }

        booking.setBookingStatus("CANCELLED");
        bookingRepository.save(booking);

        ParkingSlot slot = booking.getParkingSlot();
        slot.setStatus("AVAILABLE");
        parkingSlotRepository.save(slot);
    }

    // Admin Releases Vehicle
    @Transactional
    public void releaseVehicle(Integer bookingId) {

        Booking booking = bookingRepository.findByBookingId(bookingId).orElse(null);

        if (booking == null) {
            return;
        }

        LocalDateTime exitTime = LocalDateTime.now();
        booking.setEndTime(exitTime);

        Duration duration = Duration.between(booking.getStartTime(), exitTime);

        long hours = duration.toHours();

        if (duration.toMinutes() % 60 != 0) {
            hours++;
        }

        if (hours < 1) {
            hours = 1;
        }

        double amount = hours * booking.getParkingSlot().getHourlyRate();

        booking.setTotalAmount(amount);
        booking.setBookingStatus("COMPLETED");

        bookingRepository.save(booking);

        // Fetch a fresh ParkingSlot from the database
        Integer slotId = booking.getParkingSlot().getSlotId();

        ParkingSlot slot = parkingSlotRepository.findById(slotId).orElse(null);

        if (slot != null) {
            slot.setStatus("AVAILABLE");
            parkingSlotRepository.saveAndFlush(slot);
        }
    }
}