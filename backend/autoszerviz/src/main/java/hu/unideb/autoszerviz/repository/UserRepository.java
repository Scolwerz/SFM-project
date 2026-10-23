package hu.unideb.autoszerviz.repository;

import hu.unideb.autoszerviz.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
