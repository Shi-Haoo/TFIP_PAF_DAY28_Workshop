package practice.workshop28.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import practice.workshop28.model.Game;
import practice.workshop28.model.Review;
import practice.workshop28.repository.BoardGameRepository;

@Service
public class BoardGameService {
    
    @Autowired
    BoardGameRepository repo;
    
    public Game aggregateGameReviews(String gameId){
        Optional<Game> game = repo.aggregateGameReviews(gameId);

        if(game.isEmpty()){
            return null;
        }

        return game.get();
    }

    public List<Review> aggregatesMinMaxGameReviews(String rating, String user, int limit){

        Optional<List<Review>> gameReviews = repo.aggregatesMinMaxGameReviews(rating, user, limit);

        if(gameReviews.isEmpty()){
            return null;
        }

        return gameReviews.get();

    }

    /*If repo.aggregatesMinMaxGameReviews return Optional<List<Document>> instead:
        
    public List<Review> aggregatesMinMaxGameReviews(String rating, String user, int limit){
        Optional<List<Document>> docs = repo.aggregatesMinMaxGameReviews(rating, user, limit);
        if (docs.isEmpty()) {
            return null;
        }
        List<Document> documents = docs.get();
        List<Review> gameReviews = documents.stream()
                .map(doc -> Review.convertFromDocument(doc))
                .toList();
        return gameReviews;
    }
     * 
     */
}
