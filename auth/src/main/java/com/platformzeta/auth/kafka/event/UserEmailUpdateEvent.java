package com.platformzeta.auth.kafka.event;

/**
 * For theoretical update event
 * @param email
 */
public record UserEmailUpdateEvent(
        String email
) {}
