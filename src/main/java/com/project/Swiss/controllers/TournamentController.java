package com.project.Swiss.controllers;

import com.project.Swiss.model.Tournament;
import com.project.Swiss.services.IPlayerService;
import com.project.Swiss.services.ITournamentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tournament")
public class TournamentController {

    private final ITournamentService tournamentService;
    private final IPlayerService playerService;

    public TournamentController(ITournamentService tournamentService,
                                IPlayerService playerService) {
        this.tournamentService = tournamentService;
        this.playerService = playerService;
    }

    // ─────────────────────────────────────────
    // GET /tournament
    // Show all tournaments
    // ─────────────────────────────────────────
    @GetMapping
    public String listAll(Model model) {
        model.addAttribute("tournaments", tournamentService.getAllTournaments());
        return "tournament/list";
    }

    // ─────────────────────────────────────────
    // GET /tournament/new
    // Show empty create form
    // ─────────────────────────────────────────
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("tournament", new Tournament());
        return "tournament/create";
    }

    // ─────────────────────────────────────────
    // POST /tournament/save
    // Save new tournament
    // ─────────────────────────────────────────
    @PostMapping("/save")
    public String saveTournament(
            @Valid @ModelAttribute("tournament") Tournament tournament,
            BindingResult result) {

        if (result.hasErrors()) {
            return "tournament/create";
        }

        tournamentService.createTournament(tournament);
        return "redirect:/tournament";
    }

    // ─────────────────────────────────────────
    // GET /tournament/{id}
    // Show tournament detail page with players
    // ─────────────────────────────────────────
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("tournament", tournamentService.getTournament(id));
        model.addAttribute("players", playerService.getAllPlayers(id));
        return "tournament/detail";
    }

    // ─────────────────────────────────────────
    // GET /tournament/{id}/edit
    // Show pre-filled edit form
    // ─────────────────────────────────────────
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("tournament", tournamentService.getTournament(id));
        return "tournament/edit";
    }

    // ─────────────────────────────────────────
    // POST /tournament/{id}/update
    // Save updated tournament
    // ─────────────────────────────────────────
    @PostMapping("/{id}/update")
    public String updateTournament(
            @PathVariable Long id,
            @Valid @ModelAttribute("tournament") Tournament tournament,
            BindingResult result) {

        if (result.hasErrors()) {
            return "tournament/edit";
        }

        tournament.setId(id);
        tournamentService.updateTournament(tournament);
        return "redirect:/tournament/" + id;
    }

    // ─────────────────────────────────────────
    // GET /tournament/{id}/start
    // Start the tournament
    // ─────────────────────────────────────────
    @GetMapping("/{id}/start")
    public String start(@PathVariable Long id) {
        tournamentService.startTournament(id);
        return "redirect:/tournament/" + id;
    }

    // ─────────────────────────────────────────
    // GET /tournament/{id}/finish
    // Finish the tournament
    // ─────────────────────────────────────────
    @GetMapping("/{id}/finish")
    public String finish(@PathVariable Long id) {
        tournamentService.finishTournament(id);
        return "redirect:/tournament/" + id;
    }

    // ─────────────────────────────────────────
    // GET /tournament/{id}/delete
    // Delete the tournament
    // ─────────────────────────────────────────
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        tournamentService.deleteTournament(id);
        return "redirect:/tournament";
    }

    // ─────────────────────────────────────────
    // GET /tournament/{id}/standings
    // Show final standings for a tournament
    // ─────────────────────────────────────────
    @GetMapping("/{id}/standings")
    public String standings(@PathVariable Long id, Model model) {
        model.addAttribute("tournament", tournamentService.getTournament(id));
        // Players already sorted by score DESC in the repository
        model.addAttribute("players", playerService.getAllPlayers(id));
        return "tournament/standings";
    }
}