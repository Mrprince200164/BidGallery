package dto;

import java.util.Date;

public class BettingEventCard_DTO {

    private int artworkId;
    private String title;
    private String description;
    private String imageUrl;
    private int highestBidCoins;
    private Date startTime;
    private Date endTime;

    private String artistName;
    private Date creationYear;
    private String dimensions;

    public BettingEventCard_DTO(int artworkId, String title, String description, String imageUrl,
                                int highestBidCoins, Date startTime, Date endTime,
                                String artistName, Date creationYear, String dimensions) {
        this.artworkId = artworkId;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.highestBidCoins = highestBidCoins;
        this.startTime = startTime;
        this.endTime = endTime;
        this.artistName = artistName;
        this.creationYear = creationYear;
        this.dimensions = dimensions;
    }
    
    public BettingEventCard_DTO(){
    }

    // Add getters if needed

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
     * @return the highestBidCoins
     */
    public int getHighestBidCoins() {
        return highestBidCoins;
    }

    /**
     * @param highestBidCoins the highestBidCoins to set
     */
    public void setHighestBidCoins(int highestBidCoins) {
        this.highestBidCoins = highestBidCoins;
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
     * @return the artistName
     */
    public String getArtistName() {
        return artistName;
    }

    /**
     * @param artistName the artistName to set
     */
    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    /**
     * @return the creationYear
     */
    public Date getCreationYear() {
        return creationYear;
    }

    /**
     * @param creationYear the creationYear to set
     */
    public void setCreationYear(Date creationYear) {
        this.creationYear = creationYear;
    }

    /**
     * @return the dimensions
     */
    public String getDimensions() {
        return dimensions;
    }

    /**
     * @param dimensions the dimensions to set
     */
    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }
}

