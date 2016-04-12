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
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * ATTENTION: you must provide an existence of testUser in database
 * Project with corresponding hash and files also must exist
 * Created by artyom on 11.04.16.
 */
@RunWith(JUnit4.class)
public class ProjectServiceTest {

    private final String USER_PROJECT_HASH = "7777777";
    private final String USER_PROJECT_NAME = "projectTest";

    private final String SERVICES_PATH = "/fiddle";

    @BeforeClass
    public static void autoAuth() {
        RestAssured.basePath = "/javaFiddle";
        RestAssured.baseURI = "https://localhost:8181";
        RestAssured.config = RestAssuredConfig.config().sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation("SSL"));
        RestAssured.authentication = RestAssured.form("testUser", "testUser", new FormAuthConfig("/j_security_check", "j_username", "j_password"));
    }

    @Test
    public void shouldGetUserProjects() {

        expect().
                body(equalTo("[\"" + USER_PROJECT_HASH + "\"]")).
                contentType(ContentType.JSON).
                statusCode(Response.Status.OK.getStatusCode()).
        when().
                get(SERVICES_PATH + "/projects");
    }

    @Test
    public void shouldGetProjectStructure() {

        expect().
                body("name", equalTo(USER_PROJECT_NAME)).
                body(containsString("type")).
                body(containsString("fileId")).
                body(containsString("childNodes")).
                contentType(ContentType.JSON).
                statusCode(Response.Status.OK.getStatusCode()).
        when().
                get(SERVICES_PATH + "/projects/" + USER_PROJECT_HASH);
    }
}
