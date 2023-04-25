package practice.workshop28.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

public class ReviewResult {
    private String rating;
    private List<Review> games;
    private LocalDateTime timestamp;
    
    public ReviewResult() {
    }

    public ReviewResult(String rating, List<Review> games, LocalDateTime timestamp) {
        this.rating = rating;
        this.games = games;
        this.timestamp = timestamp;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public List<Review> getGames() {
        return games;
    }

    public void setGames(List<Review> games) {
        this.games = games;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ReviewResult [rating=" + rating + ", games=" + games + ", timestamp=" + timestamp + "]";
    }

    public JsonObject toJson(){
        JsonArrayBuilder jab = Json.createArrayBuilder();

        for(Review review : games){
            jab.add(review.toJsonForTaskB());
        }

        /*Alternate method to for loop using lambda expression:
         * this.getGames().forEach(r -> r.toJsonForTaskB());
         */
        

        return Json.createObjectBuilder()
                .add("rating", this.getRating())
                .add("games", jab)
                .add("timestamp", this.getTimestamp().toString())
                .build();
    }
}
