package com.platformzeta.auth.kafka.event;

/**
 * For theoretical registration event, carries anagraphic informations to UserDetails microservices
 * @param email
 * @param accountHolder
 * @param taxCode
 * @param country
 * @param province
 * @param town
 * @param address
 */
public record UserRegisteredEvent (
        String email,
        String accountHolder,
        String taxCode,
        String country,
        String province,
        String town,
        String address
) {}
