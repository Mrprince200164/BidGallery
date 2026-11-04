package controler;

import com.google.gson.Gson;
import entity.BettingEvents;
import entity.Cart;
import entity.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadCartItems", urlPatterns = {"/LoadCartItems"})
public class LoadCartItems extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        HttpSession httpSession = request.getSession();
        List<BettingEvents> eventList = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            if (httpSession.getAttribute("user") != null) {
                // Logged-in user
                User user = (User) httpSession.getAttribute("user");

                // Load cart items from DB
                Criteria cartCriteria = session.createCriteria(Cart.class);
                cartCriteria.add(Restrictions.eq("user", user));
                List<Cart> cartList = cartCriteria.list();

                for (Cart cart : cartList) {
                    BettingEvents event = cart.getBettingEvent();
                    if (event != null) {
                        event.setHighestBidderUserId(null); // prevent circular reference if exists
                        eventList.add(event);
                    }
                }

            } else {
                // Session cart for guest (if used)
                if (httpSession.getAttribute("sessionCart") != null) {
                    eventList = (List<BettingEvents>) httpSession.getAttribute("sessionCart");

                    for (BettingEvents event : eventList) {
                        event.setHighestBidderUserId(null);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(eventList));
    }
}
