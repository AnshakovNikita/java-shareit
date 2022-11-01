package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;

    private final ItemDto itemDtoAnswer = new ItemDto(2L, "item1",
            "des1", true, 1L);

    private final ItemDto itemDto = new ItemDto(1L, "user_1", "user_1 description", true,4L);

    private final CommentDto commentdto = new CommentDto(1L, "text", 1L, "автор", LocalDateTime.now());

    @Test
    void addItemTest() throws Exception {
        final Long xSharerUserId = 1L;

        when(itemService.addItem(any(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", xSharerUserId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void addItemWithRequestId() throws Exception {
        final Long xSharerUserId = 1L;

        when(itemService.addItem(any(), anyLong()))
                .thenReturn(itemDtoAnswer);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", xSharerUserId)
                        .content(mapper.writeValueAsString(itemDtoAnswer))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoAnswer.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoAnswer.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoAnswer.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoAnswer.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDtoAnswer.getRequestId()), Long.class));
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(commentdto);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentdto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentdto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentdto.getText())))
                .andExpect(jsonPath("$.itemId", is(commentdto.getItemId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(commentdto.getAuthorName())));
    }

    @Test
    void addCommentNotBookingExceptionTest() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenThrow(new IllegalStateException(
                        "У предмета не было бронирований")
                );

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentdto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",
                        is("У предмета не было бронирований")));
    }

    @Test
    void updateItem() throws Exception {
        final Long xSharerUserId = 1L;

        when(itemService.updateItem(any(), anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", xSharerUserId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void updateItemNotFoundFromUserTest() throws Exception {
        final Long xSharerUserId = 1L;

        when(itemService.updateItem(any(), anyLong(), anyLong()))
                .thenThrow(new EntityNotFoundException("Предмет отсутсвует у данного пользователя"));

        mvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", xSharerUserId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error", is("Предмет отсутсвует у данного пользователя")));
    }

    @Test
    void searchItemTest() throws Exception {
        final Integer from = 0;
        final Integer size = 10;
        List<ItemDto> dtoItems = List.of(itemDto, itemDtoAnswer);
        when(itemService.searchItem("text", from, size))
                .thenReturn(dtoItems);

        mvc.perform(get("/items/search")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("text", "text")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(dtoItems))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[1].id", is(itemDtoAnswer.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(itemDtoAnswer.getName())))
                .andExpect(jsonPath("$[1].description", is(itemDtoAnswer.getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemDtoAnswer.getAvailable())))
                .andExpect(jsonPath("$[1].requestId", is(itemDtoAnswer.getRequestId()), Long.class));
    }

    @Test
    void searchItemTextBlankTest() throws Exception {
        final Integer from = 0;
        final Integer size = 10;
        final String blank = "  ";
        List<ItemDto> result = List.of();

        when(itemService.searchItem(blank, from, size))
                .thenReturn(result);

        mvc.perform(get("/items/search")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("text", blank)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(result))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getByIdTest() throws Exception {
        when(itemService.getById(anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", is(itemDto.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemDto.getNextBooking())))
                .andExpect(jsonPath("$.comments", hasSize(0)));
    }

    @Test
    void getAllItemsUserTest() throws Exception {
        final Integer from = 0;
        final Integer size = 10;
        List<ItemDto> result = List.of(itemDto);
        when(itemService.getAllItemsUser(1L, from, size))
                .thenReturn(result);

        mvc.perform(get("/items")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(result))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking", is(itemDto.getLastBooking())))
                .andExpect(jsonPath("$[0].nextBooking", is(itemDto.getNextBooking())))
                .andExpect(jsonPath("$[0].comments", hasSize(0)));
    }
}