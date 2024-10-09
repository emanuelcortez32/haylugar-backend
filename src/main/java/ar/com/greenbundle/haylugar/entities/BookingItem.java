package ar.com.greenbundle.haylugar.entities;

import ar.com.greenbundle.haylugar.dto.constants.BookingState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Document("bookings")
public class BookingItem extends GenericItem {
    private String clientUserId;
    private String spotOwnerId;
    private String spotId;
    private String paymentId;
    @CreatedDate
    private Date date;
    private String startTime;
    private String endTime;
    private long totalMinutes;
    private BookingState state;
}
