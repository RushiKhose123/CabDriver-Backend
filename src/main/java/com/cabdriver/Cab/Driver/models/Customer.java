package com.cabdriver.Cab.Driver.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Customer {
    @Id
    private Integer id;
    private Integer age;
    private String password;
    private String firstName;
    private String lastNAme;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true, nullable = false)
    private Long phoneNumber;
    private String address;
}
