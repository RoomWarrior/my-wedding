package dev.roomwarrior.wedding.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.roomwarrior.wedding.enums.AttendingEnum;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GuestAdminInfo {

    private int totalGuests;
    private int attendingGuests;
    private List<GuestIncludeModel> guests;
}
