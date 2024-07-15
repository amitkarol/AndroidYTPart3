package com.example.myapplication.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import androidx.exifinterface.media.ExifInterface;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ImageLoader {

    public static class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;
        private int placeholderResId;

        public LoadImageTask(ImageView imageView, int placeholderResId) {
            this.imageView = imageView;
            this.placeholderResId = placeholderResId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageView.setImageResource(placeholderResId);
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;
            InputStream inputStream = null;
            InputStream exifInputStream = null;
            try {
                inputStream = new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);

                // Check and correct orientation
                exifInputStream = new URL(url).openStream();
                ExifInterface exif = new ExifInterface(exifInputStream);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                bitmap = rotateBitmap(bitmap, orientation);

            } catch (Exception e) {
                Log.e("ImageLoader", "Error loading image", e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (exifInputStream != null) {
                    try {
                        exifInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            } else {
                imageView.setImageResource(placeholderResId);
            }
        }

        private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                default:
                    return bitmap;
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
    }
}
