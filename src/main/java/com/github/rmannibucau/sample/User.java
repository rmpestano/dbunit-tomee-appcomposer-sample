package com.github.rmannibucau.sample;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class User {
    @Id
    private long id;

    private String name;
}
