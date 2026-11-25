package com.hackncrack.persistence;

import com.hackncrack.exceptions.DatabaseConnectionException;
import java.util.List;
import java.util.Optional;

public interface Repository<T> {
    void save(T entity) throws DatabaseConnectionException;
    Optional<T> findById(String id) throws DatabaseConnectionException;
    List<T> findAll() throws DatabaseConnectionException;
    boolean deleteById(String id) throws DatabaseConnectionException;
    int count() throws DatabaseConnectionException;
}