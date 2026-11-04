package controler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Artwork;
import entity.ArtworkStatus;
import entity.Medium;
import java.io.IOException;
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
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadFeatures", urlPatterns = {"/LoadFeatures"})
public class LoadFeatures extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();

        Session session = HibernateUtil.getSessionFactory().openSession();

        Criteria criteria1 = session.createCriteria(Medium.class);
        criteria1.addOrder(Order.asc("mediumName"));
        List<Medium> mediumList = criteria1.list();

        Criteria criteria2 = session.createCriteria(ArtworkStatus.class);
        criteria2.addOrder(Order.asc("statusName"));
        List<ArtworkStatus> artworkstatusList = criteria2.list();

        Criteria criteria3 = session.createCriteria(Artwork.class);
        criteria3.add(Restrictions.eq("Artwork_Status.statusId", 2));
        List<Artwork> artworksList = criteria3.list();

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("mediumList", gson.toJsonTree(mediumList));
        jsonObject.add("artworkstatusList", gson.toJsonTree(artworkstatusList));
        jsonObject.add("artworksList", gson.toJsonTree(artworksList));

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(jsonObject));

        session.close();

    }

}
