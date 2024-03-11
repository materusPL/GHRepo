package pl.materus.ghrepo.service;

import pl.materus.ghrepo.util.ResourceReaderUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

@SpringBootTest
@TestPropertySource(properties = "github-url=http://127.0.0.1:9123")
public class GitHubServiceTest {

    @Autowired
    GitHubService gitHubService;

    @RegisterExtension
    private static WireMockExtension wireMockExtension = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(9123).bindAddress("127.0.0.1"))
            .build();

    private void prepareRealUser() {

        wireMockExtension.stubFor(WireMock.get(WireMock.urlPathEqualTo("/users/materusPL/repos"))
                .willReturn(WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(ResourceReaderUtil.asString("classpath:jsonAnswers/RealUser.json"))));

        wireMockExtension.stubFor(WireMock.get(WireMock.urlPathEqualTo("/repos/materusPL/materusPL/branches"))
                .willReturn(WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(ResourceReaderUtil.asString("classpath:jsonAnswers/RealBranch1.json"))));
        wireMockExtension.stubFor(WireMock.get(WireMock.urlPathEqualTo("/repos/materusPL/Nixerus/branches"))
                .willReturn(WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(ResourceReaderUtil.asString("classpath:jsonAnswers/RealBranch2.json"))));

        wireMockExtension.stubFor(WireMock.get(WireMock.urlPathEqualTo("/repos/materusPL/nixos-config/branches"))
                .willReturn(WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(ResourceReaderUtil.asString("classpath:jsonAnswers/RealBranch3.json"))));

        wireMockExtension.stubFor(WireMock.get(WireMock.urlPathEqualTo("/repos/materusPL/nixpkgs/branches"))
                .willReturn(WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(ResourceReaderUtil.asString("classpath:jsonAnswers/RealBranch4.json"))));

        wireMockExtension.stubFor(WireMock.get(WireMock.urlPathEqualTo("/repos/materusPL/SNOL/branches"))
                .willReturn(WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(ResourceReaderUtil.asString("classpath:jsonAnswers/RealBranch5.json"))));
    }

    @Test
    public void testRealUser() {
        prepareRealUser();

        var response = gitHubService.getUserRepos("materusPL").collectList().block();
        response.sort((repo1, repo2) -> {
            return repo1.name().compareTo(repo2.name());
        });

        assertEquals("Nixerus", response.get(0).name());
        assertEquals("SNOL", response.get(1).name());
        assertEquals("materusPL", response.get(2).name());
        assertEquals("nixos-config", response.get(3).name());

        String owner = "materusPL";

        assertEquals(owner, response.get(0).owner());
        assertEquals(owner, response.get(1).owner());
        assertEquals(owner, response.get(2).owner());
        assertEquals(owner, response.get(3).owner());

        assertEquals("master", response.get(2).branches().get(0).name());
        assertEquals("fd6867d8963147ba40d3df428045aec82f14dbe3", response.get(2).branches().get(0).sha());

        assertEquals(3, response.get(0).branches().size());
        assertEquals(2, response.get(3).branches().size());
        assertEquals(1, response.get(2).branches().size());

    }

    @Test
    public void testUserWithoutRepositories() {
        wireMockExtension.stubFor(WireMock.get(WireMock.urlPathEqualTo("/users/empty/repos"))
                .willReturn(WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody("[]")));

        var response = gitHubService.getUserRepos("empty").collectList().block();
        assertEquals(0, response.size());
    }

    @Test
    public void testForkOnly() {
        wireMockExtension.stubFor(WireMock.get(WireMock.urlPathEqualTo("/users/fork/repos"))
                .willReturn(WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(ResourceReaderUtil.asString("classpath:jsonAnswers/forkOnly.json"))));

        var response = gitHubService.getUserRepos("fork").collectList().block();
        assertEquals(0, response.size());
    }

    @Test
    public void testNotFound() {
        wireMockExtension.stubFor(WireMock.get(WireMock.urlPathEqualTo("/users/notFound/repos"))
                .willReturn(WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(404)
                        .withBody(ResourceReaderUtil.asString("classpath:jsonAnswers/notFound.json"))));

        WebClientResponseException e = assertThrows(WebClientResponseException.class, () -> {
            gitHubService.getUserRepos("notFound").collectList().block();
        });
        assertEquals(404, e.getStatusCode().value());
    }
}
