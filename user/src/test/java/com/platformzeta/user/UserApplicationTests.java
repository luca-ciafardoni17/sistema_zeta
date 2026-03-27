package com.platformzeta.user;

import com.platformzeta.user.kafka.event.UserEmailDeleteEvent;
import com.platformzeta.user.kafka.event.UserEmailUpdateEvent;
import com.platformzeta.user.kafka.event.UserRegisteredEvent;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class UserApplicationTests {

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
