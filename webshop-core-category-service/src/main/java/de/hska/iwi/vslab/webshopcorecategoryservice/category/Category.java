package de.hska.iwi.vslab.webshopcorecategoryservice.category;

import javax.persistence.*;


@Entity
public class Category {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    public Category(){}

    public Category(String name){
        this.id = 0L; // Needed for creation
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
