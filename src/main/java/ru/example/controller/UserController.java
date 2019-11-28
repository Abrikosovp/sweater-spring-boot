package ru.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.example.domain.Role;
import ru.example.domain.User;
import ru.example.services.UserService;

import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public String fullUser(Model model) {
        model.addAttribute("users", userService.findAll());
        return "userList";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("{user}")
    public String editUser(
            @PathVariable User user,
            Model model) {
        model.addAttribute("user", userService.findByUsername(user.getUsername()));
        model.addAttribute("roles", Role.values());
        return "userEdit";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public String userSave(
            @RequestParam String username,
            @RequestParam Map<String, String> form,
            @RequestParam("userId") User user) {

        userService.saveUser(username, form, user);
        return "redirect:/user";
    }

    @GetMapping("/profile")
    public String getProfile(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("password", user.getPassword());
        model.addAttribute("email", user.getEmail());
        return "profile";
    }

    @PostMapping("/profile")
    public String saveUser(@AuthenticationPrincipal User user,
                           @RequestParam String password,
                           @RequestParam String email) {

        userService.updateProfile(user, password, email);

        return "redirect:/user/profile";
    }
}