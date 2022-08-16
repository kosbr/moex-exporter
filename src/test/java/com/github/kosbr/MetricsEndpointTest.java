package com.github.kosbr;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class MetricsEndpointTest {

    // todo more tests

    @Test
    public void testMetricsEndpointWorks() {
        given()
          .when().get("/q/metrics")
          .then()
             .statusCode(200);
    }
}