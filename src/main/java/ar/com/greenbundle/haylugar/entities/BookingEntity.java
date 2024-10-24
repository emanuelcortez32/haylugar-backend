package ar.com.greenbundle.haylugar.entities;

import ar.com.greenbundle.haylugar.pojo.constants.BookingState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("bookings")
public class BookingEntity extends GenericEntity {
    @Column("client_user_id")
    private String clientUserId;
    @Column("spot_owner_id")
    private String spotOwnerId;
    @Column("spot_id")
    private String spotId;
    @Column("payment_id")
    private String paymentId;
    @CreatedDate
    @Column("start_date")
    private LocalDateTime startDate;
    @Column("start_time")
    private String startTime;
    @Column("end_date")
    private LocalDateTime endDate;
    @Column("end_time")
    private String endTime;
    @Column("total_minutes")
    private long totalMinutes;
    private BookingState state;

    @Builder
    public BookingEntity(String id, LocalDateTime createdAt, LocalDateTime updatedAt, Long version, String clientUserId, String spotOwnerId, String spotId, String paymentId, LocalDateTime startDate, String startTime, LocalDateTime endDate, String endTime, long totalMinutes, BookingState state) {
        super(id, createdAt, updatedAt, version);
        this.clientUserId = clientUserId;
        this.spotOwnerId = spotOwnerId;
        this.spotId = spotId;
        this.paymentId = paymentId;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.totalMinutes = totalMinutes;
        this.state = state;
    }
}
