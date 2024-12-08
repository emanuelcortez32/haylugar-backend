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
                .map(vehicle -> new UserVehicleDto().dtoFromEntity(vehicle));
    }

    public Flux<UserVehicleDto> getVehiclesByUserId(String userId) {
        return vehicleRepository.findVehiclesByUserId(userId)
                .filter(vehicle -> !vehicle.isDeleted())
                .map(vehicle -> new UserVehicleDto().dtoFromEntity(vehicle));
    }

    public Mono<UserVehicleDto> saveVehicle(UserVehicleDto vehicleDto) {
        return vehicleRepository.save(new UserVehicleDto().dtoToEntity(vehicleDto))
                .map(vehicle -> new UserVehicleDto().dtoFromEntity(vehicle));
    }

    public Mono<Void> deleteVehicle(UserVehicleDto vehicleDto) {
        UserVehicleEntity entity = new UserVehicleDto().dtoToEntity(vehicleDto);
        entity.setDeleted(true);

        return vehicleRepository.save(entity)
                .then(Mono.empty());
    }
}
