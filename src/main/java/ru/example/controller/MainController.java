package ru.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.example.domain.Message;
import ru.example.domain.User;
import ru.example.repos.MessageRepo;

import java.util.List;

@Controller
public class MainController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @Autowired
    private MessageRepo repo;

    @GetMapping("/main")
    public String main(
            Model model,
            @RequestParam(name = "filter", required = false) String filter) {
        List<Message> messages;
        if (filter != null && !filter.isEmpty()) {
            messages = this.repo.findByTag(filter);
        } else {
            messages = this.repo.findAll();
        }
        model.addAttribute("messages", messages);
        return "main";
    }

    @PostMapping("/main")
    public String addMessage(
            @RequestParam(name = "text") String text,
            @RequestParam(name = "tag") String tag,
            @AuthenticationPrincipal User user,
            Model model) {
        Message message = new Message(text, tag, user);
        this.repo.save(message);
        List<Message> messages = this.repo.findAll();
        model.addAttribute("messages", messages);
        return "main";
    }
}