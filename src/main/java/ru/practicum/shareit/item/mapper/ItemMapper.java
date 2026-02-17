package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static Item toItem(ItemRequestDto itemRequestDto) {
        if (itemRequestDto == null) {
            return null;
        }
        return Item.builder()
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .available(itemRequestDto.getAvailable())
                .build();
    }

    public static ItemResponseDto toItemResponseDto(Item item) {
        if (item == null) {
            return null;
        }
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest())
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static ItemBookingDto toItemBookingDto(Item item,
                                                  BookingDto lastBooking,
                                                  BookingDto nextBooking,
                                                  List<Comment> comments) {
        if (item == null) {
            return null;
        }

        List<CommentResponseDto> commentDtos = comments != null ?
                comments.stream()
                        .map(CommentMapper::toCommentResponseDto)
                        .collect(Collectors.toList()) :
                List.of();

        return ItemBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(commentDtos)
                .build();
    }

    public static Item requestDtoToJpa(ItemRequestDto itemRequestDto) {
        return toItem(itemRequestDto);
    }

    public static ItemResponseDto jpaToResponseDto(Item item) {
        return toItemResponseDto(item);
    }
}