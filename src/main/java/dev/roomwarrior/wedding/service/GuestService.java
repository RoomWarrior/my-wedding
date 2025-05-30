package dev.roomwarrior.wedding.service;

import dev.roomwarrior.wedding.entities.Guest;
import dev.roomwarrior.wedding.enums.AttendingEnum;
import dev.roomwarrior.wedding.enums.RelationType;
import dev.roomwarrior.wedding.model.GuestModel;
import dev.roomwarrior.wedding.model.GuestResponseModel;
import dev.roomwarrior.wedding.model.GuestSummaryInfo;
import dev.roomwarrior.wedding.repos.GuestRepo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepo guestRepo;

    @Transactional
    public void saveGuest(GuestModel guestModel) {
        List<Guest> toDelete = getAllGuests().stream()
                .filter(existing -> hasSameName(guestModel, existing))
                .map(GuestModel::toEntity)
                .toList();

        if (!toDelete.isEmpty())
            guestRepo.deleteAll(toDelete);

        guestRepo.save(guestModel.toEntity());
    }

    private boolean hasSameName(GuestModel newGuestModel, GuestModel existingGuestModel) {
        String newGuestName = newGuestModel.getName();
        String existingName = existingGuestModel.getName();

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
        return guestRepo.findAll().stream().map(Guest::toModel).toList();
    }

    public GuestSummaryInfo guestSummaryInfo() {
        List<GuestModel> guestModels = getAllGuests();
        GuestStatistics statistics = calculateGuestStatistics(guestModels);

        Map<RelationType, List<GuestResponseModel>> categorizedGuests = guestModels.stream()
                .map(GuestModel::toResponseModel)
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

    private GuestStatistics calculateGuestStatistics(List<GuestModel> guestModels) {
        List<GuestResponseModel> notAttending = new ArrayList<>();
        int attendingCount = 0;
        int needTransportCount = 0;

        for (GuestModel guestModel : guestModels) {
            GuestResponseModel response = guestModel.toResponseModel();
            AttendingEnum attending = guestModel.getAttending();

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
