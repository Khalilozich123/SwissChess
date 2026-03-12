package com.project.Swiss.services;

import com.project.Swiss.model.Match;
import com.project.Swiss.model.Player;
import com.project.Swiss.model.Round;
import com.project.Swiss.model.Tournament;
import com.project.Swiss.repositories.MatchRepository;
import com.project.Swiss.repositories.PlayerRepository;
import com.project.Swiss.repositories.RoundRepository;
import com.project.Swiss.repositories.TournamentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MatchServiceImpl implements IMatchService {

    private final MatchRepository matchRepo;
    private final PlayerRepository playerRepo;
    private final RoundRepository roundRepo;
    private final TournamentRepository tournamentRepo;

    public MatchServiceImpl(MatchRepository matchRepo,
                            PlayerRepository playerRepo,
                            RoundRepository roundRepo,
                            TournamentRepository tournamentRepo) {
        this.matchRepo = matchRepo;
        this.playerRepo = playerRepo;
        this.roundRepo = roundRepo;
        this.tournamentRepo = tournamentRepo;
    }

    @Override
    public void generatePairings(Long rId) {
        Round round = roundRepo.findById(rId)
                .orElseThrow(() -> new RuntimeException("Round not found : " + rId));

        // Can only generate pairings for a PREVIEW round
        if (round.getStatut() != Round.RoundStatus.PREVIEW) {
            throw new RuntimeException("Pairings can only be generated for a PREVIEW round");
        }

        Tournament tournament = round.getTournament();

        // Get all players sorted by score descending, then buchholz descending
        List<Player> players = playerRepo.findByTournamentOrderByScoreDesc(tournament);

        Set<Long> alreadyPaired = new HashSet<>();
        List<Match> pairings = new ArrayList<>();

        for (int i = 0; i < players.size(); i++) {
            Player white = players.get(i);

            // Skip if already paired this round
            if (alreadyPaired.contains(white.getId())) continue;

            Player black = null;

            // Find the next available opponent that white has not faced before
            for (int j = i + 1; j < players.size(); j++) {
                Player candidate = players.get(j);

                if (alreadyPaired.contains(candidate.getId())) continue;

                // Check they have not faced each other before
                if (!white.getListeAdversaires().contains(candidate.getId())) {
                    black = candidate;
                    break;
                }
            }

            Match match = new Match();
            match.setRound(round);
            match.setTournament(tournament);

            if (black != null) {
                // Determine colors based on color history
                match.setWhite(determineWhite(white, black));
                match.setBlack(determineBlack(white, black));
                match.setResultat(Match.MatchResult.PENDING);
                alreadyPaired.add(white.getId());
                alreadyPaired.add(black.getId());
            } else {
                // No opponent found — this player gets a BYE
                // Only give BYE to players who have not received one yet
                if (!white.getByeRecu()) {
                    match.setWhite(white);
                    match.setBlack(null);
                    match.setResultat(Match.MatchResult.BYE);
                    white.setByeRecu(true);
                    white.setScore(white.getScore() + 1.0);
                    playerRepo.save(white);
                    alreadyPaired.add(white.getId());
                }
            }

            pairings.add(match);
            matchRepo.save(match);
        }
    }

    // Helper — decides who plays White based on color history
    private Player determineWhite(Player a, Player b) {
        long aWhiteCount = a.getListeCouleurs().stream()
                .filter(c -> c.equals("W")).count();
        long bWhiteCount = b.getListeCouleurs().stream()
                .filter(c -> c.equals("W")).count();

        // The player who has played White less often gets White
        return aWhiteCount <= bWhiteCount ? a : b;
    }

    private Player determineBlack(Player a, Player b) {
        // Black is simply the other player
        return determineWhite(a, b).equals(a) ? b : a;
    }

    @Override
    public void addResult(Long mId, Match.MatchResult result) {
        Match match = matchRepo.findById(mId)
                .orElseThrow(() -> new RuntimeException("Match not found : " + mId));

        // Cannot add result to a BYE match
        if (match.getResultat() == Match.MatchResult.BYE) {
            throw new RuntimeException("Cannot set result on a BYE match");
        }

        // Cannot add result if already set — use editResult instead
        if (match.getResultat() != Match.MatchResult.PENDING) {
            throw new RuntimeException("Result already set. Use editResult to change it");
        }

        applyResult(match, result);
    }

    @Override
    public void editResult(Long mId, Match.MatchResult result) {
        Match match = matchRepo.findById(mId)
                .orElseThrow(() -> new RuntimeException("Match not found : " + mId));

        // Cannot edit a BYE match
        if (match.getResultat() == Match.MatchResult.BYE) {
            throw new RuntimeException("Cannot edit result of a BYE match");
        }

        // Reverse the previous result before applying the new one
        reverseResult(match);
        applyResult(match, result);
    }

    // Helper — applies result and updates player scores and color history
    private void applyResult(Match match, Match.MatchResult result) {
        Player white = match.getWhite();
        Player black = match.getBlack();

        // Update color history
        white.getListeCouleurs().add("W");
        black.getListeCouleurs().add("B");

        // Update opponent history
        white.getListeAdversaires().add(black.getId());
        black.getListeAdversaires().add(white.getId());

        // Update scores based on result
        if (result == Match.MatchResult.WHITE) {
            white.setScore(white.getScore() + 1.0);
        } else if (result == Match.MatchResult.BLACK) {
            black.setScore(black.getScore() + 1.0);
        } else if (result == Match.MatchResult.DRAW) {
            white.setScore(white.getScore() + 0.5);
            black.setScore(black.getScore() + 0.5);
        }

        match.setResultat(result);

        playerRepo.save(white);
        playerRepo.save(black);
        matchRepo.save(match);
    }

    // Helper — reverses a previously applied result to allow editing
    private void reverseResult(Match match) {
        Player white = match.getWhite();
        Player black = match.getBlack();

        Match.MatchResult previous = match.getResultat();

        if (previous == Match.MatchResult.WHITE) {
            white.setScore(white.getScore() - 1.0);
        } else if (previous == Match.MatchResult.BLACK) {
            black.setScore(black.getScore() - 1.0);
        } else if (previous == Match.MatchResult.DRAW) {
            white.setScore(white.getScore() - 0.5);
            black.setScore(black.getScore() - 0.5);
        }

        playerRepo.save(white);
        playerRepo.save(black);
    }

    @Override
    public List<Match> seePairingsByRound(Long rId) {
        Round round = roundRepo.findById(rId)
                .orElseThrow(() -> new RuntimeException("Round not found : " + rId));

        return matchRepo.findByRound(round);
    }

    @Override
    public List<Match> getPairingsByTournament(Long tId) {
        Tournament tournament = tournamentRepo.findById(tId)
                .orElseThrow(() -> new RuntimeException("Tournament not found : " + tId));

        return matchRepo.findByTournament(tournament);
    }

    @Override
    public void calculateBuchholz(Long tId) {
        Tournament tournament = tournamentRepo.findById(tId)
                .orElseThrow(() -> new RuntimeException("Tournament not found : " + tId));

        List<Player> players = playerRepo.findByTournament(tournament);

        for (Player player : players) {
            double buchholz = 0.0;

            // Sum the scores of all opponents this player has faced
            for (Long opponentId : player.getListeAdversaires()) {
                Player opponent = playerRepo.findById(opponentId)
                        .orElse(null);
                if (opponent != null) {
                    buchholz += opponent.getScore();
                }
            }

            player.setBuchholz(buchholz);
            playerRepo.save(player);
        }
    }
}