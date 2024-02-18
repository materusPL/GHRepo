package pl.materus.ghrepo.model;

public class GitHubRepositoryModel {
    String name;

    GitHubRepositoryOwnerModel owner;

    Boolean fork;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GitHubRepositoryOwnerModel getOwner() {
        return owner;
    }

    public void setOwner(GitHubRepositoryOwnerModel owner) {
        this.owner = owner;
    }

    public Boolean getFork() {
        return fork;
    }

    public void setFork(Boolean fork) {
        this.fork = fork;
    }
}
