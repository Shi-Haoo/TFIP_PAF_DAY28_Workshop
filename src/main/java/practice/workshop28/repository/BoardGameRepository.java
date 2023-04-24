package practice.workshop28.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation.AddFieldsOperationBuilder;

import practice.workshop28.model.Game;

@Repository
public class BoardGameRepository {
    
    @Autowired
    MongoTemplate template;

    public Optional<Game> aggregateGameReviews(String gameId){

        MatchOperation matchGameId = Aggregation.match(
            Criteria.where("gid").is(Integer.parseInt(gameId))
        );

        LookupOperation lookUpOperation = Aggregation.lookup(
            "reviews", "gid", "gid", "reviewDocs"
        );

        //the only attribute we want to display from reviews collection is _id. So use reviewDocs._id
        ProjectionOperation projectionOperation = Aggregation.project("_id", "gid", "name", "year", "ranking", "users_rated", "url", "image")
        .and("reviewDocs._id").as("reviews");

        AddFieldsOperationBuilder aBuilder = Aggregation.addFields();
        aBuilder.addFieldWithValue("timestamp", LocalDateTime.now());
        AddFieldsOperation addFieldsOperation = aBuilder.build();

        Aggregation pipeline = Aggregation.newAggregation(matchGameId, lookUpOperation, projectionOperation, addFieldsOperation);
        
        //AggregationResults<Document> results consist of set of Documents that matches the aggregation pipeline specified
        AggregationResults<Document> results = template.aggregate(pipeline, "games", Document.class);

        
        //Now we want to map values of attributes/fields from each Document from the set of Documents  
        //to instance of Document and add into list<Document>. 
        //getMappedResults() method maps the results of the aggregation pipeline to the specified type, which in this case is Document.
        //It does this by mapping each document to a Document object with field names corresponding to the keys in the MongoDB document.
        // Can also map to Java object but the field names must be the same as attributes' name in Document

        List<Document> docs = results.getMappedResults();
        
        if(docs.isEmpty()){
            return Optional.empty();
        }
        
        Game g = Game.convertFromDocument(docs.get(0));

        /*Alternate method:
          if (!results.iterator().hasNext()) {
            return Optional.empty();
        }
        Document d = results.iterator().next();
        Game g = Game.convertFromDocument(d);
         */

        return Optional.of(g);
        
    }
}
