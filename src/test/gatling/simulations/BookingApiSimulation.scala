package com.ereservations.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BookingApiSimulation extends Simulation {
  val httpProtocol = http
    .baseUrl("https://restful-booker.herokuapp.com")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
    .userAgentHeader("Gatling Performance Test")

  val bookingData = Map(
    "firstname" -> "John",
    "lastname" -> "Doe",
    "totalprice" -> 100,
    "depositpaid" -> true,
    "bookingdates" -> Map(
      "checkin" -> "2024-01-01",
      "checkout" -> "2024-01-05"
    ),
    "additionalneeds" -> "Breakfast"
  )

  val updateBookingData = Map(
    "firstname" -> "Jane",
    "lastname" -> "Doe",
    "totalprice" -> 150,
    "depositpaid" -> true,
    "bookingdates" -> Map(
      "checkin" -> "2024-02-01",
      "checkout" -> "2024-02-05"
    ),
    "additionalneeds" -> "Lunch"
  )

  val createBooking = scenario("Create Booking")
    .exec(
      http("Create Booking Request")
        .post("/booking")
        .body(StringBody(bookingData.toString()))
        .check(status.is(200))
        .check(jsonPath("$.bookingid").saveAs("bookingId"))
    )

  val getBooking = scenario("Get Booking")
    .exec(
      http("Get Booking Request")
        .get("/booking/${bookingId}")
        .check(status.is(200))
    )

  val updateBooking = scenario("Update Booking")
    .exec(
      http("Update Booking Request")
        .put("/booking/${bookingId}")
        .header("Cookie", "token=${token}")
        .body(StringBody(updateBookingData.toString()))
        .check(status.is(200))
    )

  val deleteBooking = scenario("Delete Booking")
    .exec(
      http("Delete Booking Request")
        .delete("/booking/${bookingId}")
        .header("Cookie", "token=${token}")
        .check(status.is(201))
    )

  setUp(
    createBooking.inject(rampUsers(10).during(10.seconds)),
    getBooking.inject(rampUsers(20).during(20.seconds)),
    updateBooking.inject(rampUsers(15).during(15.seconds)),
    deleteBooking.inject(rampUsers(5).during(5.seconds))
  ).protocols(httpProtocol)
} 