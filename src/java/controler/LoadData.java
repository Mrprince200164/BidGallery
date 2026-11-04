package controler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entity.Artist;
import entity.Artwork;
import entity.BettingEvents;
import entity.Medium;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadData", urlPatterns = {"/LoadData"})
public class LoadData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            json.addProperty("success", false);

            // 1️⃣ Load Medium List
            Criteria mediumCriteria = session.createCriteria(Medium.class);
            List<Medium> mediumList = mediumCriteria.list();
            json.add("mediumList", gson.toJsonTree(mediumList));

            // 2️⃣ Load Artist List
            Criteria artistCriteria = session.createCriteria(Artist.class);
            List<Artist> artistList = artistCriteria.list();
            JsonArray artistArray = new JsonArray();
            for (Artist artist : artistList) {
                if (artist.getUser() != null) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("id", artist.getArtistId());
                    obj.addProperty("name", artist.getUser().getFirstName() + " " + artist.getUser().getLastName());
                    artistArray.add(obj);
                }
            }
            json.add("artistList", artistArray);

            // 3️⃣ Load available years from artworks (optional - you can remove this if not needed)
            Criteria yearCriteria = session.createCriteria(Artwork.class);
            yearCriteria.add(Restrictions.isNotNull("date_time"));
            List<Artwork> artworksWithDates = yearCriteria.list();
            Set<Integer> uniqueYears = new HashSet<>();
            for (Artwork artwork : artworksWithDates) {
                if (artwork.getDate_time() != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(artwork.getDate_time());
                    uniqueYears.add(cal.get(Calendar.YEAR));
                }
            }
            List<Integer> sortedYears = new ArrayList<>(uniqueYears);
            Collections.sort(sortedYears, Collections.reverseOrder());
            json.add("yearList", gson.toJsonTree(sortedYears));

            // 4️⃣ Load artworks (latest 6 with active betting events)
            Criteria artworkCriteria = session.createCriteria(Artwork.class, "a");
            artworkCriteria.createAlias("Artwork_Status", "status");
            artworkCriteria.add(Restrictions.eq("status.statusId", 1)); // active only
            artworkCriteria.addOrder(Order.desc("artworkId"));

            List<Artwork> allArtworks = artworkCriteria.list();
            
            // Filter artworks that have active betting events
            List<Artwork> artworksWithActiveBetting = new ArrayList<>();
            for (Artwork artwork : allArtworks) {
                Criteria eventCriteria = session.createCriteria(BettingEvents.class);
                eventCriteria.add(Restrictions.eq("artwork", artwork));
                eventCriteria.createAlias("status", "eventStatus");
                eventCriteria.add(Restrictions.eq("eventStatus.statusId", 1)); // active events only
                eventCriteria.addOrder(Order.desc("eventId"));
                eventCriteria.setMaxResults(1);
                BettingEvents event = (BettingEvents) eventCriteria.uniqueResult();
                
                if (event != null) {
                    artworksWithActiveBetting.add(artwork);
                    if (artworksWithActiveBetting.size() >= 6) {
                        break; // Get only first 6
                    }
                }
            }

            // Total count of artworks with active betting
            json.addProperty("allArtworkCount", artworksWithActiveBetting.size());

            JsonArray artworkArray = buildArtworkJson(artworksWithActiveBetting, session);
            json.add("artworks", artworkArray);

            json.addProperty("success", true);
        } catch (Exception e) {
            json.addProperty("error", e.toString());
            e.printStackTrace();
        } finally {
            session.close();
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(json));
    }

    private JsonArray buildArtworkJson(List<Artwork> artworks, Session session) {
        JsonArray array = new JsonArray();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a");

        for (Artwork artwork : artworks) {
            try {
                Criteria eventCriteria = session.createCriteria(BettingEvents.class);
                eventCriteria.add(Restrictions.eq("artwork", artwork));
                eventCriteria.createAlias("status", "eventStatus");
                eventCriteria.add(Restrictions.eq("eventStatus.statusId", 1)); // active events only
                eventCriteria.addOrder(Order.desc("eventId"));
                eventCriteria.setMaxResults(1);
                BettingEvents event = (BettingEvents) eventCriteria.uniqueResult();

                if (event != null) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("artworkId", artwork.getArtworkId());
                    obj.addProperty("title", artwork.getTitle() != null ? artwork.getTitle() : "Untitled");
                    obj.addProperty("description", artwork.getDescription() != null ? artwork.getDescription() : "");
                    obj.addProperty("imageUrl", artwork.getImageUrl() != null ? artwork.getImageUrl() : "");
                    obj.addProperty("highestBid", event.getCurrentHighestBidCoins());
                    obj.addProperty("endTime", sdf.format(event.getEndTime()));
                    obj.addProperty("medium", artwork.getMedium() != null ? artwork.getMedium().getMediumName() : "");
                    obj.addProperty("creationYear", artwork.getDate_time() != null
                            ? String.valueOf(getYear(artwork.getDate_time())) : "");
                    obj.addProperty("artistName", artwork.getArtist() != null && artwork.getArtist().getUser() != null
                            ? artwork.getArtist().getUser().getFirstName() + " " + artwork.getArtist().getUser().getLastName()
                            : "Unknown Artist");
                    array.add(obj);
                }
            } catch (Exception e) {
                System.out.println("Error processing artwork ID " + artwork.getArtworkId() + ": " + e.getMessage());
            }
        }
        return array;
    }

    private int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }
}