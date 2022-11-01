package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;



@DataJpaTest
@DirtiesContext
public class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private final LocalDateTime localDateTime = LocalDateTime.now();

    private final User user1 = new User(1L,"user1","user1@mail.com");
    private final User user2 = new User(2L,"user2","user2@mail.com");
    private final ItemRequest itemRequest = new ItemRequest(1L,"description", user1.getId(),
            localDateTime.minusMonths(2));
    private final Item item = new Item(1L, "item","desc", false, itemRequest.getRequesterId(), user2.getId());
    private final Booking booking = new Booking(1L, BookingStatus.REJECTED, user2, item, localDateTime, localDateTime.plusMonths(2));

    @Test
    void createBookingDataJpaTest() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRequestRepository.save(itemRequest);
        itemRepository.save(item);
        bookingRepository.save(booking);
        TypedQuery<Booking> query = em.getEntityManager().createQuery("Select i from Booking i where i.id = :id", Booking.class);
        Booking bookingQuery = query
                .setParameter("id", booking.getId())
                .getSingleResult();

        assertThat(bookingQuery.getId(), equalTo(booking.getId()));
        assertThat(bookingQuery.getBooker().getName(), equalTo(booking.getBooker().getName()));
        assertThat(bookingQuery.getStatus(), equalTo(booking.getStatus()));
        assertThat(bookingQuery.getItem().getName(), equalTo(booking.getItem().getName()));
        assertThat(bookingQuery.getStart(), equalTo(booking.getStart()));
        assertThat(bookingQuery.getEnd(), equalTo(booking.getEnd()));
    }
}