package ar.com.velascosoft.haylugar.entities;

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
@Table("user_payment_cards")
public class UserPaymentCardEntity extends GenericEntity {
    @Column("payment_profile_id")
    private String paymentProfileId;
    @Column("external_reference_id")
    private String externalReferenceId;
    private String token;
    @Column("expiration_month")
    private Integer expirationMonth;
    @Column("expiration_year")
    private Integer expirationYear;
    @Column("card_default")
    private boolean cardDefault;
    @Column("payment_type")
    private String paymentType;
    @Column("payment_method")
    private String paymentMethod;
    @Column("security_code_length")
    private Integer securityCodeLength;
    @Column("security_code_card_location")
    private String securityCodeCardLocation;
    @Column("issuer_id")
    private String issuerId;
    @Column("issuer_name")
    private String issuerName;

    @Builder
    public UserPaymentCardEntity(String id, LocalDateTime createdAt, LocalDateTime updatedAt, Long version, boolean deleted, String paymentProfileId, String externalReferenceId, String token, Integer expirationMonth, Integer expirationYear, boolean cardDefault, String paymentType, String paymentMethod, Integer securityCodeLength, String securityCodeCardLocation, String issuerId, String issuerName) {
        super(id, createdAt, updatedAt, version, deleted);
        this.paymentProfileId = paymentProfileId;
        this.externalReferenceId = externalReferenceId;
        this.token = token;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
        this.cardDefault = cardDefault;
        this.paymentType = paymentType;
        this.paymentMethod = paymentMethod;
        this.securityCodeLength = securityCodeLength;
        this.securityCodeCardLocation = securityCodeCardLocation;
        this.issuerId = issuerId;
        this.issuerName = issuerName;
    }
}
