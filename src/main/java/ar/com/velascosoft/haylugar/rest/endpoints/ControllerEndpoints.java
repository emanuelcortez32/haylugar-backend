package ar.com.velascosoft.haylugar.rest.endpoints;

public final class ControllerEndpoints {

    private static final class BookingEndpoints {
        private static final String BASE = "/bookings";
        public static final String CREATE_BOOKING = BASE;
        public static final String GET_BOOKINGS = BASE;
    }

    private static final class SpotEndpoints {
        private static final String BASE = "/spots";
        public static final String CREATE_SPOT = "/spot";
        public static final String GET_SPOTS = BASE;
        public static final String UPDATE_SPOT = "/spot";
        public static final String DELETE_SPOT = "/spot";
    }

    private static final class VehicleEndpoints {
        private static final String BASE = "/vehicles";
        public static final String GET_VEHICLES = BASE;
        public static final String CREATE_VEHICLE = "/vehicle";
        public static final String UPDATE_VEHICLE = "/vehicle";
        public static final String DELETE_VEHICLE = "/vehicle";
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
            public static final String GET_USER_BOOKINGS = ControllerEndpoints.BookingEndpoints.GET_BOOKINGS + ME;
            public static final String POST_USER_BOOKING = ControllerEndpoints.BookingEndpoints.CREATE_BOOKING + ME;
        }

        public static final class SpotEndpoints {
            public static final String DELETE_USER_SPOT = ControllerEndpoints.SpotEndpoints.DELETE_SPOT + ME;
            public static final String PUT_USER_SPOT = ControllerEndpoints.SpotEndpoints.UPDATE_SPOT + ME;
            public static final String GET_USER_SPOTS = ControllerEndpoints.SpotEndpoints.GET_SPOTS + ME;
            public static final String POST_USER_SPOT = ControllerEndpoints.SpotEndpoints.CREATE_SPOT + ME;
        }

        public static final class VehicleEndpoints {
            public static final String GET_USER_VEHICLES = ControllerEndpoints.VehicleEndpoints.GET_VEHICLES + ME;
            public static final String POST_USER_VEHICLE = ControllerEndpoints.VehicleEndpoints.CREATE_VEHICLE + ME;
            public static final String PUT_USER_VEHICLE = ControllerEndpoints.VehicleEndpoints.UPDATE_VEHICLE + ME;
            public static final String DELETE_USER_VEHICLE = ControllerEndpoints.VehicleEndpoints.DELETE_VEHICLE + ME;
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

        public static final class SpotEndpoints {
            public static final String GET_SPOTS = ControllerEndpoints.SpotEndpoints.GET_SPOTS;
        }
    }
}
