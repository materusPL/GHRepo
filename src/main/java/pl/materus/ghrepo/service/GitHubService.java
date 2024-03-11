package pl.materus.ghrepo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import pl.materus.ghrepo.model.GitHubBranchModel;
import pl.materus.ghrepo.model.GitHubRepositoryModel;
import pl.materus.ghrepo.model.ResponseBranchModel;
import pl.materus.ghrepo.model.ResponseRepositoryModel;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class GitHubService {

    private final WebClient githubWebClient;

    @Autowired
    public GitHubService(WebClient githubWebClient) {
        this.githubWebClient = githubWebClient;
    }

    public Flux<ResponseRepositoryModel> getUserRepos(String username) {
        return githubWebClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .bodyToFlux(GitHubRepositoryModel.class)
                .filter(repo -> !repo.fork())
                .flatMap(this::createResponse)
                .flatMap(loginName -> getRepoBranches(loginName[0], loginName[1])
                        .collectList()
                        .map(branches -> {
                            return new ResponseRepositoryModel(loginName[1], loginName[0], branches);
                        }))
                .subscribeOn(Schedulers.boundedElastic());

    }

    private Flux<ResponseBranchModel> getRepoBranches(String owner, String repo) {
        return githubWebClient.get()
                .uri("/repos/{owner}/{repo}/branches", owner, repo)
                .retrieve()
                .bodyToFlux(GitHubBranchModel.class)
                .flatMap(branch -> createResponseBranch(branch));

    }

    private Mono<String[]> createResponse(GitHubRepositoryModel repository) {
        String[] ret = { repository.owner().login(),repository.name() };
        return Mono.just(ret);
    }

    private Mono<ResponseBranchModel> createResponseBranch(GitHubBranchModel branch) {
        ResponseBranchModel responseBranchModel = new ResponseBranchModel(branch.name(), branch.commit().sha());
        return Mono.just(responseBranchModel);
    }

}
