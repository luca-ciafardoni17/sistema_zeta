package com.platformzeta.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class UserDto implements Serializable {
    private Long id;
    private String email;
    private String password;
    private String mobileNumber;

    // Campi da migrare a un altro microservizio
    private String accountHolder;
    private String taxCode;
    private String country;
    private String province;
    private String town;
    private String address;
}