package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class UserDto {
    private final Long id;
    @NotBlank(message = "GateWay UserDto - Name cannot be empty or null")
    private final String name;
    @NotEmpty(message = "GateWay UserDto - Email cannot be null")
    @Email(message = "GateWay UserDto - Email is mandatory")
    private final String email;
}
