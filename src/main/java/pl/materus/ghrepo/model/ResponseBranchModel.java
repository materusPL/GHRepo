package pl.materus.ghrepo.model;

public class ResponseBranchModel{
    String name;
    String sha;


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSha() {
        return sha;
    }
    public void setSha(String sha) {
        this.sha = sha;
    }
}
