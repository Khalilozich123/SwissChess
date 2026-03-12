package com.project.Swiss.services;

import com.project.Swiss.model.Player;
import com.project.Swiss.model.Tournament;

import java.util.List;

public interface ITournamentService {

    public void createTournament(Tournament tournament);

    public void startTournament(Long tId);

    public void updateTournament(Tournament tournament);

    public void deleteTournament(Long tId);

    public void finishTournament(Long tId);

    public Tournament getTournament(Long tId);

    public List<Tournament> getAllTournaments();

}
