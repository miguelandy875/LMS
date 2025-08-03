package com.lms.dao;

import java.util.List;

public interface GenericDAO<T> {
    void save(T entity);
    void update(T entity);
    void delete(int id);
    T findById(int id);
    List<T> findAll();
}