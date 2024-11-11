package ar.com.greenbundle.haylugar.service;

import ar.com.greenbundle.haylugar.dao.UserVehicleDao;
import ar.com.greenbundle.haylugar.dto.UserDto;
import ar.com.greenbundle.haylugar.dto.UserVehicleDto;
import ar.com.greenbundle.haylugar.exceptions.ResourceNotFoundException;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;

@Service
public class VehicleService {
    @Autowired
    private UserVehicleDao vehicleDao;

    @Autowired
    private BeanUtilsBean utilsBean;

    public Mono<UserVehicleDto> findVehicleByUser(String userId, String vehicleId) {
        return vehicleDao.getVehicle(vehicleId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Vehicle Not Found")))
                .flatMap(vehicle -> {
                    if(!vehicle.getUser().getId().equals(userId))
                        return Mono.error(new ResourceNotFoundException("Vehicle Not Found"));

                    return Mono.just(vehicle);
                });
    }

    public Flux<UserVehicleDto> findVehiclesByUser(String userId) {
        return vehicleDao.getVehiclesByUserId(userId);
    }

    public Mono<String> createVehicleForUser(String userId, UserVehicleDto vehicleDto) {
        vehicleDto.setUser(UserDto.builder().id(userId).build());

        return vehicleDao.saveVehicle(vehicleDto)
                .map(UserVehicleDto::getId);
    }

    public Mono<String> updateVehicle(UserVehicleDto vehicleDto) {
        return vehicleDao.getVehicle(vehicleDto.getId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Vehicle Not Found")))
                .flatMap(vehicle -> {
                    if(!vehicle.getUser().getId().equals(vehicleDto.getUser().getId()))
                        return Mono.error(new ResourceNotFoundException("Vehicle Not Found"));

                    try {
                        utilsBean.copyProperties(vehicleDto, vehicle);

                        return vehicleDao.saveVehicle(vehicleDto).map(UserVehicleDto::getId);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        return Mono.error(new RuntimeException(e));
                    }
                });
    }

    public Mono<Void> deleteVehicle(UserVehicleDto vehicleDto) {
        return vehicleDao.getVehicle(vehicleDto.getId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Vehicle Not Found")))
                .flatMap(vehicle -> {
                    if(!vehicle.getUser().getId().equals(vehicleDto.getUser().getId()))
                        throw new ResourceNotFoundException("Vehicle Not Found");

                    try {
                        utilsBean.copyProperties(vehicleDto, vehicle);

                        return vehicleDao.deleteVehicle(vehicleDto);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
