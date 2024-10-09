package ar.com.greenbundle.haylugar.rest.clients;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatusCode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThirdPartyClientResponse {
    private HttpStatusCode statusCode;
    private boolean success;
    private Object data;
}
