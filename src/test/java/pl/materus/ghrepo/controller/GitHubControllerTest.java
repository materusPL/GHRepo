package pl.materus.ghrepo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

import pl.materus.ghrepo.exception.WrongHeaderException;
import pl.materus.ghrepo.util.ResourceReaderUtil;

@SpringBootTest
@TestPropertySource(properties = "github-url=http://127.0.0.1:9123")
public class GitHubControllerTest {
    @Autowired
    GitHubController githubController;

    @RegisterExtension
    private static WireMockExtension wireMockExtension = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(9123).bindAddress("127.0.0.1"))
            .build();

    @SuppressWarnings("null")
    @Test
    void testNotFound() {
        wireMockExtension.stubFor(WireMock.get(WireMock.urlPathEqualTo("/users/notFound/repos"))
                .willReturn(WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(404)
                        .withBody(ResourceReaderUtil.asString("classpath:jsonAnswers/notFound.json"))));

        WebClientResponseException e = assertThrows(WebClientResponseException.class, () -> {
            var body = githubController.getUserRepos("application/json", "notFound").getBody();
            if (body != null)
                body.collectList().block();
        });
        assertEquals(e.getStatusCode().value(), 404);

        var response = githubController.handleResponseException(e);
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().status());
        assertEquals("User not found", response.getBody().message());
    }

    @SuppressWarnings("null")
    @Test
    void testNoHeader() {
        WrongHeaderException e = assertThrows(WrongHeaderException.class,
                () -> githubController.getUserRepos("", "shouldThrow"));

        var response = githubController.handleResponseException(e);
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Request does not contain the 'application/json' header.", response.getBody().message());
    }

    @SuppressWarnings("null")
    @Test
    void testApiLimits() {
        wireMockExtension.stubFor(WireMock.get(WireMock.urlPathEqualTo("/users/forbidden/repos"))
                .willReturn(WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(403)
                        .withBody(ResourceReaderUtil.asString("classpath:jsonAnswers/rateLimit.json"))));
        WebClientResponseException e = assertThrows(WebClientResponseException.class,
                () -> {
                    var body = githubController.getUserRepos("application/json", "forbidden").getBody();
                    if (body != null)
                        body.collectList().block();
                });

        var response = githubController.handleResponseException(e);
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().status());
        assertEquals("Github API limit", response.getBody().message());
    }
}
