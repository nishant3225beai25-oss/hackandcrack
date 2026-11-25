package com.hackncrack.persistence;

import com.hackncrack.models.Player;
import java.util.*;

public class PlayerRepository implements Repository<Player> {
    private final Map<String, Player> players = new HashMap<>();
    private final TreeSet<Player> sortedPlayers = new TreeSet<>();

    @Override
    public void save(Player player) {
        players.put(player.getCodename(), player);
        sortedPlayers.add(player);
    }

    @Override
    public Optional<Player> findById(String codename) {
        return Optional.ofNullable(players.get(codename));
    }

    @Override
    public List<Player> findAll() {
        return new ArrayList<>(players.values());
    }

    @Override
    public boolean deleteById(String codename) {
        Player removed = players.remove(codename);
        if (removed != null) {
            sortedPlayers.remove(removed);
            return true;
        }
        return false;
    }

    @Override
    public int count() {
        return players.size();
    }

    public Player findTopPlayer() {
        if (sortedPlayers.isEmpty()) { return null; }
        return sortedPlayers.first();
    }

    public List<Player> getPlayersSortedByScore() {
        return new ArrayList<>(sortedPlayers);
    }

    public void refreshRanking(Player player) {
        sortedPlayers.remove(player);
        sortedPlayers.add(player);
    }
}