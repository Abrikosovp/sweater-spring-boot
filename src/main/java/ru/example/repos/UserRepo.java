package ru.example.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.example.domain.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String name);
}