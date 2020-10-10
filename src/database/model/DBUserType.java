/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.model;

/**
 *
 * @author Kenshi
 */
public class DBUserType {
    
    private int id;
    private String name;
    private int priority;

    public DBUserType(int id, String name, int priority) {
        this.id = id;
        this.name = name;
        this.priority = priority;
    }

    public DBUserType() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    
}
