package com.platformzeta.user.repository;

import com.platformzeta.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailOrMobileNumber(String email, String mobileNumber);

}