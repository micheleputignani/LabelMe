package src.com.labelme.model;

import android.graphics.Bitmap;

/**
 * Created by Mirko Putignani on 31/01/2016.
 */
public class GridViewItem {
    private Bitmap image;
    private String title;

    public GridViewItem(Bitmap image, String title) {
        super();
        this.image = image;
        this.title = title;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
