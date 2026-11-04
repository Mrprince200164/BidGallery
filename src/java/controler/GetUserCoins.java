package controler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "GetUserCoins", urlPatterns = {"/GetUserCoins"})
public class GetUserCoins extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false); // Don't create a new session
        Gson gson = new Gson();
        JsonObject json = new JsonObject();

        if (session != null && session.getAttribute("user") != null) {
            // Get logged-in user from session
            User sessionUser = (User) session.getAttribute("user");
            int userId = sessionUser.getUserId();

            // Open Hibernate session
            Session hSession = HibernateUtil.getSessionFactory().openSession();

            try {
                // Use Criteria to fetch fresh user from DB
                Criteria criteria = hSession.createCriteria(User.class);
                criteria.add(Restrictions.eq("userId", userId));
                User dbUser = (User) criteria.uniqueResult();

                if (dbUser != null) {
                    json.addProperty("coinBalance", dbUser.getCoinBalance());
                } else {
                    json.addProperty("error", "User not found");
                }
            } catch (Exception e) {
                json.addProperty("error", "Database error: " + e.getMessage());
            } finally {
                hSession.close();
            }

        } else {
            json.addProperty("error", "User not logged in");
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(json));
    }
}
