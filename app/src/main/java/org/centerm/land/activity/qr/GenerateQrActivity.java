package org.centerm.land.activity.qr;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.centerm.smartpos.aidl.qrscan.QuickScannerZbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.centerm.land.R;
import org.centerm.land.bassactivity.SettingToolbarActivity;
import org.centerm.land.manager.HttpManager;
import org.centerm.land.utility.Preference;
import org.centerm.land.utility.Utility;

import java.util.Hashtable;

public class GenerateQrActivity extends SettingToolbarActivity implements View.OnClickListener {

    private final String TAG = "GenerateQrActivity";
    private LinearLayout ref2LinearLayout;
    private ImageView qrImage = null;
    private EditText amountBox = null;
    private EditText ref1Box = null;
    private EditText ref2Box = null;
    private Button generatorBtn = null;
    private String tagAll = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);
        initWidget();
        initBtnExit();
    }

    @Override
    public void initWidget() {
//        super.initWidget();

        qrImage = findViewById(R.id.qrImage);
        ref2LinearLayout = findViewById(R.id.ref2LinearLayout);
        amountBox = findViewById(R.id.amountBox);
        ref1Box = findViewById(R.id.ref1Box);
        ref2Box = findViewById(R.id.ref2Box);

        generatorBtn = findViewById(R.id.generatorBtn);
        generatorBtn.setOnClickListener(this);
        if (Preference.getInstance(this).getValueBoolean(Preference.KEY_REF_2)) {
            ref2LinearLayout.setVisibility(View.VISIBLE);
        } else {
            ref2LinearLayout.setVisibility(View.GONE);
        }

//        HttpManager.getInstance().getService().getStatusLogin()


    }

    private Bitmap createQRImage(String url, int QR_WIDTH, int QR_HEIGHT) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.generatorBtn:
                tagAll = Utility.idValue("", "00", "01");
                tagAll = Utility.idValue(tagAll, "01", "11");
                String tagIn30 = Utility.idValue("","00","A000000677010112");
                tagIn30 = Utility.idValue(tagIn30,"01","010352102131870");
                tagIn30 = Utility.idValue(tagIn30,"02",ref1Box.getText().toString());
                String tag30 = Utility.idValue("","30",tagIn30);
                tagAll += tag30;
                tagAll = Utility.idValue(tagAll,"53","764");
                tagAll = Utility.idValue(tagAll,"54",amountBox.getText().toString());
                tagAll = Utility.idValue(tagAll,"58","TH");
                tagAll = Utility.idValue(tagAll,"59","NAKHONRATCHASIMA PCG.");
                String tagIn62 = Utility.idValue("","07","00025068000023180517");
                String tag62 = Utility.idValue("","62",tagIn62);
                tagAll +=tag62;
                tagAll = Utility.idValue(tagAll,"63","90FD");

                Log.d(TAG, "initWidget: " + tagAll);
                qrImage.setImageBitmap(createQRImage(tagAll, 300, 300));
                break;
        }
    }
}
