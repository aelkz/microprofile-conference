package io.microprofile.showcase.speaker.rest;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.arquillian.CreateSwarm;

import java.io.File;
import java.net.URL;
import java.util.Properties;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

@RunWith(Arquillian.class)
public class MockResourceSpeakerTest {

    private URL url;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().port(7070));

    String sessions =
        "[\"100\", " +
        "\"101\","+
        "\"102\" ]";

    @Deployment
    public static WebArchive deploy() {

        File[] deps = Maven.resolver().loadPomFromFile("pom.xml").importDependencies(ScopeType.COMPILE, ScopeType.RUNTIME, ScopeType.TEST).resolve().withTransitivity().asFile();

        WebArchive wrap = ShrinkWrap.create(WebArchive.class,
            ResourceSpeakerTest.class.getName() + ".war")
            .addPackages(true, "io.microprofile.showcase.speaker")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsManifestResource("META-INF/microprofile-config.properties", "microprofile-config.properties")
            .addAsLibraries(deps);
        return wrap;
        }

        @CreateSwarm
        public static Swarm newContainer() throws Exception {
            Properties properties = new Properties();
            properties.put("swarm.http.port", "8080");
            properties.put("java.util.logging.manager", "org.jboss.logmanager.LogManager");
            Swarm swarm = new Swarm(properties);
            return swarm.withProfile("defaults");
        }

        @Test
        public void testGet() throws Exception {
            wireMockRule.stubFor(get(urlMatching("/sessions/speaker/speakerId/99"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application-json")
                .withBody(sessions)));

            given().when().get("/speaker/sessions/speakerId/99").then().body("size()",is(3));
        }

        @Test
        public void testGetSessions() throws Exception {
            wireMockRule.stubFor(get(urlMatching("/sessions/speaker/amount/515"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application-json")
                    .withBody("10")));

            given().when().get("/speaker/sessions/amount/515").then().body(containsString("10"));
        }


}
