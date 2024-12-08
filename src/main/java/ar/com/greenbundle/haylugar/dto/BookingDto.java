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
public class BookingDto extends EntityDto<BookingEntity, BookingDto> {
    private UserDto client;
    private SpotDto spot;
    private PaymentDto payment;
    private UserVehicleDto vehicle;
    private LocalDateTime startDate;
    private String startTime;
    private LocalDateTime endDate;
    private String endTime;
    private long totalMinutes;
    private BookingState state;
    private BookingUserAs bookingUserAs;

    @Builder
    public BookingDto(String id, LocalDateTime createdAt, Long version, UserDto client, SpotDto spot, PaymentDto payment, UserVehicleDto vehicle, LocalDateTime startDate, String startTime, LocalDateTime endDate, String endTime, long totalMinutes, BookingState state, BookingUserAs bookingUserAs) {
        super(id, createdAt, version);
        this.client = client;
        this.spot = spot;
        this.payment = payment;
        this.vehicle = vehicle;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.totalMinutes = totalMinutes;
        this.state = state;
        this.bookingUserAs = bookingUserAs;
    }

    @Override
    public BookingDto dtoFromEntity(BookingEntity entity) {
        return BookingDto.builder()
                .id(entity.getId())
                .client(UserDto.builder().id(entity.getClientUserId()).build())
                .payment(PaymentDto.builder().id(entity.getPaymentId()).build())
                .vehicle(UserVehicleDto.builder().id(entity.getVehicleId()).build())
                .spot(SpotDto.builder().id(entity.getSpotId()).build())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .totalMinutes(entity.getTotalMinutes())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .state(entity.getState())
                .createdAt(entity.getCreatedAt())
                .version(entity.getVersion())
                .build();
    }

    @Override
    public BookingEntity dtoToEntity(BookingDto dto) {
        return BookingEntity.builder()
                .id(dto.getId())
                .state(dto.getState())
                .startDate(dto.getStartDate())
                .startTime(dto.getStartTime())
                .endDate(dto.getEndDate())
                .endTime(dto.getEndTime())
                .totalMinutes(dto.getTotalMinutes())
                .clientUserId(dto.getClient() != null ? dto.getClient().getId() : null)
                .paymentId(dto.getPayment() != null ? dto.getPayment().getId() : null)
                .spotId(dto.getSpot() != null ? dto.getSpot().getId() : null)
                .createdAt(dto.getCreatedAt())
                .version(dto.getVersion())
                .build();
    }
}
