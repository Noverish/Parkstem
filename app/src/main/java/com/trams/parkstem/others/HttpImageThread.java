package com.trams.parkstem.others;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Noverish on 2016-01-17.
 */
public class HttpImageThread extends Thread {
    public final static int MAX_IMAGE_HEIGHT = 1920;
    public final static int MAX_IMAGE_WIDTH = 1080;

    private byte[] image;
    private String urlStr;

    public HttpImageThread(String urlStr) {
        this.urlStr = urlStr;
    }

    public void run() {
        image = getImageBitmapByteArrayFromUrl();
        Log.i("[Log]Grow","Size is " + (image.length/1024) + "KB for " + urlStr);
    }

    private byte[] getImageBitmapByteArrayFromUrl() {
        Bitmap bitmap = null;

        try {
            URL url = new URL(urlStr);

            if(urlStr.substring(0,5).matches("https")) {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
            } else if(urlStr.substring(0,5).matches("http:")){
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
            } else {
                Log.e("[Log]Error", "HttpImageThread.getImageBitmapByteArrayFromUrl - I don't know it is https or http");
            }

            if(bitmap != null) {
                int height = bitmap.getHeight();
                int width = bitmap.getWidth();

                if(height >= width) {
                    if(height > MAX_IMAGE_HEIGHT) {
                        int desiredHeight = MAX_IMAGE_HEIGHT;
                        int desiredWidth = (width * desiredHeight) / height;
                        bitmap = Bitmap.createScaledBitmap(bitmap, desiredWidth, desiredHeight, true);
                    }
                } else {
                    if(width > MAX_IMAGE_WIDTH) {
                        int desiredWidth = MAX_IMAGE_WIDTH;
                        int desiredHeight = (height * desiredWidth) / width;
                        bitmap = Bitmap.createScaledBitmap(bitmap, desiredWidth, desiredHeight, true);
                    }
                }

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                bitmap.recycle();
                bitmap = null;

                return stream.toByteArray();
            } else {
                Log.e("[Log]Error", "HttpImageThread.getImageBitmapByteArrayFromUrl - BitmapFactory.decodeStream(input) returns null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Bitmap getImage() {
        return BitmapFactory.decodeByteArray(image , 0, image.length);
    }

    public String getUrlStr() {
        return urlStr;
    }

    public void clearImage() {
        image = null;
    }
}

