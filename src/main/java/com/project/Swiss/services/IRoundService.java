package com.project.Swiss.services;

import com.project.Swiss.model.Round;

import java.util.List;

public interface IRoundService {

    public void addRound(Long tId);

    public void startRound(Long rId);

    public void endRound(Long rId);

    public Round getCurrentRound(Long tId);

    public List<Round> getAllRounds(Long tId);

    public Round getRound(Long rId);
}
