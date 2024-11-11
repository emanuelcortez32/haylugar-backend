package ar.com.greenbundle.haylugar.pojo.constants;

public enum UserState {
    ACTIVE(true, "User is active"),
    PENDING(true, "User is pending for some reason"),
    PENDING_BY_EMAIL_VALIDATION(true, "User is pending due email validation"),
    PENDING_BY_TYC(true, "User is pending due TyC accepted"),
    BLOCKED(false, "User is blocked for some reason"),
    BLOCKED_BY_FRAUD(false, "User is blocked due fraud reason"),
    BLOCKED_BY_CONFLICT(false, "User is blocked due conflict"),
    INACTIVE(false, "User is inactive for some reason"),
    CLOSED(false, "User account is closed"),
    UNKNOWN(false, "User state is unknown, must be checked");

    public final boolean enabled;
    public final String stateDescription;

    UserState(boolean enabled, String stateDescription) {
        this.enabled = enabled;
        this.stateDescription = stateDescription;
    }
}
