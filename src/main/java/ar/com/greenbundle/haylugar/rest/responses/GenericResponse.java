package ar.com.greenbundle.haylugar.rest.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class GenericResponse {
    private boolean success;
    private String message;
}
