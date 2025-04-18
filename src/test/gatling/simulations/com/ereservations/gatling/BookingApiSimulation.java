package com.ereservations.gatling;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class BookingApiSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://restful-booker.herokuapp.com")
            .header("Accept", "application/json")
            .header("Content-Type", "application/json");

    private final ScenarioBuilder scn = scenario("Booking API Load Test")
            .exec(
                http("Get All Booking IDs")
                    .get("/booking")
                    .check(
                        status().is(200),
                        jmesPath("[*]").exists()
                    )
            );

    {
        setUp(
            scn.injectOpen(
                rampUsers(10).during(5)
            )
        ).protocols(httpProtocol)
         .assertions(
            global().responseTime().max().lt(2000),
            global().successfulRequests().percent().gt(95.0)
         );
    }
} 