package com.smartparking.controller;

import com.smartparking.service.ParkingSlotService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ParkingSlotController {

    private final ParkingSlotService parkingSlotService;

    public ParkingSlotController(ParkingSlotService parkingSlotService) {
        this.parkingSlotService = parkingSlotService;
    }

    @GetMapping("/slots")
    public String viewSlots(HttpSession session, Model model) {

        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/";
        }

        model.addAttribute("slots", parkingSlotService.getAvailableSlots());

        return "parking-slots";
    }
}