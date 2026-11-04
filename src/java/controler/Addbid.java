package controler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dto.User_DTO;
import entity.BettingEvents;
import entity.Bids;
import entity.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import org.hibernate.Session;

@WebServlet(name = "Addbid", urlPatterns = {"/Addbid"})
public class Addbid extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();

        try {
            // Check user session
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                result.addProperty("success", false);
                result.addProperty("content", "Please log in to place a bid.");
                out.print(result.toString());
                return;
            }

            // Get user ID from session - handle both User and User_DTO
            Object userObj = session.getAttribute("user");
            int userId = -1;
            
            if (userObj instanceof User_DTO) {
                User_DTO userDTO = (User_DTO) userObj;
                userId = userDTO.getUserId();
            } else if (userObj instanceof User) {
                User user = (User) userObj;
                userId = user.getUserId();
            } else {
                result.addProperty("success", false);
                result.addProperty("content", "Invalid user session.");
                out.print(result.toString());
                return;
            }

            if (userId == -1) {
                result.addProperty("success", false);
                result.addProperty("content", "User ID not found in session.");
                out.print(result.toString());
                return;
            }

            // Parse JSON request body
            BufferedReader reader = request.getReader();
            JsonObject body = JsonParser.parseReader(reader).getAsJsonObject();
            
            if (!body.has("bidAmount") || !body.has("artId")) {
                result.addProperty("success", false);
                result.addProperty("content", "Missing required parameters: bidAmount and artId.");
                out.print(result.toString());
                return;
            }
            
            int bidAmount = body.get("bidAmount").getAsInt();
            int artId = body.get("artId").getAsInt();

            // Validate bid amount
            if (bidAmount <= 0) {
                result.addProperty("success", false);
                result.addProperty("content", "Bid amount must be greater than zero.");
                out.print(result.toString());
                return;
            }

            Session hibSession = HibernateUtil.getSessionFactory().openSession();
            org.hibernate.Transaction tx = null;

            try {
                tx = hibSession.beginTransaction();

                // Get user from database using the user ID from session
                User user = (User) hibSession.get(User.class, userId);
                if (user == null) {
                    result.addProperty("success", false);
                    result.addProperty("content", "User not found in database.");
                    out.print(result.toString());
                    return;
                }

                // Find betting event by artwork ID
                @SuppressWarnings("unchecked")
                List<BettingEvents> events = (List<BettingEvents>) hibSession.createQuery(
                        "FROM BettingEvents b WHERE b.artwork.artworkId = :artId ORDER BY b.startTime DESC"
                )
                .setParameter("artId", artId)
                .setMaxResults(1)
                .list();

                BettingEvents event = null;
                if (!events.isEmpty()) {
                    event = events.get(0);
                }

                if (event == null) {
                    result.addProperty("success", false);
                    result.addProperty("content", "No betting event found for this artwork.");
                    out.print(result.toString());
                    return;
                }

                // Time-based validation
                Date currentTime = new Date();
                if (event.getEndTime() != null && currentTime.after(event.getEndTime())) {
                    result.addProperty("success", false);
                    result.addProperty("content", "This betting event has ended.");
                    out.print(result.toString());
                    return;
                }

                if (event.getStartTime() != null && currentTime.before(event.getStartTime())) {
                    result.addProperty("success", false);
                    result.addProperty("content", "This betting event has not started yet.");
                    out.print(result.toString());
                    return;
                }

                int eventId = event.getEventId();

                // Validate bid amount against current highest bid
                if (bidAmount <= event.getCurrentHighestBidCoins()) {
                    result.addProperty("success", false);
                    result.addProperty("content", "Bid must be higher than current highest bid of " + 
                        event.getCurrentHighestBidCoins() + " coins.");
                    out.print(result.toString());
                    return;
                }

                // Check if user already has a bid for this event
                @SuppressWarnings("unchecked")
                List<Bids> existingBidsList = (List<Bids>) hibSession.createQuery(
                        "FROM Bids b WHERE b.event.eventId = :eventId AND b.bidder.userId = :userId"
                )
                .setParameter("eventId", eventId)
                .setParameter("userId", userId)
                .list();

                Bids existingBid = null;
                if (!existingBidsList.isEmpty()) {
                    existingBid = existingBidsList.get(0);
                }

                int coinsToDeduct = 0;
                boolean isUpdate = false;

                if (existingBid != null) {
                    // User already has a bid, update it
                    int previousBidAmount = existingBid.getBidAmountCoins();
                    coinsToDeduct = bidAmount - previousBidAmount;
                    isUpdate = true;

                    // Check if user has enough coins for the difference
                    if (coinsToDeduct > user.getCoinBalance()) {
                        result.addProperty("success", false);
                        result.addProperty("content", "Insufficient coins. You need " + coinsToDeduct + 
                            " more coins to update your bid. Current balance: " + user.getCoinBalance());
                        out.print(result.toString());
                        return;
                    }

                    // Update existing bid
                    existingBid.setBidAmountCoins(bidAmount);
                    existingBid.setBidTime(new Date());
                    hibSession.merge(existingBid);

                } else {
                    // New bid from this user
                    coinsToDeduct = bidAmount;

                    // Check if user has enough coins for full bid
                    if (bidAmount > user.getCoinBalance()) {
                        result.addProperty("success", false);
                        result.addProperty("content", "Insufficient coins. Required: " + bidAmount + 
                            ", Available: " + user.getCoinBalance());
                        out.print(result.toString());
                        return;
                    }

                    // Create new bid
                    Bids bid = new Bids();
                    bid.setBidAmountCoins(bidAmount);
                    bid.setBidder(user);
                    bid.setEvent(event);
                    bid.setBidTime(new Date());
                    hibSession.persist(bid);
                }

                // Deduct coins from user
                user.setCoinBalance(user.getCoinBalance() - coinsToDeduct);
                hibSession.merge(user);

                // Update betting event with new highest bid information
                event.setCurrentHighestBidCoins(bidAmount);
                event.setHighestBidderUserId(user);
                event.setWinningBidCoins(bidAmount);
                if (event.getReservePriceCoins() < bidAmount) {
                    event.setReservePriceCoins(bidAmount);
                }
                hibSession.merge(event);

                // Commit transaction
                tx.commit();

                // Update session with latest user data
                session.setAttribute("user", user);

                // Get latest bids for this event
                @SuppressWarnings("unchecked")
                List<Bids> latestBids = (List<Bids>) hibSession.createQuery(
                        "FROM Bids b WHERE b.event.eventId = :eventId ORDER BY b.bidTime DESC"
                )
                        .setParameter("eventId", eventId)
                        .setMaxResults(5)
                        .list();

                // Build response with latest bids
                JsonArray bidsArray = new JsonArray();
                for (Bids b : latestBids) {
                    JsonObject bidJson = new JsonObject();
                    bidJson.addProperty("email", b.getBidder().getEmail());
                    bidJson.addProperty("bidAmountCoins", b.getBidAmountCoins());
                    bidJson.addProperty("bidTime", b.getBidTime().toString());
                    bidsArray.add(bidJson);
                }

                // Success response
                String actionMessage = isUpdate ? "Bid updated successfully." : "Bid placed successfully.";
                
                result.addProperty("success", true);
                result.addProperty("message", actionMessage);
                result.addProperty("newBalance", user.getCoinBalance());
                result.addProperty("currentHighestBid", bidAmount);
                result.addProperty("coinsDeducted", coinsToDeduct);
                result.add("latestBids", bidsArray);
                result.addProperty("highestBidderEmail", user.getEmail());
                result.addProperty("eventId", eventId);

            } catch (Exception e) {
                // Rollback transaction on error
                if (tx != null && tx.isActive()) {
                    try {
                        tx.rollback();
                    } catch (Exception rollbackEx) {
                        System.err.println("Rollback failed: " + rollbackEx.getMessage());
                        rollbackEx.printStackTrace();
                    }
                }
                result.addProperty("success", false);
                result.addProperty("content", "Error placing bid: " + e.getMessage());
                e.printStackTrace();
            } finally {
                // Always close the session
                if (hibSession != null && hibSession.isOpen()) {
                    hibSession.close();
                }
            }
        } catch (Exception e) {
            result.addProperty("success", false);
            result.addProperty("content", "Server error: " + e.getMessage());
            e.printStackTrace();
        }

        out.print(result.toString());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();
        
        result.addProperty("success", false);
        result.addProperty("content", "GET method not supported. Use POST to place bids.");
        
        out.print(result.toString());
    }
}