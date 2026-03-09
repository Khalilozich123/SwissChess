package com.project.Swiss.repositories;


import com.project.Swiss.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    // Search tournament by name
    List<Tournament> findByNameContainingIgnoreCase(String keyword);
    // Find tournaments by status
    List<Tournament> findByStatut(Tournament.TournamentStatus statut);
}
