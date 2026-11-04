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
@Table(name = "Artworks")
public class Artwork implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artwork_id")
    private int artworkId;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "creation_year")
    private Date date_time;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "current_owner_user_id")
    private int ownerUserId;

    @ManyToOne
    @JoinColumn(name = "Artists_artist_id")
    private Artist artist;

    @Column(name = "dimensions", length = 100)
    private String diamensions;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "estimated_coin_value")
    private double estimatedCoinValue;

    @ManyToOne
    @JoinColumn(name = "Medium_medium_id")
    private Medium medium;

    @ManyToOne
    @JoinColumn(name = "ArtworkStatus_status_id")
    private ArtworkStatus Artwork_Status;

    public Artwork() {

    }

    // Constructors, getters, setters
    /**
     * @return the artworkId
     */
    public int getArtworkId() {
        return artworkId;
    }

    /**
     * @param artworkId the artworkId to set
     */
    public void setArtworkId(int artworkId) {
        this.artworkId = artworkId;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }


    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the ownerUserId
     */
    public int getOwnerUserId() {
        return ownerUserId;
    }

    /**
     * @param ownerUserId the ownerUserId to set
     */
    public void setOwnerUserId(int ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    /**
     * @return the artist
     */
    public Artist getArtist() {
        return artist;
    }

    /**
     * @param artist the artist to set
     */
    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    /**
     * @return the imageUrl
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * @param imageUrl the imageUrl to set
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * @return the estimatedCoinValue
     */
    public double getEstimatedCoinValue() {
        return estimatedCoinValue;
    }

    /**
     * @param estimatedCoinValue the estimatedCoinValue to set
     */
    public void setEstimatedCoinValue(double estimatedCoinValue) {
        this.estimatedCoinValue = estimatedCoinValue;
    }

    /**
     * @return the medium
     */
    public Medium getMedium() {
        return medium;
    }

    /**
     * @param medium the medium to set
     */
    public void setMedium(Medium medium) {
        this.medium = medium;
    }

    /**
     * @return the diamensions
     */
    public String getDiamensions() {
        return diamensions;
    }

    /**
     * @param diamensions the diamensions to set
     */
    public void setDiamensions(String diamensions) {
        this.diamensions = diamensions;
    }

    /**
     * @return the Artwork_Status
     */
    public ArtworkStatus getArtwork_Status() {
        return Artwork_Status;
    }

    /**
     * @param Artwork_Status the Artwork_Status to set
     */
    public void setArtwork_Status(ArtworkStatus Artwork_Status) {
        this.Artwork_Status = Artwork_Status;
    }

    /**
     * @return the date_time
     */
    public Date getDate_time() {
        return date_time;
    }

    /**
     * @param date_time the date_time to set
     */
    public void setDate_time(Date date_time) {
        this.date_time = date_time;
    }
}
