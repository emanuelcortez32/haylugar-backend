package ar.com.greenbundle.haylugar.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("user_payment_profiles")
public class UserPaymentProfileEntity extends GenericEntity {
    @Column("user_id")
    private String userId;
    @Column("customer_id")
    private String customerId;

    @Builder
    public UserPaymentProfileEntity(String id, LocalDateTime createdAt, LocalDateTime updatedAt, Long version, boolean deleted, String userId, String customerId) {
        super(id, createdAt, updatedAt, version, deleted);
        this.userId = userId;
        this.customerId = customerId;
    }
}
