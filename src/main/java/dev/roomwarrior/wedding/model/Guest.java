package dev.roomwarrior.wedding.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import dev.roomwarrior.wedding.enums.AttendingEnum;
import dev.roomwarrior.wedding.enums.RelationType;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    @Enumerated(EnumType.STRING)
    private AttendingEnum attending;
    private String message;
    private String plusOneName;
    private Boolean needsTransport;
    
    @Enumerated(EnumType.STRING)
    private RelationType relationType;

    public GuestDto toDto() {
        return GuestDto.builder()
        .id(id)
        .name(name)
        .attending(attending)
        .plusOneName(plusOneName)
        .needsTransport(needsTransport)
        .relationType(relationType)
        .build();
    }

    public GuestResponseModel toResponseModel() {
        return GuestResponseModel.builder()
                .name(name)
                .needTransport(needsTransport)
                .plusOneName(plusOneName)
                .build();
    }
} 
