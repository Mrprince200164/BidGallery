package controler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dto.User_DTO;
import entity.Artist;
import entity.Artwork;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import model.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadProfileData", urlPatterns = {"/LoadProfileData"})
public class LoadProfileData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        Gson gson = new Gson();
        JsonObject result = new JsonObject();

        HttpSession httpSession = request.getSession(false);
        User_DTO userDTO = (User_DTO) httpSession.getAttribute("user");

        if (userDTO == null) {
            result.addProperty("success", false);
            result.addProperty("message", "User not signed in");
            response.getWriter().write(result.toString());
            return;
        }

        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            // Load full User entity
            entity.User user = (entity.User) session.createCriteria(entity.User.class)
                    .add(Restrictions.eq("email", userDTO.getEmail()))
                    .uniqueResult();

            JsonObject userJson = new JsonObject();
            userJson.addProperty("firstName", user.getFirstName());
            userJson.addProperty("lastName", user.getLastName());
            userJson.addProperty("email", user.getEmail());
            userJson.addProperty("password", user.getPassword());
            userJson.addProperty("mobile", user.getMobile());
            userJson.addProperty("address", user.getAddress());
            userJson.addProperty("coins", user.getCoinBalance());
            userJson.addProperty("isArtist", user.getIsArtist());

            result.add("user", userJson);

            // Artist Info
            if (user.getIsArtist() == 1) {
                Artist artist = (Artist) session.createCriteria(Artist.class)
                        .add(Restrictions.eq("user.userId", user.getUserId()))
                        .uniqueResult();

                if (artist != null) {
                    JsonObject artistJson = new JsonObject();
                    artistJson.addProperty("bio", artist.getBio());
                    artistJson.addProperty("birthDate", artist.getBirthDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(artist.getBirthDate()) : "");
                    artistJson.addProperty("deathDate", artist.getDeathDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(artist.getDeathDate()) : "");
                    artistJson.addProperty("nationality", artist.getNationality());
                    artistJson.addProperty("style", artist.getStyle());
                    result.add("artist", artistJson);
                }
            }

            // My Artworks
            List<Artwork> artworks = session.createCriteria(Artwork.class)
                    .add(Restrictions.eq("ownerUserId", user.getUserId()))
                    .list();

            JsonArray artsArray = new JsonArray();
            for (Artwork a : artworks) {
                JsonObject art = new JsonObject();
                art.addProperty("title", a.getTitle());
                art.addProperty("dimensions", a.getDiamensions());
                art.addProperty("creationYear", new SimpleDateFormat("yyyy-MM-dd").format(a.getDate_time()));
                art.addProperty("description", a.getDescription());
                art.addProperty("value", a.getEstimatedCoinValue());
                art.addProperty("status", a.getArtwork_Status().getStatusName());
                art.addProperty("medium", a.getMedium().getMediumName());
                art.addProperty("imageUrl", a.getImageUrl());
                artsArray.add(art);
            }

            result.add("artworks", artsArray);
            result.addProperty("success", true);

        } catch (Exception e) {
            e.printStackTrace();
            result.addProperty("success", false);
            result.addProperty("message", "Server error");
        } finally {
            session.close();
        }

        response.getWriter().write(result.toString());
    }
}
