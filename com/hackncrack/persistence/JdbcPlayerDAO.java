package com.hackncrack.persistence;

import com.hackncrack.exceptions.DatabaseConnectionException;
import com.hackncrack.models.Player;
import java.util.List;
import java.util.Optional;

public class JdbcPlayerDAO implements Repository<Player> {
    @Override
    public void save(Player entity) throws DatabaseConnectionException { throw new DatabaseConnectionException("Not implemented (Phase 2)"); }
    @Override
    public Optional<Player> findById(String id) throws DatabaseConnectionException { throw new DatabaseConnectionException("Not implemented (Phase 2)"); }
    @Override
    public List<Player> findAll() throws DatabaseConnectionException { throw new DatabaseConnectionException("Not implemented (Phase 2)"); }
    @Override
    public boolean deleteById(String id) throws DatabaseConnectionException { throw new DatabaseConnectionException("Not implemented (Phase 2)"); }
    @Override
    public int count() throws DatabaseConnectionException { throw new DatabaseConnectionException("Not implemented (Phase 2)"); }
}