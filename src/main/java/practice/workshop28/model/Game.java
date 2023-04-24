package practice.workshop28.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

public class Game {
    private int gid;
    private String name;
    private int year;
    private int ranking;
    private int users_rated;
    private String url;
    private String image;
    private List<String> reviews;
    private LocalDateTime timestamp;
    
    public Game() {
    }

    

    public Game(int gid, String name, int year, int ranking, int users_rated, String url, String image,
            List<String> reviews, LocalDateTime timestamp) {
        this.gid = gid;
        this.name = name;
        this.year = year;
        this.ranking = ranking;
        this.users_rated = users_rated;
        this.url = url;
        this.image = image;
        this.reviews = reviews;
        this.timestamp = timestamp;
    }



    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public int getUsers_rated() {
        return users_rated;
    }

    public void setUsers_rated(int users_rated) {
        this.users_rated = users_rated;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getReviews() {
        return reviews;
    }

    public void setReviews(List<String> reviews) {
        this.reviews = reviews;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }



    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    

    @Override
    public String toString() {
        return "Game [gid=" + gid + ", name=" + name + ", year=" + year + ", ranking=" + ranking + ", users_rated="
                + users_rated + ", url=" + url + ", image=" + image + ", reviews=" + reviews + "]";
    }

    public static Game convertFromDocument(Document d){
        
        Game game = new Game();
        game.setGid(d.getInteger("gid"));
        game.setName(d.getString("name"));
        game.setYear(d.getInteger("year"));
        game.setRanking(d.getInteger("ranking"));
        game.setUsers_rated(d.getInteger("users_rated"));
        game.setUrl(d.getString("url"));
        game.setImage(d.getString("image"));

        //retrieve the embedded doc consisting of array
        //Mistake List<Document> reviewDocsList = (List<Document>)d.get("reviews");: 
        //Cannot cast reviews attribute to List<Document>. In our aggregation in repo, "reviews" consist of array of ObjectId values
        // So casting to List<Document> will fail resulting in ClassCastException. Correct casting should be List<ObjectId>
        
        //we use Object because Object is parent of all class. So no error will occur. Use this when we are unsure what is 
        //the exact data type of object being retrieved. But subsequently we still need to cast it to the right data type as 
        //seen in ((ObjectId) object)
        List<Object> objectList = (ArrayList<Object>) d.get("reviews");
        List<String> reviewsUrl = new ArrayList<>();
        
        for(Object object : objectList){
            ObjectId oId = (ObjectId) object;
            reviewsUrl.add("/review/"+oId.toString());
        }

        /*Alternate method:
         * Object reviewsField = d.get("reviews");
         * if (reviewsField instanceof List<ObjectId>) {
            List<ObjectId> reviewIdsList = (List<ObjectId>) reviewsField;
            for (ObjectId reviewId : reviewIdsList) {
                reviewsUrl.add("/review/" + reviewId.toString());
    }
         */

        game.setReviews(reviewsUrl);
        game.setTimestamp(LocalDateTime.now());

        return game;
    }

    public JsonObject toJson(){
        
        JsonArrayBuilder jab = Json.createArrayBuilder();

        for(String url : this.getReviews()){
            jab.add(url);
        }

        return Json.createObjectBuilder()
                .add("game_id", this.getGid())
                .add("name", this.getName())
                .add("year", this.getYear())
                .add("rank", this.getRanking())
                .add("users_rated", this.getUsers_rated())
                .add("url", this.getUrl())
                .add("thumbnail", this.getImage())
                .add("reviews", jab)
                .add("timestamp", this.getTimestamp().toString())
                .build();

    }



    


}
