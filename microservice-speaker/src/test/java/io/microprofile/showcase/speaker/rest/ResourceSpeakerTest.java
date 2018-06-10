/*
 * Copyright(c) 2016-2017 IBM, Red Hat, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.microprofile.showcase.speaker.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.arquillian.CreateSwarm;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.microprofile.showcase.speaker.model.Speaker;
import io.restassured.http.ContentType;

@RunWith(Arquillian.class)
public class ResourceSpeakerTest {

    private final Logger log = Logger.getLogger(ResourceSpeakerTest.class.getName());

    @ArquillianResource
    private URL url;

    @Deployment
    public static WebArchive deploy() {
        File[] deps = Maven.resolver().loadPomFromFile("pom.xml")
            .importDependencies(ScopeType.COMPILE, ScopeType.RUNTIME).resolve()
            .withTransitivity().asFile();

        WebArchive wrap = ShrinkWrap.create(WebArchive.class,
            ResourceSpeakerTest.class.getName() + ".war")
            .addPackages(true, "io.microprofile.showcase.speaker")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsResource(new File("src/test/resources/ConferenceData.json"))
            .addAsManifestResource("META-INF/microprofile-config.properties", "microprofile-config.properties")
            .addAsLibraries(deps);

        return wrap;
    }


    @CreateSwarm
    public static Swarm newContainer() throws Exception {
        Properties properties = new Properties();
        properties.put("swarm.http.port", 8080);
        properties.put("java.util.logging.manager", "org.jboss.logmanager.LogManager");
        Swarm swarm = new Swarm(properties);
        return swarm.withProfile("defaults");
    }

    @Test
    @RunAsClient
    public void testGet() {
        given().when().contentType(ContentType.JSON).get("/speaker").then().statusCode(200).body("findAll.size()",is(200));
    }

    @Test
    @RunAsClient
    public void testSearch() throws JsonGenerationException, JsonMappingException, IOException {

        final Speaker search = new Speaker();
        search.setNameFirst("Indig");
        search.setNameLast("8");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(search);

        given().when()
            .with()
            .body(json)
            .contentType(ContentType.JSON)
            .put("speaker/search")
        .then()
        .statusCode(200)
        .body("findAll.size()", is(1));
    }

    @Test
    @RunAsClient
    public void testAddandRetrieve() throws IOException {
        // Instantiate the speaker that will be added to the speaker list.
        Speaker speaker = new Speaker();
        speaker.setNameFirst("Andy");
        speaker.setNameLast("Gumbrecht");
        speaker.setOrganization("Tomitribe");
        speaker.setTwitterHandle("@AndyGeeDe");
        speaker.setBiography("Some bloke");
        speaker.setPicture("http://pbs.twimg.com/profile_images/425313992689475584/KIrtgA86.jpeg");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(speaker);

        String result = given().when()
            .with()
            .body(json)
            .contentType(ContentType.JSON)
            .post("speaker/add")
        .then()
        .statusCode(200)
        .extract().asString();

        Speaker response = mapper.readValue(result, Speaker.class);
        assertThat(response, notNullValue());
        String value = response.getId();

        String returnedSpeaker = given().when()
            .contentType(ContentType.JSON)
            .get("/spekaer/retrieve/" +     value)
        .then()
        .statusCode(200)
        .extract().asString();

        Speaker resultSpeaker = mapper.readValue(returnedSpeaker, Speaker.class);
        assertThat(resultSpeaker, notNullValue());
        assertThat(resultSpeaker.getNameLast(), containsString("Gumbrecht"));
    }

    @Test
    @RunAsClient
    public void testUpdate() throws JsonParseException, JsonMappingException, IOException {

        final Speaker search = new Speaker();
        search.setNameFirst("Denton");
        search.setNameLast("Weaver");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(search);

        String result = given().when()
            .with()
            .body(json)
            .contentType(ContentType.JSON)
            .put("speaker/search")
        .then()
        .statusCode(200)
        .extract().asString();

        Speaker[] objects = mapper.readValue(result, Speaker[].class);
        assertThat(objects.length, is(1));
        assertThat(objects[0].getOrganization(), containsString("Odio"));

        objects[0].setOrganization("Erat Corporations");
        String output = mapper.writeValueAsString(objects[0]);

        given().when().with()
            .body(output)
            .contentType(ContentType.JSON)
            .put("speaker/update")
        .then()
            .statusCode(200);
    }

    @Test
    @RunAsClient
    public void testRemove() throws JsonParseException, JsonMappingException, IOException {

        // Look for the speaker Brent Collins (there are more than 1 actually)
        final Speaker search = new Speaker();
        search.setNameFirst("Brent");
        search.setNameLast("Collins");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(search);

        String result = given().when()
            .with()
            .body(json)
            .contentType(ContentType.JSON)
            .put("speaker/search")
        .then()
            .statusCode(200)
            .extract().asString();

        Speaker[] objects = mapper.readValue(result, Speaker[].class);

        final String id = objects[0].getId();

        given()
            .when()
                .contentType(ContentType.JSON)
                .delete("speaker/remove/" + id)
            .then()
                .statusCode(204);
    }

}
