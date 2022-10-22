package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Component
public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, Long requesterId) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requesterId(requesterId)
                .created(LocalDateTime.now())
                .build();
    }


    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requesterId(itemRequest.getRequesterId())
                .created(itemRequest.getCreated())
                .build();
    }

    public static List<ItemRequestDto> toItemRequestDtoList(List<ItemRequest> requests) {
        List<ItemRequestDto> dtoRequests = new ArrayList<>();
        for (ItemRequest request : requests)
            dtoRequests.add(toItemRequestDto(request));
        return dtoRequests;
    }
}
