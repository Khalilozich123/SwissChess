package com.project.Swiss.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required for this tournament")
    private String name;

    public enum TournamentStatus {
        SETUP, IN_PROGRESS, COMPLETED
    }
    private TournamentStatus statut = TournamentStatus.SETUP;
    private Integer actualRound = 0;

    @Positive
    private Integer rounds;

    @OneToMany(mappedBy = "tournament")
    private List<Player> players;

}
