package com.dev4life.vidmake.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.dev4life.vidmake.databinding.FragmentHomeBinding;
import com.dev4life.vidmake.ui.activities.MainActivity;
import com.dev4life.vidmake.utils.MyDialog;
import com.dev4life.vidmake.utils.MyProgressDialog;
import com.dev4life.vidmake.utils.PermissionUtils;
import com.dev4life.vidmake.utils.VideoUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    Context context;

    Uri videoUri;
    File tempFile;
    File outputVideoFile;
    ActivityResultLauncher<String> videoPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    uri -> {
                        deleteAllTempFiles();
                        videoUri = uri;
                        if (binding.videoView.isPlaying()) {
                            binding.videoView.stopPlayback();
                        }
                        binding.videoView.setVideoURI(uri);
                        binding.videoView.start();
                    });

    private final ActivityResultLauncher<String[]> permissionResultLauncher10 =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                        int granted = 0;
                        for (boolean isGranted : result.values()) {
                            if (isGranted) {
                                granted++;
                            }
                        }
                        if (granted == result.size()) {
                            videoPickerLauncher.launch("video/*");
                        } else {
                            grantStoragePermissions10();
                        }
                    });

    private void grantStoragePermissions10() {
        permissionResultLauncher10.launch(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });
    }

    private void grantStoragePermissions11() {
        permissionResultLauncher11.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private final ActivityResultLauncher<String> permissionResultLauncher11 =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(), isGranted -> {
                        if (isGranted) {
                            videoPickerLauncher.launch("video/*");
                        } else {
                            grantStoragePermissions11();
                        }
                    });


    MyRxFFmpegSubscriber myRxFFmpegSubscriber;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tempFile = new File(context.getFilesDir(), "TEMP_VIDEO.mp4");
        outputVideoFile = new File(context.getFilesDir(), "OUTPUT_VIDEO.mp4");
        deleteAllTempFiles();

        MediaController controller = new MediaController(context);
        controller.setAnchorView(binding.videoView);
        binding.videoView.setMediaController(controller);
        binding.pickVideoBtn.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!PermissionUtils.checkStoragePermissionREAD(context)) {
                    grantStoragePermissions11();
                } else {
                    videoPickerLauncher.launch("video/*");
                }
            } else {
                if (!PermissionUtils.checkStoragePermissionREADWRITE(context)) {
                    grantStoragePermissions10();
                } else {
                    videoPickerLauncher.launch("video/*");
                }
            }
        });

        binding.compressVideoBtn.setOnClickListener(v -> {
            if (videoUri != null) {
//                deleteAllTempFiles();
                copyFile(videoUri, tempFile, outputVideoFile);
            }
        });
    }

    private void deleteAllTempFiles() {
        if (tempFile.exists())
            tempFile.delete();
        if (outputVideoFile.exists())
            outputVideoFile.delete();
    }

    private void copyFile(Uri inputUri, File tempFile, File outputVideoFile) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        MyDialog.showDialog(context, "Copying...", false);
        executor.execute(() -> {
            try {
                ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(inputUri, "r");
                InputStream is = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
                OutputStream os = new FileOutputStream(tempFile);
                byte[] buff = new byte[1024];
                int len;
                while ((len = is.read(buff)) > 0) {
                    os.write(buff, 0, len);
                }
                is.close();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            handler.post(() -> {
                MyDialog.dismissDialog();
                runFFMPEGCom(tempFile, outputVideoFile);
            });
        });
    }

    MyProgressDialog myProgressDialog;
    int totalProgress;

    private void runFFMPEGCom(File inputFile, File outputFile) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        String[] query = {
                "ffmpeg",
                "-i",
                inputFile.getPath(),
                "-vf",
                "hue=s=0",
                "-preset",
                "superfast",
                outputFile.getPath()
        };

        RxFFmpegInvoke rxFFmpegInvoke = RxFFmpegInvoke.getInstance();

        myProgressDialog = new MyProgressDialog();
        myProgressDialog.setCancelDialogListener(message -> {
        });
        myProgressDialog.showDialog(context, "Converting...", false, rxFFmpegInvoke);

        myRxFFmpegSubscriber = new MyRxFFmpegSubscriber(context);
        rxFFmpegInvoke.runCommandAsync(query, myRxFFmpegSubscriber);
//        rxFFmpegInvoke.runCommandAsync(query, new RxFFmpegInvoke.IFFmpegListener() {
//            @Override
//            public void onFinish() {
//                Log.e("TAG", "onFinish: ");
//                VideoUtils.addToApiAbove29Gallery(context, outputFile);
//                ((AppCompatActivity) context).runOnUiThread(() -> {
//                    Toast.makeText(context, "Video Saved!", Toast.LENGTH_SHORT).show();
//                    myProgressDialog.dismissDialog();
//                });
//            }
//
//            @Override
//            public void onProgress(int progress, long progressTime) {
//                Log.e("TAG", "onProgress: " + progress);
//                Log.e("TAG", "onProgressTime: " + progressTime);
//                totalProgress = (int) progressTime;
//                if (totalProgress == 0)
//                    totalProgress = 100;
//                ((AppCompatActivity) context).runOnUiThread(() -> {
//                    myProgressDialog.publishProgress(progress, totalProgress);
//                });
//            }
//
//            @Override
//            public void onCancel() {
//                ((AppCompatActivity) context).runOnUiThread(() -> {
//                    if (myRxFFmpegSubscriber != null) {
//                        myRxFFmpegSubscriber.dispose();
//                    }
//                    Toast.makeText(context, "Cancelled video processing.", Toast.LENGTH_SHORT).show();
//                });
//            }
//
//            @Override
//            public void onError(String message) {
//                Log.e("TAG", "onError: " + message);
//            }
//        });

    }

    public class MyRxFFmpegSubscriber extends RxFFmpegSubscriber {
        private WeakReference<Context> mWeakReference;
        File outputFile;

        public MyRxFFmpegSubscriber(Context context) {
            mWeakReference = new WeakReference<>(context);
        }


        @Override
        public void onFinish() {
            Context context = mWeakReference.get();
            if (context != null) {
                VideoUtils.addToApiAbove29Gallery(context, outputFile);
                ((AppCompatActivity) context).runOnUiThread(() -> {
                    Toast.makeText(context, "Video Saved!", Toast.LENGTH_SHORT).show();
                    myProgressDialog.dismissDialog();
                });
            }
        }

        @Override
        public void onProgress(int progress, long progressTime) {
            Log.e("TAG", "onProgress: " + progress);
            Log.e("TAG", "onProgressTime: " + progressTime);
            Context context = mWeakReference.get();
            totalProgress = (int) progressTime;
            if (totalProgress == 0)
                totalProgress = 100;
            if (context != null) {
                ((AppCompatActivity) context).runOnUiThread(() -> {
                    myProgressDialog.publishProgress(progress, totalProgress);
                });
            }
        }

        @Override
        public void onCancel() {
            ((AppCompatActivity) context).runOnUiThread(() -> {
//                if (myRxFFmpegSubscriber != null) {
//                    myRxFFmpegSubscriber.dispose();
//                }
                Toast.makeText(context, "Cancelled video processing.", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public void onError(String message) {
            Log.e("TAG", "onError: " + message);
        }
    }

    @Override
    public void onDestroy() {
        deleteAllTempFiles();
        if (myRxFFmpegSubscriber != null) {
            myRxFFmpegSubscriber.dispose();
        }
        super.onDestroy();
    }
}
