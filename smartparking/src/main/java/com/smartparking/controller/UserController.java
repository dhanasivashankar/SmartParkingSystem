package com.smartparking.controller;
import jakarta.servlet.http.HttpSession;
import com.smartparking.entity.User;
import com.smartparking.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/register";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "index";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {

        boolean registered = userService.registerUser(user);

        if (registered) {
            model.addAttribute("successMessage", "Registration successful.");
        } else {
            model.addAttribute("errorMessage", "Email already exists.");
        }

        model.addAttribute("user", new User());
        return "index";
    }
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        // Admin Login
        if (email.equals("admin@parkease.com") && password.equals("admin123")) {
            session.setAttribute("admin", true);
            return "redirect:/admin/dashboard";
        }

        // User Login
        User user = userService.loginUser(email, password);

        if (user != null) {
            session.setAttribute("loggedInUser", user);
            return "redirect:/dashboard";
        }

        model.addAttribute("error", "Invalid email or password.");
        model.addAttribute("user", new User());

        return "index";
    }
}