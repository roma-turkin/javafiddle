

import ru.javafiddle.jpa.entity.Access;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


/**
 * Created by artyom on 06.02.16.
 */
public class Test {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("JFPersistenceUnit");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        Access e3 = entityManager.find(Access.class, 1);
        System.out.println(e3.getAccessName());

        entityManager.close();
        entityManagerFactory.close();
    }
}
