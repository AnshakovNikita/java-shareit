package ru.practicum.shareit.user.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserDto {
    private final Long id;
    private final String name;
    private final String email;
}