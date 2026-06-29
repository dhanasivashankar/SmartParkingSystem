package com.smartparking.controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.smartparking.entity.User;
import com.smartparking.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.smartparking.entity.Booking;
@Controller
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    @PostMapping("/book/{slotId}")
    public String bookSlot(@PathVariable Integer slotId,
                           HttpSession session,
                           Model model) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/";
        }

        Booking booking = bookingService.bookSlot(slotId, user);

        if (booking == null) {
            return "redirect:/slots";
        }

        model.addAttribute("booking", booking);

        return "booking-success";
    }
    @GetMapping("/bookings")
    public String myBookings(HttpSession session,
                             Model model) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/";
        }

        model.addAttribute("bookings",
                bookingService.getBookings(user));

        return "booking-history";
    }
    @PostMapping("/cancel/{bookingId}")
    public String cancelBooking(@PathVariable Integer bookingId) {

        bookingService.cancelBooking(bookingId);

        return "redirect:/bookings";
    }
}