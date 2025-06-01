package ar.com.velascosoft.haylugar.pojo.constants;

public enum PaymentStatus {
    SUCCESS("PAY-000", "The payment has been approved and credited successfully"),
    PENDING("PAY-001", "The payment is pending"),
    AUTHORIZED("PAY-002", "The payment is authorized, but not yet captured"),
    IN_PROCESS("PAY-003", "The payment is under review"),
    IN_MEDIATION("PAY-004", "The user has started a dispute"),
    REJECTED("PAY-005", "Payment was declined (user can try to pay again)"),
    CANCELED("PAY-006", "The payment was canceled by either party or expired."),
    REFUNDED("PAY-007", "The payment was refunded to the user"),
    CHARGED_BACK("PAY-008", "A chargeback was made to the buyer's credit card"),
    FAILURE("PAY-999", "The payment has been failure for some reason");

    public final String code;
    public final String codeDescription;
    PaymentStatus(String code, String codeDescription) {
        this.code = code;
        this.codeDescription = codeDescription;
    }
}
