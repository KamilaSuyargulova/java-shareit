package ru.practicum.shareit.Item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.Item.dto.ItemCreateDto;
import ru.practicum.shareit.Item.dto.ItemUpdateDto;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.common.BaseClient;

import java.util.Collections;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getItem(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getUserItems(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> createItem(Long userId, ItemCreateDto itemCreateDto) {
        return post("", userId, itemCreateDto);
    }

    public ResponseEntity<Object> createComment(Long userId, Long itemId, CommentCreateDto commentCreateDto) {
        return post("/" + itemId + "/comment", userId, commentCreateDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemUpdateDto itemUpdateDto) {
        return patch("/" + itemId, userId, itemUpdateDto);
    }

    public ResponseEntity<Object> searchItem(Long userId, String text) {
        if (text == null || text.isBlank()) {
            return ResponseEntity.ok().body(Collections.emptyList());
        }

        return get("/search?text={text}", userId, Map.of("text", text));
    }


}