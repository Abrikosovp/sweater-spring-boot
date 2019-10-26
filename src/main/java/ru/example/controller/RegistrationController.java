package ru.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.example.domain.Role;
import ru.example.domain.User;
import ru.example.repos.UserRepo;

import java.util.Collections;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepo repo;

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String saveRegistration(
            User user,
            Model model) {

        User userFromDb = repo.findByUsername(user.getUsername());
        if (userFromDb != null) {
            model.addAttribute("message", "User exist !!!");
            return "registration";
        }
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        repo.save(user);
        return "redirect:/login";
    }
}