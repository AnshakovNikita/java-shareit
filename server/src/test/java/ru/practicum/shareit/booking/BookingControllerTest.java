package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingReturnDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    BookingService bookingService;

    BookingReturnDto bookingReturnDto;

    BookingDto bookingDto;
    Boolean approved;
    private List<BookingReturnDto> bookingList;
    String state;
    Integer from;
    Integer size;
    LocalDateTime localDateTime = LocalDateTime.now();

    @BeforeEach
    void beforeEach() {
        mapper.findAndRegisterModules();

        UserDto userDto1 = UserDto.builder()
                .id(1L)
                .name("user_1")
                .email("user_1@mail.com")
                .build();
        ItemDto itemDto1 = new ItemDto(1L, "user_1", "user_1 description", true,4L);

        bookingReturnDto = new BookingReturnDto(1L, WAITING, userDto1, itemDto1, localDateTime, localDateTime.plusHours(2));
        bookingDto = new BookingDto(1L, WAITING, userDto1.getId(), itemDto1.getId(), localDateTime, localDateTime.plusHours(2), itemDto1.getName());

        approved = true;
        bookingList = new ArrayList<>();
        state = "state";
        from = 1;
        size = 4;
    }

    @Test
    void patchBookingTest() throws Exception {
        when(bookingService.patchBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingReturnDto);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(bookingReturnDto))
                        .param("approved", String.valueOf(approved))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingReturnDto)));
    }

    @Test
    void getBookingTest() throws Exception {
        when(bookingService.getBooking(any(), any()))
                .thenReturn(bookingReturnDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingReturnDto)));
    }

    @Test
    void getUserBookingListTest() throws Exception {
        when(bookingService.getUserBookingList(any(), any(), any(), any()))
                .thenReturn(bookingList);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", state)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingList)));
    }

    @Test
    void getOwnerBookingListTest() throws Exception {
        when(bookingService.getOwnerBookingList(any(), any(), any(), any()))
                .thenReturn(bookingList);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", state)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingList)));
    }
}