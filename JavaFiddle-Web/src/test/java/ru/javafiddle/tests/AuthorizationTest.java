package ru.javafiddle.tests;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.authentication.FormAuthConfig;
import com.jayway.restassured.config.RedirectConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.config.SSLConfig;
import com.jayway.restassured.response.ValidatableResponse;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ru.javafiddle.tests.utils.SystemOutFilter;


import javax.ws.rs.core.Response;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

/**
 * Created by artyom on 28.03.16.
 */
@RunWith(JUnit4.class)
public class AuthorizationTest {

    private final String HOME_PAGE_URL = "https://localhost:8181/javaFiddle";
    private final String EXICTING_USER_NICKNAME = "newUser";
    private final String EXICTING_USER_PASSWORD = "newUser";
    private final FormAuthConfig formAuthConfig = new FormAuthConfig("/j_security_check", "j_username", "j_password");

    @BeforeClass
    public static void autoAuth() {
        RestAssured.basePath = "/javaFiddle/";
        RestAssured.baseURI = "https://localhost:8181";
        RestAssured.config = RestAssuredConfig.config().sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation("SSL"));
        RestAssured.authentication = RestAssured.form("atsanda", "03061995", new FormAuthConfig("j_security_check", "j_username", "j_password"));
    }




    @Test
    public void shouldLoadUserInfo() {

        expect().
                statusCode(200). //expect at least status 200
        when().
                get(HOME_PAGE_URL + "/fiddle/users").
        then().log().body();


    }


}
