package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User requestor1;
    private User requestor2;
    private ItemRequest request1;
    private ItemRequest request2;

    @BeforeEach
    void setUp() {
        requestor1 = User.builder()
                .name("Requestor 1")
                .email("req1@test.com")
                .build();
        requestor1 = userRepository.save(requestor1);

        requestor2 = User.builder()
                .name("Requestor 2")
                .email("req2@test.com")
                .build();
        requestor2 = userRepository.save(requestor2);

        LocalDateTime now = LocalDateTime.now();

        request1 = ItemRequest.builder()
                .description("Need a drill")
                .requestor(requestor1)
                .created(now.minusDays(1))
                .build();

        request2 = ItemRequest.builder()
                .description("Need a hammer")
                .requestor(requestor1)
                .created(now)
                .build();

        ItemRequest request3 = ItemRequest.builder()
                .description("Need a saw")
                .requestor(requestor2)
                .created(now.minusHours(1))
                .build();

        itemRequestRepository.saveAll(List.of(request1, request2, request3));
    }

    @Test
    void findByRequestor_IdOrderByCreatedDesc_ShouldReturnRequestsOrderedByCreatedDesc() {
        List<ItemRequest> requests = itemRequestRepository
                .findByRequestor_IdOrderByCreatedDesc(requestor1.getId());

        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getCreated()).isAfter(requests.get(1).getCreated());
        assertThat(requests.get(0).getDescription()).isEqualTo("Need a hammer");
        assertThat(requests.get(1).getDescription()).isEqualTo("Need a drill");
    }

    @Test
    void findAllByRequestorIdNot_ShouldReturnRequestsFromOtherUsers() {
        List<ItemRequest> requests = itemRequestRepository
                .findAllByRequestorIdNot(requestor1.getId());

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getRequestor().getId()).isEqualTo(requestor2.getId());
        assertThat(requests.get(0).getDescription()).isEqualTo("Need a saw");
    }

}