package com.techdegree.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public class BaseEntity {
    // I will leave both id and version
    // @NotNull and @NotEmpty for now ...
    // because it does make sense to me, and although
    // @craigsdennis advices to use `final id`
    // I will leave it as it is for now
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @NotEmpty
    private Long id;

    @Version
    @NotNull
    @NotEmpty
    private Long version;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    // Constructors

    public BaseEntity() {
        // default constructor for JPA
    }
}
