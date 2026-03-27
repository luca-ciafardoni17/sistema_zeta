package com.platformzeta.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class UserDto implements Serializable {
    private Long id;
    private String email;
    private String password;
}