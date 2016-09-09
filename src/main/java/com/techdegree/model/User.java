package com.techdegree.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;

// extends BaseEntity, so that no @Id and @Version fields
// here, and implements UserDetails, well because I also
// take @christherama's position on this
@Entity
public class User extends BaseEntity implements UserDetails {

    // fields

    // let it be just "name" for now. It could be split into
    // first name and last name, but well, for now I'll leave it
    // be so
    @NotNull
    @NotEmpty
    private String name;

    @Column(unique = true)
    @Size(min = 2, max = 20,
            message = "Username should be between 2 and 20 characters")
    private String username;

    @JsonIgnore
    private String password;

    // Getters and Setters

    // Constructors

    // Override methods

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
