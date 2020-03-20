package org.centerm.Tollway.utility;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.centerm.smartpos.util.HexUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.centerm.Tollway.R;
import org.centerm.Tollway.activity.posinterface.PosInterfaceActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;


public class Utility {

    private static Dialog dialogAlert = null;

    // Paul_20181205
    public static String SETTLEMENT_REPRINT_POS_PATH = "/cache/customer/media/SummaryReprintPOS.bit";
    public static String SETTLEMENT_REPRINT_EPS_PATH = "/cache/customer/media/SummaryReprintEPS.bit";
    public static String SETTLEMENT_REPRINT_TMS_PATH = "/cache/customer/media/SummaryReprintTMS.bit";

    /**
     * Interface
     */
//    private PosInterfaceActivity posInterfaceActivity = MainApplication.getPosInterfaceActivity();

    // Paul_20181205 Reprint file save
    public static void SettlementReprintBmpWrite(String HostType, Bitmap bitmapFile) {
        String bmp_file = null;
        switch (HostType) {
            case "POS":
                bmp_file = SETTLEMENT_REPRINT_POS_PATH;
                break;
            case "EPS":
                bmp_file = SETTLEMENT_REPRINT_EPS_PATH;
                break;
            case "TMS":
                bmp_file = SETTLEMENT_REPRINT_TMS_PATH;
                break;
            default:
                bmp_file = SETTLEMENT_REPRINT_TMS_PATH;
                break;
        }
        // Paul_20181205 Start reprint bitmap write
        File file = new File( bmp_file );
        try {
            OutputStream outStream = new FileOutputStream( file );
            bitmapFile.compress( Bitmap.CompressFormat.JPEG, 50, outStream );
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Paul_20181205 Reprint file read
    public static Bitmap SettlementReprintBitmapRead(String HostType) {
        String bmp_file = null;
        switch(HostType)
        {
            case "POS":
                bmp_file = SETTLEMENT_REPRINT_POS_PATH;
                break;
            case "EPS":
                bmp_file = SETTLEMENT_REPRINT_EPS_PATH;
                break;
            case "TMS":
                bmp_file = SETTLEMENT_REPRINT_TMS_PATH;
                break;
            default:
                bmp_file = SETTLEMENT_REPRINT_TMS_PATH;
                break;
        }
        Bitmap temp_bmp = BitmapFactory.decodeFile( bmp_file );

        return temp_bmp;
    }



    public static void customDialogAlert(Context context, String msg, final OnClickCloseImage onClickCloseImage) {
        System.out.printf("utility:: customDialogAlert NNNNNNNNNN00001 msg = %s\n",msg);
//        if (dialogAlert != null) {
//            if (dialogAlert.isShowing()) {
//                dialogAlert.dismiss();
//            }
//            dialogAlert = null;
//        }
        try {
            if (dialogAlert.isShowing()) {
                dialogAlert.dismiss();
            }
            dialogAlert = null;
        } catch (Exception e) {
            dialogAlert = null;
        }

        dialogAlert = new Dialog(context, R.style.ThemeWithCorners); //K.GAME 180821
        View view = dialogAlert.getLayoutInflater().inflate(R.layout.dialog_custom_alert, null);//K.GAME 180821
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
        dialogAlert.setContentView(view);//K.GAME 180821
        dialogAlert.setCancelable(false);//K.GAME 180821

        TextView msgLabel = dialogAlert.findViewById(R.id.msgLabel);
        Button btn_close = dialogAlert.findViewById(R.id.btn_dialog_close);//K.GAME 180821
        if (PosInterfaceActivity.PosInterfaceExistFlg == 1) // Paul_20181215 Cancel Key Not Use
        {
            System.out.printf("utility:: Utility customDialogAlert 002 \n");
            btn_close.setEnabled( false );      // Paul_20181214 Cancel Key Not Use
        }
        btn_close.setOnClickListener(new OnClickListener() {//K.GAME 180821
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
//        if(dialogAlert!=null)  ////20180723 SINN Sometime crash app close
//        dialogAlert.show();
        try {//20180724 SINN  Activity has leaked.
            dialogAlert.show();
        } catch (Exception e) {
            dialogAlert.dismiss();
        }

    }

    public static void customDialogAlert(final Context context, String msg) {

        try {
            if(dialogAlert.isShowing()) {
                dialogAlert.dismiss();
            }
            dialogAlert = null;
        } catch (Exception e) {
            dialogAlert = null;
        }

        dialogAlert = new Dialog(context, R.style.ThemeWithCorners);

        View view = dialogAlert.getLayoutInflater().inflate(R.layout.dialog_custom_alert, null);
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAlert.setContentView(view);
        dialogAlert.setCancelable(false);
        TextView msgLabel = dialogAlert.findViewById(R.id.msgLabel);
        if (msg != null) {
            msgLabel.setText(msg);
        }

        if(!dialogAlert.isShowing()) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialogAlert.show();
                }
            }, 0);

        }


        CountDownTimer timer = new CountDownTimer(2500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                dialogAlert.dismiss();
            }
        };
        timer.start();
    }

    public static void customDialogAlert_gotomain(Context context, String msg, final OnClickCloseImage onClickCloseImage) {
        System.out.printf("utility:: customDialogAlert_gotomain NNNNNNNNNN00001 msg = %s \n",msg);
        try {
            if (dialogAlert.isShowing()) {
                dialogAlert.dismiss();
            }
            dialogAlert = null;
        } catch (Exception e) {
            dialogAlert = null;
        }
//        dialogAlert = new Dialog(context, R.style.ThemeWithCorners); //K.GAME 180821
//        View view = dialogAlert.getLayoutInflater().inflate(R.layout.dialog_custom_alert, null);//K.GAME 180821
//        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
//        dialogAlert.setContentView(view);//K.GAME 180821
//        dialogAlert.setCancelable(false);//K.GAME 180821

        dialogAlert = new Dialog(context);
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogAlert.setContentView( R.layout.dialog_custom_alert);
        dialogAlert.setContentView(R.layout.dialog_custom_alert_gotomain);//K.GAME 180911 change dialog full screen
        dialogAlert.setCancelable(false);
        dialogAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAlert.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        TextView msgLabel = dialogAlert.findViewById(R.id.msgLabel);
        Button btn_close = dialogAlert.findViewById(R.id.btn_dialog_close);//K.GAME 180821

        if (PosInterfaceActivity.PosInterfaceExistFlg == 1) // Paul_20181215 Cancel Key Not Use
        {
            System.out.printf("utility:: Utility customDialogAlert 002 \n");
            btn_close.setEnabled( false );      // Paul_20181214 Cancel Key Not Use
        }

        btn_close.setOnClickListener(new OnClickListener() {//K.GAME 180821
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
        try {
            dialogAlert.show();
        } catch (Exception e) {
            dialogAlert.dismiss();
        }

    }

    public static void customDialogselect(Context context, String msg,final OnClickOk onClickOk) {//K.GAME 181002 print iso
        try {
            if (dialogAlert.isShowing()) {
                dialogAlert.dismiss();
            }
            dialogAlert = null;
        } catch (Exception e) {
            dialogAlert = null;
        }

        dialogAlert = new Dialog(context, R.style.ThemeWithCorners); //K.GAME 180821
        View view = dialogAlert.getLayoutInflater().inflate(R.layout.dialog_custom_alert_not_connect, null);//K.GAME 180821
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
        dialogAlert.setContentView(view);//K.GAME 180821
        dialogAlert.setCancelable(false);//K.GAME 180821

        TextView msgLabel = dialogAlert.findViewById(R.id.msgLabel);
        Button btn_close = dialogAlert.findViewById(R.id.closeImage);//K.GAME 180821
        Button okBtn = dialogAlert.findViewById(R.id.okBtn);//K.GAME 180821
        btn_close.setOnClickListener(new OnClickListener() {//K.GAME 180821
            @Override
            public void onClick(View v) {
                if (onClickOk != null) {
                    onClickOk.onClickCancel(dialogAlert);
                }
            }
        });
        okBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickOk != null) {
                    onClickOk.onClickOk(dialogAlert);
                }
            }
        });
        if (msg != null) {
            msgLabel.setText(msg);
        }

        try {//20180724 SINN  Activity has leaked.
            dialogAlert.show();
        } catch (Exception e) {
            dialogAlert.dismiss();
        }

    }

    // Paul_20180717
    public static void customDialogAlertAuto(Context context, String msg) {
        System.out.printf("utility:: customDialogAlertAuto NNNNNNNNNN00001 msg = %s \n",msg);
//        if (dialogAlert != null) {
//            if (dialogAlert.isShowing()) {
//                dialogAlert.dismiss();
//            }
//            dialogAlert = null;
//        }
        try {
            if (dialogAlert != null && dialogAlert.isShowing()) {
                dialogAlert.dismiss();
            }
            dialogAlert = null;
        } catch (Exception e) {
            dialogAlert = null;
        }
        dialogAlert = new Dialog(context, R.style.ThemeWithCorners); //K.GAME 180821
        View view = dialogAlert.getLayoutInflater().inflate(R.layout.dialog_custom_alert, null);//K.GAME 180821
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
        dialogAlert.setContentView(view);//K.GAME 180821
        dialogAlert.setCancelable(false);//K.GAME 180821

//        dialogAlert = new Dialog(context);
//        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogAlert.setContentView(R.layout.dialog_custom_alert);
//        dialogAlert.setCancelable(false);
//        dialogAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogAlert.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        TextView msgLabel = dialogAlert.findViewById(R.id.msgLabel);
//        Button btn_close = dialogAlert.findViewById(R.id.btn_dialog_close);//K.GAME 180821
        Button btn_close = dialogAlert.findViewById(R.id.btn_dialog_close);//K.GAME 180821
        System.out.printf("utility:: Utility customDialogAlertAuto 001 \n");

        if (PosInterfaceActivity.PosInterfaceExistFlg == 1) // Paul_20181214 Cancel Key Not Use
        {
            System.out.printf("utility:: Utility customDialogAlertAuto 002 \n");
            btn_close.setEnabled( false );      // Paul_20181214 Cancel Key Not Use
        }
        btn_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PosInterfaceActivity.PosInterfaceExistFlg != 1)  ////SINN 20181013  rs232 not allow click cancel
                    dialogAlert.dismiss();
            }
        });
//        btn_close.setOnClickListener(new View.OnClickListener() {//K.GAME 180821
//            @Override
//            public void onClick(View v) {
////                if (onClickCloseImage != null) {
////                    onClickCloseImage.onClickImage(dialogAlert);
////                }
//            }
//        });
        if (msg != null) {
            msgLabel.setText(msg);
        }
//        if(dialogAlert!=null)  ////20180723 SINN Sometime crash app close
//        dialogAlert.show();
        try {//20180724 SINN  Activity has leaked.
            if (dialogAlert != null)
                dialogAlert.show();
        } catch (Exception e) {
            dialogAlert.dismiss();
        }
    }

    public static void customDialogAlertAuto_gotomain(Context context, String msg) {//K.GAME 180911 change dialog full screen
        System.out.printf("utility:: customDialogAlertAuto_gotomain NNNNNNNNNN00001 \n");
        try {
            if (dialogAlert.isShowing()) {
                dialogAlert.dismiss();
            }
            dialogAlert = null;
        } catch (Exception e) {
            dialogAlert = null;
        }
//        dialogAlert = new Dialog(context, R.style.ThemeWithCorners); //K.GAME 180821
//        View view = dialogAlert.getLayoutInflater().inflate(R.layout.dialog_custom_alert, null);//K.GAME 180821
//        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
//        dialogAlert.setContentView(view);//K.GAME 180821
//        dialogAlert.setCancelable(false);//K.GAME 180821

        dialogAlert = new Dialog(context);
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogAlert.setContentView(R.layout.dialog_custom_alert);
        dialogAlert.setContentView(R.layout.dialog_custom_alert_gotomain);//K.GAME 180911 change dialog full screen
        dialogAlert.setCancelable(false);
        dialogAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAlert.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        TextView msgLabel = dialogAlert.findViewById(R.id.msgLabel);
        Button btn_close = dialogAlert.findViewById(R.id.btn_dialog_close);//K.GAME 180821
        if (PosInterfaceActivity.PosInterfaceExistFlg == 1) // Paul_20181214 Cancel Key Not Use
        {
            btn_close.setEnabled( false );      // Paul_20181214 Cancel Key Not Use
        }

        if (msg != null) {
            msgLabel.setText(msg);
        }

        try {//20180724 SINN  Activity has leaked.
            if (dialogAlert != null)
                dialogAlert.show();
        } catch (Exception e) {
            dialogAlert.dismiss();
        }
    }

    // Paul_20180717
    public static void customDialogAlertOKAuto(Context context, String msg) {
//        if (dialogAlert != null) {
//            if (dialogAlert.isShowing()) {
//                dialogAlert.dismiss();
//            }
//            dialogAlert = null;
//        }

        try {
            if (dialogAlert.isShowing()) {
                dialogAlert.dismiss();
            }
            dialogAlert = null;
        } catch (Exception e) {
            dialogAlert = null;
        }
        dialogAlert = new Dialog(context, R.style.ThemeWithCorners);//K.GAME 180821
        View view = dialogAlert.getLayoutInflater().inflate(R.layout.dialog_custom_success, null);//K.GAME 180821
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
        dialogAlert.setContentView(view);//K.GAME 180821
        dialogAlert.setCancelable(false);//K.GAME 180821

//        dialogAlert = new Dialog(context);
//        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogAlert.setContentView(R.layout.dialog_custom_success);
//        dialogAlert.setCancelable(false);
//        dialogAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogAlert.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        TextView msgLabel = dialogAlert.findViewById(R.id.msgLabel);
        Button btn_success = dialogAlert.findViewById(R.id.btn_dialog_success);//K.GAME 180821
//        btn_success.setOnClickListener(new View.OnClickListener() {//K.GAME 180821
//            @Override
//            public void onClick(View v) {
//                if (onClickCloseImage != null) {
//                    onClickCloseImage.onClickImage(dialogAlert);
//                }
//            }
//        });
        if (msg != null) {
            msgLabel.setText(msg);
        }
//        if(dialogAlert != null)
//        dialogAlert.show();
        if (dialogAlert != null) {
            try {//20180724 SINN  Activity has leaked.
                dialogAlert.show();
            } catch (Exception e) {
                dialogAlert.dismiss();
            }
        }
    }

    public static void customDialogAlertAutoClear() {
//        if(dialogAlert != null)
//        dialogAlert.dismiss();
        if (dialogAlert != null) {
            try {//20180724 SINN  Activity has leaked.
                dialogAlert.dismiss();      // Paul_20180730
            } catch (Exception e) {
                dialogAlert.dismiss();
            }
        }
    }

    public static void customDialogAlertSuccess(Context context, @Nullable String msg, final OnClickCloseImage onClickCloseImage) {
        System.out.printf("utility:: customDialogAlertSuccess NNNNNNNNNN00001 \n");
        final Dialog dialogAlert = new Dialog(context, R.style.ThemeWithCorners); //K.GAME 180821
        View view = dialogAlert.getLayoutInflater().inflate(R.layout.dialog_custom_success, null);//K.GAME 180821
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
        dialogAlert.setContentView(view);//K.GAME 180821
        dialogAlert.setCancelable(false);//K.GAME 180821
//        final Dialog dialogAlert = new Dialog(context);
//        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogAlert.setContentView(R.layout.dialog_custom_success);
//        dialogAlert.setCancelable(false);
//        dialogAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogAlert.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        TextView msgLabel = dialogAlert.findViewById(R.id.msgLabel);
        Button btn_success = dialogAlert.findViewById(R.id.btn_dialog_success);//K.GAME 180821
        if (msg != null) {
            msgLabel.setText(msg);
        }
        btn_success.setOnClickListener(new OnClickListener() {//K.GAME 180821
            @Override
            public void onClick(View v) {
                if (onClickCloseImage != null) {
                    onClickCloseImage.onClickImage(dialogAlert);
                }
            }
        });
//        if(dialogAlert!=null)  ////20180723 SINN Sometime crash app close
//        dialogAlert.show();
        try {//20180724 SINN  Activity has leaked.
            dialogAlert.show();
        } catch (Exception e) {
            dialogAlert.dismiss();
        }
    }

    public static void customDialogAlertSuccessAuto(Context context, @Nullable String msg, final OnClickCloseImage onClickCloseImage) {
        System.out.printf("utility:: customDialogAlertSuccessAuto NNNNNNNNNN00001 \n");
        final Dialog dialogAlert = new Dialog(context, R.style.ThemeWithCorners);//K.GAME 180821
        View view = dialogAlert.getLayoutInflater().inflate(R.layout.dialog_custom_success, null);//K.GAME 180821
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
        dialogAlert.setContentView(view);//K.GAME 180821
        dialogAlert.setCancelable(false);//K.GAME 180821

//        final Dialog dialogAlert = new Dialog(context);
//        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogAlert.setContentView(R.layout.dialog_custom_success);
//        dialogAlert.setCancelable(false);
//        dialogAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogAlert.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        TextView msgLabel = dialogAlert.findViewById(R.id.msgLabel);
        Button btn_success = dialogAlert.findViewById(R.id.btn_dialog_success);//K.GAME 180821
        if (msg != null) {
            msgLabel.setText(msg);
        }
        btn_success.setOnClickListener(new OnClickListener() {//K.GAME 180821
            @Override
            public void onClick(View v) {
                if (onClickCloseImage != null) {
                    onClickCloseImage.onClickImage(dialogAlert);
                }
            }
        });

        final Dialog finaldialogAlert = dialogAlert;

        new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if(finaldialogAlert != null && finaldialogAlert.isShowing())       // Paul_20181025
                    finaldialogAlert.dismiss();
                if(dialogAlert != null && !dialogAlert.isShowing()) // Paul_20181025 I don't understand
                    onClickCloseImage.onClickImage(dialogAlert);
            }
        }.start();
//        if(dialogAlert!=null)  ////20180723 SINN Sometime crash app close
//        dialogAlert.show();
        try {//20180724 SINN  Activity has leaked.
            dialogAlert.show();
        } catch (Exception e) {
            dialogAlert.dismiss();
        }
    }

    public static void customDialogAlertFailAuto(Context context, @Nullable String msg, final OnClickCloseImage onClickCloseImage) {
        System.out.printf("utility:: customDialogAlertFailAuto NNNNNNNNNN00001 \n");
        final Dialog dialogAlert = new Dialog(context, R.style.ThemeWithCorners);//K.GAME 180821
        View view = dialogAlert.getLayoutInflater().inflate(R.layout.dialog_custom_alert, null);//K.GAME 180821
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
        dialogAlert.setContentView(view);//K.GAME 180821
        dialogAlert.setCancelable(false);//K.GAME 180821

//        final Dialog dialogAlert = new Dialog(context);
//        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogAlert.setContentView(R.layout.dialog_custom_success);
//        dialogAlert.setCancelable(false);
//        dialogAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogAlert.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        TextView msgLabel = dialogAlert.findViewById(R.id.msgLabel);
        Button btn_close = dialogAlert.findViewById(R.id.btn_dialog_close);//K.GAME 180821
        if (msg != null) {
            msgLabel.setText(msg);
        }
        btn_close.setOnClickListener(new OnClickListener() {//K.GAME 180821
            @Override
            public void onClick(View v) {
                if (onClickCloseImage != null) {
                    onClickCloseImage.onClickImage(dialogAlert);
                }
            }
        });

        final Dialog finaldialogAlert = dialogAlert;

        new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if(finaldialogAlert != null && finaldialogAlert.isShowing())       // Paul_20181025
                    finaldialogAlert.dismiss();
                if(dialogAlert != null && !dialogAlert.isShowing()) // Paul_20181025 I don't understand
                    onClickCloseImage.onClickImage(dialogAlert);
            }
        }.start();
//        if(dialogAlert!=null)  ////20180723 SINN Sometime crash app close
//        dialogAlert.show();
        try {//20180724 SINN  Activity has leaked.
            dialogAlert.show();
        } catch (Exception e) {
            dialogAlert.dismiss();
        }
    }

    public static void customDialogAlertSuccessAutoPaul(Context context, @Nullable String msg) {
        System.out.printf("utility:: Utility customDialogAlertSuccessAutoPaul \n");
        final Dialog dialogAlert = new Dialog(context, R.style.ThemeWithCorners);//K.GAME 180821
        View view = dialogAlert.getLayoutInflater().inflate(R.layout.dialog_custom_success, null);//K.GAME 180821
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
        dialogAlert.setContentView(view);//K.GAME 180821
        dialogAlert.setCancelable(false);//K.GAME 180821

//        final Dialog dialogAlert = new Dialog(context);
//        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogAlert.setContentView(R.layout.dialog_custom_success);
//        dialogAlert.setCancelable(false);
//        dialogAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogAlert.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        TextView msgLabel = dialogAlert.findViewById(R.id.msgLabel);
        Button btn_success = dialogAlert.findViewById(R.id.btn_dialog_success);//K.GAME 180821
        if (msg != null) {
            msgLabel.setText(msg);
        }
// Paul_20180718 Start
//        btn_success.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (onClickCloseImage != null) {
//                    onClickCloseImage.onClickImage(dialogAlert);
//                }
//            }
//        });
//
//        final Dialog finaldialogAlert = dialogAlert;
//
//        new CountDownTimer(1500, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//            }
//
//            @Override
//            public void onFinish() {
//                finaldialogAlert.dismiss();
//                onClickCloseImage.onClickImage(dialogAlert);
//            }
//        }.start();
//        if(dialogAlert!=null)  ////20180723 SINN Sometime crash app close
//        dialogAlert.show();
        try {//20180724 SINN  Activity has leaked.
            dialogAlert.show();
        } catch (Exception e) {
            dialogAlert.dismiss();
        }


        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dialogAlert.dismiss();

// Paul_20180718 End
    }

    public static void customDialogAlertFailAutoPaul(Context context, @Nullable String msg) {
        final Dialog dialogAlert = new Dialog(context, R.style.ThemeWithCorners);//K.GAME 180821
        View view = dialogAlert.getLayoutInflater().inflate(R.layout.dialog_custom_alert, null);//K.GAME 180821
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
        dialogAlert.setContentView(view);//K.GAME 180821
        dialogAlert.setCancelable(false);//K.GAME 180821

        TextView msgLabel = dialogAlert.findViewById(R.id.msgLabel);
        Button btn_close = dialogAlert.findViewById(R.id.btn_dialog_close);//K.GAME 180821

//        btn_close.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (PosInterfaceActivity.PosInterfaceExistFlg != 1)  ////SINN 20181013  rs232 not allow click cancel
//                    dialogAlert.dismiss();
//            }
//        });
//        btn_close.setOnClickListener(new View.OnClickListener() {//K.GAME 180821
//            @Override
//            public void onClick(View v) {
////                if (onClickCloseImage != null) {
////                    onClickCloseImage.onClickImage(dialogAlert);
////                }
//            }
//        });
        if (msg != null) {
            msgLabel.setText(msg);
        }
//        if(dialogAlert!=null)  ////20180723 SINN Sometime crash app close
//        dialogAlert.show();
        try {//20180724 SINN  Activity has leaked.
            if (dialogAlert != null)
                dialogAlert.show();
        } catch (Exception e) {
            dialogAlert.dismiss();
        }
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dialogAlert.dismiss();
    }


    public static void customDialogAlertNotConnect(Context context, @Nullable String msg, final OnClickCloseImage onClickCloseImage) {
        final Dialog dialogAlert = new Dialog(context, R.style.ThemeWithCorners); //K.GAME 180821
        View view = dialogAlert.getLayoutInflater().inflate(R.layout.dialog_custom_alert_not_connect, null);//K.GAME 180821
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
        dialogAlert.setContentView(view);//K.GAME 180821
        dialogAlert.setCancelable(false);//K.GAME 180821

//        final Dialog dialogAlert = new Dialog(context);
//        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogAlert.setContentView(R.layout.dialog_custom_alert_not_connect);
//        dialogAlert.setCancelable(false);
//        dialogAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogAlert.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        TextView msgLabel = dialogAlert.findViewById(R.id.msgLabel);
        Button okBtn = dialogAlert.findViewById(R.id.okBtn);
        Button closeImage = dialogAlert.findViewById(R.id.closeImage);
        if (msg != null) {
            msgLabel.setText(msg);
        }
        closeImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickCloseImage != null) {
                    onClickCloseImage.onClickImage(dialogAlert);
                }
            }
        });
        okBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickCloseImage != null) {
                    onClickCloseImage.onClickImage(dialogAlert);
                }
            }
        });
//        if(dialogAlert!=null)  ////20180723 SINN Sometime crash app close
//        dialogAlert.show();
        try {//20180724 SINN  Activity has leaked.
            dialogAlert.show();
        } catch (Exception e) {
            dialogAlert.dismiss();
        }
    }

    public static void customDialogAlertSelect(final Context context, @Nullable String msg, final OnClickCloseImage onTouchoutSide) {
        final Dialog dialogAlert = new Dialog(context, R.style.ThemeWithCorners); //K.GAME 180926
        View view = dialogAlert.getLayoutInflater().inflate(R.layout.dialog_custom_alert_not_connect, null);//K.GAME 180926
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180926
        dialogAlert.setContentView(view);//K.GAME 180926
        dialogAlert.setCancelable(false);//K.GAME 180926

//        final Dialog dialogAlert = new Dialog(context);
//        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogAlert.setContentView(R.layout.dialog_custom_alert_not_connect);
//        dialogAlert.setCancelable(false);
//        dialogAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogAlert.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        dialogAlert.setCancelable(true);
        dialogAlert.setCanceledOnTouchOutside(true);


        TextView msgLabel = dialogAlert.findViewById(R.id.msgLabel);
        Button okBtn = dialogAlert.findViewById(R.id.okBtn);
        okBtn.setText("*");

        if (msg != null) {
            msgLabel.setText(msg);
        }

        okBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onTouchoutSide.onClickImage(dialogAlert);
            }
        });


//        ImageView closeImage = dialogAlert.findViewById(R.id.closeImage);
        Button closeImage = dialogAlert.findViewById(R.id.closeImage);//K.GAME 180926 ImageView > Button
        closeImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        dialogAlert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // dialog dismiss without button press
            }
        });
//        if(dialogAlert!=null)  ////20180723 SINN Sometime crash app close
//        dialogAlert.show();
        try {//20180724 SINN  Activity has leaked.
            dialogAlert.show();
        } catch (Exception e) {
            dialogAlert.dismiss();
        }

    }


    public static void customDialogSelect(Context context, @Nullable String msg, final onTouchoutSide onTouchoutSide) {
        final Dialog dialogAlert = new Dialog(context);
        dialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAlert.setContentView(R.layout.dialog_custom_select);
        dialogAlert.setCancelable(false);
        dialogAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAlert.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        dialogAlert.setCancelable(true);
        dialogAlert.setCanceledOnTouchOutside(true);


        TextView msgLabel = dialogAlert.findViewById(R.id.msgLabel);
        Button okBtn = dialogAlert.findViewById(R.id.okBtn);
        Button cancelBtn = dialogAlert.findViewById(R.id.CancelBtn);
        //okBtn.setText("*");
        Utility.animation_Waiting_new(dialogAlert);

        if (msg != null) {
            msgLabel.setText(msg);
        }

        okBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onTouchoutSide.onClickImage(dialogAlert);
            }
        });

        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onTouchoutSide.onCancel(dialogAlert);
            }
        });

        ImageView closeImage = dialogAlert.findViewById(R.id.closeImage);
        closeImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onTouchoutSide.onCancel(dialogAlert);
            }
        });

        dialogAlert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // dialog dismiss without button press
                onTouchoutSide.onCancel(dialogAlert);
            }
        });
        try {
            dialogAlert.show();
        } catch (Exception e) {
            dialogAlert.dismiss();
        }

    }


    public static String calNumTraceNo(String trace) {
        String traceNo = "";
        for (int i = trace.length(); i < 6; i++) {
            traceNo += "0";
        }
        return traceNo + trace;
    }

    public static String idValue(String szQr, String szId, String szValue) {
        String szLen = null;
        szLen = String.valueOf(szValue.length());
        if (szLen.length() == 1)
            szLen = "0" + szLen;

        szQr += szId + szLen + szValue;

        return szQr;
    }

    public static String replaceString(int indexstr, String source, String setString) {
        String data1 = source.substring(0, indexstr);  //123
        String data2 = source.substring(indexstr + setString.length());  //89

        return data1 + setString + data2;
    }


    public static Bitmap createQRImage(String url, int QR_WIDTH, int QR_HEIGHT, Context context) {
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
                        pixels[y * QR_HEIGHT + x] = 0x00000000;//K.GAME 180926 new color สีใส
//                        pixels[y * QR_HEIGHT + x] = 0xffffff;
                    }
                }
            }//生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
//            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);

            Bitmap overlay = BitmapFactory.decodeResource(context.getResources(), R.drawable.qr_bot_s);
            //setting bitmap to image view
            bitmap = mergeBitmaps(overlay, bitmap);
            //显示到一个ImageView上面
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Bitmap mergeBitmaps(Bitmap overlay, Bitmap bitmap) {

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        Bitmap combined = Bitmap.createBitmap(width, height, bitmap.getConfig());
        Canvas canvas = new Canvas(combined);
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        canvas.drawBitmap(bitmap, new Matrix(), null);

        int centreX = (canvasWidth - overlay.getWidth()) / 2;
        int centreY = (canvasHeight - overlay.getHeight()) / 2;
        canvas.drawBitmap(overlay, centreX, centreY, null);

        return combined;
    }

    public static String getLength62(String slength62) {
        StringBuilder length = new StringBuilder();
        for (int i = slength62.length(); i < 4; i++) {
            length.append("0");
        }
        return length + slength62;
    }

    public static String CheckSumCrcCCITT(String SourceString) {
        String OutString = null;
        int crc = 0xffff;
//        int polynomial = 0xffff;
        int polynomial = 0x1021;

//        byte[] array = HexUtil.hexStringToByte( SourceString );
        byte[] array = SourceString.getBytes();
        for (byte b : array) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
            }
        }
        crc &= 0xFFFF;
        OutString = Integer.toHexString(crc);
        if (OutString.length() < 4) {
            for (int i = OutString.length(); i < 4; i++) {
                OutString = "0" + OutString;
            }
        }
        System.out.printf("utility:: CheckSumCrcCCITT AAAAAAAAAAAAAAA  %s \n", OutString);
        OutString = OutString.toUpperCase();        // Paul_20190311
        System.out.printf("utility:: CheckSumCrcCCITT AAAAAAAAAAAAAAA  %s \n", OutString);
        return OutString.toUpperCase();
    }


    // Paul_20180624
    public static int JavaHexDump(byte[] s, int len) {
        int i, j, quota = (len / 16), remainder = (len % 16);
        int ii;
//        byte[] TempBuf = new byte[100+1];
//        byte[] TempBuf1 = new byte[50+1];

        System.out.printf("utility:: Offset  Hex Value                                        Ascii value\n");
        j = 0;
        for (ii = 0; ii < quota; ii++) {
            System.out.printf("utility:: 0x%04X  ", j);
            for (i = 0; i < 16; i++) {
                System.out.printf("%02X ", s[i + j]);
            }
            System.out.print(" ");
            for (i = 0; i < 16; i++) {
                if ((s[i + j] >= 0x20) && (s[i + j] < 0x80)) {
                    System.out.printf("%c", s[i + j]);
                } else {
                    System.out.printf(".");
                }
            }
            j += 16;
            System.out.printf("\n");
        }
        if (remainder != 0) {
            System.out.printf("utility:: 0x%04X  ", j);

            for (i = 0; i < remainder; i++) {
                System.out.printf("%02X ", s[i + j]);
            }
            for (i = 0; i < (16 - remainder); i++) {
                System.out.printf("   ");
            }
            System.out.printf(" ");
            for (i = 0; i < remainder; i++) {
                if ((s[i + j] >= 0x20) && (s[i + j] < 0x80)) {
                    System.out.printf("%c", s[i + j]);
                } else {
                    System.out.printf(".");
                }
            }
            for (i = 0; i < (16 - remainder); i++) {
                System.out.printf(" ");
            }
            System.out.printf("\n");
        }
        return 0;
    }

    // Paul_20180705
    public static int BCDtoInt(byte FirstByte, byte SecondByte) {
        int Returnint = 0;
        byte[] bcd = new byte[2];
        String StrBcd;

        bcd[0] = FirstByte;
        bcd[1] = SecondByte;
        StrBcd = HexUtil.bcd2str(bcd);
        Returnint = Integer.parseInt(StrBcd);

        return Returnint;
/*
        Returnint = ((int)((FirstByte & (byte) 0xF0) >> 4) * 1000) + ((int)(FirstByte & (byte) 0x0F) * 100) + ((int)((SecondByte & (byte) 0xF0) >> 4) * 10) + ((int)(SecondByte & (byte) 0x0F));
        return Returnint;
*/
    }

    // Paul_20180624
    public static byte[] IntToBCD(int iLen) {
        int i;
        int op;
        int iiLen;
        byte[] dst = new byte[2];

        op = 1000;
        for (i = 0; i < 2; i++) {
            iiLen = iLen / op;
            iLen %= op;
            op /= 10;
            dst[i] = (byte) ((iiLen << 4) & (byte) 0xF0);

            iiLen = iLen / op;
            iLen %= op;
            op /= 10;
            dst[i] |= (iiLen & 0x0F);
        }
        return dst;
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

//    public static void dialogSuccess_GotoMain(final Context context) { //K.GAME 180903 add new dialog
//        final Dialog dialogSuccess_GotoMain = new Dialog(context);
//        dialogSuccess_GotoMain.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogSuccess_GotoMain.setContentView(R.layout.dialog_custom_success_gotomain);
//        dialogSuccess_GotoMain.setCancelable(false);
//        dialogSuccess_GotoMain.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        dialogSuccess_GotoMain.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        TextView msgLabel = dialogSuccess_GotoMain.findViewById(R.id.msgLabel);
//        Button btn_gotoMain = dialogSuccess_GotoMain.findViewById(R.id.btn_gotoMain);
//        btn_gotoMain.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "btn_gotoMain", Toast.LENGTH_SHORT).show();
//
//            }
//        });
//        dialogSuccess_GotoMain.show();
//    }

    public static void animation_Waiting_new(Dialog dialogWaiting) {
        ImageView imageView = dialogWaiting.findViewById(R.id.img_waitting_new);

//        ObjectAnimator anim = ObjectAnimator.ofFloat(imageView, View.ROTATION, 360.0f);
        ObjectAnimator anim = ObjectAnimator.ofFloat(imageView, View.ROTATION, 360.0f);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(700);
//        anim.setDuration(1000);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.start();


//        ImageView view = dialogWaiting.findViewById(R.id.img_waitting_new); //Initialize ImageView via FindViewById or programatically
//
//        RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//
////Setup anim with desired properties
//        anim.setInterpolator(new LinearInterpolator());
//        anim.setRepeatCount(Animation.INFINITE); //Repeat animation indefinitely
//        anim.setDuration(700); //Put desired duration per anim cycle here, in milliseconds
////Start animation
//        view.startAnimation(anim);
    }

    public interface OnClickOk {//K.GAME 181002

        void onClickOk(Dialog dialog);

        void onClickCancel(Dialog dialog);
    }

    public interface OnClickCloseImage {
        void onClickImage(Dialog dialog);
    }

    public interface onTouchoutSide {
        void onClickImage(Dialog dialog);

        void onCancel(Dialog dialog);
    }

    public static Dialog getDialogAlert() {
        return dialogAlert;
    }

}
