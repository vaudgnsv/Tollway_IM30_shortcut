package org.centerm.land.utility;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.centerm.land.R;

import java.util.Hashtable;

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


    public static Bitmap createQRImage(String url, int QR_WIDTH, int QR_HEIGHT) {
        try {//判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_HEIGHT + x] = 0xffffffff;
                    }
                }
            }//生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            //显示到一个ImageView上面
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
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
        if (OutString.length() < 4) {
            for (int i = OutString.length(); i < 4; i++ ) {
                OutString = "0"+OutString;
            }
        }
        System.out.printf("utility:: CheckSumCrcCCITT AAAAAAAAAAAAAAA  %s \n",OutString);
        return OutString;
    }



    public interface OnClickCloseImage {
        void onClickImage(Dialog dialog);
    }
}
