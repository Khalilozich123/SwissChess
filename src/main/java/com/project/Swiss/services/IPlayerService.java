package com.project.Swiss.services;

import com.project.Swiss.model.Player;

import java.util.List;

public interface IPlayerService {

    public void addPlayer(Player pPlayer);

    public void updatePlayer(Player pPlayer);

    public void deletePlayer(Long pId);

    public List<Player> getAllPlayers();

    public List<Player> searchPlayer(String pName);
}
