package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullPrintDto;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> jsonItem;

    @Autowired
    private JacksonTester<ItemFullPrintDto> jsonFullPrint;

    private final ItemDto itemDtoWithAnswer = new ItemDto(2L, "Щётка для обуви",
            "Стандартная щётка для обуви", true, 1L);


    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Щётка для машины")
            .description("Стандартная щётка для машины")
            .available(true)
            .build();


    private final ItemFullPrintDto itemFullPrintDto = ItemFullPrintDto.builder()
            .id(1L)
            .name("Щётка для машины")
            .description("Стандартная щётка для машины")
            .available(true)
            .lastBooking(null)
            .nextBooking(null)
            .comments(List.of())
            .build();


    @Test
    void testItemDtoWithAnswer() throws Exception {
        JsonContent<ItemDto> result = jsonItem.write(itemDtoWithAnswer);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDtoWithAnswer.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDtoWithAnswer.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDtoWithAnswer.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    @Test
    void testItemDto() throws Exception {
        JsonContent<ItemDto> result = jsonItem.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathValue("$.requestId").isNull();
    }

    @Test
    void testFullPrintDtoCreateDto() throws Exception {
        JsonContent<ItemFullPrintDto> result = jsonFullPrint.write(itemFullPrintDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemFullPrintDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemFullPrintDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemFullPrintDto.getAvailable());
        assertThat(result).extractingJsonPathValue("$.lastBooking").isNull();
        assertThat(result).extractingJsonPathValue("$.nextBooking").isNull();
        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(0);
    }

}