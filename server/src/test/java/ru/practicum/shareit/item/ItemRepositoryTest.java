package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
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
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private ItemRepository itemRepository;

    LocalDateTime localDateTime = LocalDateTime.now();
    User user1 = new User(1L, "user1", "user1@mail.com");
    User user2 = new User(2L,"user2","user2@mail.com");
    ItemRequest itemRequest = new ItemRequest(1L,"description", user1.getId(), localDateTime.minusMonths(2));
    Item item = new Item(1L,"item","desc",true, itemRequest.getId(), user2.getId());

    @Test
    void createItemTest() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRequestRepository.save(itemRequest);
        itemRepository.save(item);
        TypedQuery<Item> query = em.getEntityManager().createQuery("Select i from Item i where i.id = :id", Item.class);
        Item itemQuery = query
                .setParameter("id", item.getId())
                .getSingleResult();

        assertThat(itemQuery.getId(), equalTo(item.getId()));
        assertThat(itemQuery.getName(), equalTo(item.getName()));
        assertThat(itemQuery.getDescription(), equalTo(item.getDescription()));
        assertThat(itemQuery.getName(), equalTo(item.getName()));
    }

}