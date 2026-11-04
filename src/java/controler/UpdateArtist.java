package controler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.Response_DTO;
import dto.User_DTO;
import entity.Artist;
import entity.User;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "UpdateArtist", urlPatterns = {"/UpdateArtist"})
public class UpdateArtist extends HttpServlet {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Response_DTO responseDTO = new Response_DTO();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        User_DTO artistDTO = gson.fromJson(request.getReader(), User_DTO.class);

        if (artistDTO.getEmail() == null || artistDTO.getEmail().isEmpty()) {
            responseDTO.setContent("Email is required.");
            writeResponse(response, gson, responseDTO);
            return;
        }

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            Criteria userCriteria = session.createCriteria(User.class);
            userCriteria.add(Restrictions.eq("email", artistDTO.getEmail()));
            User user = (User) userCriteria.uniqueResult();

            if (user == null) {
                responseDTO.setContent("User not found.");
                writeResponse(response, gson, responseDTO);
                return;
            }

            // Fetch existing Artist linked to user
            Criteria artistCriteria = session.createCriteria(Artist.class);
            artistCriteria.add(Restrictions.eq("user", user));
            Artist artist = (Artist) artistCriteria.uniqueResult();

            if (artist == null) {
                artist = new Artist();
                artist.setUser(user);
            }

            artist.setBio(artistDTO.getBio());
            artist.setNationality(artistDTO.getNationality());
            artist.setStyle(artistDTO.getStyle());

            if (artistDTO.getBirthDate() != null && !((String) artistDTO.getBirthDate()).trim().isEmpty()) {
                Date birth = sdf.parse((String) artistDTO.getBirthDate());
                artist.setBirthDate(birth);
            } else {
                artist.setBirthDate(null);
            }

            if (artistDTO.getDeathDate() != null && !((String) artistDTO.getDeathDate()).trim().isEmpty()) {
                Date death = sdf.parse((String) artistDTO.getDeathDate());
                artist.setDeathDate(death);
            } else {
                artist.setDeathDate(null);
            }

            session.saveOrUpdate(artist);
            tx.commit();

            responseDTO.setSuccess(true);
            responseDTO.setContent("Artist profile updated successfully.");

        } catch (Exception e) {
            tx.rollback();
            responseDTO.setContent("Error updating artist profile: " + e.getMessage());
        } finally {
            session.close();
        }

        writeResponse(response, gson, responseDTO);
    }

    private void writeResponse(HttpServletResponse response, Gson gson, Response_DTO responseDTO) throws IOException {
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseDTO));
    }
}
