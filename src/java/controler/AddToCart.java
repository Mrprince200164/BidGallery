package controler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dto.Response_DTO;
import entity.BettingEvents;
import entity.Cart;
import entity.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AddToCart", urlPatterns = {"/AddToCart"})
public class AddToCart extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Response_DTO response_DTO = new Response_DTO();
        Gson gson = new Gson();

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = hibernateSession.beginTransaction();

        try {
            // Read and parse JSON body
            BufferedReader reader = request.getReader();
            JsonObject body = JsonParser.parseReader(reader).getAsJsonObject();

            int bettingEventId = body.get("bettingEventId").getAsInt();
            int userId = body.get("userId").getAsInt();

            // Find user
            Criteria userCriteria = hibernateSession.createCriteria(User.class);
            userCriteria.add(Restrictions.eq("userId", userId));
            User user = (User) userCriteria.uniqueResult();

            if (user == null) {
                response_DTO.setSuccess(false);
                response_DTO.setContent("User not found.");
            } else {
                // Find event
                Criteria eventCriteria = hibernateSession.createCriteria(BettingEvents.class);
                eventCriteria.add(Restrictions.eq("id", bettingEventId));
                BettingEvents event = (BettingEvents) eventCriteria.uniqueResult();

                if (event == null) {
                    response_DTO.setSuccess(false);
                    response_DTO.setContent("Betting event not found.");
                } else {
                    // Check if already in cart
                    Criteria cartCriteria = hibernateSession.createCriteria(Cart.class);
                    cartCriteria.add(Restrictions.eq("user", user));
                    cartCriteria.add(Restrictions.eq("bettingEvent", event));

                    List<Cart> cartItems = cartCriteria.list();

                    if (cartItems.isEmpty()) {
                        // Add to cart
                        Cart newCart = new Cart();
                        newCart.setUser(user);
                        newCart.setBettingEvent(event);

                        hibernateSession.save(newCart);
                        transaction.commit();

                        response_DTO.setSuccess(true);
                        response_DTO.setContent("Event added to cart.");
                    } else {
                        // Already in cart
                        response_DTO.setSuccess(false);
                        response_DTO.setContent("This event is already in your cart.");
                    }
                }
            }

        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            e.printStackTrace();
            response_DTO.setSuccess(false);
            response_DTO.setContent("Something went wrong.");
        } finally {
            hibernateSession.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));
    }
}
