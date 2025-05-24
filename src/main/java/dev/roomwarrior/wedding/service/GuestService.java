package dev.roomwarrior.wedding.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import dev.roomwarrior.wedding.enums.AttendingEnum;
import dev.roomwarrior.wedding.model.Guest;
import dev.roomwarrior.wedding.model.GuestDto;
import dev.roomwarrior.wedding.model.GuestResponseModel;
import dev.roomwarrior.wedding.model.GuestSummaryInfo;
import dev.roomwarrior.wedding.repository.GuestRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;

    public GuestDto saveGuest(GuestDto guestDto) {
        return guestRepository.save(guestDto.toEntity()).toDto();
    }

    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }

    public GuestSummaryInfo guestSummaryInfo() {
        List<Guest> guests = guestRepository.findAll();

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
                categorizeGuest(guest, responseModel, friends, family, colleagues);

            } else if (AttendingEnum.PLUS_ONE.equals(attending)) {
                attendingCount += 2;
                needTransportCount += responseModel.getNeedTransport() != null && responseModel.getNeedTransport() ? 2 : 0;
                categorizeGuest(guest, responseModel, friends, family, colleagues);

            } else {
                notAttending.add(responseModel);
            }
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

    private void categorizeGuest(Guest guest, GuestResponseModel model,
                                 List<GuestResponseModel> friends,
                                 List<GuestResponseModel> family,
                                 List<GuestResponseModel> colleagues) {
        switch (guest.getRelationType()) {
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
