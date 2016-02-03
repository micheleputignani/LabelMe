package src.com.labelme.model;

public class NavDrawerItem {
    private String title;
    private int icon;

    public NavDrawerItem() {

    }

    public NavDrawerItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getIcon() {
        return this.icon;
    }
}
