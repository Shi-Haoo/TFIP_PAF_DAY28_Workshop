package practice.workshop28.restController;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import practice.workshop28.model.Game;
import practice.workshop28.model.Review;
import practice.workshop28.model.ReviewResult;
import practice.workshop28.service.BoardGameService;

@RestController
public class BoardGameController {
    
    @Autowired
    BoardGameService svc;

    @GetMapping(path="/game/{gameId}/reviews")
    public ResponseEntity<String> getGameReviews(@PathVariable String gameId){

        Game game = svc.aggregateGameReviews(gameId);

        if(game == null){
            return ResponseEntity  
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Json.createObjectBuilder()
                            .add("Error","Game with _id: %s not found".formatted(gameId))
                            .build().toString());
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(game.toJson().toString());

    }

    @GetMapping(path="/games/{rating}")
    public ResponseEntity<String> getUserGameReviews(@PathVariable String rating, @RequestParam String user, @RequestParam String limit){

        List<Review> reviews = svc.aggregatesMinMaxGameReviews(rating, user, Integer.parseInt(limit));

        if(reviews == null){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Json.createObjectBuilder()
                            .add("Error", "User %s not found".formatted(user))
                            .build().toString());
        }

        ReviewResult result = new ReviewResult(rating, reviews, LocalDateTime.now());

        return ResponseEntity
        .status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(result.toJson().toString());
    }

}
    

