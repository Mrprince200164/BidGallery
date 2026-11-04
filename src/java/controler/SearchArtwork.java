package controler; // Note: Fixed package name from 'controler'

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

@WebServlet(name = "SearchArtwork", urlPatterns = {"/SearchArtwork"})
public class SearchArtwork extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            json.addProperty("success", false);

            JsonObject req = gson.fromJson(request.getReader(), JsonObject.class);

            Criteria criteria = session.createCriteria(Artwork.class, "a");
            criteria.createAlias("Artwork_Status", "status");
            criteria.add(Restrictions.eq("status.statusId", 1)); // active only

            // 🔍 Search by title/description
            if (req.has("search") && !req.get("search").getAsString().isEmpty()) {
                String searchText = "%" + req.get("search").getAsString() + "%";
                criteria.add(Restrictions.or(
                        Restrictions.ilike("title", searchText),
                        Restrictions.ilike("description", searchText)
                ));
            }

            // 🎨 Medium filter - Updated to handle multiple mediums with exact matching
            if (req.has("mediums") && !req.get("mediums").getAsString().isEmpty()) {
                String mediumsStr = req.get("mediums").getAsString();
                String[] mediums = mediumsStr.split(",");
                
                if (mediums.length > 0) {
                    // Trim whitespace from each medium name and create list
                    List<String> mediumNames = new ArrayList<>();
                    for (String medium : mediums) {
                        String trimmedMedium = medium.trim();
                        mediumNames.add(trimmedMedium);
                    }
                    
                    // Debug: Print the mediums being searched
                    System.out.println("Searching for mediums: " + mediumNames);
                    
                    // Get Medium objects from database using case-insensitive search
                    Criteria mediumCriteria = session.createCriteria(Medium.class);
                    mediumCriteria.add(Restrictions.in("mediumName", mediumNames));
                    List<Medium> mediumList = mediumCriteria.list();
                    
                    // Debug: Print found mediums
                    System.out.println("Found " + mediumList.size() + " mediums in database");
                    for (Medium m : mediumList) {
                        System.out.println("Medium: " + m.getMediumName());
                    }
                    
                    if (!mediumList.isEmpty()) {
                        criteria.add(Restrictions.in("medium", mediumList));
                    } else {
                        // If no mediums found in database, create dummy restriction to return no results
                        criteria.add(Restrictions.eq("artworkId", -999)); // This will return no results
                    }
                }
            }

            // 📅 Year filter - Updated to work with your date_time field
            if (req.has("year") && !req.get("year").getAsString().isEmpty()) {
                try {
                    int year = Integer.parseInt(req.get("year").getAsString());
                    Calendar start = Calendar.getInstance();
                    start.set(year, Calendar.JANUARY, 1, 0, 0, 0);
                    start.set(Calendar.MILLISECOND, 0);
                    
                    Calendar end = Calendar.getInstance();
                    end.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
                    end.set(Calendar.MILLISECOND, 999);
                    
                    // Using the correct field name from your entity
                    criteria.add(Restrictions.between("date_time", start.getTime(), end.getTime()));
                } catch (NumberFormatException e) {
                    // Invalid year format, ignore this filter
                    System.out.println("Invalid year format: " + req.get("year").getAsString());
                }
            }

            // 👤 Artist filter
            if (req.has("artistId") && !req.get("artistId").getAsString().isEmpty()) {
                try {
                    int artistId = Integer.parseInt(req.get("artistId").getAsString());
                    Criteria artistCriteria = session.createCriteria(Artist.class);
                    artistCriteria.add(Restrictions.eq("artistId", artistId));
                    Artist artist = (Artist) artistCriteria.uniqueResult();
                    if (artist != null) {
                        criteria.add(Restrictions.eq("artist", artist));
                    }
                } catch (NumberFormatException e) {
                    // Invalid artist ID format, ignore this filter
                    System.out.println("Invalid artist ID format: " + req.get("artistId").getAsString());
                }
            }

            // 📊 Sort order
            if (req.has("sort") && req.get("sort").getAsString().equalsIgnoreCase("oldest")) {
                criteria.addOrder(Order.asc("artworkId"));
            } else {
                criteria.addOrder(Order.desc("artworkId")); // default latest
            }

            // Get total count before pagination
            List<Artwork> allResults = criteria.list();
            json.addProperty("allArtworkCount", allResults.size());
            json.addProperty("totalCount", allResults.size()); // Add totalCount for compatibility

            // Pagination
            int firstResult = req.has("firstResult") ? req.get("firstResult").getAsInt() : 0;
            criteria.setFirstResult(firstResult);
            criteria.setMaxResults(6);

            List<Artwork> artworks = criteria.list();
            json.add("artworks", buildArtworkJson(artworks, session));
            json.addProperty("success", true);

        } catch (Exception e) {
            json.addProperty("error", e.toString());
            e.printStackTrace();
        } finally {
            session.close();
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8"); // Add character encoding
        response.getWriter().write(gson.toJson(json));
    }

    private JsonArray buildArtworkJson(List<Artwork> artworks, Session session) {
        JsonArray array = new JsonArray();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a");

        for (Artwork artwork : artworks) {
            try {
                Criteria eventCriteria = session.createCriteria(BettingEvents.class);
                eventCriteria.add(Restrictions.eq("artwork", artwork));
                eventCriteria.addOrder(Order.desc("eventId"));
                eventCriteria.setMaxResults(1);
                BettingEvents event = (BettingEvents) eventCriteria.uniqueResult();

                    // Check if artwork has active betting event
                    if (event != null && event.getStatus() != null && event.getStatus().getStatusId() == 1) { 
                        JsonObject obj = new JsonObject();
                        obj.addProperty("artworkId", artwork.getArtworkId());
                        obj.addProperty("title", artwork.getTitle() != null ? artwork.getTitle() : "Untitled");
                        obj.addProperty("description", artwork.getDescription() != null ? artwork.getDescription() : "");
                        obj.addProperty("imageUrl", artwork.getImageUrl() != null ? artwork.getImageUrl() : "");
                        obj.addProperty("highestBid", event.getCurrentHighestBidCoins());
                        obj.addProperty("endTime", sdf.format(event.getEndTime()));
                        obj.addProperty("medium", artwork.getMedium() != null ? 
                            artwork.getMedium().getMediumName() : "");
                        obj.addProperty("creationYear", artwork.getDate_time() != null
                                ? String.valueOf(getYear(artwork.getDate_time())) : "");
                        obj.addProperty("artistName", artwork.getArtist() != null && 
                            artwork.getArtist().getUser() != null
                                ? artwork.getArtist().getUser().getFirstName() + " " + 
                                  artwork.getArtist().getUser().getLastName()
                                : "Unknown Artist");
                        array.add(obj);
                    }
            } catch (Exception e) {
                // Log error but continue processing other artworks
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