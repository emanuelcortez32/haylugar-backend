package ar.com.greenbundle.haylugar.rest.controllers;

import ar.com.greenbundle.haylugar.dto.CreateBookingData;
import ar.com.greenbundle.haylugar.rest.requests.CreateBookingRequest;
import ar.com.greenbundle.haylugar.rest.responses.BookingActionResponse;
import ar.com.greenbundle.haylugar.rest.responses.BookingResponse;
import ar.com.greenbundle.haylugar.rest.responses.CancelBookingResponse;
import ar.com.greenbundle.haylugar.rest.responses.CreateBookingResponse;
import ar.com.greenbundle.haylugar.rest.responses.FinishBookingResponse;
import ar.com.greenbundle.haylugar.rest.responses.GetBookingsResponse;
import ar.com.greenbundle.haylugar.rest.responses.StartBookingResponse;
import ar.com.greenbundle.haylugar.service.BookingService;
import ar.com.greenbundle.haylugar.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static ar.com.greenbundle.haylugar.rest.endpoints.ControllerEndpoints.BookingEndpoints.ACTION_BOOKING;
import static ar.com.greenbundle.haylugar.rest.endpoints.ControllerEndpoints.BookingEndpoints.CREATE_BOOKING;
import static ar.com.greenbundle.haylugar.rest.endpoints.ControllerEndpoints.BookingEndpoints.GET_BOOKING;

@RestController
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping(value = GET_BOOKING, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<GetBookingsResponse>> getBookings(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String authToken) {
        return bookingService.getBookingsByUser(JwtUtil.extractSubjectFromAuthHeader(authToken))
                .collectList()
                .map(bookings -> ResponseEntity.ok(GetBookingsResponse.builder()
                                .success(true)
                                .message("OK")
                                .bookings(bookings.stream().map(booking -> BookingResponse.builder()
                                        .id(booking.getId())
                                        .paymentId(booking.getPaymentId())
                                        .state(booking.getState())
                                        .startTime(booking.getStartTime())
                                        .endTime(booking.getEndTime())
                                        .totalMinutes(booking.getTotalMinutes())
                                        .build()).toList())
                        .build()));
    }

    @PostMapping(value = CREATE_BOOKING, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<CreateBookingResponse>> createBooking(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String authToken,
                                                                     @RequestBody CreateBookingRequest request) {

        request.validate();

        CreateBookingData bookingData = CreateBookingData.builder()
                .spotId(request.getSpotId())
                .paymentMethod(request.getPaymentMethod())
                .paymentCurrency(request.getCurrency())
                .build();

        return bookingService.createBooking(JwtUtil.extractSubjectFromAuthHeader(authToken), bookingData)
                .map(booking -> new ResponseEntity<>(CreateBookingResponse.builder()
                        .success(true)
                        .message("OK")
                        .id(booking.getId())
                        .build(), HttpStatus.CREATED));
    }

    @PostMapping(value = ACTION_BOOKING, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<BookingActionResponse>> finishBooking(@PathVariable(name = "booking_id") String bookingId,
                                                                     @RequestParam String action) {

        return switch (action) {
            case "finish" ->
                    bookingService.finishBooking(bookingId).map(booking -> ResponseEntity.ok(FinishBookingResponse.builder()
                            .success(true)
                            .message("OK")
                            .totalMinutes(booking.getTotalMinutes())
                            .paymentId(booking.getPaymentId())
                            .build()));
            case "start" ->
                    bookingService.startBooking(bookingId).map(booking -> ResponseEntity.ok(StartBookingResponse.builder()
                            .success(true)
                            .message("OK")
                            .startedTime(booking.getStartTime())
                            .build()));
            case "cancel" ->
                    bookingService.cancelBooking(bookingId).map(booking -> ResponseEntity.ok(CancelBookingResponse.builder()
                            .success(true)
                            .message("OK")
                            .reason("CANCELED")
                            .build()));
            default ->
                    Mono.just(ResponseEntity.badRequest().body(new BookingActionResponse(false, "Action invalid")));
        };
    }
}
