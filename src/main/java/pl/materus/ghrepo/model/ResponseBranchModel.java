package pl.materus.ghrepo.model;

public record ResponseBranchModel(
        String name,
        String sha) {
    public ResponseBranchModel(String name, String sha)
    {
        this.name = name;
        this.sha = sha;
    }
}
