package com.project.Swiss.controllers;

import com.project.Swiss.model.Match;
import com.project.Swiss.services.IMatchService;
import com.project.Swiss.services.IRoundService;
import com.project.Swiss.services.ITournamentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/match")
public class MatchController {

    private final IMatchService matchService;
    private final IRoundService roundService;
    private final ITournamentService tournamentService;

    public MatchController(IMatchService matchService,
                           IRoundService roundService,
                           ITournamentService tournamentService) {
        this.matchService = matchService;
        this.roundService = roundService;
        this.tournamentService = tournamentService;
    }

    // ─────────────────────────────────────────
    // GET /match/{mId}/result?result=WHITE
    // Submit a result for a match
    // ─────────────────────────────────────────
    @GetMapping("/{mId}/result")
    public String addResult(@PathVariable Long mId,
                            @RequestParam Match.MatchResult result) {

        matchService.addResult(mId, result);

        // Get the round and tournament to redirect correctly
        Match.MatchResult savedResult = result;
        return "redirect:/match/" + mId + "/detail";
    }

    // ─────────────────────────────────────────
    // GET /match/{mId}/edit?result=DRAW
    // Edit an existing result
    // ─────────────────────────────────────────
    @GetMapping("/{mId}/edit")
    public String editResult(@PathVariable Long mId,
                             @RequestParam Match.MatchResult result) {

        matchService.editResult(mId, result);
        return "redirect:/match/" + mId + "/detail";
    }

    // ─────────────────────────────────────────
    // GET /match/{mId}/detail
    // Show match detail
    // ─────────────────────────────────────────
    @GetMapping("/{mId}/detail")
    public String detail(@PathVariable Long mId, Model model) {
        // Get the round this match belongs to
        // to show context in the view
        model.addAttribute("matchId", mId);
        model.addAttribute("results", Match.MatchResult.values());
        return "match/detail";
    }

    // ─────────────────────────────────────────
    // GET /match/round/{rId}
    // Show all matches of a round with result buttons
    // ─────────────────────────────────────────
    @GetMapping("/round/{rId}")
    public String matchesByRound(@PathVariable Long rId, Model model) {
        model.addAttribute("matches", matchService.seePairingsByRound(rId));
        model.addAttribute("round", roundService.getRound(rId));
        model.addAttribute("results", Match.MatchResult.values());
        return "match/list";
    }
}