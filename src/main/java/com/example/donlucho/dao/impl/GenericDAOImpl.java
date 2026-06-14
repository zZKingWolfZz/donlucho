package com.example.donlucho.dao.impl;

import com.example.donlucho.dao.IGenericDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public abstract class GenericDAOImpl<T, ID, R extends JpaRepository<T, ID>> implements IGenericDAO<T, ID> {

    protected final R repository;

    protected GenericDAOImpl(R repository) {
        this.repository = repository;
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }
}
