package entity;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "cart")
public class Cart implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bettingevents_event_id", referencedColumnName = "event_id")
    private BettingEvents bettingEvent;

    @ManyToOne(optional = false)
    @JoinColumn(name = "users_user_id", referencedColumnName = "user_id")
    private User user;

    // Constructors
    public Cart() {}

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BettingEvents getBettingEvent() {
        return bettingEvent;
    }

    public void setBettingEvent(BettingEvents bettingEvent) {
        this.bettingEvent = bettingEvent;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
