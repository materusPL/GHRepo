package pl.materus.ghrepo.controller;

import pl.materus.ghrepo.exception.WrongHeaderException;
import pl.materus.ghrepo.model.ResponseErrorModel;
import pl.materus.ghrepo.model.ResponseRepositoryModel;
import pl.materus.ghrepo.service.GitHubService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Flux;

@RestController
public class GitHubController {

    private final GitHubService gitHubService;

    @Autowired
    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<Flux<ResponseRepositoryModel>> getUserRepos(
            @RequestHeader(name = HttpHeaders.ACCEPT) String acceptHeader, @PathVariable String username) {
        
        if (!acceptHeader.contains("application/json")) {
            throw new WrongHeaderException("Request does not contain the 'application/json' header.");
        }
        HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return ResponseEntity.ok().headers(httpHeaders).body(gitHubService.getUserRepos(username));
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ResponseErrorModel> handleResponseException(WebClientResponseException ex) {
        ResponseErrorModel responseErrorModel = new ResponseErrorModel();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        responseErrorModel.setStatus(ex.getStatusCode().value());
        switch (responseErrorModel.getStatus()) {
            case 404:
                responseErrorModel.setMessage("User not found");
                status = HttpStatus.NOT_FOUND;
                break;

            case 403:
                responseErrorModel.setMessage("Github API limit");
                status = HttpStatus.FORBIDDEN;
                break;

            default:
                responseErrorModel.setMessage("Unknown github webclient error");
                break;

        }

        return ResponseEntity.status(status).headers(httpHeaders).body(responseErrorModel);

    }

    @ExceptionHandler(WrongHeaderException.class)
    public ResponseEntity<ResponseErrorModel> handleResponseException(WrongHeaderException ex) {
        ResponseErrorModel responseErrorModel = new ResponseErrorModel();
        responseErrorModel.setStatus(HttpStatus.BAD_REQUEST.value());
        responseErrorModel.setMessage(ex.getMessage());
        
        HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        return ResponseEntity.badRequest().headers(httpHeaders).body(responseErrorModel);
    }
}
