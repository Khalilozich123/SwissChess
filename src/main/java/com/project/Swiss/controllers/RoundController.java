package com.project.Swiss.controllers;

import com.project.Swiss.services.IMatchService;
import com.project.Swiss.services.IRoundService;
import com.project.Swiss.services.ITournamentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tournament/{tId}/round")
public class RoundController {

    private final IRoundService roundService;
    private final ITournamentService tournamentService;
    private final IMatchService matchService;

    public RoundController(IRoundService roundService,
                           ITournamentService tournamentService,
                           IMatchService matchService) {
        this.roundService = roundService;
        this.tournamentService = tournamentService;
        this.matchService = matchService;
    }

    // ─────────────────────────────────────────
    // GET /tournament/{tId}/round/new
    // Create a new round for the tournament
    // ─────────────────────────────────────────
    @GetMapping("/new")
    public String createRound(@PathVariable Long tId) {
        roundService.addRound(tId);
        // After creating the round, redirect to tournament detail
        return "redirect:/tournament/" + tId;
    }

    // ─────────────────────────────────────────
    // GET /tournament/{tId}/round/{rId}
    // Show round detail with its matches
    // ─────────────────────────────────────────
    @GetMapping("/{rId}")
    public String detail(@PathVariable Long tId,
                         @PathVariable Long rId,
                         Model model) {

        model.addAttribute("round", roundService.getRound(rId));
        model.addAttribute("tournament", tournamentService.getTournament(tId));
        model.addAttribute("matches", matchService.seePairingsByRound(rId));

        return "round/detail";
    }

    // ─────────────────────────────────────────
    // GET /tournament/{tId}/round/{rId}/pairings
    // Generate pairings for this round
    // ─────────────────────────────────────────
    @GetMapping("/{rId}/pairings")
    public String generatePairings(@PathVariable Long tId,
                                   @PathVariable Long rId,
                                   Model model) {

        // Generate the pairings via the match service
        matchService.generatePairings(rId);

        model.addAttribute("round", roundService.getRound(rId));
        model.addAttribute("tournament", tournamentService.getTournament(tId));
        model.addAttribute("matches", matchService.seePairingsByRound(rId));

        return "round/pairings";
    }

    // ─────────────────────────────────────────
    // GET /tournament/{tId}/round/{rId}/start
    // Start the round — moves status from PREVIEW to ACTIVE
    // ─────────────────────────────────────────
    @GetMapping("/{rId}/start")
    public String startRound(@PathVariable Long tId,
                             @PathVariable Long rId) {

        roundService.startRound(rId);
        return "redirect:/tournament/" + tId + "/round/" + rId;
    }

    // ─────────────────────────────────────────
    // GET /tournament/{tId}/round/{rId}/end
    // End the round — moves status from ACTIVE to COMPLETED
    // ─────────────────────────────────────────
    @GetMapping("/{rId}/end")
    public String endRound(@PathVariable Long tId,
                           @PathVariable Long rId) {

        roundService.endRound(rId);

        // After ending the round, recalculate Buchholz for all players
        matchService.calculateBuchholz(tId);

        return "redirect:/tournament/" + tId;
    }

    // ─────────────────────────────────────────
    // GET /tournament/{tId}/round/history
    // Show all past rounds of a tournament
    // ─────────────────────────────────────────
    @GetMapping("/history")
    public String history(@PathVariable Long tId, Model model) {
        model.addAttribute("tournament", tournamentService.getTournament(tId));
        model.addAttribute("rounds", roundService.getAllRounds(tId));
        return "round/history";
    }
}