package com.platformzeta.auth;

import com.platformzeta.auth.kafka.event.UserEmailDeleteEvent;
import com.platformzeta.auth.kafka.event.UserEmailUpdateEvent;
import com.platformzeta.auth.kafka.event.UserRegisteredEvent;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class AuthApplicationTests {

	@MockitoBean
	private KafkaTemplate<String, UserRegisteredEvent> kafkaTemplateRegistration;

	@MockitoBean
	private KafkaTemplate<String, UserEmailUpdateEvent> kafkaTemplateUpdate;

	@MockitoBean
	private KafkaTemplate<String, UserEmailDeleteEvent> kafkaTemplateDelete;

	@Test
	void contextLoads() {
	}

}
