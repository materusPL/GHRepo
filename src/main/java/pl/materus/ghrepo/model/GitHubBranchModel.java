package pl.materus.ghrepo.model;

public record GitHubBranchModel(
        String name,
        GitHubBranchCommitModel commit) {
}
