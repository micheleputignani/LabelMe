package src.com.labelme.model;

/**
 * Classe di supporto per set e get della lista delle annotazioni presenti nella sorgente dati.
 */

public class ScheduleItem {
    private String cropped_image;
    private String label;
    private String author;

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

    // setter
    public void setThumbnail(String cropped_image) {
        this.cropped_image = cropped_image;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
