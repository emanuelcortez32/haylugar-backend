package ar.com.greenbundle.haylugar.rest.controllers;

import ar.com.greenbundle.haylugar.dto.UserDto;
import ar.com.greenbundle.haylugar.dto.UserVehicleDto;
import ar.com.greenbundle.haylugar.rest.requests.CreateVehicleRequest;
import ar.com.greenbundle.haylugar.rest.requests.UpdateVehicleRequest;
import ar.com.greenbundle.haylugar.rest.responses.ApiResponse;
import ar.com.greenbundle.haylugar.rest.responses.CreateVehicleResponse;
import ar.com.greenbundle.haylugar.rest.responses.GetUserVehicleResponse;
import ar.com.greenbundle.haylugar.rest.responses.GetUserVehiclesResponse;
import ar.com.greenbundle.haylugar.rest.responses.VehicleResponse;
import ar.com.greenbundle.haylugar.service.VehicleService;
import ar.com.greenbundle.haylugar.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static ar.com.greenbundle.haylugar.rest.endpoints.ControllerEndpoints.MeEndpoints.VehicleEndpoints.DELETE_USER_VEHICLE;
import static ar.com.greenbundle.haylugar.rest.endpoints.ControllerEndpoints.MeEndpoints.VehicleEndpoints.GET_USER_VEHICLES;
import static ar.com.greenbundle.haylugar.rest.endpoints.ControllerEndpoints.MeEndpoints.VehicleEndpoints.POST_USER_VEHICLE;
import static ar.com.greenbundle.haylugar.rest.endpoints.ControllerEndpoints.MeEndpoints.VehicleEndpoints.PUT_USER_VEHICLE;

@RestController
@RequestMapping("/api")
public class VehicleController {
    @Autowired
    private VehicleService vehicleService;

    @GetMapping(value = GET_USER_VEHICLES, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ApiResponse>> getVehicles(@AuthenticationPrincipal UserDetails principal,
                                                         @RequestParam(required = false) String vehicleId) {

        if(!StringUtils.isNullOrEmpty(vehicleId))
            return vehicleService.findVehicleByUser(principal.getUsername(), vehicleId)
                    .map(mapVehicleToResponse)
                    .map(vehicleResponse -> ResponseEntity.ok(GetUserVehicleResponse.builder()
                                    .success(true)
                                    .message("OK")
                                    .vehicleResponse(vehicleResponse)
                            .build()));

        return vehicleService.findVehiclesByUser(principal.getUsername())
                .collectList()
                .map(vehicles -> ResponseEntity.ok(GetUserVehiclesResponse.builder()
                                .success(true)
                                .message("OK")
                                .vehicles(vehicles.stream().map(mapVehicleToResponse).toList())
                        .build()));
    }

    @PostMapping(value = POST_USER_VEHICLE, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ApiResponse>> createVehicle(@AuthenticationPrincipal UserDetails principal,
                                                           @RequestBody CreateVehicleRequest request) {

        request.validate();

        UserVehicleDto vehicleDto = UserVehicleDto.builder()
                .brand(request.getBrand())
                .model(request.getModel())
                .year(request.getYear())
                .patent(request.getPatent())
                .size(request.getSize())
                .type(request.getType())
                .build();

        return vehicleService.createVehicleForUser(principal.getUsername(), vehicleDto)
                .map(vehicleId -> new ResponseEntity<>(CreateVehicleResponse.builder()
                        .id(vehicleId)
                        .message("OK")
                        .success(true)
                        .build(), HttpStatus.CREATED));
    }

    @PutMapping(value = PUT_USER_VEHICLE, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ApiResponse>> updateVehicle(@AuthenticationPrincipal UserDetails principal,
                                                           @RequestParam String vehicleId,
                                                           @RequestBody UpdateVehicleRequest request) {

        UserVehicleDto vehicle = UserVehicleDto.builder()
                .id(vehicleId)
                .user(UserDto.builder().id(principal.getUsername()).build())
                .brand(request.getBrand())
                .model(request.getModel())
                .year(request.getYear())
                .type(request.getType())
                .size(request.getSize())
                .patent(request.getPatent())
                .build();

        return vehicleService.updateVehicle(vehicle)
                .map(id -> ResponseEntity.ok(CreateVehicleResponse.builder()
                                .success(true)
                                .message("OK")
                                .id(id)
                        .build()));
    }

    @DeleteMapping(value = DELETE_USER_VEHICLE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Void>> deleteVehicle(@AuthenticationPrincipal UserDetails principal,
                                                    @RequestParam String vehicleId) {

        UserVehicleDto vehicle = UserVehicleDto.builder()
                .id(vehicleId)
                .user(UserDto.builder().id(principal.getUsername()).build())
                .build();

        return vehicleService.deleteVehicle(vehicle)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    private final Function<UserVehicleDto, VehicleResponse> mapVehicleToResponse = vehicle -> VehicleResponse.builder()
            .id(vehicle.getId())
            .brand(vehicle.getBrand())
            .model(vehicle.getModel())
            .year(vehicle.getYear())
            .patent(vehicle.getPatent())
            .type(vehicle.getType())
            .size(vehicle.getSize())
            .build();
}
