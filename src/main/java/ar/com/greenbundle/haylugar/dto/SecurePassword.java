package ar.com.greenbundle.haylugar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurePassword {
    private byte[] salt;
    private byte[] hash;
}
