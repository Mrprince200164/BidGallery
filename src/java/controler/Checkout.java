package controler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.User_DTO;
import entity.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import model.HibernateUtil;
import model.Payhere;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "Checkout", urlPatterns = {"/Checkout"})
public class Checkout extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject requestJson = gson.fromJson(request.getReader(), JsonObject.class);
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("success", false);

        HttpSession httpSession = request.getSession();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            String packageType = requestJson.get("packageType").getAsString();

            // Check if user is logged in
            User_DTO userDTO = (User_DTO) httpSession.getAttribute("user");
            if (userDTO == null) {
                jsonResponse.addProperty("message", "User not signed in.");
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }

            // Fetch User
            User user = (User) session.createCriteria(User.class)
                    .add(Restrictions.eq("email", userDTO.getEmail()))
                    .uniqueResult();

            if (user == null) {
                jsonResponse.addProperty("message", "User not found.");
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }

            // Fetch Coin Package
            CoinPackage coinPackage = (CoinPackage) session.createCriteria(CoinPackage.class)
                    .add(Restrictions.eq("packageName", packageType))
                    .add(Restrictions.eq("isActive", 1))
                    .uniqueResult();

            if (coinPackage == null) {
                jsonResponse.addProperty("message", "Invalid package.");
                response.getWriter().write(gson.toJson(jsonResponse));
                return;
            }

            int coins = coinPackage.getCoinAmount();
            double price = coinPackage.getPricePerUnit();

            // Prepare transaction
            TransactionType txnType = (TransactionType) session.get(TransactionType.class, 1);
            TransactionStatus status = (TransactionStatus) session.get(TransactionStatus.class, 1);

            tx = session.beginTransaction();

            entity.Transaction transaction = new entity.Transaction();
            transaction.setUser(user);
            transaction.setType(txnType);
            transaction.setStatus(status);
            transaction.setCoinAmount(coins);
            transaction.setRealMoneyAmount(price);
            transaction.setTransactionTime(new Date());

            session.save(transaction);

            // Update coin balance
            user.setCoinBalance(user.getCoinBalance() + coins);
            session.update(user);

            tx.commit();

            // Generate hash
            String merchantId = "1226970";
            String merchantSecret = "NDE4MTU0MzE1NzMxMzU3MTIzNTAyMjk4MDc1MTgyOTY4MTI1NDkw";
            String orderId = "COINPKG-" + transaction.getTransactionId();
            String amountFormatted = new DecimalFormat("0.00").format(price);
            String currency = "LKR";

            String hash = Payhere.generateMD5(
                    merchantId + orderId + amountFormatted + currency +
                    Payhere.generateMD5(merchantSecret)
            );

            JsonObject payhereJson = new JsonObject();
            payhereJson.addProperty("sandbox", true);
            payhereJson.addProperty("merchant_id", merchantId);
            payhereJson.addProperty("return_url", "");  // Optional
            payhereJson.addProperty("cancel_url", "");  // Optional
            payhereJson.addProperty("notify_url", "");  // Optional
            payhereJson.addProperty("order_id", orderId);
            payhereJson.addProperty("items", coinPackage.getPackageName() + " Package");
            payhereJson.addProperty("amount", amountFormatted);
            payhereJson.addProperty("currency", currency);
            payhereJson.addProperty("hash", hash);

            payhereJson.addProperty("first_name", user.getFirstName());
            payhereJson.addProperty("last_name", user.getLastName());
            payhereJson.addProperty("email", user.getEmail());
            payhereJson.addProperty("phone", user.getMobile());
            payhereJson.addProperty("address", user.getAddress() != null ? user.getAddress() : "");
            payhereJson.addProperty("city", "Colombo");
            payhereJson.addProperty("country", "Sri Lanka");

            jsonResponse.addProperty("success", true);
            jsonResponse.add("payhereJson", gson.toJsonTree(payhereJson));

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            jsonResponse.addProperty("message", "Unexpected error: " + e.getMessage());
        } finally {
            session.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(jsonResponse));
    }
}
