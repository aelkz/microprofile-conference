package io.microprofile.showcase.speaker.rest;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.microprofile.showcase.speaker.model.Speaker;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.equalTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.given;

public class SpeakerRestMockTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().port(8080));

    String allegra = "{\n" +
        "       \"id\": 99,\n" +
        "       \"nameFirst\": \"Allegra\",\n" +
        "       \"nameLast\": \"Morrison\",\n" +
        "       \"organization\": \"Leo Vivamus PC\",\n" +
        "       \"biography\": \"Lorem ipsum dolor sit amet, consectetuer\",\n" +
        "       \"picture\": \"assets/images/unknown.jpg\",\n" +
        "       \"twitterHandle\": \"\",\n" +
        "       \"links\": { }, \n" +
        "       \"title\": \"\"\n" +
        "       }";

    @Test
    public void testSearch() {
        wireMockRule.stubFor(put("/speaker")
            .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(allegra)));

        Speaker speaker = new Speaker();
        speaker.setNameFirst("Al");
        speaker.setNameLast("Morrison");
        given().body(speaker).when().put("/speaker").then().body("nameFirst",equalTo("Allegra"));
    }

    @Test
    public void testSearchById() {
        wireMockRule.stubFor(get(urlMatching("/speaker/99"))
            .willReturn(aResponse().withStatus(200).withHeader("Content-Type","application/json").withBody(allegra)));

        given().when().get("/speaker/99").then().body("nameFirst",equalTo("Allegra"));
    }


}
