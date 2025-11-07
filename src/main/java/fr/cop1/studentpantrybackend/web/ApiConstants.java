package fr.cop1.studentpantrybackend.web;

/**
 * Constantes pour les endpoints de l'API
 */
public final class ApiConstants {
    public static final String API_BASE_PATH = "/api/v1";

    // Endpoints utilisateurs
    public static final String USERS_ENDPOINT = API_BASE_PATH + "/users";
    public static final String USER_ID_PATH = "/{id}";

    // Endpoints authentification
    public static final String AUTH_ENDPOINT = API_BASE_PATH + "/auth";
    public static final String LOGIN_PATH = "/login";
    public static final String REGISTER_PATH = "/register";
    public static final String RESET_PASSWORD_PATH = "/reset-password";

    // Endpoints événements
    public static final String EVENTS_ENDPOINT = API_BASE_PATH + "/events";
    public static final String EVENT_ID_PATH = "/{id}";
    public static final String EVENT_TIMESLOTS_PATH = "/{id}/timeslots";
    public static final String TIMESLOT_ID_PATH = "/{timeslotId}";

    // Endpoints réservations
    public static final String RESERVATIONS_ENDPOINT = API_BASE_PATH + "/reservations";
    public static final String RESERVATION_ID_PATH = "/{id}";

    // Endpoints bénévoles
    public static final String VOLUNTEER_ENDPOINT = API_BASE_PATH + "/volunteers";
    public static final String VOLUNTEER_SHIFTS_ENDPOINT = API_BASE_PATH + "/volunteer-shifts";
    public static final String VOLUNTEER_SHIFT_ID_PATH = "/{id}";
    public static final String VOLUNTEER_REGISTRATIONS_ENDPOINT = API_BASE_PATH + "/volunteer-registrations";
    public static final String VOLUNTEER_REGISTRATION_ID_PATH = "/{id}";
    public static final String VOLUNTEER_AVAILABILITIES_ENDPOINT = API_BASE_PATH + "/volunteer-availabilities";
    public static final String VOLUNTEER_AVAILABILITY_ID_PATH = "/{id}";

    // Endpoints types de paniers
    public static final String BASKET_TYPES_ENDPOINT = API_BASE_PATH + "/basket-types";
    public static final String BASKET_TYPE_ID_PATH = "/{id}";

    // Endpoints inventaire
    public static final String INVENTORY_ENDPOINT = API_BASE_PATH + "/inventory";
    public static final String INVENTORY_ID_PATH = "/{id}";

    // Endpoints notifications
    public static final String NOTIFICATIONS_ENDPOINT = API_BASE_PATH + "/notifications";
    public static final String NOTIFICATION_ID_PATH = "/{id}";

    // Paramètres de pagination
    public static final String PAGE_PARAM = "page";
    public static final String SIZE_PARAM = "size";
    public static final String SORT_PARAM = "sort";

    // Éviter l'instanciation
    private ApiConstants() {}
}
