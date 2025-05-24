package dev.roomwarrior.wedding.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GuestSummaryInfo {
    private long totalGuests;
    private long attendingGuests;
    private long notAttendingGuests;
    private List<GuestResponseModel> friends;
    private List<GuestResponseModel> family;
    private List<GuestResponseModel> colleagues;
}
