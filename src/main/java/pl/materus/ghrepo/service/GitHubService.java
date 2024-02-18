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
                .filter(repo -> !repo.getFork())
                .flatMap(this::createResponse)
                .flatMap(repo -> getRepoBranches(repo.getOwner(), repo.getName())
                        .collectList()
                        .map(branches -> {
                            repo.setBranches(branches);
                            return repo;
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

    private Mono<ResponseRepositoryModel> createResponse(GitHubRepositoryModel repository) {
        ResponseRepositoryModel responseRepositoryModel = new ResponseRepositoryModel();
        responseRepositoryModel.setName(repository.getName());
        responseRepositoryModel.setOwner(repository.getOwner().getLogin());
        return Mono.just(responseRepositoryModel);
    }

    private Mono<ResponseBranchModel> createResponseBranch(GitHubBranchModel branch) {
        ResponseBranchModel responseBranchModel = new ResponseBranchModel();
        responseBranchModel.setName(branch.getName());
        responseBranchModel.setSha(branch.getCommit().getSha());
        return Mono.just(responseBranchModel);
    }

}