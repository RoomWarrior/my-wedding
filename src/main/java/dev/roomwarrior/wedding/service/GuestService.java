package dev.roomwarrior.wedding.service;

import dev.roomwarrior.wedding.enums.AttendingEnum;
import dev.roomwarrior.wedding.enums.RelationType;
import dev.roomwarrior.wedding.model.Guest;
import dev.roomwarrior.wedding.model.GuestResponseModel;
import dev.roomwarrior.wedding.model.GuestSummaryInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final JsonFileService jsonFileService;

    public void saveGuest(Guest guest) {
        List<Guest> guests = jsonFileService.loadData(Guest.class);
        
        if (guest.getId() == null) {
            Long newId = guests.stream()
                    .mapToLong(Guest::getId)
                    .max()
                    .orElse(0L) + 1;
            guest.setId(newId);
            guest.setCts(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        
        guests.removeIf(g -> g.getId().equals(guest.getId()));
        guests.add(guest);
        
        jsonFileService.saveData(guests);
    }

    public List<Guest> getAllGuests() {
        return jsonFileService.loadData(Guest.class);
    }

    public GuestSummaryInfo guestSummaryInfo() {
        List<Guest> guests = getAllGuests();

        List<GuestResponseModel> notAttending = new ArrayList<>();
        List<GuestResponseModel> friends = new ArrayList<>();
        List<GuestResponseModel> family = new ArrayList<>();
        List<GuestResponseModel> colleagues = new ArrayList<>();

        int attendingCount = 0;
        int needTransportCount = 0;

        for (Guest guest : guests) {
            GuestResponseModel responseModel = guest.toResponseModel();
            AttendingEnum attending = guest.getAttending();

            if (AttendingEnum.YES.equals(attending)) {
                attendingCount++;
                needTransportCount += responseModel.getNeedTransport() != null && responseModel.getNeedTransport() ? 1 : 0;
            } else if (AttendingEnum.PLUS_ONE.equals(attending)) {
                attendingCount += 2;
                needTransportCount += responseModel.getNeedTransport() != null && responseModel.getNeedTransport() ? 2 : 0;
            } else {
                notAttending.add(responseModel);
            }
            categorizeGuest(guest.getRelationType(), responseModel, friends, family, colleagues);
        }

        int totalGuests = attendingCount + notAttending.size();

        return GuestSummaryInfo.builder()
                .attendingGuests(attendingCount)
                .notAttendingGuests(notAttending.size())
                .totalGuests(totalGuests)
                .needTransportCount(needTransportCount)
                .friends(friends)
                .family(family)
                .colleagues(colleagues)
                .wontCome(notAttending)
                .build();
    }

    private void categorizeGuest(RelationType relationType, GuestResponseModel model,
                                 List<GuestResponseModel> friends,
                                 List<GuestResponseModel> family,
                                 List<GuestResponseModel> colleagues) {
        switch (relationType) {
            case FRIEND:
                friends.add(model);
                break;
            case RELATIVE:
                family.add(model);
                break;
            case COLLEAGUE:
                colleagues.add(model);
                break;
            default:
                break;
        }
    }
} 
