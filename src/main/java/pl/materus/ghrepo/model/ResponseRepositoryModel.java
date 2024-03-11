package pl.materus.ghrepo.model;

import java.util.List;

public record ResponseRepositoryModel(
        String name,
        String owner,
        List<ResponseBranchModel> branches) {
}
