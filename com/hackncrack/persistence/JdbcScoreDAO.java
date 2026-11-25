package com.hackncrack.persistence;

import com.hackncrack.exceptions.DatabaseConnectionException;
import com.hackncrack.models.Score;
import java.util.List;
import java.util.Optional;

public class JdbcScoreDAO implements Repository<Score> {
    @Override
    public void save(Score entity) throws DatabaseConnectionException { throw new DatabaseConnectionException("Not implemented (Phase 2)"); }
    @Override
    public Optional<Score> findById(String id) throws DatabaseConnectionException { throw new DatabaseConnectionException("Not implemented (Phase 2)"); }
    @Override
    public List<Score> findAll() throws DatabaseConnectionException { throw new DatabaseConnectionException("Not implemented (Phase 2)"); }
    @Override
    public boolean deleteById(String id) throws DatabaseConnectionException { throw new DatabaseConnectionException("Not implemented (Phase 2)"); }
    @Override
    public int count() throws DatabaseConnectionException { throw new DatabaseConnectionException("Not implemented (Phase 2)"); }
}