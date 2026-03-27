package com.platformzeta.auth.kafka.event;

/**
 * For theoretical delete event
 * @param email
 */
public record UserEmailDeleteEvent(
        String email
) {}
