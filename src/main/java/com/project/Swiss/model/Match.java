package com.project.Swiss.model;

import jakarta.persistence.*;
import lombok.*;



@Entity
@Getter
@Setter
@NoArgsConstructor
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Player white;

    @ManyToOne(optional = true)
    private Player black;

    public enum MatchResult {
        WHITE, BLACK, DRAW, BYE, PENDING
    }

    private MatchResult resultat;

    @ManyToOne
    private Round round;

    @ManyToOne
    private Tournament tournament;
}
