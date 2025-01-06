package com.javafx.test.dao;


// Interface générique DAO
public interface Dao<T> {
    boolean create(T obj);
    T read(int id);
    boolean update(T obj);
    boolean delete(int id);
}

