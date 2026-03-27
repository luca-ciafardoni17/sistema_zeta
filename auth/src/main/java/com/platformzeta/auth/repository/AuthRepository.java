package com.platformzeta.auth.repository;

import com.platformzeta.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * User repository, uses standard JpaRepository and a custom function to search user for email field
 */
public interface AuthRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}