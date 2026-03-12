package com.project.Swiss.services;

import com.project.Swiss.model.Match;
import com.project.Swiss.model.Round;
import com.project.Swiss.model.Tournament;
import com.project.Swiss.repositories.RoundRepository;
import com.project.Swiss.repositories.TournamentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoundServiceImpl implements IRoundService {

    private final RoundRepository roundRepo;
    private final TournamentRepository tournamentRepo;

    public RoundServiceImpl(RoundRepository roundRepo,
                            TournamentRepository tournamentRepo) {
        this.roundRepo = roundRepo;
        this.tournamentRepo = tournamentRepo;
    }

    @Override
    public void addRound(Long tId) {
        Tournament tournament = tournamentRepo.findById(tId)
                .orElseThrow(() -> new RuntimeException("Tournament not found : " + tId));

        // Can only add rounds to an IN_PROGRESS tournament
        if (tournament.getStatut() != Tournament.TournamentStatus.IN_PROGRESS) {
            throw new RuntimeException("Cannot add a round to a tournament that is not IN_PROGRESS");
        }

        // Cannot exceed the total number of rounds defined
        long existingRounds = roundRepo.countByTournament(tournament);
        if (existingRounds >= tournament.getRounds()) {
            throw new RuntimeException("Maximum number of rounds already reached");
        }

        Round round = new Round();
        round.setTournament(tournament);
        round.setNumber(tournament.getActualRound());
        round.setStatut(Round.RoundStatus.PREVIEW);
        roundRepo.save(round);
    }

    @Override
    public void startRound(Long rId) {
        Round round = roundRepo.findById(rId)
                .orElseThrow(() -> new RuntimeException("Round not found : " + rId));

        // Can only start a round that is in PREVIEW
        if (round.getStatut() != Round.RoundStatus.PREVIEW) {
            throw new RuntimeException("Round can only be started from PREVIEW status");
        }

        // Cannot start if there are no matches generated yet
        if (round.getMatches() == null || round.getMatches().isEmpty()) {
            throw new RuntimeException("Cannot start a round with no pairings generated");
        }

        round.setStatut(Round.RoundStatus.ACTIVE);
        roundRepo.save(round);
    }

    @Override
    public void endRound(Long rId) {
        Round round = roundRepo.findById(rId)
                .orElseThrow(() -> new RuntimeException("Round not found : " + rId));

        // Can only end a round that is ACTIVE
        if (round.getStatut() != Round.RoundStatus.ACTIVE) {
            throw new RuntimeException("Round can only be ended from ACTIVE status");
        }

        // Cannot end if some matches still have no result
        boolean hasUnfinishedMatches = round.getMatches().stream()
                .anyMatch(m -> m.getResultat() == null ||
                        m.getResultat() == Match.MatchResult.PENDING);
        if (hasUnfinishedMatches) {
            throw new RuntimeException("All matches must have a result before ending the round");
        }

        round.setStatut(Round.RoundStatus.COMPLETED);
        roundRepo.save(round);

        // Advance the tournament to the next round
        Tournament tournament = round.getTournament();
        tournament.setActualRound(tournament.getActualRound() + 1);
        tournamentRepo.save(tournament);
    }

    @Override
    public Round getCurrentRound(Long tId) {
        Tournament tournament = tournamentRepo.findById(tId)
                .orElseThrow(() -> new RuntimeException("Tournament not found : " + tId));

        return roundRepo.findByTournamentAndStatut(tournament, Round.RoundStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("No active round found for this tournament"));
    }

    @Override
    public List<Round> getAllRounds(Long tId) {
        Tournament tournament = tournamentRepo.findById(tId)
                .orElseThrow(() -> new RuntimeException("Tournament not found : " + tId));

        return roundRepo.findByTournament(tournament);
    }

    @Override
    public Round getRound(Long rId) {
        return roundRepo.findById(rId)
                .orElseThrow(() -> new RuntimeException("Round not found : " + rId));
    }
}