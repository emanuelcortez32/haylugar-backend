package ar.com.greenbundle.haylugar.dto.constants;

public enum PaymentStatus {
    INVALID_OR_INCOMPLETE(99, "At least one of the payment data fields is invalid or missing."),
    CANCELLED_BY_COSTUMER(1, "The customer has cancelled the transaction by himself."),
    AUTHORIZATION_REFUSED(2, "The authorisation has been refused by the financial institution.\n" +
            "The customer can retry the authorisation process after selecting another card or another payment method."),
    ORDER_STORED(4, ""),
    AUTHORISED(5, "The authorisation has been accepted."),
    AUTHORISED_AND_CANCELLED(6, ""),
    REFUND(7, "The payment has been refunded"),
    REQUESTED(8, "The payment has been accepted."),
    COMPLETED(0, "The payment has been completed"),
    CREATED(9, "The payment has been created, but not yet processed");

    public final int code;
    public final String codeDescription;
    PaymentStatus(int code, String codeDescription) {
        this.code = code;
        this.codeDescription = codeDescription;
    }
}
