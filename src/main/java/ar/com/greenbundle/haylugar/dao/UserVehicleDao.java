package ar.com.greenbundle.haylugar.dao;

import ar.com.greenbundle.haylugar.dto.UserVehicleDto;
import ar.com.greenbundle.haylugar.entities.UserVehicleEntity;
import ar.com.greenbundle.haylugar.repositories.UserVehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class UserVehicleDao {
    @Autowired
    private UserVehicleRepository vehicleRepository;

    public Mono<UserVehicleDto> getVehicle(String vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .filter(vehicle -> !vehicle.isDeleted())
                .map(vehicle -> UserVehicleDto.builderFromEntity(vehicle).build());
    }

    public Flux<UserVehicleDto> getVehiclesByUserId(String userId) {
        return vehicleRepository.findVehiclesByUserId(userId)
                .filter(vehicle -> !vehicle.isDeleted())
                .map(vehicle -> UserVehicleDto.builderFromEntity(vehicle).build());
    }

    public Mono<UserVehicleDto> saveVehicle(UserVehicleDto vehicleDto) {
        return vehicleRepository.save(UserVehicleDto.mapToEntity(vehicleDto))
                .map(vehicle -> UserVehicleDto.builderFromEntity(vehicle).build());
    }

    public Mono<Void> deleteVehicle(UserVehicleDto vehicleDto) {
        UserVehicleEntity entity = UserVehicleDto.mapToEntity(vehicleDto);
        entity.setDeleted(true);

        return vehicleRepository.save(entity)
                .then(Mono.empty());
    }
}
