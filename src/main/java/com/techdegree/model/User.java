package com.techdegree.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// does not extend BaseEntity, so that no @Id and @Version fields
// added automatically, so I'll put @Id
// here, and implements UserDetails, well because I also
// take @christherama's position on this
@Entity
public class User implements UserDetails {
    public static final PasswordEncoder PASSWORD_ENCODER =
            new BCryptPasswordEncoder();

    // fields

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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

    // no constraints here... that is a good question to
    // @christherama
    @JsonIgnore
    private String password;

    // adding this because of 'spring-weather-app'
    // by @christherama ... should ask later
    @Column(nullable = false)
    private boolean enabled;

    // role: for now I'll leave it one role, not like
    // @craigsdennis made them String[] ... but again
    // I better ask this...
    @OneToOne
    @JoinColumn(name = "role_id")
    private Role role;

    // favorite recipes
    @ManyToMany(mappedBy = "favoriteUsers")
    private List<Recipe> favoriteRecipes = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    private List<Recipe> ownedRecipes = new ArrayList<>();

    // Getters and Setters

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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = PASSWORD_ENCODER.encode(password);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Recipe> getFavoriteRecipes() {
        return favoriteRecipes;
    }

    public void setFavoriteRecipes(List<Recipe> favoriteRecipes) {
        this.favoriteRecipes = favoriteRecipes;
    }

    public List<Recipe> getOwnedRecipes() {
        return ownedRecipes;
    }

    public void setOwnedRecipes(List<Recipe> ownedRecipes) {
        this.ownedRecipes = ownedRecipes;
    }


    // Constructors

    public User() {
        this.id = null;
        this.username = null;
        this.password = null;
        this.enabled = true;
        this.role = null;
    }

    public User(String name,
                String username,
                String password) {
        this();
        this.username = username;
        this.name = name;
        setPassword(password);
    }

    // Override methods

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList(
                role.getName()
        );
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
