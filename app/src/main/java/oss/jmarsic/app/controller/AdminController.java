package oss.jmarsic.app.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import oss.jmarsic.app.model.Role;
import oss.jmarsic.app.model.User;
import oss.jmarsic.app.service.RoleService;
import oss.jmarsic.app.service.UserService;

import java.util.HashSet;
import java.util.Set;

@Controller
public class AdminController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/admin")
    public String adminPage(Model model) {
        model.addAttribute("users", userService.findAll());

        return "admin";
    }

    @PostMapping("/add-user")
    public String addUser(@Valid @RequestParam String fullName, @RequestParam String email, @RequestParam String password, @RequestParam String role) {
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(password);

        Role userRole = roleService.findByName(role).orElseThrow(() -> new IllegalArgumentException("Invalid role!"));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        userService.save(user);

        return "redirect:/admin";
    }
}
