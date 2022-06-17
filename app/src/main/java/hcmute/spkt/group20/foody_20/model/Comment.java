package hcmute.spkt.group20.foody_20.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.Date;

/*OK*/

public class Comment implements Serializable {
    private User user;//
    private String uid;
    private Date time_created;
    private float rate;
    private String description;

    public Comment() {

    }

    public Comment(User user, Date time_created, float rate, String description) {
        this.user = user;
        this.time_created = time_created;
        this.rate = rate;
        this.description = description;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
//        load user
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "user=" + user +
                ", uid='" + uid + '\'' +
                ", time_created=" + time_created +
                ", rate=" + rate +
                ", description='" + description + '\'' +
                '}';
    }

    @Exclude
    public User getUser() {
        return user;
    }

    @Exclude
    public void setUser(User user) {
        this.user = user;
    }

    public Date getTime_created() {
        return time_created;
    }

    public void setTime_created(Date time_created) {
        this.time_created = time_created;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
