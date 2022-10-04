package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class User {
    private long id;
    @NotBlank
    private String name;
    @NotEmpty
    @Email
    private String email;
}