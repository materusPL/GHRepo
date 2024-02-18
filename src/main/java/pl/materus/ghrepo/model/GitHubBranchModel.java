package pl.materus.ghrepo.model;


public class GitHubBranchModel {
    String name;

    GitHubBranchCommitModel commit;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GitHubBranchCommitModel getCommit() {
        return commit;
    }

    public void setCommit(GitHubBranchCommitModel commit) {
        this.commit = commit;
    }
}
