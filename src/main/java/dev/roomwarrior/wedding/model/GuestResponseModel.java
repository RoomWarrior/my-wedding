package dev.roomwarrior.wedding.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import dev.roomwarrior.wedding.enums.RelationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GuestResponseModel {
    private String name;
    private String plusOneName;
    private Boolean needTransport;
    private RelationType relationType;
}
