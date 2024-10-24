package ar.com.greenbundle.haylugar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class EntityDto {
    private String id;
    private LocalDateTime createdAt;
    private Long version;
}
