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

import java.util.Collections;
import java.util.UUID;

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

            String message = String.format("Hello, %s \n" +
                            "Welcome to Sweater. Please, visit next link: http://localhost:8080/activate/%s",
                    user.getUsername(),
                    user.getActivationCode());
            mailSender.sand(user.getEmail(), "Activation code", message);
            result = true;
        }

        return result;
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
}