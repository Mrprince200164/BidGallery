/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Bids")
public class Bids implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bid_id")
    private int bidId;

    @ManyToOne
    @JoinColumn(name = "BettingEvents_event_id")
    private BettingEvents event;

    @ManyToOne
    @JoinColumn(name = "bidder_user_id")
    private User bidder;

    @Column(name = "bid_amount_coins")
    private int bidAmountCoins;

    @Column(name = "bid_time")
    private Date bidTime;
    
    public Bids() {
    
}

    // Constructors, getters, setters

    /**
     * @return the bidId
     */
    public int getBidId() {
        return bidId;
    }

    /**
     * @param bidId the bidId to set
     */
    public void setBidId(int bidId) {
        this.bidId = bidId;
    }

    /**
     * @return the event
     */
    public BettingEvents getEvent() {
        return event;
    }

    /**
     * @param event the event to set
     */
    public void setEvent(BettingEvents event) {
        this.event = event;
    }

    /**
     * @return the bidder
     */
    public User getBidder() {
        return bidder;
    }

    /**
     * @param bidder the bidder to set
     */
    public void setBidder(User bidder) {
        this.bidder = bidder;
    }

    /**
     * @return the bidAmountCoins
     */
    public int getBidAmountCoins() {
        return bidAmountCoins;
    }

    /**
     * @param bidAmountCoins the bidAmountCoins to set
     */
    public void setBidAmountCoins(int bidAmountCoins) {
        this.bidAmountCoins = bidAmountCoins;
    }

    /**
     * @return the bidTime
     */
    public Date getBidTime() {
        return bidTime;
    }

    /**
     * @param bidTime the bidTime to set
     */
    public void setBidTime(Date bidTime) {
        this.bidTime = bidTime;
    }
}


