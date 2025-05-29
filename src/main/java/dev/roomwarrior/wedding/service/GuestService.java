package dev.roomwarrior.wedding.service;

import dev.roomwarrior.wedding.enums.AttendingEnum;
import dev.roomwarrior.wedding.enums.RelationType;
import dev.roomwarrior.wedding.model.Guest;
import dev.roomwarrior.wedding.model.GuestResponseModel;
import dev.roomwarrior.wedding.model.GuestSummaryInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final JsonFileService jsonFileService;

    private static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public synchronized void saveGuest(Guest guest) {
        List<Guest> guests = getAllGuests();

        if (guest.getId() == null) {
            initializeNewGuest(guest, guests);
        }

        guests.removeIf(existingGuest -> hasSameName(guest, existingGuest));
        guests.add(guest);
        jsonFileService.saveData(guests);
    }

    private void initializeNewGuest(Guest guest, List<Guest> guests) {
        Long newId = guests.stream()
                .mapToLong(Guest::getId)
                .max()
                .orElse(0L) + 1;
        guest.setId(newId);
        guest.setCts(LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN)));
    }

    private boolean hasSameName(Guest newGuest, Guest existingGuest) {
        String newGuestName = newGuest.getName();
        String existingName = existingGuest.getName();

        if (newGuestName == null || existingName == null) {
            return false;
        }

        Set<String> newGuestNameParts = splitNameIntoWords(newGuestName);
        Set<String> existingNameParts = splitNameIntoWords(existingName);

        return !newGuestNameParts.isEmpty() && newGuestNameParts.equals(existingNameParts);
    }

    private Set<String> splitNameIntoWords(String name) {
        return new HashSet<>(Arrays.asList(name.toLowerCase().trim().split("\\s+")));
    }

    public List<Guest> getAllGuests() {
        return jsonFileService.loadData(Guest.class);
    }

    public GuestSummaryInfo guestSummaryInfo() {
        List<Guest> guests = getAllGuests();
        GuestStatistics statistics = calculateGuestStatistics(guests);

        Map<RelationType, List<GuestResponseModel>> categorizedGuests = guests.stream()
                .map(Guest::toResponseModel)
                .collect(Collectors.groupingBy(
                        GuestResponseModel::getRelationType,
                        Collectors.toList()
                ));

        return GuestSummaryInfo.builder()
                .attendingGuests(statistics.getAttendingCount())
                .notAttendingGuests(statistics.getNotAttendingCount())
                .totalGuests(statistics.getTotalGuests())
                .needTransportCount(statistics.getNeedTransportCount())
                .friends(categorizedGuests.getOrDefault(RelationType.FRIEND, Collections.emptyList()))
                .family(categorizedGuests.getOrDefault(RelationType.RELATIVE, Collections.emptyList()))
                .colleagues(categorizedGuests.getOrDefault(RelationType.COLLEAGUE, Collections.emptyList()))
                .wontCome(statistics.getNotAttendingGuests())
                .build();
    }

    @Data
    @AllArgsConstructor
    private static class GuestStatistics {
        private int attendingCount;
        private int needTransportCount;
        private List<GuestResponseModel> notAttendingGuests;

        public int getNotAttendingCount() {
            return notAttendingGuests.size();
        }

        public int getTotalGuests() {
            return attendingCount + getNotAttendingCount();
        }
    }

    private GuestStatistics calculateGuestStatistics(List<Guest> guests) {
        List<GuestResponseModel> notAttending = new ArrayList<>();
        int attendingCount = 0;
        int needTransportCount = 0;

        for (Guest guest : guests) {
            GuestResponseModel response = guest.toResponseModel();
            AttendingEnum attending = guest.getAttending();

            if (attending == AttendingEnum.NO) {
                notAttending.add(response);
                continue;
            }

            int guestCount = attending == AttendingEnum.PLUS_ONE ? 2 : 1;
            attendingCount += guestCount;

            if (Boolean.TRUE.equals(response.getNeedTransport())) {
                needTransportCount += guestCount;
            }
        }

        return new GuestStatistics(attendingCount, needTransportCount, notAttending);
    }
} 
