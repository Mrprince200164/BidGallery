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
@Table(name = "Notifications")
public class Notification implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private int notificationId;

    @ManyToOne
    @JoinColumn(name = "Users_user_id")
    private User user;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @ManyToOne
    @JoinColumn(name = "NotificationType_type_id")
    private NotificationType type;

    @Column(name = "related_event_id")
    private Integer relatedEventId;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "is_viewed")
    private int isViewed;
    
    public Notification() {
    
}

    // Constructors, getters, setters

    /**
     * @return the notificationId
     */
    public int getNotificationId() {
        return notificationId;
    }

    /**
     * @param notificationId the notificationId to set
     */
    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
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
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the type
     */
    public NotificationType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(NotificationType type) {
        this.type = type;
    }

    /**
     * @return the relatedEventId
     */
    public Integer getRelatedEventId() {
        return relatedEventId;
    }

    /**
     * @param relatedEventId the relatedEventId to set
     */
    public void setRelatedEventId(Integer relatedEventId) {
        this.relatedEventId = relatedEventId;
    }

    /**
     * @return the createdAt
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return the isViewed
     */
    public int getIsViewed() {
        return isViewed;
    }

    /**
     * @param isViewed the isViewed to set
     */
    public void setIsViewed(int isViewed) {
        this.isViewed = isViewed;
    }
}


