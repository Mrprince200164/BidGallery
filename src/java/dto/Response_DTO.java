package dto;

import com.google.gson.annotations.Expose;
import java.io.Serializable;

public class Response_DTO implements Serializable {


    @Expose
    private boolean success;

    @Expose
    private Object content; // Changed Object to String for typical use cases

    public Response_DTO() {
        // Default constructor
    }

    public Response_DTO(boolean success, Object content) { // Changed Object to String
        this.success = success;
        this.content = content;
    }

    /**
     * @return the success status
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success the success status to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
        /**
     * @return the content
     */
    public Object getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(Object content) {
        this.content = content;
        
    }
}