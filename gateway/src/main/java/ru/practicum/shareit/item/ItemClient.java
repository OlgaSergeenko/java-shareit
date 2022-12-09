package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveNewItem(long userId, ItemCreateRequestDto itemCreateRequestDto) {
        return post("", userId, itemCreateRequestDto);
    }

    public ResponseEntity<Object> updateItem(long userId, long itemId, ItemCreateRequestDto itemCreateRequestDto) {
        return patch("/" + itemId, userId, itemCreateRequestDto);
    }

    public ResponseEntity<Object> getItem(long userId, long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemByUserId(long userId) {
        return get("/", userId);
    }

    public ResponseEntity<Object> searchItemByQuery(String text) {
        Map<String, Object> parameters = Map.of(
                "text", text);
        return get("/search?text={text}", null, parameters);
    }

    public ResponseEntity<Object> saveNewComment(CommentRequestDto commentRequestDto, long userId, long itemId) {
        return post("/" + itemId + "/comment", userId, commentRequestDto);
    }
}
