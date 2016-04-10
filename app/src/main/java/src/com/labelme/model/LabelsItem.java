package src.com.labelme.model;

/**
 * Classe di supporto per le annotazioni create dall'utente collegato.
 */

public class LabelsItem {
    private String imageID;
    private String cropped_image;
    private String label;
    private String author;
    private String rating;

    // getter
    public String getImageID() {
        return imageID;
    }

    public String getThumbnail() {
        return cropped_image;
    }

    public String getLabel() {
        return label;
    }

    public String getAuthor() {
        return author;
    }

    public String getRating() {
        return rating;
    }

    // setter
    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public void setThumbnail(String image) {
        this.cropped_image = image;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}