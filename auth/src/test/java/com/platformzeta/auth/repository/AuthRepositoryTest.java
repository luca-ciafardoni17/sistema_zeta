package com.platformzeta.auth.repository;

import com.platformzeta.auth.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AuthRepositoryTest {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setEmail("mario.rossi@aruba.it");
        sampleUser.setPasswordHash("hashedPassword");

        // Campi da migrare
        sampleUser.setCreatedBy("TEST_SUITE");

        entityManager.persist(sampleUser);
        entityManager.flush();
    }

    @Test
    void findByEmail_Success() {
        Optional<User> found = authRepository.findByEmail("mario.rossi@aruba.it");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("mario.rossi@aruba.it");
    }

    @Test
    void findByEmail_NotFound() {
        Optional<User> found = authRepository.findByEmail("non.esiste@aruba.it");
        assertThat(found).isEmpty();
    }
}