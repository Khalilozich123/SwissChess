package com.project.Swiss.repositories;

import com.project.Swiss.model.Player;
import com.project.Swiss.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    // Find all players of a specific tournament
    List<Player> findByTournament(Tournament tournament);
    // Find players by tournament ordered by score descending (for standings)
    List<Player> findByTournamentOrderByScoreDesc(Tournament tournament);

    List<Player> findByNameContainingIgnoreCaseAndTournament(String name, Tournament tournament);

}
