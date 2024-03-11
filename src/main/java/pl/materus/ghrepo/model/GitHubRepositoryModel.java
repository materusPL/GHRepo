package pl.materus.ghrepo.model;

public record GitHubRepositoryModel(
        String name,
        GitHubRepositoryOwnerModel owner,
        Boolean fork) {
}
