package controler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import entity.Artwork;
import entity.ArtworkStatus;
import entity.BettingEvents;
import entity.EventStatus;
import entity.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

@WebServlet(name = "AddEvents", urlPatterns = {"/AddEvents"})
public class AddEvents extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Response_DTO response_DTO = new Response_DTO();
        Gson gson = new Gson();
        Session session = null;
        Transaction tx = null;

        try {
            // 🧾 Read JSON from request body
            BufferedReader reader = request.getReader();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

            // 📥 Extract fields
            int artworkId = jsonObject.get("artworkId").getAsInt();
            String eventName = jsonObject.get("eventName").getAsString();
            String startTimeStr = jsonObject.get("startTime").getAsString();
            String endTimeStr = jsonObject.get("endTime").getAsString();
            int startingBidCoins = jsonObject.get("startingBidCoins").getAsInt();

            // ✅ Validate and extract highestBidderUserId
            if (!jsonObject.has("highestBidderUserId") || jsonObject.get("highestBidderUserId").isJsonNull()) {
                response_DTO.setSuccess(false);
                response_DTO.setContent("Highest bidder user ID is required.");
                response.setContentType("application/json");
                response.getWriter().write(gson.toJson(response_DTO));
                return;
            }

            int highestBidderUserId = jsonObject.get("highestBidderUserId").getAsInt();

            // 🕒 Parse date-times
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date startTime = sdf.parse(startTimeStr);
            Date endTime = sdf.parse(endTimeStr);

            // ❗ Basic validation
            if (eventName.trim().isEmpty() || startingBidCoins <= 0) {
                response_DTO.setSuccess(false);
                response_DTO.setContent("Invalid input: Event name and starting bid are required.");
                response.setContentType("application/json");
                response.getWriter().write(gson.toJson(response_DTO));
                return;
            }

            // 💾 Hibernate save
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();

            Artwork artwork = (Artwork) session.get(Artwork.class, artworkId);

            if (artwork == null || artwork.getArtwork_Status().getStatusId() != 2) {
                response_DTO.setSuccess(false);
                response_DTO.setContent("Selected artwork is not eligible for a betting event.");
            } else {
                // ✅ Fetch User (highest bidder)
                User bidder = (User) session.get(User.class, highestBidderUserId);
                if (bidder == null) {
                    response_DTO.setSuccess(false);
                    response_DTO.setContent("Selected highest bidder user not found.");
                    response.setContentType("application/json");
                    response.getWriter().write(gson.toJson(response_DTO));
                    return;
                }

                // 🎯 Create BettingEvent
                BettingEvents event = new BettingEvents();
                event.setArtwork(artwork);
                event.setEventName(eventName);
                event.setStartTime(startTime);
                event.setEndTime(endTime);
                event.setStartingBidCoins(startingBidCoins);
                event.setHighestBidderUserId(bidder); // ✅ required

                // Set default EventStatus (1)
                EventStatus status = (EventStatus) session.get(EventStatus.class, 1);
                event.setStatus(status);

                // Save event
                session.save(event);

                // 🖼️ Update Artwork status to 1 (active)
                ArtworkStatus activeStatus = (ArtworkStatus) session.get(ArtworkStatus.class, 1);
                artwork.setArtwork_Status(activeStatus);
                session.update(artwork);

                tx.commit();

                response_DTO.setSuccess(true);
                response_DTO.setContent("Betting event added successfully!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
            response_DTO.setSuccess(false);
            response_DTO.setContent("Server error: " + e.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }

        // 📤 Return JSON response
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));
    }
}
