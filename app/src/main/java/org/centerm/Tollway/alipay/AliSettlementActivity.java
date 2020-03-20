package org.centerm.Tollway.alipay;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.centerm.Tollway.R;
import org.centerm.Tollway.database.QrCode;
import org.centerm.Tollway.utility.Preference;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import io.realm.Realm;
import io.realm.RealmResults;

import static java.lang.Math.floor;

//import org.centerm.Tollway.alipay.database.AliTemp;

public class AliSettlementActivity extends AppCompatActivity implements View.OnClickListener {

    public String ALIPAY_CER_PATH = "/data/ct/ktb_alipay_uat.cer";
    private SettlementLister settlementLister = null;

    private Realm realm;
    private TextView txt_unitsale;
    private TextView txt_unitvoid;
    private TextView txt_amountsale;
    private TextView txt_amountvoid;

    private Button btn_ok;

    private QrCode aliTemp; // Database
    private AliServiceActivity.AlipayListener alipayListener = null;
    private AliServiceActivity.AliDbUpdateDatabase insertOrUpdateDatabase = null;

    private Context context = null;

    private CountDownTimer timer = null;
    private HttpsURLConnection urlConnection;
    private SSLContext sslContext;

    private final String TAG = this.getClass().getName();

    private Dialog dialogWaiting = null;

    private Intent intent;
    private URL url;

    private JSONObject jsonObject;
    private JSONObject jsonObject2;
    private CryptoServices cryptoServices;
    private InputStream is = null;
    String result = "";

    //Header
    private String reqBy;
    private String reqChannel = "EDC";
    private String reqChannelDtm;
    private String uniqueData;
    private String reqChannelRefId;
    private String service;

    //Body
    private String amt;
    private String token;
    private String deviceid;
    private String merid;
    private String storeid;
    private String record;
    private int endRecord;
    private String page = "0";
    private int endPage;
    private String startDate;
    private String endDate;

    private String invoice = "";
    private String type = "";
    private String data = "";
    private String param1 = "";
    private String param2 = "";

    private AliConfig aliConfig;

    private Date dateTime;
    private DateFormat dateFormat;
    private DateFormat dateFormat2;

    //result
    private String status = "";

    private String respcode = "";
    private String reqid = "";
    private String reqdt = "";
    private String transid = "";
    private String walletcode = "";
    private String wallettransid = "";
    private String canceldt = "";
    private String cii = "";
    private String transdt = "";
    private String foramt = "";
    private String buyerid = "";
    private String format = "";
    private String convrate = "";
    private String walletcurr = "";
    private String exchrateunit = "";
    private String fee = "";
    private String amtplusfee = "";
    private String receipttext = "";
    private String feeType = "";
    private String feeRate = "";
    private String merType = "";

    private int cntSale = 0;
    private int cntVoid= 0;
    private Double amountSale = 0.0;
    private Double amountVoid = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_ali_settlement);
        initWidget();
        function();

        settlementLister = new SettlementLister() {
            @Override
            public void onSuccess() {
                deleteDB();
System.out.printf("utility:: onCreate onSuccess \n");
                status = AliConfig.Success;
                Intent slip = new Intent(AliSettlementActivity.this, AliSettleSlipActivity.class);
                slip.putExtra("STATUS", status);
                slip.putExtra("SALECNT", String.valueOf(cntSale));
                slip.putExtra("VOIDCNT", String.valueOf(cntVoid));
                slip.putExtra("SALEAMOUNT", String.valueOf(amountSale));
                slip.putExtra("VOIDAMOUNT",String.valueOf(amountVoid));
                startActivity(slip);
                finish();
            }

            @Override
            public void onContinue() {
                System.out.printf("utility:: onCreate onContinue \n");
                if(page.equals(String.valueOf(endPage))){
                    deleteDB();
                    status = AliConfig.Success;
                    Intent slip = new Intent(AliSettlementActivity.this, AliSettleSlipActivity.class);
                    slip.putExtra("STATUS", status);
                    slip.putExtra("SALECNT", String.valueOf(cntSale));
                    slip.putExtra("VOIDCNT", String.valueOf(cntVoid));
                    slip.putExtra("SALEAMOUNT", String.valueOf(amountSale));
                    slip.putExtra("VOIDAMOUNT",String.valueOf(amountVoid));
                    startActivity(slip);
                    finish();
                }else{

                    page = String.valueOf(Integer.parseInt(page)+1);

                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                if (realm == null)
                                    realm = Realm.getDefaultInstance();

                                RealmResults<QrCode> saleTemp = realm.where(QrCode.class).equalTo( "hostTypeCard","ALIPAY" ).equalTo("voidflag", "N").findAll();

                                settlement2(saleTemp);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                }
            }

            @Override
            public void onFail() {
                System.out.printf("utility:: onCreate onFail \n");
                status = AliConfig.Fail;
                Intent slip = new Intent(AliSettlementActivity.this, AliSettleSlipActivity.class);
                slip.putExtra("STATUS", status);
                startActivity(slip);
                finish();
            }
        };
    }

    private void function() {
        String tmp;
        DecimalFormat nf = new DecimalFormat("##,###,##0.00");

        try {
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }

            RealmResults<QrCode> saleTemp = realm.where(QrCode.class).equalTo( "hostTypeCard","ALIPAY" ).equalTo("voidflag", "N").equalTo("respcode", "0").findAll();
            cntSale = saleTemp.size();
            for (int i = 0; i < cntSale; i++) {
//                if(saleTemp.get(i).getFee().equals( "null" ))   // Paul_20181009
//                {
//                    tmp = saleTemp.get(i).getAmt();
//                }
//                else
//                {
//                    tmp = saleTemp.get(i).getAmtplusfee();
//                }
                tmp = saleTemp.get(i).getAmt();
                tmp = delcomma(tmp);
                amountSale += Float.valueOf(tmp);
                amountSale = floor( amountSale * 100.f + 0.5 ) / 100.f;
            }

            RealmResults<QrCode> voidTemp = realm.where(QrCode.class).equalTo( "hostTypeCard","ALIPAY" ).equalTo("voidflag", "Y").equalTo("respcode", "0").findAll();
            cntVoid = voidTemp.size();
            for (int i = 0; i < cntVoid; i++) {
//                if(voidTemp.get(i).getFee().equals( "null" ))   // Paul_20181009
//                {
//                    tmp = voidTemp.get(i).getAmt();
//                }
//                else
//                {
//                    tmp = voidTemp.get(i).getAmtplusfee();
//                }
                tmp = voidTemp.get(i).getAmt();
                tmp = delcomma(tmp);
                amountVoid += Float.valueOf(tmp);
                amountVoid = floor( amountVoid * 100.f + 0.5 ) / 100.f;
            }

            txt_unitsale.setText(String.valueOf(cntSale));
            txt_unitvoid.setText(String.valueOf(cntVoid));

            txt_amountsale.setText(nf.format(amountSale));
            txt_amountvoid.setText(nf.format(amountVoid));

        } finally {
            if (realm != null) {
                realm.close();
            }
            realm = null;

            // Paul_20181009
            if(cntSale == 0  && cntVoid == 0) {
//                Toast.makeText(AliSettlementActivity.this, "Transaction empty ! ", Toast.LENGTH_SHORT).show();
                finish();
//                btn_ok.setVisibility(View.GONE);
//                Utility.customDialogAlert(AliSettlementActivity.this, "ไม่มีข้อมูล", new Utility.OnClickCloseImage() {
//                    @Override
//                    public void onClickImage(Dialog dialog) {
//                        dialog.dismiss();
//                        finish();
//                    }
//                });

            }else{
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            settlement();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }

        }
    }

    private void initWidget() {

        aliConfig = new AliConfig();
        jsonObject = new JSONObject();
        jsonObject2 = new JSONObject();
        cryptoServices = new CryptoServices();

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        dateFormat2 = new SimpleDateFormat("yyyyMMddHHmmss");

        txt_unitsale = findViewById( R.id.txt_unitsale);
        txt_unitvoid = findViewById( R.id.txt_unitvoid);
        txt_amountsale = findViewById( R.id.txt_amountsale);
        txt_amountvoid = findViewById( R.id.txt_amountvoid);
        btn_ok = findViewById( R.id.btn_ok);                  // Paul_20181009
        btn_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_ok :
                if(cntSale == 0  && cntVoid == 0) {
                    Toast.makeText(AliSettlementActivity.this, "Transaction empty ! ", Toast.LENGTH_SHORT).show();
                }else{
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                settlement();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
                break;
        }
    }

    private void settlement() {

        try {
            dateTime = new Date();
            reqChannelDtm = dateFormat.format(dateTime); // + dateFormat_time.format(dateTime);
            uniqueData = makeUniqueData(dateTime);

//            data = aliConfig.getHttps() + "transaction/inquiry";

            if (realm == null)
                realm = Realm.getDefaultInstance();



            RealmResults<QrCode> saleTemp = realm.where(QrCode.class).equalTo( "hostTypeCard","ALIPAY" ).equalTo("voidflag", "N").equalTo("respcode", "0").findAll();

            endRecord = saleTemp.size();
            checkPage(endRecord);

            if (endRecord < 20) {
                if(endRecord == 0){
                    settlementLister.onSuccess();
                }else{
                    startDate = saleTemp.get(0).getReqChannelDtm().substring(0, 10);
                    endDate = saleTemp.get(saleTemp.size() - 1).getReqChannelDtm().substring(0, 10);
                    record = String.valueOf(endRecord);

                    page = checkLength(page, 3);
                    record = checkLength(record, 3);

                    try {
//                        jsonObject.put("deviceid", aliConfig.getDeviceId());
//                        jsonObject.put("merid", aliConfig.getMerId());
//                        jsonObject.put("storeid", aliConfig.getStoreId());
                        jsonObject.put("deviceid", Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_TERMINAL_ID));    // Paul_20181007
                        jsonObject.put("merid", Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_MERCHANT_ID));          // Paul_20181007
                        jsonObject.put("storeid", Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_STORE_ID));      // Paul_20181007

                        jsonObject.put("startDate", startDate);
                        jsonObject.put("endDate", endDate);
                        jsonObject.put("currentPage", page);
                        jsonObject.put("recordPerPage", record);

                        System.out.printf("utility:: %s , settlement = %s \n",TAG,jsonObject.toString());

                        param1 = cryptoServices.encryptAES(jsonObject.toString(), cryptoServices.AES_KEY);
//            param2 = cryptoServices.encryptRSA(cryptoServices.AES_KEY, cryptoServices.PUBLIC_PATH); //20181114Jeff
                        param2 = cryptoServices.encryptRSA(cryptoServices.AES_KEY, Preference.getInstance(AliSettlementActivity.this).getValueString(Preference.KEY_ALIPAY_PUBLIC_ID));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.d("JSON data :: ", jsonObject.toString());
                    Log.d("param1 :: ", param1);
                    Log.d("param2 :: ", param2);

                    sendMessage();
                }


            } else {
                settlement2(saleTemp);
            }
        }finally {
            if (realm != null) {
                realm.close();
            }
            realm = null;
        }
    }

    private void checkPage(int endRecord) {
        int value;
        int value2;

        value = endRecord/20;
        value2 = endRecord%20;

        if(value == 0){
            endPage = 1;
            page = "1";
        }else{
            if(value2 > 0)
                endPage = value+1;
            else
                endPage = value;
            page = "1";
        }
    }

    private String delcomma(String amount) {
        String result_amount;
        result_amount = amount.toString().replaceAll(",", "");
        return result_amount;
    }

    private String checkLength(String trace, int i ) {
        int tmp_lc = 0;

        tmp_lc = trace.length();

        for(int j = 0; j<(i-tmp_lc); j++)
            trace = "0" + trace;

        return trace;
    }

    private void settlement2(RealmResults<QrCode> saleTemp) {

        settlementDataset(saleTemp);

        try {

//            jsonObject.put("deviceid", aliConfig.getDeviceId());
//            jsonObject.put("merid", aliConfig.getMerId());
//            jsonObject.put("storeid", aliConfig.getStoreId());
            jsonObject.put("deviceid", Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_TERMINAL_ID));    // Paul_20181007
            jsonObject.put("merid", Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_MERCHANT_ID));          // Paul_20181007
            jsonObject.put("storeid", Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_STORE_ID));      // Paul_20181007

            jsonObject.put("startDate", startDate);
            jsonObject.put("endDate", endDate);
            jsonObject.put("currentPage", page);
            jsonObject.put("recordPerPage", record);

            System.out.printf("utility:: %s , settlement2 = %s \n",TAG,jsonObject.toString());

            param1 = cryptoServices.encryptAES(jsonObject.toString(), cryptoServices.AES_KEY);
//            param2 = cryptoServices.encryptRSA(cryptoServices.AES_KEY, cryptoServices.PUBLIC_PATH); //20181114Jeff
            param2 = cryptoServices.encryptRSA(cryptoServices.AES_KEY, Preference.getInstance(AliSettlementActivity.this).getValueString(Preference.KEY_ALIPAY_PUBLIC_ID));

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("JSON data :: ", jsonObject.toString());
        Log.d("param1 :: ", param1);
        Log.d("param2 :: ", param2);

        sendMessage2();
    }

    private void settlementDataset(RealmResults<QrCode> saleTemp) {
        int cnt = Integer.parseInt(page)-1;

        if(page.equals(String.valueOf(endPage)))
            record = String.valueOf((saleTemp.size()-(20*cnt))-1);
        else
            record = "20";

        if(page.equals(String.valueOf(endPage))){
            startDate =saleTemp.get(20*cnt).getReqChannelDtm().substring(0,10);
            endDate = saleTemp.get(saleTemp.size()-1).getReqChannelDtm();
        }else{
            startDate = saleTemp.get(20*cnt).getReqChannelDtm().substring(0,10);
            endDate = saleTemp.get((20*(cnt+1))-1).getReqChannelDtm().substring(0,10);
        }
    }

    private void sendMessage() {

        try {
            //set certification
            setCertification(context);

            if(url != null)
                url = null;

            url = new URL(data);

            if(urlConnection != null )
                urlConnection.disconnect();

            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());

            urlConnection.setDefaultUseCaches(false);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");

//            setTimer(15000);

            setPostHeader();
            setPostBody();
            connetPost();

        } catch (IOException e) {
            e.printStackTrace();
            settlementLister.onFail();
        }
    }

    private void sendMessage2() {

        try {
            //set certification
            setCertification(context);

            if(url == null)
                url = new URL(data);

            if(urlConnection != null )
                urlConnection.disconnect();

            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());

            urlConnection.setDefaultUseCaches(false);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");

//            setTimer(15000);

            setPostHeader();
            setPostBody();
            connetPost2();

        } catch (IOException e) {
            e.printStackTrace();
            settlementLister.onFail();
        }
    }

    private void setPostHeader() {
//        reqBy = aliConfig.getMerId() + aliConfig.getDeviceId();
//        reqChannelRefId = aliConfig.getDeviceId() + uniqueData;
        reqBy = Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_MERCHANT_ID) + Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_TERMINAL_ID);     // Paul_20181007
        reqChannelRefId = Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_TERMINAL_ID) + uniqueData;     // Paul_20181007

        urlConnection.setRequestProperty("content-type", "application/json");
        urlConnection.setRequestProperty("reqBy", reqBy);
        urlConnection.setRequestProperty("reqChannel",reqChannel);
        urlConnection.setRequestProperty("reqChannelDtm", reqChannelDtm);
        urlConnection.setRequestProperty("reqChannelRefId", reqChannelRefId);
        urlConnection.setRequestProperty("service", type);

        Log.d("set_Header :: ", "");
        Log.d("reqBy", reqBy);
        Log.d("reqChannel", reqChannel);
        Log.d("reqChannelDtm", reqChannelDtm);
        Log.d("reqChannelRefId",  reqChannelRefId);
        Log.d("service", type);

    }

    private void setPostBody() {
        try {
            jsonObject2.put("param1", param1);
            jsonObject2.put("param2", param2);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        data = jsonObject2.toString();
    }

    private void connetPost() {
        try {

            if (!data.isEmpty()) { // 웹 서버로 보낼 매개변수가 있는 경우
                OutputStream os = null; // 서버로 보내기 위한 출력 스트림
                os = urlConnection.getOutputStream();

                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8")); // UTF-8로 전송
                bw.write(data); // 매개변수 전송
                bw.flush();
                bw.close();
                os.close();
            }

            if(urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK){
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                getResponseData(in);
                dialogWaiting.dismiss();
            }else{
                status = AliConfig.Fail;
                settlementLister.onFail();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        urlConnection.disconnect();
    }

    private void connetPost2() {
        try {

            if (!data.isEmpty()) { // 웹 서버로 보낼 매개변수가 있는 경우
                OutputStream os = null; // 서버로 보내기 위한 출력 스트림
                os = urlConnection.getOutputStream();

                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8")); // UTF-8로 전송
                bw.write(data); // 매개변수 전송
                bw.flush();
                bw.close();
                os.close();
            }

            if(urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK){
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                getResponseData2(in);
                dialogWaiting.dismiss();
            }else{
                status = AliConfig.Fail;
                settlementLister.onFail();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        urlConnection.disconnect();
    }

    private void getResponseData(InputStream in) {
        final String data = readData(in);
        String tmp;
        JSONObject obj;
        JSONObject obj2;
        try {
            tmp = cryptoServices.decryptAES(data, cryptoServices.AES_KEY);

            obj = new JSONObject(tmp);
            status = obj.getString("status");
            result = obj.getString("result");
            if(!result.equals("null")){
//                obj2 = new JSONObject(result);

//                respcode = obj2.getString("respcode");
//                reqid = obj2.getString("origreqid");
//                reqdt = obj2.getString("origreqdt");
//                transid = obj2.getString("transid");
//                wallettransid = obj2.getString("wallettransid");
//                wallettransid = obj2.getString("canceldt");
//                wallettransid = obj2.getString("cii");
//                receipttext = obj2.getString("receipttext");

                if(status.equals(AliConfig.Success))
                    settlementLister.onSuccess();
                else
                    settlementLister.onFail();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getResponseData2(InputStream in) {
        final String data = readData(in);
        String tmp;
        JSONObject obj;
        JSONObject obj2;
        try {
            tmp = cryptoServices.decryptAES(data, cryptoServices.AES_KEY);

            obj = new JSONObject(tmp);
            status = obj.getString("status");
            result = obj.getString("result");
            if(!result.equals("null")){
                obj2 = new JSONObject(result);

//                respcode = obj2.getString("respcode");
//                reqid = obj2.getString("origreqid");
//                reqdt = obj2.getString("origreqdt");
//                transid = obj2.getString("transid");
//                wallettransid = obj2.getString("wallettransid");
//                wallettransid = obj2.getString("canceldt");
//                wallettransid = obj2.getString("cii");
//                receipttext = obj2.getString("receipttext");
                if(status.equals(AliConfig.Success))
                    settlementLister.onContinue();
                else
                    settlementLister.onFail();
            }



        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteDB() {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<QrCode> transTemp = realm.where(QrCode.class).equalTo( "hostTypeCard","ALIPAY" ).findAll();
                transTemp.deleteAllFromRealm();
            }
        });
    }

    public String readData(InputStream is){
        String data = "";
        Scanner s = new Scanner(is);
        while(s.hasNext()) data += s.nextLine() + "\n";
        s.close();
        return data;
    }

    private void setCertification(Context context) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate ca = null;
            FileInputStream fr;

            System.out.printf( "AlipayCertRead Start %s \n", ALIPAY_CER_PATH );

            File file = new File( ALIPAY_CER_PATH );

            if (!file.exists()) {
                System.out.printf( "AlipayCert file not found %s \n", ALIPAY_CER_PATH );
            }else{
                fr = new FileInputStream( file );

                ca = cf.generateCertificate(fr);
                System.out.printf( "ca =" + ((X509Certificate) ca).getSubjectDN());
                fr.close();
            }

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null,null);
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

    }

    private String makeUniqueData(Date data) {

        String temp = dateFormat2.format(data);  // yyyyMMddhhmmss
        dateFormat2 = new SimpleDateFormat("D");
        String julian_date = dateFormat2.format(data);

        String temp_year = temp.substring(0,4);
        String temp_time = temp.substring(8,14);


        temp_year = String.valueOf(Integer.parseInt(temp_year) + 483);

        temp = temp_year.substring(3,4) + julian_date + temp_time;

        return temp;
    }

    interface SettlementLister {
        public void onSuccess();
        public void onContinue();
        public void onFail();
    }
}
