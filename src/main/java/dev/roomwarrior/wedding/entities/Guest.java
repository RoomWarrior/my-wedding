package dev.roomwarrior.wedding.entities;

import dev.roomwarrior.wedding.enums.AttendingEnum;
import dev.roomwarrior.wedding.enums.RelationType;
import dev.roomwarrior.wedding.model.GuestModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "guests")
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Enumerated(EnumType.STRING)
    private AttendingEnum attending;
    @Column(name = "message", columnDefinition = "text")
    private String message;
    @Column(name = "plus_one_name")
    private String plusOneName;
    @Column(name = "needs_transport")
    private Boolean needsTransport;

    @Enumerated(EnumType.STRING)
    @Column(name = "relation_type")
    private RelationType relationType;
    private LocalDateTime cts;

    @PrePersist
    public void prePersist() {
        cts = LocalDateTime.now();
    }

    public GuestModel toModel() {
        return GuestModel.builder()
                .id(id)
                .name(name)
                .attending(attending)
                .message(message)
                .plusOneName(plusOneName)
                .needsTransport(needsTransport)
                .relationType(relationType)
                .cts(cts != null ? cts.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
                .build();
    }
}
