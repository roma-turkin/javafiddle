

import ru.javafiddle.jpa.entity.Access;
import ru.javafiddle.jpa.entity.Status;
import ru.javafiddle.jpa.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


/**
 * Created by artyom on 06.02.16.
 */
public class Test {
    private static final String DEFAULT_USER_STATUS = "registered";

    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("JFPersistenceUnit");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        Status e3 = entityManager.find(Status.class, 1);
        System.out.println(e3.getStatusName());

        Status status = (Status)entityManager.createQuery("SELECT s FROM Status s WHERE s.statusName =:status")
                .setParameter("status", DEFAULT_USER_STATUS)
                .getSingleResult();

        System.out.println(status.getStatusName());

        User user = (User)entityManager.createQuery("SELECT u FROM User u WHERE u.nickName =:nickName")
                .setParameter("nickName", "teddy")
                .getSingleResult();

        System.out.println(user.getLastName());

        entityManager.close();
        entityManagerFactory.close();
    }
}
