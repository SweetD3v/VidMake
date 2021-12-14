package com.dev4life.vidmake.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;


import com.dev4life.vidmake.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class VideoUtils {
    public static Uri addToApiAbove29Gallery(Context context, File file) {

        String videoFileName = context.getResources().getString(R.string.video_prefix) + System.currentTimeMillis() + ".mp4";

        ContentValues valuesvideos;
        valuesvideos = new ContentValues();
        valuesvideos.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + File.separator + context.getResources().getString(R.string.app_name));
        valuesvideos.put(MediaStore.Video.Media.TITLE, videoFileName);
        valuesvideos.put(MediaStore.Video.Media.DISPLAY_NAME, videoFileName);
        valuesvideos.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        valuesvideos.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        valuesvideos.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
        valuesvideos.put(MediaStore.Video.Media.IS_PENDING, 1);
        ContentResolver resolver = context.getContentResolver();
        Uri collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri uriSavedVideo = resolver.insert(collection, valuesvideos);

        ParcelFileDescriptor pfd;

        try {
            pfd = context.getContentResolver().openFileDescriptor(uriSavedVideo, "w");

            assert pfd != null;
            FileOutputStream out = new FileOutputStream(pfd.getFileDescriptor());

            FileInputStream in = new FileInputStream(file);


            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) > 0) {

                out.write(buf, 0, len);
            }
            out.close();
            in.close();
            pfd.close();
            valuesvideos.clear();
            valuesvideos.put(MediaStore.Video.Media.IS_PENDING, 0); //only your app can see the files until pending is turned into 0

            context.getContentResolver().update(uriSavedVideo, valuesvideos, null, null);
            return uriSavedVideo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
