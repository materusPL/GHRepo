package pl.materus.ghrepo.model;

public record ResponseErrorModel(
        Integer status,
        String message) {

    public ResponseErrorModel(Integer status, String message) {
        this.status = status;
        this.message = message;
    }
}
