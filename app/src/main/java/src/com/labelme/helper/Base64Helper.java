package src.com.labelme.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Classe di supporto per la cifratura/decifratura delle informazioni sensibili.
 * Tali informazioni sono le immagini delle annotazioni e le password.
 * Le immagini sono convertite in String per non appensantire la sorgente dati.
 * Le password sono cifrate per garantire la sicurezza dell'applicazione.
 */

public class Base64Helper {

    /**
     * Metodo che converte la password in Base64
     * @param string = password in chiaro
     * @return password cifrata
     */
    public static String encodePassword(String string) {
        byte[] data = string.getBytes();
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    /**
     * Metodo che decifra la password proveniente dalla sorgente dati
     * @param string = password in Base64
     * @return password in chiaro
     */
    public static String decodePassword(String string) {
        byte[] data = Base64.decode(string, Base64.DEFAULT);
        return new String(data);
    }

    /**
     * Metodo che converte l'immagine da Bitmap a String
     * @param image immagine di tipo Bitmap
     * @return immagine di tipo String
     */
    public static String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteFormat = stream.toByteArray();
        String imageString = Base64.encodeToString(byteFormat, Base64.DEFAULT);
        return imageString;
    }

    /**
     * Metodo che converte l'immagine da String a Bitmap per la visualizzazione
     * @param encodedString immagine di tipo String
     * @return immagine di tipo Bitmap
     */
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
