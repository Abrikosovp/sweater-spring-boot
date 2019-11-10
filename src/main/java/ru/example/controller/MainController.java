package ru.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.example.domain.Message;
import ru.example.domain.User;
import ru.example.repos.MessageRepo;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
public class MainController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @Autowired
    private MessageRepo repo;

    @Value("${upload.path}")
    private String uploadPath;

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
            @RequestParam("file") MultipartFile file,
            Model model) throws IOException {
        Message message = new Message(text, tag, user);

        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File fileUpload = new File(this.uploadPath);
            if (!fileUpload.exists()) {
                fileUpload.mkdir();
            }

            String fileName = String.format("%s.%s", UUID.randomUUID().toString(), file.getOriginalFilename());
            String transferFile = String.format("%s/%s", this.uploadPath, fileName);

            file.transferTo(new File(transferFile));
            message.setFileName(fileName);
        }
        this.repo.save(message);
        List<Message> messages = this.repo.findAll();
        model.addAttribute("messages", messages);
        return "main";
    }
}