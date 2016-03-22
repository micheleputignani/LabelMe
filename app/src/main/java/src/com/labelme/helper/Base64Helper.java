package src.com.labelme.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by Mirko Putignani on 18/02/2016.
 */
public class Base64Helper {

    public static String encodePassword(String string) {
        byte[] data = string.getBytes();
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public static String decodePassword(String string) {
        byte[] data = Base64.decode(string, Base64.DEFAULT);
        return new String(data);
    }

    public static String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteFormat = stream.toByteArray();
        String imageString = Base64.encodeToString(byteFormat, Base64.DEFAULT);
        return imageString;
    }

    public static Bitmap decodeBase64(String encodedString) {
        try {
            byte[] decodedByte = Base64.decode(encodedString, Base64.DEFAULT);
//            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
//            return bitmap;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length, options);
            // calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 500, 500);
            // decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length, options);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // raw height and width of image
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // calculate the largest inSampleSize value
            // that is a power of 2 and keeps both height and width
            // larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
