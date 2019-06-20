package hska.iwi.eShopMaster.model.domain;

public class Role {

    private int id;
    private String type;
    private int level;

    public Role() {
    }

    public Role(int id, String type, int level) {
        this.id = id;
        this.type = type;
        this.level = level;
    }

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

}
