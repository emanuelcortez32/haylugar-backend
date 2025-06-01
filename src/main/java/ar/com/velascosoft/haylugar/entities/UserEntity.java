package ar.com.velascosoft.haylugar.entities;

import ar.com.velascosoft.haylugar.pojo.constants.UserRole;
import ar.com.velascosoft.haylugar.pojo.constants.UserState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("users")
public class UserEntity extends GenericEntity {
    private String email;
    private String password;
    private UserState state;
    private List<UserRole> roles;
    @Builder
    public UserEntity(String id, LocalDateTime createdAt, LocalDateTime updatedAt, Long version, boolean deleted, String email, String password, UserState state, List<UserRole> roles) {
        super(id, createdAt, updatedAt, version, deleted);
        this.email = email;
        this.password = password;
        this.state = state;
        this.roles = roles;
    }
}
