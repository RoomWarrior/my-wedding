package dev.roomwarrior.wedding.model;

import dev.roomwarrior.wedding.enums.AttendingEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GuestIncludeModel {
    private Long id;
    private String name;
    private String plusOneName;
    private AttendingEnum attending;
    private String cts;
}
