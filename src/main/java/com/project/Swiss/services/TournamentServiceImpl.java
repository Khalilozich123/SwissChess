package com.project.Swiss.services;

import com.project.Swiss.model.Tournament;
import com.project.Swiss.repositories.TournamentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TournamentServiceImpl implements ITournamentService {

    private final TournamentRepository tournamentRepo;

    public TournamentServiceImpl(TournamentRepository tournamentRepo) {
        this.tournamentRepo = tournamentRepo;
    }

    @Override
    public void createTournament(Tournament tournament) {
        // Status and actualRound already have default values in the model
        // (SETUP and 0), so we just save directly
        tournamentRepo.save(tournament);
    }

    @Override
    public void updateTournament(Tournament tournament) {
        // save() in JPA does both INSERT and UPDATE
        // If the tournament has an id → UPDATE
        // If no id → INSERT
        tournamentRepo.save(tournament);
    }

    @Override
    public void deleteTournament(Long tId) {
        // Check it exists before deleting to give a clear error message
        if (!tournamentRepo.existsById(tId)) {
            throw new RuntimeException("Tournament not found with id : " + tId);
        }
        tournamentRepo.deleteById(tId);
    }

    @Override
    public Tournament getTournament(Long tId) {
        // orElseThrow() throws an exception if not found
        // instead of returning null which would cause NullPointerException later
        return tournamentRepo.findById(tId)
                .orElseThrow(() -> new RuntimeException("Tournament not found with id : " + tId));
    }

    @Override
    public List<Tournament> getAllTournaments() {
        return tournamentRepo.findAll();
    }

    @Override
    public void startTournament(Long tId) {
        Tournament tournament = tournamentRepo.findById(tId)
                .orElseThrow(() -> new RuntimeException("Tournament not found with id : " + tId));

        // Cannot start a tournament that is already in progress or finished
        if (tournament.getStatut() != Tournament.TournamentStatus.SETUP) {
            throw new RuntimeException("Tournament can only be started from SETUP status");
        }

        // Cannot start with less than 2 players
        if (tournament.getPlayers() == null || tournament.getPlayers().size() < 2) {
            throw new RuntimeException("Tournament needs at least 2 players to start");
        }

        tournament.setStatut(Tournament.TournamentStatus.IN_PROGRESS);
        tournament.setActualRound(1);
        tournamentRepo.save(tournament);
    }

    @Override
    public void finishTournament(Long tId) {
        Tournament tournament = tournamentRepo.findById(tId)
                .orElseThrow(() -> new RuntimeException("Tournament not found with id : " + tId));

        // Can only finish a tournament that is in progress
        if (tournament.getStatut() != Tournament.TournamentStatus.IN_PROGRESS) {
            throw new RuntimeException("Only an IN_PROGRESS tournament can be finished");
        }

        tournament.setStatut(Tournament.TournamentStatus.COMPLETED);
        tournamentRepo.save(tournament);
    }
}