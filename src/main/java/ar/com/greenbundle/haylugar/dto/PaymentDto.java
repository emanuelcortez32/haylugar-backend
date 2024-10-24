package ar.com.greenbundle.haylugar.dto;

import ar.com.greenbundle.haylugar.entities.PaymentEntity;
import ar.com.greenbundle.haylugar.pojo.PaymentTransactionDetail;
import ar.com.greenbundle.haylugar.pojo.constants.Currency;
import ar.com.greenbundle.haylugar.pojo.constants.PaymentMethod;
import ar.com.greenbundle.haylugar.pojo.constants.PaymentProvider;
import ar.com.greenbundle.haylugar.pojo.constants.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentDto extends EntityDto {
    private String referenceId;
    private PaymentMethod method;
    private PaymentProvider provider;
    private double totalPrice;
    private double providerAmount;
    private double platformAmount;
    private double userNetAmount;
    private Currency currency;
    private PaymentStatus lastStatus;
    private List<PaymentTransactionDetail> transactionDetails;

    @Builder
    public PaymentDto(String id, LocalDateTime createdAt, Long version, String referenceId, PaymentMethod method, PaymentProvider provider, double totalPrice, double providerAmount, double platformAmount, double userNetAmount, Currency currency, PaymentStatus lastStatus, List<PaymentTransactionDetail> transactionDetails) {
        super(id, createdAt, version);
        this.referenceId = referenceId;
        this.method = method;
        this.provider = provider;
        this.totalPrice = totalPrice;
        this.providerAmount = providerAmount;
        this.platformAmount = platformAmount;
        this.userNetAmount = userNetAmount;
        this.currency = currency;
        this.lastStatus = lastStatus;
        this.transactionDetails = transactionDetails;
    }

    public static PaymentDto.PaymentDtoBuilder builderFromEntity(PaymentEntity entity) {
        return PaymentDto.builder()
                .id(entity.getId())
                .referenceId(entity.getReferenceId())
                .method(entity.getMethod())
                .provider(entity.getProvider())
                .totalPrice(entity.getTotalPrice())
                .providerAmount(entity.getProviderAmount())
                .platformAmount(entity.getPlatformAmount())
                .userNetAmount(entity.getUserNetAmount())
                .currency(entity.getCurrency())
                .lastStatus(entity.getLastStatus())
                .transactionDetails(entity.getTransactionDetails())
                .createdAt(entity.getCreatedAt())
                .version(entity.getVersion());
    }

    public static PaymentEntity mapToEntity(PaymentDto dto) {
        return PaymentEntity.builder()
                .id(dto.getId())
                .referenceId(dto.getReferenceId())
                .method(dto.getMethod())
                .provider(dto.getProvider())
                .totalPrice(dto.getTotalPrice())
                .providerAmount(dto.getProviderAmount())
                .platformAmount(dto.getPlatformAmount())
                .userNetAmount(dto.getUserNetAmount())
                .currency(dto.getCurrency())
                .lastStatus(dto.getLastStatus())
                .transactionDetails(dto.getTransactionDetails())
                .createdAt(dto.getCreatedAt())
                .version(dto.getVersion())
                .build();
    }
}
