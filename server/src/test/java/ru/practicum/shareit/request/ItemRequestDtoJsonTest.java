package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    private static final LocalDateTime time = LocalDateTime.now();

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description", 1L, time);

    private final ItemRequestDto itemRequestDto2 = new ItemRequestDto(1L, "description", 1L, time, List.of());

    @Test
    void testItemRequestDto() throws Exception {
        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathNumberValue("$.requesterId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(time.format(formatter));
        assertThat(result).extractingJsonPathValue("$.items").isNull();
    }

    @Test
    void testItemRequestDtoWithItems() throws Exception {
        JsonContent<ItemRequestDto> result = json.write(itemRequestDto2);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathNumberValue("$.requesterId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(time.format(formatter));
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(0);
    }

}