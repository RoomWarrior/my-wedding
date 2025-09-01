package dev.roomwarrior.wedding.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GuestAdminInfo {

    private int totalGuests;
    private int totalSize;
    private int attendingGuests;
    private List<GuestIncludeModel> guests;
}
