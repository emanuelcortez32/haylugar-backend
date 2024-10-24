package ar.com.greenbundle.haylugar.repositories.queries;

public class Queries {
    public static class Users {
        public static final String SELECT_USERS_BY_EMAIL = "SELECT * FROM users WHERE email = :email";
    }
    public static class UserProfiles {
        public static final String SELECT_USER_PROFILES_BY_USER_ID =  "SELECT * FROM user_profiles WHERE user_id = :user_id";
    }
    public static class UserPaymentProfiles {
        public static final String SELECT_PAYMENT_PROFILE_BY_USER_ID = "SELECT * FROM user_payment_profiles WHERE user_id = :user_id";
    }
    public static class UserPaymentCards {
        public static final String SELECT_PAYMENT_CARDS_BY_PAYMENT_PROFILE_ID = "SELECT * FROM user_payment_cards WHERE payment_profile_id = :payment_profile_id";
    }
    public static class Spots {
        public static final String SELECT_SPOTS_BY_LANDLORD_USER_ID = "SELECT * FROM spots WHERE landlord_user_id = :landlord_user_id";
    }
    public static class Bookings {
        public static final String SELECT_BOOKINGS_BY_SPOT_ID = "SELECT * FROM bookings WHERE spot_id = :spot_id";
        public static final String SELECT_BOOKINGS_BY_USER_ID = "SELECT * FROM bookings WHERE spot_owner_id = :user_id OR client_user_id = :user_id";
    }
}
