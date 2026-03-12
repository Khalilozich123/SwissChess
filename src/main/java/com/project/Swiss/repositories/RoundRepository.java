package com.project.Swiss.repositories;


import com.project.Swiss.model.Round;
import com.project.Swiss.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoundRepository extends JpaRepository<Round, Long> {
    // Find all rounds of a tournament
    List<Round> findByTournament(Tournament tournament);
    // Find a specific round by its number in a tournament
    Round findByTournamentAndNumber(Tournament tournament, Integer number);
    // Find the current active round

    Optional<Round> findByTournamentAndStatut(Tournament tournament, Round.RoundStatus statut);

    long countByTournament(Tournament tournament);

}
