package dev.roomwarrior.wedding.model;

import dev.roomwarrior.wedding.enums.AttendingEnum;
import dev.roomwarrior.wedding.enums.RelationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Guest {
    private Long id;
    
    private String name;
    private AttendingEnum attending;
    private String message;
    private String plusOneName;
    private Boolean needsTransport;
    
    private RelationType relationType;

    private String cts;

    public GuestResponseModel toResponseModel() {
        return GuestResponseModel.builder()
                .name(name)
                .needTransport(needsTransport)
                .plusOneName(plusOneName)
                .relationType(relationType)
                .build();
    }
} 
