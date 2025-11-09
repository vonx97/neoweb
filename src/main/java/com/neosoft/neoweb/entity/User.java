package com.neosoft.neoweb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import enums.RoleName;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(name = "identity_number")
    private String identityNumber;

    private String country;
    private String city;
    private String district;
    private String address;

    @Column(unique = true)
    private String email;

    private String phone;

    @Column(name = "user_name", unique = true, nullable = false)
    private String username;

    @JsonIgnore
    private String password;

    public User(String username, String password, String name, String surname,LocalDateTime creationDate) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.creationDate = creationDate;
    }


    @ElementCollection(targetClass = RoleName.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<RoleName> roles = new HashSet<>();

    protected User() {}



    // === Getters & Setters ===
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDateTime getCreationDate() { return creationDate; }

    public String getName() { return name; }

    public String getSurname() { return surname; }

    public String getIdentityNumber() { return identityNumber; }
    public void setIdentityNumber(String identityNumber) { this.identityNumber = identityNumber; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public Set<RoleName> getRoles() { return roles; }
    public void setRoles(Set<RoleName> roles) { this.roles = roles; }
}
