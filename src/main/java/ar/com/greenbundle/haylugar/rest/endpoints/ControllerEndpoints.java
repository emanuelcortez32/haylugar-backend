package ar.com.greenbundle.haylugar.rest.endpoints;

public final class ControllerEndpoints {
    public static final class BookingEndpoints {
        private static final String BASE = "/bookings";
        public static final String CREATE_BOOKING = BASE;
        public static final String GET_BOOKING = BASE + "/{booking_id}";
        public static final String GET_BOOKINGS = BASE;
    }

    public static final class SpotEndpoints {
        private static final String BASE = "/spots";
        public static final String CREATE_SPOT = BASE;
        public static final String GET_SPOTS = BASE;
        public static final String GET_SPOT = BASE + "/{spot_id}";
    }

    public static final class UserEndpoints {
        private static final String BASE = "/user";
        public static final String GET_USER = BASE;
        public static final String CREATE_USER_PAYMENT_PROFILE = BASE + "/paymentProfile";
    }

    public static final class AuthEndpoints {
        private static final String BASE = "/auth";
        public static final String USER_LOGIN = BASE + "/login";
        public static final String USER_SIGNUP = BASE + "/signup";
    }

    public static final class LocationEndpoints {
        private static final String BASE = "/location";
        public static final String GET_LOCATION = BASE;
    }
}
