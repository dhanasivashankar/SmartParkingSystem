package com.smartparking.controller;

import com.smartparking.entity.ParkingSlot;
import com.smartparking.repository.BookingRepository;
import com.smartparking.repository.ParkingSlotRepository;
import com.smartparking.repository.UserRepository;
import com.smartparking.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminController {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final BookingService bookingService;

    public AdminController(UserRepository userRepository,
                           BookingRepository bookingRepository,
                           ParkingSlotRepository parkingSlotRepository,
                           BookingService bookingService) {

        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.parkingSlotRepository = parkingSlotRepository;
        this.bookingService = bookingService;
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/";
        }

        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalBookings", bookingRepository.count());
        model.addAttribute("totalSlots", parkingSlotRepository.count());
        model.addAttribute("availableSlots",
                parkingSlotRepository.findByStatus("AVAILABLE").size());

        return "admin-dashboard";
    }

    @GetMapping("/admin/users")
    public String viewUsers(HttpSession session, Model model) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/";
        }

        model.addAttribute("users", userRepository.findAll());

        return "admin-users";
    }

    @GetMapping("/admin/bookings")
    public String viewBookings(HttpSession session, Model model) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/";
        }

        model.addAttribute("bookings", bookingRepository.findAll());

        return "admin-bookings";
    }

    @GetMapping("/admin/slots")
    public String manageSlots(HttpSession session, Model model) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/";
        }

        model.addAttribute("slots", parkingSlotRepository.findAll());
        model.addAttribute("parkingSlot", new ParkingSlot());

        return "manage-slots";
    }

    @PostMapping("/admin/release/{id}")
    public String releaseVehicle(@PathVariable Integer id,
                                 HttpSession session) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/";
        }

        bookingService.releaseVehicle(id);

        return "redirect:/admin/bookings";
    }
}