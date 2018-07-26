package com.eyalengel.currenthometest.Utils;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.eyalengel.currenthometest.Listeners.OnSnackBarActionClickListener;

public class SnackbarUtils {

    public static void showSnackBarMsg(View mainRelativeLayout, String msg,
                                       final OnSnackBarActionClickListener snackbarListener,
                                       String action) {
        Snackbar snackbar = Snackbar.make(mainRelativeLayout, msg, Snackbar.LENGTH_INDEFINITE)
                .setAction(action, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbarListener.onSnackBarActionClick();
            }
        });

        snackbar.show();
    }
}
