package com.platformzeta.user.repository;

import com.platformzeta.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setEmail("mario.rossi@aruba.it");
        sampleUser.setMobileNumber("3898786265");
        sampleUser.setPasswordHash("hashedPassword");
        sampleUser.setAccountHolder("Mario Rossi");

        // Campi da migrare
        sampleUser.setAddress("Via Roma 1");
        sampleUser.setTaxCode("CODICEFISCALE");
        sampleUser.setCountry("Italy");
        sampleUser.setProvince("Roma");
        sampleUser.setTown("Roma");
        sampleUser.setCreatedBy("TEST_SUITE");

        entityManager.persist(sampleUser);
        entityManager.flush();
    }

    @Test
    void findByEmail_Success() {
        Optional<User> found = userRepository.findByEmail("mario.rossi@aruba.it");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("mario.rossi@aruba.it");
    }

    @Test
    void findByEmail_NotFound() {
        Optional<User> found = userRepository.findByEmail("non.esiste@aruba.it");
        assertThat(found).isEmpty();
    }

    @Test
    void findByEmailOrMobileNumber_Success() {
        Optional<User> foundByEmail = userRepository.findByEmailOrMobileNumber("mario.rossi@aruba.it", "000000");
        Optional<User> foundByMobile = userRepository.findByEmailOrMobileNumber("falso@test.it", "3898786265");
        assertThat(foundByEmail).isPresent();
        assertThat(foundByMobile).isPresent();
        assertThat(foundByEmail.get().getId()).isEqualTo(foundByMobile.get().getId());
    }

    @Test
    void findByEmailOrMobileNumber_NotFound() {
        Optional<User> found = userRepository.findByEmailOrMobileNumber("alieno@marte.it", "999999");
        assertThat(found).isEmpty();
    }
}