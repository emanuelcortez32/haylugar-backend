package ar.com.greenbundle.haylugar.rest.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class ApiResponse {
    private boolean success;
    private String message;
    protected Map<String, ?> data;

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
