package practice.workshop28.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import practice.workshop28.model.Game;
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
}
