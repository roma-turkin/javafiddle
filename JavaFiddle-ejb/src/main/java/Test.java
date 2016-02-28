

import ru.javafiddle.core.ejb.ProjectBean;
import ru.javafiddle.jpa.entity.*;
import ru.javafiddle.core.ejb.UserBean;
import ru.javafiddle.core.ejb.GroupBean;

import javax.faces.event.SystemEvent;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;


/**
 * Created by artyom on 06.02.16.
 */
public class Test {
    private static final String DEFAULT_USER_STATUS = "registered";

    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("JFPersistenceUnit");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

     /*   Status e3 = entityManager.find(Status.class, 1);
        System.out.println(e3.getStatusName());*/

       // ProjectBean pb = new ProjectBean();
       // pb.createProject("myProg", "default", entityManager);
       // pb.changeProjectName("123456789", "Hello", entityManager);
       // Project p = pb.getProject("123456789", entityManager);

        UserBean u = new UserBean();
       // u.deleteUser("atsanda",entityManager);
       // User uu = u.register("Nastia", "Ruzh", "skotti", "aa", "12345", entityManager);
       // User uu1 = u.register("Nast", "Aruzh", "barny", "aaa", "123456", entityManager);
        //System.out.println(u.getUser("barny",entityManager));
        //pb.deleteProject("123456789", entityManager);
       // pb.deleteProject("101");
        //System.out.println(p.getProjectName());

        //UserBean u = new UserBean();
        //User u1 = u.getUser("atsanda");
        //System.out.println(u1.getEmail());
        //GroupBean g = new GroupBean();
        //Group g1 = g.getGroup(1, entityManager);
        //System.out.println(g1.getGroupId());
       // Access ac = g.getAccess("ac", entityManager);
       // System.out.println(ac.getAccessName());

        //Status status = (Status)entityManager.createQuery("SELECT s FROM Status s WHERE s.statusId = 1")
        //        //.setParameter("status", DEFAULT_USER_STATUS)
        //        .getSingleResult();

        //System.out.println(status.getStatusName());

        //User user = (User)entityManager.createQuery("SELECT u FROM User u WHERE u.nickName =:nickName")
         //       .setParameter("nickName", "teddy")
         //       .getSingleResult();

        //System.out.println(user.getLastName());

        entityManager.close();
        entityManagerFactory.close();
    }
}
