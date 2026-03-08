package com.project.Swiss.model;

import jakarta.persistence.*;
import lombok.*;


import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer number;


    public enum RoundStatus {
        PREVIEW, ACTIVE, COMPLETED
    }
    private RoundStatus statut;

    @OneToMany(mappedBy = "round")
    private List<Match> matches;

    @ManyToOne
    private Tournament tournament;

}
