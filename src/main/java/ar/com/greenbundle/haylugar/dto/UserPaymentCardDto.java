package ar.com.greenbundle.haylugar.dto;

import ar.com.greenbundle.haylugar.entities.UserPaymentCardEntity;
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
public class UserPaymentCardDto extends EntityDto<UserPaymentCardEntity, UserPaymentCardDto>  {
    private String paymentProfileId;
    private String externalReferenceId;
    private String token;
    private Integer expirationMonth;
    private Integer expirationYear;
    private boolean cardDefault;
    private String paymentType;
    private String paymentMethod;
    private Integer securityCodeLength;
    private String securityCodeCardLocation;
    private String issuerId;
    private String issuerName;
    @Builder
    public UserPaymentCardDto(String id, LocalDateTime createdAt, Long version, String paymentProfileId, String externalReferenceId, String token, Integer expirationMonth, Integer expirationYear, boolean cardDefault, String paymentType, String paymentMethod, Integer securityCodeLength, String securityCodeCardLocation, String issuerId, String issuerName) {
        super(id, createdAt, version);
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

    @Override
    public UserPaymentCardDto dtoFromEntity(UserPaymentCardEntity entity) {
        return UserPaymentCardDto.builder()
                .id(entity.getId())
                .paymentProfileId(entity.getPaymentProfileId())
                .externalReferenceId(entity.getExternalReferenceId())
                .token(entity.getToken())
                .expirationMonth(entity.getExpirationMonth())
                .expirationYear(entity.getExpirationYear())
                .cardDefault(entity.isCardDefault())
                .paymentType(entity.getPaymentType())
                .paymentMethod(entity.getPaymentMethod())
                .securityCodeLength(entity.getSecurityCodeLength())
                .securityCodeCardLocation(entity.getSecurityCodeCardLocation())
                .issuerId(entity.getIssuerId())
                .issuerName(entity.getIssuerName())
                .createdAt(entity.getCreatedAt())
                .version(entity.getVersion())
                .build();
    }

    @Override
    public UserPaymentCardEntity dtoToEntity(UserPaymentCardDto dto) {
        return UserPaymentCardEntity.builder()
                .id(dto.getId())
                .paymentProfileId(dto.getPaymentProfileId())
                .externalReferenceId(dto.getExternalReferenceId())
                .token(dto.getToken())
                .expirationMonth(dto.getExpirationMonth())
                .expirationYear(dto.getExpirationYear())
                .cardDefault(dto.isCardDefault())
                .paymentType(dto.getPaymentType())
                .paymentMethod(dto.getPaymentMethod())
                .securityCodeLength(dto.getSecurityCodeLength())
                .securityCodeCardLocation(dto.getSecurityCodeCardLocation())
                .issuerId(dto.getIssuerId())
                .issuerName(dto.getIssuerName())
                .createdAt(dto.getCreatedAt())
                .version(dto.getVersion())
                .build();
    }
}
