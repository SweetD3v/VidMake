package com.dev4life.vidmake.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

import com.dev4life.vidmake.R;
import com.dev4life.vidmake.databinding.ProgressDialogBinding;
import com.dev4life.vidmake.interfaces.CancelDialogListener;

import io.microshow.rxffmpeg.RxFFmpegInvoke;


public class MyProgressDialog {

    AlertDialog alertDialog;
    ProgressDialogBinding dialogBinding;
    public CancelDialogListener cancelDialogListener;

    public void setCancelDialogListener(CancelDialogListener cancelDialogListener) {
        this.cancelDialogListener = cancelDialogListener;
    }

    public void showDialog(Context context, String text, boolean cancelable, RxFFmpegInvoke rxFFmpegInvoke) {
        dialogBinding = ProgressDialogBinding.inflate(LayoutInflater.from(context));
        alertDialog = new AlertDialog.Builder(context, R.style.RoundedCornersDialog)
                .setView(dialogBinding.getRoot())
                .setCancelable(cancelable)
                .create();
        if (alertDialog != null && !alertDialog.isShowing()) {
            alertDialog.show();
        }

//        LinearLayout linearLayout = new LinearLayout(context);
//        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        linearLayout.setLayoutParams(layoutParams);
//        ProgressBar progressBar = new ProgressBar(context);
//        progressBar.getIndeterminateDrawable().setTint(context.getResources().getColor(R.color.colorAccent));
//        LinearLayout.LayoutParams layoutParams_progress = new LinearLayout.LayoutParams(
//                context.getResources().getDimensionPixelSize(R.dimen.progressDialogSize),
//                context.getResources().getDimensionPixelSize(R.dimen.progressDialogSize));
//        layoutParams_progress.gravity = Gravity.CENTER_VERTICAL;
//        LinearLayout.LayoutParams linearlayoutParams_progress = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        linearLayout.setPadding(40, 24, 24, 24);
//        linearlayoutParams_progress.gravity = Gravity.CENTER;
//        progressBar.setLayoutParams(layoutParams_progress);
//        linearLayout.addView(progressBar);
//
//        TextView textView = new TextView(context);
//        textView.setTextSize(15);
//        textView.setText(text);
//        textView.setTextColor(Color.GRAY);
//        textView.setGravity(Gravity.CENTER_VERTICAL);
//        textView.setPadding(40, 0, 0, 0);
//        LinearLayout.LayoutParams linearlayoutParams_text = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        textView.setLayoutParams(linearlayoutParams_text);
//        linearLayout.addView(textView);

        dialogBinding.txtDialogMessage.setText(text);
        dialogBinding.brnCancel.setOnClickListener(v -> {
            cancelDialog(rxFFmpegInvoke, cancelDialogListener);
        });
    }

    public void dismissDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            dialogBinding.progressBar.setProgress(0);
            dialogBinding.txtProgressPercent.setText(0 + "%");
            alertDialog.dismiss();
        }
    }

    public void cancelDialog(RxFFmpegInvoke rxFFmpegInvoke, CancelDialogListener cancelDialogListener) {
        if (alertDialog != null && alertDialog.isShowing()) {
            cancelDialogListener.onDialogCancelled("Cancelled video decoding.");
            rxFFmpegInvoke.onCancel();
            dialogBinding.progressBar.setProgress(0);
            dialogBinding.txtProgressPercent.setText(0 + "%");
            alertDialog.dismiss();
        }
    }

    public void publishProgress(int progress, int totalProgress) {
        if (alertDialog != null && alertDialog.isShowing() && dialogBinding != null) {
            dialogBinding.progressBar.setProgress(progress);
            dialogBinding.txtProgressPercent.setText(progress + "%");
        }
    }
}