package com.dev4life.vidmake.utils;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.dev4life.vidmake.interfaces.CopyStatusListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    public static boolean copyFile(Context context, Uri uri, File dst, CopyStatusListener copyStatusListener) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
        InputStream is = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
        OutputStream os = new FileOutputStream(dst);
        byte[] buff = new byte[1024];
        int size = is.available();
        int len;
        while ((len = is.read(buff)) > 0) {
            os.write(buff, 0, len);
        }
        is.close();
        os.close();

        copyStatusListener.onCopyComplete("Copy Done...");
        return true;
    }
}
