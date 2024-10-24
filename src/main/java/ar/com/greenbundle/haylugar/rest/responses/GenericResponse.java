package ar.com.greenbundle.haylugar.rest.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
abstract class GenericResponse {
    private boolean success;
    private String message;
    protected Map<String, ?> data;

    public GenericResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
