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
@Table(name = "BettingEvents")
public class BettingEvents implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private int eventId;

    @ManyToOne
    @JoinColumn(name = "Artworks_artwork_id")
    private Artwork artwork;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "starting_bid_coins")
    private int startingBidCoins;

    @Column(name = "reserve_price_coins")
    private int reservePriceCoins;

    @Column(name = "current_highest_bid_coins")
    private int currentHighestBidCoins;

    @ManyToOne
    @JoinColumn(name = "highest_bidder_user_id")
    private User highestBidderUserId;

    @Column(name = "winning_bid_coins")
    private Integer winningBidCoins;

    @ManyToOne
    @JoinColumn(name = "EventStatus_status_id")
    private EventStatus status;

    public BettingEvents() {

    }

    // Constructors, getters, setters
    /**
     * @return the eventId
     */
    public int getEventId() {
        return eventId;
    }

    /**
     * @param eventId the eventId to set
     */
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    /**
     * @return the artwork
     */
    public Artwork getArtwork() {
        return artwork;
    }

    /**
     * @param artwork the artwork to set
     */
    public void setArtwork(Artwork artwork) {
        this.artwork = artwork;
    }

    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the startingBidCoins
     */
    public int getStartingBidCoins() {
        return startingBidCoins;
    }

    /**
     * @param startingBidCoins the startingBidCoins to set
     */
    public void setStartingBidCoins(int startingBidCoins) {
        this.startingBidCoins = startingBidCoins;
    }

    /**
     * @return the reservePriceCoins
     */
    public int getReservePriceCoins() {
        return reservePriceCoins;
    }

    /**
     * @param reservePriceCoins the reservePriceCoins to set
     */
    public void setReservePriceCoins(int reservePriceCoins) {
        this.reservePriceCoins = reservePriceCoins;
    }

    /**
     * @return the currentHighestBidCoins
     */
    public int getCurrentHighestBidCoins() {
        return currentHighestBidCoins;
    }

    /**
     * @param currentHighestBidCoins the currentHighestBidCoins to set
     */
    public void setCurrentHighestBidCoins(int currentHighestBidCoins) {
        this.currentHighestBidCoins = currentHighestBidCoins;
    }

  
    /**
     * @return the winningBidCoins
     */
    public Integer getWinningBidCoins() {
        return winningBidCoins;
    }

    /**
     * @param winningBidCoins the winningBidCoins to set
     */
    public void setWinningBidCoins(Integer winningBidCoins) {
        this.winningBidCoins = winningBidCoins;
    }

    /**
     * @return the status
     */
    public EventStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(EventStatus status) {
        this.status = status;
    }

    /**
     * @return the eventName
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * @param eventName the eventName to set
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * @return the highestBidderUserId
     */
    public User getHighestBidderUserId() {
        return highestBidderUserId;
    }

    /**
     * @param highestBidderUserId the highestBidderUserId to set
     */
    public void setHighestBidderUserId(User highestBidderUserId) {
        this.highestBidderUserId = highestBidderUserId;
    }


}