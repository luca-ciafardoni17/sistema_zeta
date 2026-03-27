package com.platformzeta.user.kafka.event;

public record UserRegisteredEvent (
        String email,
        String accountHolder,
        String taxCode,
        String country,
        String province,
        String town,
        String address
) {}
