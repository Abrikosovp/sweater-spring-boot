package ru.example.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.example.domain.Role;
import ru.example.domain.User;
import ru.example.repos.UserRepo;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo repo;

    @Autowired
    private MailSender mailSender;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        return repo.findByUsername(name);
    }

    public boolean addUser(User user) {

        boolean result = false;

        User userFromDb = repo.findByUsername(user.getUsername());

        if (userFromDb == null && !StringUtils.isEmpty(user.getEmail())) {
            user.setActive(true);
            user.setRoles(Collections.singleton(Role.USER));
            user.setActivationCode(UUID.randomUUID().toString());
            repo.save(user);

            sendMessage(user);
            result = true;
        }

        return result;
    }

    private void sendMessage(User user) {
        String message = String.format("Hello, %s \n" +
                        "Welcome to Sweater. Please, visit next link: http://localhost:8080/activate/%s",
                user.getUsername(),
                user.getActivationCode());
        mailSender.sand(user.getEmail(), "Activation code", message);
    }

    public boolean activateCode(String code) {
        boolean result = false;
        User user = repo.findByActivationCode(code);

        if (user != null) {
            user.setActivationCode(null);
            repo.save(user);
            result = true;
        }
        return result;
    }

    public List<User> findAll() {
        return repo.findAll();
    }

    public User findByUsername(String username) {
        return repo.findByUsername(username);
    }

    public void saveUser(String username, Map<String, String> form, User user) {
        user.setUsername(username);
        user.getRoles().clear();

        Set<String> roles = Arrays
                .stream(Role.values())
                    .map(Role::name)
                        .collect(Collectors.toSet());

        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
            }
        }

        repo.save(user);
    }

    public void updateProfile(User user, String password, String email) {

        String userEmail = user.getEmail();

        boolean isEmailChanged = (((email != null) && !email.equals(userEmail))
                || ((userEmail != null) && (!userEmail.equals(email))));

        if (isEmailChanged) {
            user.setEmail(email);

            if (!StringUtils.isEmpty(email)) {
                user.setActivationCode(UUID.randomUUID().toString());
            }
        }

        if (!StringUtils.isEmpty(password)) {
            user.setPassword(password);
        }
        repo.save(user);

        if (isEmailChanged) {
            sendMessage(user);
        }
    }
}