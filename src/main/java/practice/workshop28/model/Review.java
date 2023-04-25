package practice.workshop28.model;

import java.util.List;

//import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

public class Review {
    private String _id;
    private List<String> name;
    private int rating;
    private String user;
    private String c_text;
    private String c_id;
    private int gid;
    
    public Review() {
    }

    public Review(String _id, List<String> name, int rating, String user, String c_text, String c_id, int gid) {
        this._id = _id;
        this.name = name;
        this.rating = rating;
        this.user = user;
        this.c_text = c_text;
        this.c_id = c_id;
        this.gid = gid;
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getC_text() {
        return c_text;
    }

    public void setC_text(String c_text) {
        this.c_text = c_text;
    }

    public String getC_id() {
        return c_id;
    }

    public void setC_id(String c_id) {
        this.c_id = c_id;
    }

    public int getGid() {
        return gid;
    }



    public void setGid(int gid) {
        this.gid = gid;
    }


    @Override
    public String toString() {
        return "Review [_id=" + _id + ", name=" + name + ", rating=" + rating + ", user=" + user + ", c_text=" + c_text
                + ", c_id=" + c_id + ", gid=" + gid + "]";
    }

    /* If we want to convert Document to Review Object from repo instead of mapping document to Review Object directly
    public static Review convertFromDocument(Document d){
        Review r = new Review();

        r.set_id(d.getObjectId("_id").toString());
        r.setRating(d.getInteger("rating"));
        r.setUser(d.getString("user"));
        r.setC_text(d.getString("c_text"));
        r.setC_id(d.getString("c_id"));
        r.setGid(d.getInteger("gid"));

        //name is referenced from "games" collection via lookup. Thus attribute "name" consist of array of 
        //embedded documents which in this case is just array of names based on what we aggregated in repo
        //we retrieve the array of values out from "name" and cast it to List<String>. and then set the first 
        //name obtained to r. In this case, since gid is unique, each gid only has 1 game name. So the list only 
        //consist of 1 game name. 
        List<String> gameNames = (List<String>) d.get("name");
        r.setName(gameNames.get(0));

        return r;
    }
*/
    //toJsonForTaskB only converts the necessary fields to Json to produce the desired result stated 
    //in the task; which in this case is to display game id as _id and don't display objectId
    public JsonObject toJsonForTaskB(){
        
        //Since we are using List<String> name, we need arraybuilder
        JsonArrayBuilder jab = Json.createArrayBuilder();
        
        for(String n : name){
            jab.add(n);
        }

        return Json.createObjectBuilder()
                .add("_id", this.getGid())
                .add("name", jab)
                .add("rating", this.getRating())
                .add("user", this.getUser())
                .add("comment", this.getC_text())
                .add("review_id", this.getC_id())
                .build();
    }

    //convert all the fields in Review object to Json. Not required for this workshop but 
    //put down this method in case future user want to use this method for other purpose
    public JsonObject toJson(){
        
        JsonArrayBuilder jab = Json.createArrayBuilder();
        
        for(String n : name){
            jab.add(n);
        }
        return Json.createObjectBuilder()
                .add("_id", this.get_id())
                .add("name", jab)
                .add("rating", this.getRating())
                .add("user", this.getUser())
                .add("comment", this.getC_text())
                .add("review_id", this.getC_id())
                .add("game_id", this.getGid())
                .build();
    }

    


    
}
