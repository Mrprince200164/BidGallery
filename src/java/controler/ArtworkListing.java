package controler;

import com.google.gson.Gson;
import dto.Response_DTO;
import dto.User_DTO;
import entity.Artist;
import entity.Artwork;
import entity.ArtworkStatus;
import entity.Medium;
import entity.User;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@MultipartConfig
@WebServlet(name = "ArtworkListing", urlPatterns = {"/ArtworkListing"})
public class ArtworkListing extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Response_DTO response_DTO = new Response_DTO();
        Gson gson = new Gson();

//        HttpSession usession = request.getSession(false);
        String title = request.getParameter("title");
        String creationYear = request.getParameter("creationYear");
        String diamension = request.getParameter("diamension");
        String description = request.getParameter("description");
        String mediumId = request.getParameter("mediumId");
        String coinValue = request.getParameter("coinValue");
        String currentOwner = request.getParameter("currentOwner");
        Part artImage = request.getPart("artImage");

        Session session = HibernateUtil.getSessionFactory().openSession();

//          User_DTO userdetail = (User_DTO) usession.getAttribute("user");
        // ===== VALIDATION =====
        if (title.isEmpty()) {
            response_DTO.setContent("Please fill Title");

        } else if (!Validations.isDate(creationYear)) {
            response_DTO.setContent("Invalid creation year");

        } else if (diamension.isEmpty()) {
            response_DTO.setContent("Invalid dimension");

        } else if (description.isEmpty()) {
            response_DTO.setContent("Please enter the description");

        } else if (!Validations.isInteger(mediumId)) {
            response_DTO.setContent("Invalid medium");

        } else if (!Validations.isDouble(coinValue)) {
            response_DTO.setContent("Invalid coin value");

        } else if (Double.parseDouble(coinValue) <= 0) {
            response_DTO.setContent("Coin value must be greater than 0");

        } else if (artImage == null || artImage.getSubmittedFileName().isEmpty()) {
            response_DTO.setContent("Please upload an art image");

        } else {

            Medium medium = (Medium) session.get(Medium.class, Integer.parseInt(mediumId));

            if (medium == null) {
                response_DTO.setContent("Please select a valid Medium");

            } else {
                try {
                    // ==== BUILD ARTWORK OBJECT ====
                    Artwork art = new Artwork();
                    art.setTitle(title);
                    art.setDate_time((new Date()));
                    art.setDescription(description);
                    art.setDiamensions(diamension);
                    art.setEstimatedCoinValue(Double.parseDouble(coinValue));
                    art.setMedium(medium);

                    // Set status (Assume status id 1 is "active")
                    ArtworkStatus artworkStatus = (ArtworkStatus) session.get(ArtworkStatus.class, 2);
                    art.setArtwork_Status(artworkStatus);

                    // ===== Get logged-in user =====
                    User_DTO user_DTO = (User_DTO) request.getSession().getAttribute("user");
                    Criteria userCriteria = session.createCriteria(User.class);
                    userCriteria.add(Restrictions.eq("email", user_DTO.getEmail()));
                    User user = (User) userCriteria.uniqueResult();

                    if (user == null) {
                        response_DTO.setContent("User not found");
                        session.close();
                        response.setContentType("application/json");
                        response.getWriter().write(gson.toJson(response_DTO));
                        return;
                    }

                    // Set owner
                    art.setOwnerUserId(user.getUserId());

                    // ===== Get Artist from User =====
                    Criteria artistCriteria = session.createCriteria(Artist.class);
                    artistCriteria.add(Restrictions.eq("user", user));
                    Artist artistObj = (Artist) artistCriteria.uniqueResult();

                    if (artistObj == null) {
                        response_DTO.setContent("You are not registered as an artist.");
                        session.close();
                        response.setContentType("application/json");
                        response.getWriter().write(gson.toJson(response_DTO));
                        return;
                    }

                    art.setArtist(artistObj);

                    // ===== Save Image File Temporarily to Get Path =====
                    String applicationPath = request.getServletContext().getRealPath("");
                    String tempId = String.valueOf(System.currentTimeMillis()); // temp name before saving
                    String uploadPath = applicationPath.replace("build" + File.separator + "web", "web")
                            + File.separator + "artwork-images" + File.separator + tempId;

                    File imageFolder = new File(uploadPath);
                    imageFolder.mkdirs();

                    File imageFile = new File(imageFolder, "image.png");
                    try (InputStream input = artImage.getInputStream()) {
                        Files.copy(input, imageFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }

// Set image URL in entity
                    art.setImageUrl("artwork-images/" + tempId + "/image.png");

// ===== Save Artwork with imageUrl =====
                    Transaction tx = session.beginTransaction();
                    int artworkId = (int) session.save(art);
                    tx.commit();

// ===== Rename folder to real artwork ID =====
                    File realFolder = new File(imageFolder.getParent(), String.valueOf(artworkId));
                    if (imageFolder.renameTo(realFolder)) {
                        // Update image URL in DB
                        String finalImageUrl = "artwork-images/" + artworkId + "/image.png";
                        art.setImageUrl(finalImageUrl);
                        art.setArtworkId(artworkId); // Ensure ID is set before update

                        tx = session.beginTransaction();
                        session.update(art);
                        tx.commit();
                    } else {
                        // Handle rename failure
                        System.err.println("Failed to rename image folder.");
                    }

                    response_DTO.setSuccess(true);
                    response_DTO.setContent("Artwork added successfully!");

                } catch (Exception e) {
                    e.printStackTrace();
                    response_DTO.setContent("An error occurred while saving artwork.");
                }
            }
        }

        session.close();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));
    }
}
