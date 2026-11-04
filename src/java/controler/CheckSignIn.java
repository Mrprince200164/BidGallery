package controler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dto.BettingEventCard_DTO;
import dto.Response_DTO;
import dto.User_DTO;
import entity.Artwork;
import entity.BettingEvents;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

@WebServlet(name = "CheckSignIn", urlPatterns = {"/CheckSignIn"})
public class CheckSignIn extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject jsonObject = new JsonObject();

        Response_DTO response_DTO = new Response_DTO();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        Session session = HibernateUtil.getSessionFactory().openSession();

        // Check login session
        if (request.getSession().getAttribute("user") != null) {
            User_DTO user_DTO = (User_DTO) request.getSession().getAttribute("user");
            response_DTO.setSuccess(true);
            response_DTO.setContent(user_DTO);
        }

        jsonObject.add("response_dto", gson.toJsonTree(response_DTO));

        
        // Get 3 most recent betting events
        Criteria criteria = session.createCriteria(BettingEvents.class);
        criteria.addOrder(Order.desc("eventId"));
        criteria.setMaxResults(3);
        List<BettingEvents> eventList = criteria.list();

        List<BettingEventCard_DTO> cardList = new ArrayList<>();

        for (BettingEvents event : eventList) {
            Artwork artwork = event.getArtwork();
            if (artwork != null) {
                BettingEventCard_DTO dto = new BettingEventCard_DTO();

                dto.setArtistName(artwork.getArtist().getUser().getFirstName());
                dto.setArtworkId(artwork.getArtworkId());
                dto.setTitle(artwork.getTitle());
                dto.setDescription(artwork.getDescription());
                dto.setImageUrl(artwork.getImageUrl());
                dto.setHighestBidCoins(event.getCurrentHighestBidCoins());
                dto.setStartTime(event.getStartTime());
                dto.setEndTime(event.getEndTime());
                dto.setCreationYear(artwork.getDate_time());
                dto.setDimensions(artwork.getDiamensions());

                cardList.add(dto);

            }
        }
        
        

        Gson gson1 = new Gson();
        jsonObject.add("betting_event_cards", gson1.toJsonTree(cardList));

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(jsonObject));

        session.close();
    }
}
