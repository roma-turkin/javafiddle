package ru.javafiddle.tests.utils;

import com.jayway.restassured.filter.Filter;
import com.jayway.restassured.filter.FilterContext;
import com.jayway.restassured.response.Cookies;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import com.jayway.restassured.specification.FilterableResponseSpecification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by artyom on 28.03.16.
 */
public class SystemOutFilter implements Filter {

    private static Map<String, String> cookies;

    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        if (cookies != null) {
            requestSpec.cookies(cookies);
        }

        final Response response = ctx.next(requestSpec, responseSpec); // Invoke the request by delegating to the next filter in the filter chain.
        if(response.getCookies() != null) {
            if (cookies == null) {
                cookies = new HashMap<>();
            }
            cookies.putAll(response.getCookies());
        }
        return response;
    }
}
