package controler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.Response_DTO;
import dto.User_DTO;
import entity.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SignIn", urlPatterns = {"/SignIn"})
public class SignIn extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest requset, HttpServletResponse response) throws ServletException, IOException {
        
        Response_DTO response_DTO = new Response_DTO();
        
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        User_DTO user_DTO = gson.fromJson(requset.getReader(), User_DTO.class);

        
        if (user_DTO.getEmail().isEmpty()) {
            response_DTO.setContent("Please enter your Email");
            
        } else if (user_DTO.getPassword().isEmpty()) {
            response_DTO.setContent("Please enter your Password");
            
        } else {
            
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("email", user_DTO.getEmail()));
            criteria.add(Restrictions.eq("password", user_DTO.getPassword()));
            
            if (!criteria.list().isEmpty()) {
                
                User user = (User) criteria.uniqueResult();
                
                if (!user.getVerificationCode().equals("Verified")) {
                    //not verified

                    requset.getSession().setAttribute("email", user_DTO.getEmail());
                    response_DTO.setContent("Unverified");
                    
                } else {
                    //verified
                    user_DTO.setUserId(user.getUserId());
                    user_DTO.setFirstName(user.getFirstName());
                    user_DTO.setLastName(user.getLastName());
                    user_DTO.setMobile(user.getMobile());
                    user_DTO.setEmail(user.getEmail());
                    user_DTO.setIsArtist(user.getIsArtist());
                    user_DTO.setAddress(user.getAddress());
                    user_DTO.setIsAdmin(user.getIsAdmin()); 
                    user_DTO.setPassword(null);
                    requset.getSession().setAttribute("user", user_DTO);
           
                    response_DTO.setSuccess(true);
                    response_DTO.setContent("Sign in success");
                }
                
            } else {
                response_DTO.setContent("Invalid Details!");
            }
        }
        
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));
        
    }
    
}