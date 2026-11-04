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
@Table(name = "Transactions")
public class Transaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private int transactionId;

    @ManyToOne
    @JoinColumn(name = "Users_user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "TransactionType_type_id", nullable = false)
    private TransactionType type;

    @Column(name = "real_money_amount")
    private double realMoneyAmount;

    @Column(name = "coin_amount")
    private int coinAmount;

    @Column(name = "transaction_time")
    private Date transactionTime;

    @ManyToOne
    @JoinColumn(name = "TransactionStatus_status_id")
    private TransactionStatus status;
    
    public Transaction() {
    
}

    // Constructors, getters, setters

    /**
     * @return the transactionId
     */
    public int getTransactionId() {
        return transactionId;
    }

    /**
     * @param transactionId the transactionId to set
     */
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the type
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(TransactionType type) {
        this.type = type;
    }

    /**
     * @return the realMoneyAmount
     */
    public double getRealMoneyAmount() {
        return realMoneyAmount;
    }

    /**
     * @param realMoneyAmount the realMoneyAmount to set
     */
    public void setRealMoneyAmount(double realMoneyAmount) {
        this.realMoneyAmount = realMoneyAmount;
    }

    /**
     * @return the coinAmount
     */
    public int getCoinAmount() {
        return coinAmount;
    }

    /**
     * @param coinAmount the coinAmount to set
     */
    public void setCoinAmount(int coinAmount) {
        this.coinAmount = coinAmount;
    }

    /**
     * @return the transactionTime
     */
    public Date getTransactionTime() {
        return transactionTime;
    }

    /**
     * @param transactionTime the transactionTime to set
     */
    public void setTransactionTime(Date transactionTime) {
        this.transactionTime = transactionTime;
    }

    /**
     * @return the status
     */
    public TransactionStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
}


