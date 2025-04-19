package Interfaces;

import java.util.List;

public interface CRUD<T> {
    boolean create(T objeto);
    T read(int id);
    List<T> readAll();
    boolean update(T objeto);
    boolean delete(int id);
}