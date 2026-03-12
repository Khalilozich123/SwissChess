package com.project.Swiss.services;

import com.project.Swiss.model.Match;

import java.util.List;

public interface IMatchService {

    public void generatePairings(Long rId);

    public List<Match> seePairingsByRound(Long rId);

    public List<Match> getPairingsByTournament(Long tId);

    public void addResult(Long mId, Match.MatchResult result);

    public void editResult(Long mId, Match.MatchResult result);

    public void calculateBuchholz(Long tId);
}
