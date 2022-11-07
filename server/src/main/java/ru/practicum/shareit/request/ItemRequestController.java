package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto create(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Create itemRequest by user with id={}", userId);
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemRequestDto> findAllByRequester(@RequestHeader(value = "X-Sharer-User-Id") Long requesterId) {
        log.info("Get all requests by requester with id={}", requesterId);
        return itemRequestService.findAllByRequester(requesterId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemRequestDto> findAll(
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        log.info("Get all requests by owner with id={}", ownerId);
        return itemRequestService.findAll(ownerId, from, size);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto findById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                   @PathVariable(value = "requestId") Long requestId) {
        log.info("Get from requestId by user with id={}", userId);
        return itemRequestService.findById(userId, requestId);
    }
}
