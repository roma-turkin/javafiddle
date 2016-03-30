package ru.javafiddle.tests;

import com.jayway.restassured.authentication.FormAuthConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ru.javafiddle.tests.utils.SystemOutFilter;


import javax.ws.rs.core.Response;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

/**
 * Created by artyom on 28.03.16.
 */
@RunWith(JUnit4.class)
public class AuthorizationTest {

    private final String HOME_PAGE_URL = "https://localhost:8181/javaFiddle";
    private final String EXICTING_USER_NICKNAME = "atsanda";
    private final String EXICTING_USER_PASSWORD = "03061995";
    private final FormAuthConfig formAuthConfig = new FormAuthConfig("j_security_check", "j_username", "j_password");


    @Test
    public void shouldLoadLoginPage() {

        com.jayway.restassured.response.Response r = given().
                filter(new SystemOutFilter()).
                relaxedHTTPSValidation().
                auth().form(EXICTING_USER_NICKNAME, EXICTING_USER_PASSWORD, formAuthConfig).
        expect().
                body(containsString("j_security_check")).
                body(containsString("j_username")).
                body(containsString("j_password")).
                statusCode(200).
        when().
                get(HOME_PAGE_URL);

        System.out.println(r.body());

    }

    @Test
    public void shouldLoadHomePage() {
        com.jayway.restassured.response.Response r = given().
                filter(new SystemOutFilter()).
                relaxedHTTPSValidation().
                auth().form(EXICTING_USER_NICKNAME, EXICTING_USER_PASSWORD, formAuthConfig).
        expect().
                body(containsString("j_security_check")).
                body(containsString("j_username")).
                body(containsString("j_password")).
                statusCode(200).
        when().
                get(HOME_PAGE_URL + "/fiddle/users");

        System.out.println(r.body());
    }

    @Test
    public void shouldDeleteUser() {
        com.jayway.restassured.response.Response r = given().
                filter(new SystemOutFilter()).
                relaxedHTTPSValidation().
                auth().form(EXICTING_USER_NICKNAME, EXICTING_USER_PASSWORD, formAuthConfig).
        expect().
                body(containsString("j_security_check")).
                body(containsString("j_username")).
                body(containsString("j_password")).
                statusCode(200).
        when().
                delete(HOME_PAGE_URL + "/fiddle/users/newUser");

        System.out.println(r.body());
    }

}
