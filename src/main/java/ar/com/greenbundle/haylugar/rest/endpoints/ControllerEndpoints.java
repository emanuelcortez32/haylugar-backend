package ar.com.greenbundle.haylugar.rest.endpoints;

public final class ControllerEndpoints {

    private static final class BookingEndpoints {
        private static final String BASE = "/bookings";
        public static final String CREATE_BOOKING = BASE;
        public static final String GET_BOOKINGS = BASE;
    }

    private static final class SpotEndpoints {
        private static final String BASE = "/spots";
        public static final String CREATE_SPOT = BASE;
        public static final String GET_SPOTS = BASE;
    }

    private static final class UserEndpoints {
        private static final String BASE = "/user";
        public static final String GET_USER = BASE;
    }

    private static final class AuthEndpoints {
        private static final String BASE = "/auth";
        public static final String USER_LOGIN = BASE + "/login";
        public static final String USER_SIGNUP = BASE + "/signup";
    }

    private static final class LocationEndpoints {
        private static final String BASE = "/location";
        public static final String GET_LOCATION = BASE;
    }

    public static final class MeEndpoints {
        private static final String ME = "/me";

        public static final class UserEndpoints {
            public static final String GET_USER = ControllerEndpoints.UserEndpoints.GET_USER + ME;
        }

        public static final class BookingEndpoints {
            public static final String GET_BOOKINGS = ControllerEndpoints.BookingEndpoints.GET_BOOKINGS + ME;
            public static final String POST_BOOKING = ControllerEndpoints.BookingEndpoints.CREATE_BOOKING + ME;
        }

        public static final class SpotEndpoints {
            public static final String GET_SPOTS = ControllerEndpoints.SpotEndpoints.GET_SPOTS + ME;
            public static final String POST_SPOT = ControllerEndpoints.SpotEndpoints.CREATE_SPOT + ME;
        }
    }

    public static final class GlobalEndpoints {

        public static final String HEALTHCHECK = "/healthcheck";

        public static final class AuthEndpoints {
            public static final String USER_LOGIN = ControllerEndpoints.AuthEndpoints.USER_LOGIN;
            public static final String USER_SIGNUP = ControllerEndpoints.AuthEndpoints.USER_SIGNUP;
        }

        public static final class LocationEndpoints {
            public static final String GET_LOCATION = ControllerEndpoints.LocationEndpoints.GET_LOCATION;
        }
    }
}
