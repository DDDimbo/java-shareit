package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.markerinterface.Create;
import ru.practicum.shareit.markerinterface.Update;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                         @Validated(Create.class) @RequestBody ItemDto itemDto
    ) {
        if (itemDto.getRequestId() != null)
            log.info("Create item for request with id={} validation", itemDto.getRequestId());
        else
            log.info("Create item validation");
        return itemClient.createItem(userId, itemDto);

    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                @PathVariable(value = "itemId") Long itemId,
                                                @Validated(Create.class) @RequestBody CommentDto commentDto) {
        log.info("Create comment for item with id={} by user with id={} validation", itemId, userId);
        return itemClient.createComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                         @PathVariable(value = "itemId") Long itemId,
                                         @Validated(Update.class) @RequestBody ItemDto itemDto) {
        log.info("Update item userId={}, itemId={} validation", userId, itemId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                           @PathVariable(value = "itemId", required = false) Long itemId) {
        log.info("User userId={} get item itemId={} validation", userId, itemId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Get all items by owner ownerId={} validation", userId);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "text", required = false) String text) {
        log.info("Get result of search text: {} validation", text);
        return itemClient.searchItem(text, from, size);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteById(@PathVariable(value = "itemId", required = false) Long id) {
        log.info("Delete item id={} validation", id);
        return itemClient.deleteItem(id);
    }
}
