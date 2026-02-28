package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public BookingResponseDto getBooking(Long userId, Long bookingId) {
        log.info("User with id={} getting booking with id={}", userId, bookingId);

        Booking existingBooking = bookingRepository.findByIdWithDetails(bookingId).orElseThrow(() -> {
            log.warn("Booking with id={} not found", bookingId);
            return new NotFoundException("Booking with id=" + bookingId + " not found");
        });
        Long itemOwnerId = existingBooking.getItem().getOwner().getId();
        Long bookerId = existingBooking.getBooker().getId();
        if (!Objects.equals(userId, itemOwnerId) && !Objects.equals(userId, bookerId)) {
            throw new ConflictException("User with id=" + userId + " is not owner of booking with id=" + bookingId);
        }

        return BookingMapper.mapToResponseDto(existingBooking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookerBookings(Long userId, BookingState state) {
        log.info("Booker with id={} getting his bookings", userId);

        if (!userRepository.existsById(userId)) {
            log.warn("User with id={} not found", userId);
            throw new NotFoundException("User with id=" + userId + " not found");
        }
        LocalDateTime now = LocalDateTime.now();

        return switch (state) {
            case CURRENT -> bookingRepository.findCurrentByBookerId(userId, now).stream()
                    .map(BookingMapper::mapToResponseDto)
                    .toList();
            case PAST -> bookingRepository.findPastByBookerId(userId, now).stream()
                    .map(BookingMapper::mapToResponseDto)
                    .toList();
            case FUTURE -> bookingRepository.findFutureByBookerId(userId, now).stream()
                    .map(BookingMapper::mapToResponseDto)
                    .toList();
            case WAITING -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING).stream()
                    .map(BookingMapper::mapToResponseDto)
                    .toList();
            case REJECTED -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED).stream()
                    .map(BookingMapper::mapToResponseDto)
                    .toList();
            default -> bookingRepository.findAllByBookerId(userId).stream()
                    .map(BookingMapper::mapToResponseDto)
                    .toList();
        };
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDto> getOwnerBookings(Long userId, BookingState state) {
        log.info("ItemOwner with id={} getting his bookings", userId);

        if (!userRepository.existsById(userId)) {
            log.warn("User with id={} not found", userId);
            throw new NotFoundException("User with id=" + userId + " not found");
        }
        LocalDateTime now = LocalDateTime.now();

        return switch (state) {
            case CURRENT -> bookingRepository.findCurrentByOwnerId(userId, now).stream()
                    .map(BookingMapper::mapToResponseDto)
                    .toList();
            case PAST -> bookingRepository.findPastByOwnerId(userId, now).stream()
                    .map(BookingMapper::mapToResponseDto)
                    .toList();
            case FUTURE -> bookingRepository.findFutureByOwnerId(userId, now).stream()
                    .map(BookingMapper::mapToResponseDto)
                    .toList();
            case WAITING -> bookingRepository.findByOwnerIdAndStatus(userId, BookingStatus.WAITING).stream()
                    .map(BookingMapper::mapToResponseDto)
                    .toList();
            case REJECTED -> bookingRepository.findByOwnerIdAndStatus(userId, BookingStatus.REJECTED).stream()
                    .map(BookingMapper::mapToResponseDto)
                    .toList();
            default -> bookingRepository.findAllByOwnerId(userId).stream()
                    .map(BookingMapper::mapToResponseDto)
                    .toList();
        };
    }

    @Transactional
    public BookingResponseDto createBooking(Long userId, BookingCreateDto bookingCreateDto) {
        Long itemId = bookingCreateDto.getItemId();
        Booking bookingToCreate = BookingMapper.mapToBooking(bookingCreateDto);

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id=" + userId + " not found"));
        Item item = itemRepository.findByIdWithDetails(itemId).orElseThrow(
                () -> new NotFoundException("Item with id=" + itemId + " not found"));

        if (bookingToCreate.getEndBooking().isBefore(bookingToCreate.getStartBooking()) ||
                bookingToCreate.getEndBooking().equals(bookingToCreate.getStartBooking())) {
            throw new BadRequestException("End date must be after start date");
        }
        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Owner cannot book his own item");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("Item with id=" + itemId + " is not available");
        }

        bookingToCreate.setItem(item);
        bookingToCreate.setBooker(user);
        bookingToCreate.setStatus(BookingStatus.WAITING);

        Booking createdBooking = bookingRepository.save(bookingToCreate);
        return BookingMapper.mapToResponseDto(createdBooking);
    }

    @Transactional
    public BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approved) {
        Booking existingBooking = bookingRepository.findByIdWithDetails(bookingId).orElseThrow(() -> {
            log.warn("Booking with id={} not found", bookingId);
            return new NotFoundException("Booking with id=" + bookingId + " not found");
        });

        if (!Objects.equals(existingBooking.getItem().getOwner().getId(), userId)) {
            throw new BadRequestException("User with id=" + userId + " is not owner of booking with id=" + bookingId);
        }

        if (!existingBooking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BadRequestException("Booking status has already been changed");
        }

        existingBooking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        Booking updatedBooking = bookingRepository.save(existingBooking);
        return BookingMapper.mapToResponseDto(updatedBooking);
    }
}