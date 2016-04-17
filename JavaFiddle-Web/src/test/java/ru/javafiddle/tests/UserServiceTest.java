package ru.javafiddle.tests;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.authentication.FormAuthConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.config.SSLConfig;
import com.jayway.restassured.http.ContentType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.ws.rs.core.Response;

import static com.jayway.restassured.RestAssured.expect;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * ATTENTION: you must provide an existence of testUser in database
 * with appropriate parameters
 * Created by artyom on 11.04.16.
 */
@RunWith(JUnit4.class)
public class UserServiceTest {

    private final String USER_NICKNAME  = "testUser";
    private final String USER_FIRSTNAME = "testUser";
    private final String USER_LASTNAME  = "testUser";
    private final String USER_EMAIL     = "testUser@test.user";

    private final String SERVICES_PATH = "/fiddle";

    @BeforeClass
    public static void autoAuth() {
        RestAssured.basePath = "/javaFiddle";
        RestAssured.baseURI = "https://localhost:9090";
        RestAssured.config = RestAssuredConfig.config().sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation("SSL"));
        RestAssured.authentication = RestAssured.form("testUser", "testUser", new FormAuthConfig("/j_security_check", "j_username", "j_password"));
    }

    @Test
    public void shouldGetUserInfo() {

        expect().
                body(
                        "firstName",    equalTo(USER_FIRSTNAME),
                        "lastName",     equalTo(USER_LASTNAME),
                        "nickName",     equalTo(USER_NICKNAME),
                        "email",        equalTo(USER_EMAIL)
                ).
                contentType(ContentType.JSON).
                statusCode(Response.Status.OK.getStatusCode()).
        when().
                get(SERVICES_PATH + "/user");
    }
}
