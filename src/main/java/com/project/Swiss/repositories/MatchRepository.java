package com.project.Swiss.repositories;

import com.project.Swiss.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    // Find all matches of a round
    List<Match> findByRound(Round round);
    // Find all matches of a tournament
    List<Match> findByTournament(Tournament tournament);
    // Find matches involving a specific player
    List<Match> findByWhiteOrBlack(Player white, Player black);
}
