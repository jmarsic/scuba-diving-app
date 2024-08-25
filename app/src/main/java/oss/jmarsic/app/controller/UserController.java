package oss.jmarsic.app.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import oss.jmarsic.app.model.User;
import oss.jmarsic.app.service.UserService;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public String userPage(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        return "user";
    }

    @GetMapping("/edit")
    public String editProfile(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        return "edit-profile";
    }

    @PostMapping("/edit")
    public String updateProfile(@Valid @RequestParam String fullName, @RequestParam String email, @RequestParam String newPassword, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        user.setFullName(fullName);
        user.setEmail(email);

        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(newPassword);
        }

        userService.save(user);
        return "redirect:/user";
    }

    @GetMapping("change-password")
    public String changePasswordPage() {
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        user.setPassword(newPassword);
        user.setPasswordChanged(true);
        userService.save(user);
        return "redirect:/user";
    }
}
