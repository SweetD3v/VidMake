package com.dev4life.vidmake.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dev4life.vidmake.R;

public class MyDialog {
    static Dialog dialog;

    public static void showDialog(Context context, String text, boolean cancelable) {
        dialog = new Dialog(context, R.style.RoundedCornersDialog);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(layoutParams);
        ProgressBar progressBar = new ProgressBar(context);
        progressBar.getIndeterminateDrawable().setTint(context.getResources().getColor(R.color.colorAccent));
        LinearLayout.LayoutParams layoutParams_progress = new LinearLayout.LayoutParams(
                context.getResources().getDimensionPixelSize(R.dimen.progressDialogSize),
                context.getResources().getDimensionPixelSize(R.dimen.progressDialogSize));
        layoutParams_progress.gravity = Gravity.CENTER_VERTICAL;
        LinearLayout.LayoutParams linearlayoutParams_progress = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setPadding(40, 24, 24, 24);
        linearlayoutParams_progress.gravity = Gravity.CENTER;
        progressBar.setLayoutParams(layoutParams_progress);
        linearLayout.addView(progressBar);

        TextView textView = new TextView(context);
        textView.setTextSize(15);
        textView.setText(text);
        textView.setTextColor(Color.GRAY);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(40, 0, 0, 0);
        LinearLayout.LayoutParams linearlayoutParams_text = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(linearlayoutParams_text);
        linearLayout.addView(textView);

        dialog.getWindow().setContentView(linearLayout, layoutParams);
        dialog.setCancelable(cancelable);

        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public static void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
