package controler;

import entity.Artwork;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Session;

/**
 * Servlet to load artworks with ArtworkStatus.statusId = 2 (available)
 * Returns a JSON response with artworkId, title, description, estimatedPrice, and artistEmail
 * 
 * URL: /LoadAvilableArtworks
 * Method: GET
 * Response: application/json
 */
@WebServlet(name = "LoadAvilableArtworks", urlPatterns = {"/LoadAvilableArtworks"})
public class LoadAvilableArtworks extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {

            // Open Hibernate session
            Session session = HibernateUtil.getSessionFactory().openSession();

            // Fetch artworks where ArtworkStatus.statusId = 2 (Available)
            String hql = "FROM Artwork a WHERE a.artworkStatus.statusId = 2";
//            List<Artwork> artworks = session.createQuery(hql, Artwork.class).getResultList();
            List<Artwork> artworks = (List<Artwork>) session.createQuery(hql).list();


            // Build JSON array of artworks
            JsonArrayBuilder artworkArrayBuilder = Json.createArrayBuilder();

            for (Artwork art : artworks) {
                JsonObjectBuilder objBuilder = Json.createObjectBuilder();

                objBuilder.add("artworkId", art.getArtworkId());
                objBuilder.add("title", art.getTitle() != null ? art.getTitle() : "");
                objBuilder.add("description", art.getDescription() != null ? art.getDescription() : "");
                objBuilder.add("estimatedPrice", art.getEstimatedCoinValue());

                // Safely extract artist's email
                String email = "unknown";
                try {
                    email = art.getArtist().getUser().getEmail();
                } catch (Exception e) {
                    e.printStackTrace(); // Log if artist/user missing
                }

                objBuilder.add("artistEmail", email);

                artworkArrayBuilder.add(objBuilder);
            }

            // Final JSON response
            JsonObject jsonResponse = Json.createObjectBuilder()
                    .add("artworks", artworkArrayBuilder)
                    .build();

            out.print(jsonResponse.toString());

            session.close();

        } catch (Exception e) {
            e.printStackTrace(); // Log server error
        }
    }
}
