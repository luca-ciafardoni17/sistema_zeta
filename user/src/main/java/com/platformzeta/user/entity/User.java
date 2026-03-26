package com.platformzeta.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 500)
    private String passwordHash;

    @Column(name = "mobile_number", length = 20)
    private String mobileNumber;

    @Column(name = "account_holder", nullable = false, length = 30)
    private String accountHolder;

    @Column(name = "tax_code", nullable = false, length = 16)
    private String taxCode;

    @Column(name = "country", nullable = false, length = 30)
    private String country;

    @Column(name = "province", nullable = false, length = 50)
    private String province;

    @Column(name = "town", nullable = false, length = 50)
    private String town;

    @Column(name = "address", nullable = false, length = 50)
    private String address;

}