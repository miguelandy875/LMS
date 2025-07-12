package com.lms.dao;

import java.sql.*;
import java.util.List;

public interface GenericDAO<T> {

    T findById(int id) throws SQLException;

    List<T> findAll() throws SQLException;

    boolean insert(T entity) throws SQLException;

    boolean update(T entity) throws SQLException;

    boolean delete(int id) throws SQLException;
}
