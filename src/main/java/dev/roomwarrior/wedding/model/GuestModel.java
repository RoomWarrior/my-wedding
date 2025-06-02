package dev.roomwarrior.wedding.model;

import dev.roomwarrior.wedding.enums.AttendingEnum;
import dev.roomwarrior.wedding.enums.RelationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuestModel {
    private Long id;
    @NotBlank
    private String name;
    @NotNull
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
