package org.centerm.land.utility;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.centerm.land.R;

public class Utility {

    private static Dialog dialogAlert = null;

    public static void customDialogAlert(Context context, String msg, final OnClickCloseImage onClickCloseImage) {
        if (dialogAlert != null) {
            if (dialogAlert.isShowing()) {
                dialogAlert.dismiss();
            }
            dialogAlert = null;
        }
        dialogAlert = new Dialog(context);
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAlert.setContentView(R.layout.dialog_custom_alert);
        dialogAlert.setCancelable(false);
        dialogAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAlert.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        TextView msgLabel = dialogAlert.findViewById(R.id.msgLabel);
        ImageView closeImage = dialogAlert.findViewById(R.id.closeImage);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickCloseImage != null) {
                    onClickCloseImage.onClickImage(dialogAlert);
                }
            }
        });
        if (msg != null) {
            msgLabel.setText(msg);
        }
        dialogAlert.show();
    }
    public static void customDialogAlertSuccess(Context context, final OnClickCloseImage onClickCloseImage) {
        final Dialog dialogAlert = new Dialog(context);
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAlert.setContentView(R.layout.dialog_custom_success);
        dialogAlert.setCancelable(false);
        dialogAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAlert.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        TextView msgLabel = dialogAlert.findViewById(R.id.msgLabel);
        ImageView closeImage = dialogAlert.findViewById(R.id.closeImage);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickCloseImage != null) {
                    onClickCloseImage.onClickImage(dialogAlert);
                }
            }
        });
        dialogAlert.show();
    }

    public static String calNumTraceNo(String trace) {
        String traceNo = "";
        for (int i = trace.length(); i < 6; i++) {
            traceNo += "0";
        }
        return traceNo + trace;
    }

    public static String idValue(String szQr, String szId, String szValue) {
        String szLen=null;
        szLen = String.valueOf(szValue.length());
        if(szLen.length()==1)
            szLen="0"+szLen;

        szQr += szId+szLen+szValue;

        return szQr;
    }

    public static String CheckSumCrcCCITT(String SourceString)
    {
        String OutString=null;
        int crc = 0xffff;
//        int polynomial = 0xffff;
        int polynomial = 0x1021;

//        byte[] array = HexUtil.hexStringToByte( SourceString );
        byte[] array = SourceString.getBytes();
        for (byte b : array) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b   >> (7-i) & 1) == 1);
                boolean c15 = ((crc >> 15    & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
            }
        }
        crc &= 0xFFFF;
        OutString = Integer.toHexString(crc);
        return OutString;
    }



    public interface OnClickCloseImage {
        void onClickImage(Dialog dialog);
    }
}
