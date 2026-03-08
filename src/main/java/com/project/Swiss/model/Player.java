package com.project.Swiss.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;


import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor

public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message="Name is mandatory")
    @Size(max = 50, message = "Max 50 characters")
    private String name;


    private Double score = 0.0;
    private Double buchholz = 0.0;
    private Boolean byeRecu = false;


    @ElementCollection
    private List<Long> listeAdversaires;

    @ElementCollection
    private List<String> listeCouleurs;

    @ManyToOne
    private Tournament tournament;
}
