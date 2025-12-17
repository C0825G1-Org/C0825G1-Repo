package com.codegym.homestay_booking.service;

import java.util.List;

public interface IService<T> {
    List<T> getAll();
    T getById(int id);
    boolean save(T entity);
    boolean delete(int id);
    boolean update(T entity);
}
