
package ru.javabegin.micro.booksseller.authapi.Entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private LocalDate birth;
    private Integer age;
    private Float total_spend_amount;

    @Column(name = "provider")
    private String provider; // LOCAL, GOOGLE, GITHUB

    @Column(name = "provider_id")
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role = Role.ROLE_USER;

    public enum Role {
        ROLE_USER,
        ROLE_PREMIUM_USER,
        ROLE_VIP_USER
    }




    public User() {
    }

    public User(Long id, String name, String email, String password, LocalDate birth, Integer age, Float total_spend_amount, String provider, String providerId, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.birth = birth;
        this.age = age;
        this.total_spend_amount = total_spend_amount;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirth() {
        return birth;
    }

    public void setBirth(LocalDate birth) {
        this.birth = birth;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Float getTotal_spend_amount() {
        return total_spend_amount;
    }

    public void setTotal_spend_amount(Float total_spend_amount) {
        this.total_spend_amount = total_spend_amount;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", birth=" + birth +
                ", age=" + age +
                ", total_spend_amount=" + total_spend_amount +
                ", provider='" + provider + '\'' +
                ", providerId='" + providerId + '\'' +
                ", role=" + role +
                '}';
    }


}