package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemRequestDto {
    public ItemRequestDto(Long id, String description, Long requesterId, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.requesterId = requesterId;
        this.created = created;
    }

    @Positive
    private Long id;
    @NotBlank
    private String description;
    @Positive
    private Long requesterId;
    @PastOrPresent
    private LocalDateTime created;
    private List<ItemDto> items;

}
