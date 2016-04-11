package ru.javafiddle.tests;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.authentication.FormAuthConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.config.SSLConfig;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


import javax.ws.rs.core.Response;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

/**
 * ATTENTION: you must provide an existence of testUser in database
 * Created by artyom on 28.03.16.
 */
@RunWith(JUnit4.class)
public class AuthorizationTest {

    private final String VALID_NICKNAME = "testUser";
    private final String VALID_PASSWORD = "testUser";
    private final String INVALID_PASSWORD = "******";
    private final FormAuthConfig formAuthConfig = new FormAuthConfig("/j_security_check", "j_username", "j_password");

    @BeforeClass
    public static void autoAuth() {
        RestAssured.basePath = "/javaFiddle";
        RestAssured.baseURI = "https://localhost:8181";
        RestAssured.config = RestAssuredConfig.config().sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation("SSL"));
    }


    /**
     * Test compares body of returned html page with peace of index.html
     */
    @Test
    public void shouldAuthorize() {

        given().
                auth().form(VALID_NICKNAME, VALID_PASSWORD, formAuthConfig).
        expect().
                statusCode(Response.Status.OK.getStatusCode()).
                body(containsString("<div id=\"logo\"><span class=\"bracket\">&lt;</span>\n"
                        + "                    <h2><a href=\"/javaFiddle/jsf/index.xhtml\">JavaFiddle</a></h2><span class=\"bracket\">&gt;</span>\n"
                        + "                </div><div id=\"projectname\">NetCrackerTasks</div>")).
        when().
                get();

    }

    @Test
    public void shouldRedirectToAuthPage() {

        given().
                auth().form(VALID_NICKNAME, INVALID_PASSWORD, formAuthConfig).
        expect().
                statusCode(Response.Status.OK.getStatusCode()).
                body(containsString("j_security_check")).
                body(containsString("j_username")).
                body(containsString("j_password")).
        when().
                get();

    }


}
