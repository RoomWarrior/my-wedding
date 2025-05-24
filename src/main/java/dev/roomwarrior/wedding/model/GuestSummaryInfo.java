package dev.roomwarrior.wedding.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GuestSummaryInfo {
    private int totalGuests;
    private int attendingGuests;
    private int notAttendingGuests;
    private int needTransportCount;
    private List<GuestResponseModel> friends;
    private List<GuestResponseModel> family;
    private List<GuestResponseModel> colleagues;
    private List<GuestResponseModel> wontCome;
}
