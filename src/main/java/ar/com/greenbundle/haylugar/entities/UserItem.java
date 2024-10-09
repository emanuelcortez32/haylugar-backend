package ar.com.greenbundle.haylugar.entities;

import ar.com.greenbundle.haylugar.dto.UserProfile;
import ar.com.greenbundle.haylugar.dto.constants.UserRole;
import ar.com.greenbundle.haylugar.dto.constants.UserState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ar.com.greenbundle.haylugar.dto.constants.UserRole.ROLE_USER;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Document("users")
public class UserItem extends GenericItem {
    @Indexed(unique = true)
    private String email;
    private String passwordHash;
    private String passwordSalt;
    private UserState state;
    private UserProfile profile;
    @Builder.Default
    private List<UserRole> roles = new ArrayList<>(Collections.singletonList(ROLE_USER));
}
