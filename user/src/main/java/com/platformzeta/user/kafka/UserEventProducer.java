package com.platformzeta.user.kafka;

import com.platformzeta.user.kafka.event.UserEmailDeleteEvent;
import com.platformzeta.user.kafka.event.UserEmailUpdateEvent;
import com.platformzeta.user.kafka.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEventProducer {

    private static final String TOPIC_REGISTRATION = "user-registered";
    private static final String TOPIC_UPDATE = "user-update";
    private static final String TOPIC_DELETE = "user-delete";

    @Qualifier("kafkaTemplateRegistration")
    private final KafkaTemplate<String, UserRegisteredEvent> kafkaTemplateRegistration;
    @Qualifier("kafkaTemplateUpdate")
    private final KafkaTemplate<String, UserEmailUpdateEvent> kafkaTemplateUpdated;
    @Qualifier("kafkaTemplateDelete")
    private final KafkaTemplate<String, UserEmailDeleteEvent> kafkaTemplateDeleted;

    public void publishUserRegistered(UserRegisteredEvent event) {
        kafkaTemplateRegistration.send(TOPIC_REGISTRATION, event.email(), event);
    }

    public void publishUserUpdated(UserEmailUpdateEvent event) {
        kafkaTemplateUpdated.send(TOPIC_UPDATE, event.email(), event);
    }

    public void publishUserDeleted(UserEmailDeleteEvent event) {
        kafkaTemplateDeleted.send(TOPIC_DELETE, event.email(), event);
    }


}