package controler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Artwork;
import entity.BettingEvents;
import entity.Bids;
import entity.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadSingleProduct", urlPatterns = {"/LoadSingleProduct"})
public class LoadSingleProduct extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            String artIdParam = request.getParameter("pid");

            if (Validations.isInteger(artIdParam)) {
                int artworkId = Integer.parseInt(artIdParam);
                Artwork artwork = (Artwork) session.get(Artwork.class, artworkId);

                if (artwork != null) {
                    // Get the betting event related to the artwork
                    BettingEvents bettingEvent = (BettingEvents) session.createCriteria(BettingEvents.class)
                            .add(Restrictions.eq("artwork", artwork))
                            .uniqueResult();

                    // Load top 5 bids for this event
                    List<Bids> bidList = session.createCriteria(Bids.class)
                            .add(Restrictions.eq("event", bettingEvent))
                            .addOrder(Order.desc("bidAmountCoins"))
                            .setMaxResults(5)
                            .list();

                    // Clean user details for privacy
                    for (Bids bid : bidList) {
                        User bidder = bid.getBidder();
//                        bidder.setEmail(null);
                        bidder.setPassword(null);
                        bidder.setVerificationCode(null);
                    }

                    JsonObject json = new JsonObject();
                    json.add("artwork", gson.toJsonTree(artwork));
                    json.add("event", gson.toJsonTree(bettingEvent));
                    json.add("bids", gson.toJsonTree(bidList));

                    response.setContentType("application/json");
                    response.getWriter().write(gson.toJson(json));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
