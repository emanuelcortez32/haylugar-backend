package ar.com.greenbundle.haylugar.rest.controllers;

import ar.com.greenbundle.haylugar.dto.BookingDto;
import ar.com.greenbundle.haylugar.dto.PaymentDto;
import ar.com.greenbundle.haylugar.dto.SpotDto;
import ar.com.greenbundle.haylugar.rest.requests.CreateBookingRequest;
import ar.com.greenbundle.haylugar.rest.responses.ApiResponse;
import ar.com.greenbundle.haylugar.rest.responses.BookingActionResponse;
import ar.com.greenbundle.haylugar.rest.responses.BookingResponse;
import ar.com.greenbundle.haylugar.rest.responses.CancelBookingResponse;
import ar.com.greenbundle.haylugar.rest.responses.CreateBookingResponse;
import ar.com.greenbundle.haylugar.rest.responses.FinishBookingResponse;
import ar.com.greenbundle.haylugar.rest.responses.GetUserBookingResponse;
import ar.com.greenbundle.haylugar.rest.responses.GetUserBookingsResponse;
import ar.com.greenbundle.haylugar.rest.responses.StartBookingResponse;
import ar.com.greenbundle.haylugar.service.BookingService;
import ar.com.greenbundle.haylugar.util.StringUtils;
import ar.com.greenbundle.haylugar.util.time.TimeZoneUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.function.Function;

import static ar.com.greenbundle.haylugar.rest.endpoints.ControllerEndpoints.MeEndpoints.BookingEndpoints.GET_BOOKINGS;
import static ar.com.greenbundle.haylugar.rest.endpoints.ControllerEndpoints.MeEndpoints.BookingEndpoints.POST_BOOKING;

@RestController
@RequestMapping("/api")
public class BookingController {
    @Autowired
    private ZoneId representationTimeZone;
    @Autowired
    private BookingService bookingService;

    @GetMapping(value = GET_BOOKINGS, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ApiResponse>> getBookings(@AuthenticationPrincipal UserDetails principal,
                                                         @RequestParam(required = false) String bookingId) {

        if(!StringUtils.isNullOrEmpty(bookingId))
            return bookingService.findBookingByUser(principal.getUsername(), bookingId)
                    .map(mapBookingToResponse)
                    .map(bookingResponse -> ResponseEntity.ok(GetUserBookingResponse.builder()
                            .success(true)
                            .message("OK")
                            .bookingResponse(bookingResponse)
                            .build()));

        return bookingService.findBookingsByUser(principal.getUsername())
                .collectList()
                .map(bookings -> ResponseEntity.ok(GetUserBookingsResponse.builder()
                                .success(true)
                                .message("OK")
                                .bookings(bookings.stream().map(mapBookingToResponse).toList())
                        .build()));
    }

    @PostMapping(value = POST_BOOKING, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<BookingActionResponse>> performActionOnBooking(@RequestParam String bookingId,
                                                                              @RequestParam String action) {
        return bookingService.performActionOnBooking(bookingId, action)
                .map(booking -> switch (action) {
                    case "start" -> ResponseEntity.ok(StartBookingResponse.builder()
                                    .success(true)
                                    .message("OK")
                                    .startedTime(booking.getStartTime())
                            .build());
                    case "finish" -> ResponseEntity.ok(FinishBookingResponse.builder()
                                    .success(true)
                                    .message("OK")
                                    .totalMinutes(booking.getTotalMinutes())
                                    .paymentId(booking.getPayment().getId())
                            .build());
                    case "cancel" -> ResponseEntity.ok(CancelBookingResponse.builder()
                                    .success(true)
                                    .message("OK")
                                    .reason("CANCELED")
                            .build());
                    default -> ResponseEntity.badRequest().body(new BookingActionResponse(false, "Action invalid"));
                });
    }

    @PostMapping(value = POST_BOOKING, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<CreateBookingResponse>> createBooking(@AuthenticationPrincipal UserDetails principal,
                                                                     @RequestBody CreateBookingRequest request) {

        request.validate();

        BookingDto bookingDto = BookingDto.builder()
                .spot(SpotDto.builder().id(request.getSpotId()).build())
                .payment(PaymentDto.builder().method(request.getPaymentMethod()).currency(request.getCurrency()).build())
                .build();


        return bookingService.createBooking(principal.getUsername(), bookingDto)
                .map(bookingId -> new ResponseEntity<>(CreateBookingResponse.builder()
                        .success(true)
                        .message("OK")
                        .id(bookingId)
                        .build(), HttpStatus.CREATED));
    }

    private final Function<BookingDto, BookingResponse> mapBookingToResponse = booking -> BookingResponse.builder()
            .id(booking.getId())
            .spotId(booking.getSpot().getId())
            .paymentId(booking.getPayment().getId())
            .state(booking.getState())
            .startDate(TimeZoneUtils.representationDateTimeUTCToZone(booking.getStartDate(),
                    representationTimeZone))
            .startTime(TimeZoneUtils.representationTimeUTCToZone(LocalTime.parse(booking.getStartTime()),
                    representationTimeZone))
            .endDate(TimeZoneUtils.representationDateTimeUTCToZone(booking.getEndDate(),
                    representationTimeZone))
            .endTime(TimeZoneUtils.representationTimeUTCToZone(booking.getEndTime() == null ? null :
                            LocalTime.parse(booking.getEndTime()),
                    representationTimeZone))
            .totalMinutes(booking.getTotalMinutes())
            .userAs(booking.getBookingUserAs())
            .build();
}
