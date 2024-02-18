package pl.materus.ghrepo.model;
import java.util.List;

public class ResponseRepositoryModel {
    
    String name;
    String owner;
    List<ResponseBranchModel> branches;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public List<ResponseBranchModel> getBranches() {
        return branches;
    }
    public void setBranches(List<ResponseBranchModel> branches) {
        this.branches = branches;
    }
}
