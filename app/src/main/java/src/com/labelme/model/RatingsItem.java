package src.com.labelme.model;

public class RatingsItem {
    private String cropped_image;
    private String label;
    private String author;
    private String average;
    private String personal_rating;

    // getter
    public String getThumbnail() {
        return cropped_image;
    }

    public String getLabel() {
        return label;
    }

    public String getAuthor() {
        return author;
    }

    public String getAverage() {
        return average;
    }

    public String getPersonal_rating() {
        return personal_rating;
    }

    // setter
    public void setThumbnail(String cropped_image) {
        this.cropped_image = cropped_image;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setAverage(String average) {
        this.average = average;
    }

    public void setPersonalRating(String personal_rating) {
        this.personal_rating = personal_rating;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
