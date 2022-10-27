package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.EmptyRequestParameterException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullPrintDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.markerinterface.Create;
import ru.practicum.shareit.markerinterface.Update;

import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                          @Validated(Create.class) @RequestBody ItemDto itemDto
    ) {
        log.info("Create item");
        return itemService.create(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto createComment(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                    @PathVariable(value = "itemId") Long itemId,
                                    @Validated(Create.class) @RequestBody CommentDto commentDto) {
        log.info("Create comment for item with id={} by user with id={}", itemId, userId);
        return itemService.createComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                          @PathVariable(value = "itemId") Long itemId,
                          @Validated(Update.class) @RequestBody ItemDto itemDto
    ) {
        log.info("Update item userId={}, itemId={}", userId, itemId);
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemFullPrintDto findById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                     @PathVariable(value = "itemId", required = false) Long itemId) {
        log.info("User userId={} get item itemId={}", userId, itemId);
        return itemService.findById(userId, itemId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemFullPrintDto> findAll(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Get all items by owner ownerId={}", userId);
        return itemService.findAll(userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteById(@PathVariable(value = "itemId", required = false) Long id) {
        log.info("Delete item id={}", id);
        itemService.deleteById(id);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemDto> search(@RequestParam(value = "text", required = false) String text) {
        if (text == null)
            throw new EmptyRequestParameterException("Параметр запроса должен содержать текст");
        if (text.isBlank())
            return new ArrayList<>();
        log.info("Get result of search text: {}", text);
        return itemService.search(text);
    }
}
