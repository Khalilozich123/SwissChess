package com.project.Swiss.services;

import com.project.Swiss.model.Player;
import com.project.Swiss.model.Tournament;
import com.project.Swiss.repositories.PlayerRepository;
import com.project.Swiss.repositories.TournamentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerServiceImpl implements IPlayerService{

    private final PlayerRepository playerRepo;
    private final TournamentRepository tournamentRepo;

    public PlayerServiceImpl(PlayerRepository playerRepo, TournamentRepository tournamentRepo){
        this.playerRepo = playerRepo;
        this.tournamentRepo = tournamentRepo;
    }

    @Override
    public void createPlayer(Player pPlayer){
        playerRepo.save(pPlayer);
    }

    @Override
    public void updatePlayer(Player pPlayer){
        playerRepo.save(pPlayer);
    }

    @Override
    public void deletePlayer(Player pPlayer){
        playerRepo.delete(pPlayer);
    }

    @Override
    public List<Player> getAllPlayers(Long tId) {
        Tournament tournament = tournamentRepo.findById(tId).orElseThrow();
        return playerRepo.findByTournament(tournament);
    }

    @Override
    public List<Player> searchPlayer(String name, Long tId) {
        Tournament tournament = tournamentRepo.findById(tId).orElseThrow();
        return playerRepo.findByNameContainingIgnoreCaseAndTournament(name, tournament);
    }


}
