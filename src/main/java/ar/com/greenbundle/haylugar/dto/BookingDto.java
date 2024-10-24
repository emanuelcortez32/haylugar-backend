package ar.com.greenbundle.haylugar.dto;

import ar.com.greenbundle.haylugar.entities.BookingEntity;
import ar.com.greenbundle.haylugar.pojo.constants.BookingState;
import ar.com.greenbundle.haylugar.pojo.constants.BookingUserAs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BookingDto extends EntityDto {
    private UserDto client;
    private UserDto spotOwner;
    private SpotDto spot;
    private PaymentDto payment;
    private LocalDateTime startDate;
    private String startTime;
    private LocalDateTime endDate;
    private String endTime;
    private long totalMinutes;
    private BookingState state;
    private BookingUserAs bookingUserAs;

    @Builder
    public BookingDto(String id, LocalDateTime createdAt, Long version, UserDto client, UserDto spotOwner, SpotDto spot, PaymentDto payment, LocalDateTime startDate, String startTime, LocalDateTime endDate, String endTime, long totalMinutes, BookingState state, BookingUserAs bookingUserAs) {
        super(id, createdAt, version);
        this.client = client;
        this.spotOwner = spotOwner;
        this.spot = spot;
        this.payment = payment;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.totalMinutes = totalMinutes;
        this.state = state;
        this.bookingUserAs = bookingUserAs;
    }

    public static BookingDto.BookingDtoBuilder builderFromEntity(BookingEntity entity) {
        return BookingDto.builder()
                .id(entity.getId())
                .spotOwner(UserDto.builder().id(entity.getSpotOwnerId()).build())
                .client(UserDto.builder().id(entity.getClientUserId()).build())
                .payment(PaymentDto.builder().id(entity.getPaymentId()).build())
                .spot(SpotDto.builder().id(entity.getSpotId()).build())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .totalMinutes(entity.getTotalMinutes())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .state(entity.getState())
                .createdAt(entity.getCreatedAt())
                .version(entity.getVersion());
    }

    public static BookingEntity mapToEntity(BookingDto dto) {
        return BookingEntity.builder()
                .id(dto.getId())
                .state(dto.getState())
                .startDate(dto.getStartDate())
                .startTime(dto.getStartTime())
                .endDate(dto.getEndDate())
                .endTime(dto.getEndTime())
                .totalMinutes(dto.getTotalMinutes())
                .clientUserId(dto.getClient().getId())
                .spotOwnerId(dto.getSpotOwner().getId())
                .paymentId(dto.getPayment().getId())
                .spotId(dto.getSpot().getId())
                .createdAt(dto.getCreatedAt())
                .version(dto.getVersion())
                .build();
    }
}
