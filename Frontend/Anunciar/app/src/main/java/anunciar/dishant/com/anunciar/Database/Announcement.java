package anunciar.dishant.com.anunciar.Database;

import java.util.ArrayList;

/**
 * Created by dishantkaushik on 12/17/16.
 */

public class Announcement {

    public Announcement(int id, String title, String description, String deadline, String tags, String created_at, String updated_at) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.tags = tags;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    int id;
    String title, description, deadline, tags, created_at, updated_at;
    static ArrayList<Announcement> mAnnouncements = new ArrayList<>();

    public static ArrayList<Announcement> getmAnnouncements() {
        return mAnnouncements;
    }

    public static void addToList(Announcement announcement){
        mAnnouncements.add(announcement);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getTags() {
        return tags;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}
