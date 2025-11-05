package com.yusufelkaan.jwt_auth.auth.repos;

import com.yusufelkaan.jwt_auth.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
