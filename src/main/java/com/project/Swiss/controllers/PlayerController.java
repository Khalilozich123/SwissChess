package com.project.Swiss.controllers;

import com.project.Swiss.model.Player;
import com.project.Swiss.model.Tournament;
import com.project.Swiss.services.IPlayerService;
import com.project.Swiss.services.ITournamentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tournament/{tId}/player")
public class PlayerController {

    private final IPlayerService playerService;
    private final ITournamentService tournamentService;

    public PlayerController(IPlayerService playerService,
                            ITournamentService tournamentService) {
        this.playerService = playerService;
        this.tournamentService = tournamentService;
    }

    // ─────────────────────────────────────────
    // GET /tournament/{tId}/player/add
    // Show empty form to add a new player
    // ─────────────────────────────────────────
    @GetMapping("/add")
    public String showAddForm(@PathVariable Long tId, Model model) {
        // Fetch the tournament so the view can display its name
        Tournament tournament = tournamentService.getTournament(tId);

        // Send an empty Player object for the form to bind to
        model.addAttribute("player", new Player());
        model.addAttribute("tournament", tournament);

        return "player/add";
    }

    // ─────────────────────────────────────────
    // POST /tournament/{tId}/player/save
    // Save a new player to the tournament
    // ─────────────────────────────────────────
    @PostMapping("/save")
    public String savePlayer(@PathVariable Long tId,
                             @Valid @ModelAttribute("player") Player player,
                             BindingResult result,
                             Model model) {

        // If validation fails, go back to the form with errors
        if (result.hasErrors()) {
            model.addAttribute("tournament", tournamentService.getTournament(tId));
            return "player/add";
        }

        // Link the player to the correct tournament before saving
        Tournament tournament = tournamentService.getTournament(tId);
        player.setTournament(tournament);

        playerService.createPlayer(player);

        // Redirect back to tournament detail page after saving
        return "redirect:/tournament/" + tId;
    }

    // ─────────────────────────────────────────
    // GET /tournament/{tId}/player/{pId}/edit
    // Show pre-filled form to edit an existing player
    // ─────────────────────────────────────────
    @GetMapping("/{pId}/edit")
    public String showEditForm(@PathVariable Long tId,
                               @PathVariable Long pId,
                               Model model) {

        // Load the existing player data to pre-fill the form
        Player player = playerService.getPlayer(pId);
        Tournament tournament = tournamentService.getTournament(tId);

        model.addAttribute("player", player);
        model.addAttribute("tournament", tournament);

        return "player/edit";
    }

    // ─────────────────────────────────────────
    // POST /tournament/{tId}/player/{pId}/update
    // Save the updated player data
    // ─────────────────────────────────────────
    @PostMapping("/{pId}/update")
    public String updatePlayer(@PathVariable Long tId,
                               @PathVariable Long pId,
                               @Valid @ModelAttribute("player") Player player,
                               BindingResult result,
                               Model model) {

        // If validation fails, go back to the edit form
        if (result.hasErrors()) {
            model.addAttribute("tournament", tournamentService.getTournament(tId));
            return "player/edit";
        }

        // Make sure the id and tournament are preserved correctly
        player.setId(pId);
        player.setTournament(tournamentService.getTournament(tId));

        playerService.updatePlayer(player);

        return "redirect:/tournament/" + tId;
    }

    // ─────────────────────────────────────────
    // GET /tournament/{tId}/player/{pId}/delete
    // Delete a player from the tournament
    // ─────────────────────────────────────────
    @GetMapping("/{pId}/delete")
    public String deletePlayer(@PathVariable Long tId,
                               @PathVariable Long pId) {

        // Fetch the full player object then delete
        Player player = playerService.getPlayer(pId);
        playerService.deletePlayer(player);

        return "redirect:/tournament/" + tId;
    }

    // ─────────────────────────────────────────
    // GET /tournament/{tId}/player/search?name=...
    // Search players by name within a tournament
    // ─────────────────────────────────────────
    @GetMapping("/search")
    public String searchPlayer(@PathVariable Long tId,
                               @RequestParam(required = false) String name,
                               Model model) {

        model.addAttribute("tournament", tournamentService.getTournament(tId));

        // If no name provided, return all players
        if (name == null || name.isBlank()) {
            model.addAttribute("players", playerService.getAllPlayers(tId));
        } else {
            model.addAttribute("players", playerService.searchPlayer(name, tId));
        }

        model.addAttribute("searchName", name);

        return "player/search";
    }
}