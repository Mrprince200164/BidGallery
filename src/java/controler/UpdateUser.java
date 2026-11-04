package controler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.Response_DTO;
import dto.User_DTO;
import entity.User;
import java.io.IOException;
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

@WebServlet(name = "UpdateUser", urlPatterns = {"/UpdateUser"})
public class UpdateUser extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Response_DTO responseDTO = new Response_DTO();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        User_DTO userDTO = gson.fromJson(request.getReader(), User_DTO.class);

        if (userDTO.getEmail() == null || userDTO.getEmail().isEmpty()) {
            responseDTO.setContent("Email is required.");
            writeResponse(response, gson, responseDTO);
            return;
        }

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("email", userDTO.getEmail()));

            User user = (User) criteria.uniqueResult();

            if (user == null) {
                responseDTO.setContent("User not found.");
            } else {
                // Update user fields
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                user.setMobile(userDTO.getMobile());
                user.setAddress(userDTO.getAddress());

                if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                    user.setPassword(userDTO.getPassword());
                }

                user.setIsArtist(userDTO.getIsArtist()); // 1 or 0 from checkbox

                session.update(user);
                tx.commit();

                responseDTO.setSuccess(true);
                responseDTO.setContent("User updated successfully.");
            }

        } catch (Exception e) {
            tx.rollback();
            responseDTO.setContent("Error updating user: " + e.getMessage());
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
