package de.hska.iwi.vslab.edgeserver.model;

public class RoleCore {

    private int id;

    private String type;

    private int level;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "RoleCore{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", level=" + level +
                '}';
    }

}
