package practice.workshop28.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation.AddFieldsOperationBuilder;

import practice.workshop28.model.Game;
import practice.workshop28.model.Review;

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

    public Optional<List<Review>> aggregatesMinMaxGameReviews(String rating, String user, int limit){

        MatchOperation mOp = null;

        if(rating.equalsIgnoreCase("highest")){
            /*mOp = Aggregation.match(
                Criteria.where("user").is(user).and("rating").gte(5)
            */
                //To account for case insensitive of user
                mOp = Aggregation.match(
                Criteria.where("user").regex(user, "i").and("rating").gte(5)
            );
        }

        else{
            mOp = Aggregation.match(
                Criteria.where("user").is(user).and("rating").lt(5)
            );
        }

        LookupOperation LookupOp = Aggregation.lookup("games", "gid", "gid", "gameReviews");

        ProjectionOperation pOp = Aggregation.project("_id", "c_id", "user", "rating", "c_text", "gid")
            .and("gameReviews.name").as("name");

        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "rating");

        LimitOperation limitOperation = Aggregation.limit(limit);

        Aggregation pipeline = Aggregation.newAggregation(mOp, LookupOp, pOp, sortOperation, limitOperation);

        //fields of Review object must be same name as attributes in document from MongoDB
        //since "name" is an array of values, when we map directly to Review.java object, field "name" must also be a list
        //List<String> name in Review.java. Else there will be error:
        //"Cannot convert [Diplomacy] of type class java.util.ArrayList into an instance of class java.lang.String;
        AggregationResults<Review> results = template.aggregate(pipeline,"reviews", Review.class);

        //results.getMappedResults() return empty list if no aggregation matches found
        List<Review> gameReviews = results.getMappedResults();

        /*If want to get List<Document> instead:
        AggregationResults<Document> results = template.aggregate(pipeline,"reviews", Document.class);
        List<Document> gameReviews = results.getMappedResults();
         */

         if(gameReviews.isEmpty())
         return Optional.empty();
        
         return Optional.of(gameReviews);
    }
}
