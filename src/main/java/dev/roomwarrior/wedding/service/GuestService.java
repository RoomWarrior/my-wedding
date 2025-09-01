package dev.roomwarrior.wedding.service;

import dev.roomwarrior.wedding.enums.AttendingEnum;
import dev.roomwarrior.wedding.enums.RelationType;
import dev.roomwarrior.wedding.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final JsonFileService jsonFileService;

    private static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final ZoneId APP_ZONE = ZoneId.of("UTC+06:00");

    public synchronized void saveGuest(GuestModel guest) {
        List<GuestModel> guests = getAllGuests();

        if (guest.getId() == null) {
            initializeNewGuest(guest, guests);
        }

        guests.removeIf(existingGuest -> hasSameName(guest, existingGuest) || existingGuest.getName() == null);
        guests.add(guest);
        jsonFileService.saveData(guests);
    }

    public synchronized void initNewGuests(List<GuestModel> guestsModels) {
        List<GuestModel> guests = getAllGuests();
        long maxId = guests.stream()
                .mapToLong(GuestModel::getId)
                .max()
                .orElse(0L);

        String timestamp = LocalDateTime.now(APP_ZONE).format(DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN));

        List<GuestModel> validGuests = new ArrayList<>();

        for (GuestModel guest : guestsModels) {
            if (guest.getName() != null && guest.getAttending() != null && guest.getRelationType() != null) {
                guest.setId(++maxId);
                guest.setCts(timestamp);
                validGuests.add(guest);
            }
        }

        guests.addAll(validGuests);
        jsonFileService.saveData(guests);
    }

    private void initializeNewGuest(GuestModel guest, List<GuestModel> guests) {
        Long newId = guests.stream()
                .mapToLong(GuestModel::getId)
                .max()
                .orElse(0L) + 1;
        guest.setId(newId);
        guest.setCts(LocalDateTime.now(APP_ZONE).format(DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN)));
    }

    private boolean hasSameName(GuestModel newGuest, GuestModel existingGuest) {
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

    public List<GuestModel> getAllGuests() {
        return jsonFileService.loadData(GuestModel.class);
    }

    public GuestAdminInfo guestAdminInfo(String search, AttendingEnum attending) {
        List<GuestModel> guests = getAllGuests();
        GuestStatistics statistics = calculateGuestStatistics(guests);

        List<GuestModel> filteredGuests = guests.stream()
                .filter(guest -> search == null || search.isEmpty() ||
                        guest.getName().toLowerCase().contains(search.toLowerCase()) ||
                        (guest.getPlusOneName() != null && guest.getPlusOneName().toLowerCase().contains(search.toLowerCase())))
                .filter(guest -> attending == null || guest.getAttending() == attending)
                .toList();

        return GuestAdminInfo.builder()
                .totalGuests(statistics.getTotalGuests())
                .attendingGuests(statistics.getAttendingCount())
                .totalSize(guests.size())
                .guests(filteredGuests.stream().map(item ->
                                GuestIncludeModel.builder()
                                        .id(item.getId())
                                        .name(item.getName())
                                        .cts(item.getCts())
                                        .attending(item.getAttending())
                                        .plusOneName(item.getPlusOneName())
                                        .build())
                        .collect(Collectors.toList()))
                .build();

    }

    public GuestSummaryInfo guestSummaryInfo() {
        List<GuestModel> guests = getAllGuests();
        GuestStatistics statistics = calculateGuestStatistics(guests);

        Map<RelationType, List<GuestResponseModel>> categorizedGuests = guests.stream()
                .filter(g -> g.getAttending() == AttendingEnum.YES || g.getAttending() == AttendingEnum.PLUS_ONE)
                .flatMap(g -> {
                    List<GuestResponseModel> res = new ArrayList<>();
                    res.add(g.toResponseModel());
                    if (g.getAttending() == AttendingEnum.PLUS_ONE && g.getPlusOneName() != null && !g.getPlusOneName().isBlank()) {
                        GuestResponseModel plusOne = GuestResponseModel.builder()
                                .name(g.getPlusOneName())
                                .relationType(g.getRelationType())
                                .needTransport(g.getNeedsTransport())
                                .plusOneName(g.getName())
                                .build();
                        res.add(plusOne);
                    }
                    return res.stream();
                })
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

    private GuestStatistics calculateGuestStatistics(List<GuestModel> guests) {
        List<GuestResponseModel> notAttending = new ArrayList<>();
        int attendingCount = 0;
        int needTransportCount = 0;

        for (GuestModel guest : guests) {
            GuestResponseModel response = guest.toResponseModel();
            AttendingEnum attending = guest.getAttending();

            if (attending == null) {
                continue;
            }

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
