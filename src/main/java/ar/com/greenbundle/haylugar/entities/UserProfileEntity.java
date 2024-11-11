package ar.com.greenbundle.haylugar.entities;

import ar.com.greenbundle.haylugar.pojo.constants.Gender;
import ar.com.greenbundle.haylugar.pojo.constants.Nationality;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("user_profiles")
public class UserProfileEntity extends GenericEntity {
    @Column("user_id")
    private String userId;
    private String name;
    private String surname;
    private String dni;
    private Gender gender;
    @Column("birth_date")
    private String birthDate;
    private Nationality nationality;

    @Builder
    public UserProfileEntity(String id, LocalDateTime createdAt, LocalDateTime updatedAt, Long version, boolean deleted, String userId, String name, String surname, String dni, Gender gender, String birthDate, Nationality nationality) {
        super(id, createdAt, updatedAt, version, deleted);
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.dni = dni;
        this.gender = gender;
        this.birthDate = birthDate;
        this.nationality = nationality;
    }
}
