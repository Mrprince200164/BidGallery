package controler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entity.BettingEvents;
import entity.Bids;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Session;

@WebServlet(name = "GetLiveBids", urlPatterns = {"/GetLiveBids"})
public class GetLiveBids extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JsonObject result = new JsonObject();

        String artIdParam = request.getParameter("artId");
        if (artIdParam == null || artIdParam.trim().isEmpty()) {
            result.addProperty("success", false);
            result.addProperty("message", "Artwork ID is required.");
            out.print(result.toString());
            return;
        }

        int artId;
        try {
            artId = Integer.parseInt(artIdParam);
        } catch (NumberFormatException e) {
            result.addProperty("success", false);
            result.addProperty("message", "Invalid artwork ID format.");
            out.print(result.toString());
            return;
        }

        Session hibSession = HibernateUtil.getSessionFactory().openSession();

        try {
            // Find the betting event using artwork ID
            BettingEvents event = (BettingEvents) hibSession.createQuery(
                    "FROM BettingEvents b WHERE b.artwork.artworkId = :artId"
            )
            .setParameter("artId", artId)
            .uniqueResult();

            if (event == null) {
                result.addProperty("success", false);
                result.addProperty("message", "No active betting event found for this artwork.");
                out.print(result.toString());
                return;
            }

            int eventId = event.getEventId();

            // Get latest bids for this event
            @SuppressWarnings("unchecked")
            List<Bids> latestBids = (List<Bids>) hibSession.createQuery(
                    "FROM Bids b WHERE b.event.eventId = :eventId ORDER BY b.bidTime DESC"
            )
                    .setParameter("eventId", eventId)
                    .setMaxResults(5)
                    .list();

            JsonArray bidsArray = new JsonArray();
            for (Bids b : latestBids) {
                JsonObject bidJson = new JsonObject();
                bidJson.addProperty("email", b.getBidder().getEmail());
                bidJson.addProperty("bidAmountCoins", b.getBidAmountCoins());
                bidsArray.add(bidJson);
            }

            String highestBidderEmail = "";
            if (event.getHighestBidderUserId() != null) {
                highestBidderEmail = event.getHighestBidderUserId().getEmail();
            }

            result.addProperty("success", true);
            result.add("latestBids", bidsArray);
            result.addProperty("highestBidderEmail", highestBidderEmail);
            result.addProperty("currentHighestBid", event.getCurrentHighestBidCoins());

        } catch (Exception e) {
            result.addProperty("success", false);
            result.addProperty("message", "Error loading bids: " + e.getMessage());
        } finally {
            if (hibSession != null && hibSession.isOpen()) {
                hibSession.close();
            }
        }

        out.print(result.toString());
    }
}