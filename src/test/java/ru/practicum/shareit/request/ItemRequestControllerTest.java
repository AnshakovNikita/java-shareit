package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemRequestService itemRequestService;


    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;


    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "описание", 1L, LocalDateTime.now());

    private final ItemRequestDto itemRequestDto2 = new ItemRequestDto(2L, "описание2", 1L, LocalDateTime.now());

    @Test
    void createItemRequestTest() throws Exception {
        when(itemRequestService.createItemRequest(anyLong(), any()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requesterId", is(itemRequestDto.getRequesterId()), Long.class))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().format(formatter))));
    }


    @Test
    void findAllByRequesterTest() throws Exception {
        List<ItemRequestDto> items = Arrays.asList(itemRequestDto, itemRequestDto2);

        when(itemRequestService.findAllByRequester(anyLong()))
                .thenReturn(items);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", anyLong())
                        .content(mapper.writeValueAsString(items))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].id", is(itemRequestDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(itemRequestDto2.getDescription())))
                .andExpect(jsonPath("$[1].requesterId", is(itemRequestDto2.getRequesterId()), Long.class))
                .andExpect(jsonPath("$[1].created", is(itemRequestDto2.getCreated().format(formatter))))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].requesterId", is(itemRequestDto.getRequesterId()), Long.class))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated().format(formatter))));
    }



    @Test
    void findAllTest() throws Exception {
        final Integer from = 0;
        final Integer size = 10;
        final Long xSharerUserId = 1L;
        ItemDto itemDto = new ItemDto(4L, "itemDto", "description1", true, 1L);
        ItemRequestDto itemRequestWithItem = new ItemRequestDto(
                3L, "description", 1L, LocalDateTime.now(), List.of(itemDto)
        );
        List<ItemRequestDto> result = new ArrayList<>();
        result.add(itemRequestWithItem);

        when(itemRequestService.findAll(xSharerUserId, from, size)).thenReturn(result);

        mvc.perform(get("/requests/all")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", xSharerUserId)
                        .content(mapper.writeValueAsString(result))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestWithItem.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestWithItem.getDescription())))
                .andExpect(jsonPath("$[0].requesterId", is(itemRequestWithItem.getRequesterId()), Long.class))
                .andExpect(jsonPath("$[0].created", is(itemRequestWithItem.getCreated().format(formatter))))
                .andExpect(jsonPath("$[0].items.length()", is(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId", is(itemDto.getRequestId()), Long.class));
    }


    @Test
    void findByIdTest() throws Exception {
        final Long xSharerUserId = 1L;
        final Long requestId = 1L;
        ItemDto itemDto = new ItemDto(4L, "itemDto", "description1", true, 1L);
        ItemRequestDto itemRequestWithItem = new ItemRequestDto(
                3L, "description", 1L, LocalDateTime.now(), List.of(itemDto)
        );

        when(itemRequestService.findById(xSharerUserId, requestId)).thenReturn(itemRequestWithItem);

        mvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", xSharerUserId)
                        .content(mapper.writeValueAsString(itemRequestWithItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestWithItem.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestWithItem.getDescription())))
                .andExpect(jsonPath("$.requesterId", is(itemRequestWithItem.getRequesterId()), Long.class))
                .andExpect(jsonPath("$.created", is(itemRequestWithItem.getCreated().format(formatter))))
                .andExpect(jsonPath("$.items.length()", is(1)))
                .andExpect(jsonPath("$.items[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.items[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.items[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.items[0].requestId", is(itemDto.getRequestId()), Long.class));
    }


    @Test
    void findByIdItemRequestNotFoundExceptionTest() throws Exception {
        final Long xSharerUserId = 1L;
        final Long requestId = 1L;
        ItemDto itemDto = new ItemDto(4L, "itemDto", "description1", true, 1L);
        ItemRequestDto itemRequestWithItem = new ItemRequestDto(
                3L, "description", 1L, LocalDateTime.now(), List.of(itemDto)
        );

        when(itemRequestService.findById(xSharerUserId, requestId))
                .thenThrow(new EntityNotFoundException("Запрос не существует"));

        mvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", xSharerUserId)
                        .content(mapper.writeValueAsString(itemRequestWithItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error", is("Запрос не существует")));
    }

}