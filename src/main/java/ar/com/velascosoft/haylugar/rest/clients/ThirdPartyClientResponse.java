package ar.com.velascosoft.haylugar.rest.clients;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatusCode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThirdPartyClientResponse<T> {
    private HttpStatusCode statusCode;
    private boolean success;
    private T data;
}
