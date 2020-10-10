/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.dao;

import java.util.List;

/**
 *
 * @author Kenshi
 * @param <T>
 */
public interface GenericCrud <T> {
    void add(T t);
    void delete(T t);
    void update(T t);
    List<T> getAll();
}
