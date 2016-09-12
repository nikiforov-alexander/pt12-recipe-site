package com.techdegree.model;

import javax.persistence.*;

@MappedSuperclass
public class BaseEntity {
    // I will leave both id and version
    // @NotNull and @NotEmpty for now ...
    // because it does make sense to me, and although
    // @craigsdennis advices to use `final id`
    // I will leave it as it is for now
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
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
