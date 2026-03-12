package com.project.Swiss.services;

import com.project.Swiss.model.Player;

import java.util.List;

public interface IPlayerService {

    public void createPlayer(Player pPlayer);

    public void updatePlayer(Player pPlayer);

    public void deletePlayer(Player pPlayer);

    public List<Player> getAllPlayers(Long tId);

    public List<Player> searchPlayer(String name, Long tId);
}
