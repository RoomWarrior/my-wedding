package dev.roomwarrior.wedding.model;

import dev.roomwarrior.wedding.entities.Guest;
import dev.roomwarrior.wedding.enums.AttendingEnum;
import dev.roomwarrior.wedding.enums.RelationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuestModel {
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

    public Guest toEntity() {
        return Guest.builder()
                .id(id)
                .name(name)
                .attending(attending)
                .message(message)
                .plusOneName(plusOneName)
                .needsTransport(needsTransport)
                .relationType(relationType)
                .cts(
                        cts != null && !cts.isBlank()
                                ? LocalDateTime.parse(cts, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                : null
                )
                .build();
    }
}
