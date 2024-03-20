package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.markerinterface.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                         @Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Create itemRequest by user with id={} validation", userId);
        return itemRequestClient.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByRequester(@RequestHeader(value = "X-Sharer-User-Id") Long requesterId) {
        log.info("Get all requests by requester with id={} validation", requesterId);
        return itemRequestClient.getRequestsByRequester(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        log.info("Get all requests by owner with id={} validation", ownerId);
        return itemRequestClient.getRequests(ownerId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                           @PathVariable(value = "requestId") Long requestId) {
        log.info("Get from requestId by user with id={} validation", userId);
        return itemRequestClient.getRequest(userId, requestId);
    }
}
