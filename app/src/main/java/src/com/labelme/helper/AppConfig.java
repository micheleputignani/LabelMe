package src.com.labelme.helper;

/**
 * Classe relativa alla gestione di tutti gli URL dei file php per le operazioni sulla sorgente dati,
 * di tutti i nomi dei campi dell'array json proveniente dai file php.
 */
public class AppConfig {

    public static final String url_login = "http://androidlabelme.altervista.org/login.php";
    public static final String url_all_images = "http://androidlabelme.altervista.org/all_images.php";
    public static final String url_check_existing_labels = "http://androidlabelme.altervista.org/check_existing_labels.php";
    public static final String url_all_existing_labels = "http://androidlabelme.altervista.org/all_existing_labels.php";
    public static final String url_uploadImage = "http://androidlabelme.altervista.org/upload_annotation.php";
    public static final String url_register = "http://androidlabelme.altervista.org/registration.php";
    public static final String url_all_labels = "http://androidlabelme.altervista.org/all_labels.php";
    public static final String url_ratings = "http://androidlabelme.altervista.org/upload_rating.php";
    public static final String url_ratings_labels = "http://androidlabelme.altervista.org/ratings_annotation.php";
    public static final String url_get_user_password = "http://androidlabelme.altervista.org/get_user_password.php";
    public static final String url_change_password = "http://androidlabelme.altervista.org/change_password.php";
    public static final String url_schedule = "http://androidlabelme.altervista.org/all_annotation.php";

    public static final String TAG_USER_ID = "id";
    public static final String TAG_EMAIL = "email";
    public static final String TAG_PASSWORD = "password";
    public static final String TAG_NAME = "name";
    public static final String TAG_USER_ARRAY = "user";
    public static final String TAG_SUCCESS = "success";
    public static final String TAG_IMAGES = "images";
    public static final String TAG_IMAGE_ID = "id";
    public static final String TAG_IMAGE_LINK = "link_image";
    public static final String TAG_ORIGINAL_IMAGE = "original_image";
    public static final String TAG_EXISTING_LABELS_ARRAY = "existing_labels";
    public static final String TAG_CROPPED_IMAGE = "cropped_image";
    public static final String TAG_LABEL = "label";
    public static final String TAG_AUTHOR = "author";
    public static final String TAG_LABELS_ARRAY = "labels";
    public static final String TAG_RATING = "rating";
    public static final String TAG_PEOPLE_RATING = "people_rating";
    public static final String TAG_RATINGS_ARRAY = "ratings_labels";
    public static final String TAG_AVERAGE = "average";
    public static final String TAG_SCHEDULE_ARRAY = "all_labels";
}
