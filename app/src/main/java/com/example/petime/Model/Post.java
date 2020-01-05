package com.example.petime.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.firebase.Timestamp;

//import java.sql.Timestamp;
//import javax.annotation.Nonnull;

@Entity(tableName = "posts")
@TypeConverters({TimestampConverters.class})
public class Post {
    @PrimaryKey
    @NonNull
    private String id;
    @NonNull
    private String userId;
    @NonNull
    private String image;
    @NonNull
    private String userName;
    @NonNull
    private String text;
    @NonNull
    private Timestamp lastUpdate = Timestamp.now();
    @NonNull
    private int isActive;

    public Post(){

    }

    /*@Ignore
    public Post(Parcel in) {
        this.id = Objects.requireNonNull(in.readString());
        this.userId = Objects.requireNonNull(in.readString());
        this.image = Objects.requireNonNull(in.readString());
        this.userName = Objects.requireNonNull(in.readString());
        this.text = Objects.requireNonNull(in.readString());
        this.lastUpdate = (Timestamp) Objects.requireNonNull(in.readParcelable(Timestamp.class.getClassLoader()));
        this.isActive = in.readByte() != 0;
    }*/

    @Ignore
    public Post(@NonNull String userId,@NonNull String image,
                @NonNull String userName, @NonNull String text) {
        this.id = "";
        this.userId = userId;
        this.image = image;
        this.userName = userName;
        this.text = text;
        this.lastUpdate = Timestamp.now();
        this.isActive = 1;
    }

    @Ignore
    public Post(@NonNull String id, @NonNull String userId,@NonNull String image,
                   @NonNull String userName, @NonNull String text,
                    @NonNull Timestamp lastUpdate, @NonNull int isActive) {
        this.id = id;
        this.userId = userId;
        this.image = image;
        this.userName = userName;
        this.text = text;
        this.lastUpdate = lastUpdate;
        this.isActive = isActive;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setImage(@NonNull String image) {
        this.image = image;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    public void setUserName(@NonNull String userName) {
        this.userName = userName;
    }

    public void setText(@NonNull String text) {
        this.text = text;
    }

    public void setLastUpdate(@NonNull Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setIsActive(@NonNull int active) {
        isActive = active;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getImage() {
        return image;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    @NonNull
    public String getUserName() {
        return userName;
    }

    @NonNull
    public String getText() {
        return text;
    }

    @NonNull
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    @NonNull
    public int getIsActive() {
        return isActive;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", image='" + image + '\'' +
                ", userName='" + userName + '\'' +
                ", text='" + text + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", isActive=" + isActive +
                '}';
    }
}