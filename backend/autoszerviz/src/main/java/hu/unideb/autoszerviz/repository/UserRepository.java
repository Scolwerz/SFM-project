package hu.unideb.autoszerviz.repository;

import hu.unideb.autoszerviz.entity.*;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
}
