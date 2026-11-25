package com.hackncrack.services;

import com.hackncrack.models.Player;
import com.hackncrack.persistence.PlayerRepository;
import java.util.List;
import java.util.Optional;

public class PlayerService {
    private final PlayerRepository repository;

    public PlayerService(PlayerRepository repository) { this.repository = repository; }

    public void register(Player player) {
        repository.save(player);
    }

    public Optional<Player> getPlayer(String codename) {
        return repository.findById(codename);
    }

    public List<Player> getAllPlayers() {
        return repository.findAll();
    }

    public List<Player> getPlayersSortedByScore() {
        return repository.getPlayersSortedByScore();
    }

    public Player getLeaderboardWinner() {
        return repository.findTopPlayer();
    }

    public boolean removePlayer(String codename) {
        return repository.deleteById(codename);
    }
}