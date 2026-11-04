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

/**
 *
 * @author Sahan Vishvid
 */
@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId; // Renamed for better Java convention

    @Column(name = "email", length = 100, nullable = false, unique = true) // Added unique constraint
    private String email;

    @Column(name = "password", length = 50, nullable = false)
    private String password;

    @Column(name = "first_name", length = 45, nullable = false)
    private String firstName; // Renamed for better Java convention

    @Column(name = "last_name", length = 45, nullable = false)
    private String lastName; // Renamed for better Java convention

    @Column(name = "mobile", length = 10, nullable = false)
    private String mobile;

    @Column(name = "registration_date" , nullable = false)
    private Date registrationDate; // Renamed for better Java convention

    @Column(name = "coin_balance")
    private int coinBalance; // Renamed for better Java convention

    @Column(name = "is_admin")
    private int isAdmin; // Renamed for better Java convention

    @Column(name = "is_artist")
    private int isArtist; // Renamed for better Java convention

    @Column(name = "address", nullable = true, columnDefinition = "TEXT") // Explicitly define column type if needed
    private String address;

    @Column(name = "verification_code") // Renamed column for clarity
    private String verificationCode; // Changed to String as it's a code, not a boolean/int for status

    @ManyToOne // Many Users can have One UserStatus
    @JoinColumn(name = "UserStatus_status_id", referencedColumnName = "status_id", nullable = false)
    private UserStatus userStatus; // Renamed and changed to UserStatus object

    public User() {
    }
    
    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @param mobile the mobile to set
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * @return the registrationDate
     */
    public Date getRegistrationDate() {
        return registrationDate;
    }

    /**
     * @param registrationDate the registrationDate to set
     */
    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    /**
     * @return the coinBalance
     */
    public int getCoinBalance() {
        return coinBalance;
    }

    /**
     * @param coinBalance the coinBalance to set
     */
    public void setCoinBalance(int coinBalance) {
        this.coinBalance = coinBalance;
    }

    /**
     * @return the isAdmin
     */
    public int getIsAdmin() {
        return isAdmin;
    }

    /**
     * @param isAdmin the isAdmin to set
     */
    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    /**
     * @return the isArtist
     */
    public int getIsArtist() {
        return isArtist;
    }

    /**
     * @param isArtist the isArtist to set
     */
    public void setIsArtist(int isArtist) {
        this.isArtist = isArtist;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the verificationCode
     */
    public String getVerificationCode() {
        return verificationCode;
    }

    /**
     * @param verificationCode the verificationCode to set
     */
    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    /**
     * @return the userStatus
     */
    public UserStatus getUserStatus() {
        return userStatus;
    }

    /**
     * @param userStatus the userStatus to set
     */
    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

}