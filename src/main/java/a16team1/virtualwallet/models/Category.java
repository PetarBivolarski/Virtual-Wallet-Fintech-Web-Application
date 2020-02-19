package a16team1.virtualwallet.models;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Size (min = 4, max = 50)
    private String name;

    @ManyToOne
    @JoinColumn (name = "creator_id")
    private User creator;

    public Category() {
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

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }
}
