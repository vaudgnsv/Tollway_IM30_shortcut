package org.centerm.Tollway.alipay;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.centerm.Tollway.R;
import org.centerm.Tollway.database.QrCode;
import org.centerm.Tollway.helper.CardPrefix;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;
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

//import org.centerm.Tollway.alipay.database.AliTemp;

public class AliServiceActivity extends AppCompatActivity implements View.OnClickListener{


//    public String ALIPAY_CER_PATH = "/data/thaivan/ktb_alipay_uat.cer";
    public String ALIPAY_CER_PATH = ""; //20181115JFEFF

    private QrCode aliTemp; // Database // Paul_20181009
    private Realm realm = null;
    private AlipayListener alipayListener = null;
    private AliDbUpdateDatabase insertOrUpdateDatabase = null;

    private Context context = null;
    private TextView msgLabel;

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
    String resmsg = "";        // Paul_20181003

    //Header
    private String reqBy;
    private String reqChannel = "EDC";
    private String reqChannelDtm;
    private String uniqueData;
    private String reqChannelRefId;
    private String service;

    //Body
    private String amt;
    private String tmp_amt;
    private String token;
    private String deviceid;
    private String merid;
    private String storeid;
    private String record;
    private String page;
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

    private String alipay_http;
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
    private String fee = "null";
    private String amtplusfee = "null";
    private String receipttext = "";
    private String feeType = "";
    private String feeRate = "";
    private String merType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_ali_service);

        intent = getIntent();
        aliConfig = new AliConfig();
        jsonObject = new JSONObject();
        jsonObject2 = new JSONObject();
        cryptoServices = new CryptoServices();

        type = intent.getExtras().getString("TYPE");
        walletcode = intent.getExtras().getString("WALLET_CODE");
        check_walletcode();
        initData();

        dialogWaiting.show();
    }

    private void check_walletcode() {
        if(walletcode.equals("ALI_SALE") || walletcode.equals("ALIPAY"))
            walletcode = "ALIPAY";
        else
            walletcode = "WECHAT";
    }

    private void initData() {

        merid = Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_MERCHANT_ID);               // Paul_20181007
        storeid = Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_STORE_ID);           // Paul_20181007
        deviceid = Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_TERMINAL_ID);         // Paul_20181007
        alipay_http = Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_URL_ID); //20181114Jeff
        ALIPAY_CER_PATH = Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_CERTI_ID);//20181115Jeff
        invoice = CardPrefix.getInvoice(context, walletcode);

        System.out.printf("utility:: initData , invoice = %s \n",invoice);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        dateFormat2 = new SimpleDateFormat("yyyyMMddHHmmss");

        DbListener();
        transactionListener();
        customDialogWaiting();

        if(merid.isEmpty() || storeid.isEmpty() || deviceid.isEmpty()){
            resmsg ="Please check Json File for Alipay";
            AliConfig.Fail = resmsg;
            alipayListener.onDenclined();
        }else{
            function();
        }
    }


    private void function() {

        new Thread() {
            @Override
            public void run() {
                try {

                    if(type.equals( AliConfig.SubmitSale)) {
                        System.out.printf("utility:: %s function submitsale Start \n",TAG);
                        submitsale();
                    }else  if(type.equals( AliConfig.Sale)) {
                        System.out.printf("utility:: %s function sale Start \n",TAG);
                        sale();
                    }else if(type.equals( AliConfig.Inquiry)){
                        System.out.printf("utility:: %s function inquiry Start \n",TAG);
                        inquiry();
                    }else  if(type.equals( AliConfig.Void)) {
                        System.out.printf("utility:: %s function void_sale Start \n",TAG);
                        void_sale();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void inquiry() {
        invoice = intent.getExtras().getString("INVOICE");

        System.out.printf("utility:: inquiry 0000001 \n");
        System.out.printf("utility:: inquiry invoice = %s \n",invoice);

        dateTime = new Date();
        reqChannelDtm = dateFormat.format(dateTime); // + dateFormat_time.format(dateTime);
        uniqueData = makeUniqueData(dateTime);

//        data = aliConfig.getHttps() +"sale/inquiry";
        data = alipay_http + "sale/inquiry"; //20181114JEFF

        try {
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }

            QrCode aliTemp = realm.where(QrCode.class).equalTo( "hostTypeCard", walletcode).equalTo("trace", invoice).findFirst();

            System.out.printf("utility:: inquiry 0000002 \n");

            amt = aliTemp.getAmt();
            tmp_amt = delcomma(amt);
            fee = aliTemp.getFee();
            amtplusfee = aliTemp.getAmtplusfee();
            System.out.printf("utility:: inquiry 0000003 amt = %s , fee = %s , amtplusfee = %s \n",amt,fee,amtplusfee);
            reqdt = aliTemp.getReqdt();
            reqid = aliTemp.getReqid();

        } finally {
            if (realm != null) {
                realm.close();
            }
            realm = null;
        }

        try {
            jsonObject.put("amt", tmp_amt);
            System.out.printf("utility:: inquiry 0000004 amt = %s , fee = %s , amtplusfee = %s \n",amt,fee,amtplusfee);
            if(!fee.equals( "null" ))   // Paul_20181009
            {
                jsonObject.put( "fee", fee );
                jsonObject.put( "amtplusfee", amtplusfee );
            }

            jsonObject.put("origreqdt", reqdt);
            jsonObject.put("origreqid", reqid);

            jsonObject.put("deviceid", deviceid);
            jsonObject.put("merid", merid);
            jsonObject.put("storeid", storeid);

            System.out.printf("utility:: %s , INQUIRESALE = %s \n",TAG,jsonObject.toString());

            param1 = cryptoServices.encryptAES(jsonObject.toString(), cryptoServices.AES_KEY);
//            param2 = cryptoServices.encryptRSA(cryptoServices.AES_KEY, cryptoServices.PUBLIC_PATH);
            param2 = cryptoServices.encryptRSA(cryptoServices.AES_KEY, Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_PUBLIC_ID));

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


    private void void_sale() {
        invoice = intent.getExtras().getString("INVOICE");
        System.out.printf("utility:: void_sale invoice = %s \n",invoice);
        dateTime = new Date();
        reqChannelDtm = dateFormat.format(dateTime); // + dateFormat_time.format(dateTime);
        uniqueData = makeUniqueData(dateTime);

//        data = aliConfig.getHttps() +"void";
        data = alipay_http +"void"; // 20181114JEFF

        if (realm == null)
            realm = Realm.getDefaultInstance();

        QrCode voidTemp = realm.where(QrCode.class).equalTo("trace", invoice).findFirst();

        amt = voidTemp.getAmt();
        tmp_amt = delcomma(amt);
        amtplusfee = voidTemp.getAmtplusfee();  // Paul_20181006

        try {
            jsonObject.put("origamt", tmp_amt);
            if(!voidTemp.getFee().equals( "null" )) {       // Paul_20181009
                jsonObject.put( "origfee", voidTemp.getFee() );
                jsonObject.put( "origamtplusfee", voidTemp.getAmtplusfee() );
            }
            jsonObject.put("origreqdt", voidTemp.getReqdt());
            jsonObject.put("origreqid", voidTemp.getReqid());
            jsonObject.put("origtransid", voidTemp.getTransid());
            jsonObject.put("deviceid", deviceid);
            jsonObject.put("merid", merid);
            jsonObject.put("storeid", storeid);

            System.out.printf("utility:: %s , VOID = %s \n",TAG,jsonObject.toString());

            param1 = cryptoServices.encryptAES(jsonObject.toString(), cryptoServices.AES_KEY);
//            param2 = cryptoServices.encryptRSA(cryptoServices.AES_KEY, cryptoServices.PUBLIC_PATH);
            param2 = cryptoServices.encryptRSA(cryptoServices.AES_KEY, Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_PUBLIC_ID));


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

    private void customDialogWaiting() {
        dialogWaiting = new Dialog(this);
        dialogWaiting.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWaiting.setCancelable(false);
        dialogWaiting.setContentView(R.layout.dialog_custom_load_process);
        dialogWaiting.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogWaiting.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        /////////////////////////K.GAME 180830 Change Dialog Waiting
        ImageView waitingImage = dialogWaiting.findViewById(R.id.waitingImage);
        AnimationDrawable animationDrawable = (AnimationDrawable) waitingImage.getBackground();
        msgLabel = dialogWaiting.findViewById(R.id.msgLabel);
        animationDrawable.start();

        msgLabel.setText("กำลังทำรายการ");
        Utility.animation_Waiting_new(dialogWaiting);//K.GAME 180830 Change Dialog Waiting
        /////////////////////////END 180830 K.GAME Change Dialog Waiting
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
System.out.printf("utility:: sendMessage 000000000001 \n");
            setPostHeader();
            setPostBody();
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            setTimer(15000);
            connetPost();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setPostHeader() {

        reqBy = merid + deviceid;
        reqChannelRefId = deviceid + uniqueData;

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

        System.out.printf("utility:: reqBy = %s , reqChannel=%s , reqChannelDtm=%s , reqChannelRefId=%s service type=%s \n",reqBy,reqChannel,reqChannelDtm,reqChannelRefId,type);

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
                }else  if(reqdt == null || reqid == null){
                    status = "2";
                    resmsg = "ข้อมูลถูกยกเลิกแล้ว";
                    alipayListener.onDenclined();
                } else{
                    alipayListener.onServerError();
                }
            } catch (IOException e) {
               alipayListener.onServerError();
                e.printStackTrace();
            } catch (Exception e) {
                 alipayListener.onServerError();
                e.printStackTrace();
            }


        urlConnection.disconnect();
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

    private void submitsale() throws Exception {
        amt = intent.getExtras().getString("AMOUNT");
        tmp_amt = delcomma(amt);
        token = intent.getExtras().getString("TOKEN");
        dateTime = new Date();
        reqChannelDtm = dateFormat.format(dateTime); // + dateFormat_time.format(dateTime);
        uniqueData = makeUniqueData(dateTime);

//        data = aliConfig.getHttps() +"sale/submit";
        data = alipay_http +"sale/submit"; //20181114JEFF

        try {
            jsonObject.put("amt", tmp_amt);
            jsonObject.put("deviceid", deviceid);
            jsonObject.put("merid", merid);
            jsonObject.put("storeid", storeid);
            jsonObject.put("token", token);
            jsonObject.put("walletcode", walletcode);

            System.out.printf("utility:: %s , submitSALE = %s \n",TAG,jsonObject.toString());

            param1 = cryptoServices.encryptAES(jsonObject.toString(), cryptoServices.AES_KEY);
//            param2 = cryptoServices.encryptRSA(cryptoServices.AES_KEY, cryptoServices.PUBLIC_PATH);
            param2 = cryptoServices.encryptRSA(cryptoServices.AES_KEY, Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_PUBLIC_ID));

            System.out.printf("utility:: submitSALE = JSON data :: %s , param1 :: %s , param2 :: %s \n",jsonObject.toString(),param1,param2);
            System.out.printf("utility:: %s , submitSALE = %s \n",TAG,jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("JSON data :: ", jsonObject.toString());
        Log.d("param1 :: ", param1);
        Log.d("param2 :: ", param2);

        sendMessage();
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

    private void sale() throws Exception {
        type = AliConfig.Sale;
        tmp_amt = delcomma(amt);
        token = intent.getExtras().getString("TOKEN");

        invoice = CardPrefix.getInvoice(context, "ALIPAY");
        System.out.printf("utility:: sale , invoice = %s \n",invoice);

        if(dateTime == null){
            dateTime = new Date();
            reqChannelDtm = dateFormat.format(dateTime); // + dateFormat_time.format(dateTime);
            uniqueData = makeUniqueData(dateTime);
        }

//        data = aliConfig.getHttps() +"sale";
        data = alipay_http +"sale"; //20181114JEFF

        try {
            jsonObject.put("amt", tmp_amt);
            if(!fee.equals( "null" )) {
                jsonObject.put( "fee", fee );
                jsonObject.put( "amtplusfee", amtplusfee );
            }
            jsonObject.put("token", token);
            jsonObject.put("curr", "THB");
            jsonObject.put("deviceid",deviceid);
            jsonObject.put("merid", merid);
            jsonObject.put("storeid", storeid);
            jsonObject.put("walletcode", walletcode);   // Paul_20181022 Add to ALIPAY , WECHAT

            System.out.printf("utility:: %s , SALE = %s \n",TAG,jsonObject.toString());

            param1 = cryptoServices.encryptAES(jsonObject.toString(), cryptoServices.AES_KEY);
//            param2 = cryptoServices.encryptRSA(cryptoServices.AES_KEY, cryptoServices.PUBLIC_PATH);
            param2 = cryptoServices.encryptRSA(cryptoServices.AES_KEY, Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_PUBLIC_ID));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("JSON data :: ", jsonObject.toString());
        Log.d("param1 :: ", param1);
        Log.d("param2 :: ", param2);

        System.out.printf("utility:: SALE = JSON data :: %s , param1 :: %s , param2 :: %s \n",jsonObject.toString(),param1,param2);

        sendMessage();
    }

    private String checkLength(String trace, int i ) {
        int tmp_lc = 0;

        tmp_lc = trace.length();

        for(int j = 0; j<(i-tmp_lc); j++)
            trace = "0" + trace;

        return trace;
    }

    private void insertTransaction() {

        makeTransTemp();

        if(type.equals(AliConfig.Sale)){

            int inV = Integer.parseInt( Preference.getInstance(context).getValueString( Preference.KEY_INVOICE_NUMBER_ALL));

            if(respcode.equals("0")){
                if(walletcode.equals("ALIPAY"))
                    Preference.getInstance(context).setValueString( Preference.KEY_ALIPAY_LAST_TRACE, String.valueOf(inV));
                else
                    Preference.getInstance(context).setValueString( Preference.KEY_WECHAT_LAST_TRACE, String.valueOf(inV));

            }
            inV = inV + 1;
            Preference.getInstance(context).setValueString( Preference.KEY_INVOICE_NUMBER_ALL, String.valueOf(inV));
        }else{
            //void
            if(respcode.equals("0")){
                if(walletcode.equals("ALIPAY"))
                    Preference.getInstance(context).setValueString( Preference.KEY_ALIPAY_LAST_TRACE, invoice);
                else
                    Preference.getInstance(context).setValueString( Preference.KEY_WECHAT_LAST_TRACE, invoice);

            }
        }
    }

    private void insertTransaction_timeout() {

        makeTransTemp_timeout();

        int inV = Integer.parseInt( Preference.getInstance(context).getValueString( Preference.KEY_INVOICE_NUMBER_ALL));
        inV = inV + 1;
        Preference.getInstance(context).setValueString( Preference.KEY_INVOICE_NUMBER_ALL, String.valueOf(inV));
    }

    private void makeTransTemp() {
        QrCode aliTemp;     // Paul_20181009

        if(type.equals( AliConfig.SubmitSale) || type.equals( AliConfig.Sale) || type.equals( AliConfig.Inquiry)){
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }
            realm.beginTransaction();
            Number currentId = realm.where(QrCode.class).max("id");

            int nextId;

            if(type.equals( AliConfig.SubmitSale) || type.equals( AliConfig.Sale)){
                if (currentId == null) {
                    nextId = 1;
                } else {
                    nextId = currentId.intValue() + 1;
                }
                aliTemp = realm.createObject(QrCode.class, nextId);
            }else{
                //20181120Jeff
                nextId = currentId.intValue();
                invoice = checkLength(invoice, 6);
                aliTemp = realm.where(QrCode.class).equalTo("trace", invoice).findFirst();
            }


            invoice = checkLength(invoice, 6);

            if(type.equals( AliConfig.SubmitSale)){
                aliTemp.setHostTypeCard(walletcode);
                aliTemp.setRespcode(respcode);
                aliTemp.setReqBy(reqBy);
                aliTemp.setReqChannel(reqChannel);
                aliTemp.setReqChannelDtm(reqChannelDtm);
                aliTemp.setReqChannelRefId(reqChannelRefId);
                aliTemp.setService(type);
                aliTemp.setTrace(invoice);
                aliTemp.setAmt(amt);
                aliTemp.setFee(fee);
                aliTemp.setAmtplusfee(amtplusfee);
                aliTemp.setToken(token);
                aliTemp.setDeviceId(deviceid);
                aliTemp.setMerId(merid);
                aliTemp.setStoreId(storeid);
                aliTemp.setVoidFlag("N");
                System.out.printf("utility:: makeTransTemp 001 fee = %s , amtplusfee = %s \n",fee,amtplusfee);
            }else if(type.equals( AliConfig.Sale)){
                aliTemp.setHostTypeCard(walletcode);
                aliTemp.setRespcode(respcode);
                aliTemp.setReqChannelDtm(reqChannelDtm);
                aliTemp.setReqChannel(reqChannel);
                aliTemp.setService(type);
                aliTemp.setTrace(invoice);
                aliTemp.setDeviceId(deviceid);
                aliTemp.setMerId(merid);
                aliTemp.setStoreId(storeid);
                aliTemp.setToken(token);
                aliTemp.setReqid(reqid);
                aliTemp.setReqdt(reqdt);
                aliTemp.setAmt(amt);
                System.out.printf("utility:: makeTransTemp 002 fee = %s , amtplusfee = %s \n",fee,amtplusfee);
                aliTemp.setFee(fee);                    // Paul_20181004
                aliTemp.setAmtplusfee(amtplusfee);      // Paul_20181004
                aliTemp.setTransid(transid);
                aliTemp.setWalletcode(walletcode);
                aliTemp.setWallettransid(wallettransid);
                aliTemp.setTransdt(transdt);
                aliTemp.setBuyerid(buyerid);
                aliTemp.setForamt(foramt);
                aliTemp.setConvrate(convrate);
                aliTemp.setWalletcurr(walletcurr);
                aliTemp.setExchrateunit(exchrateunit);
                aliTemp.setVoidFlag("N");
            }else{//Inquiry
                System.out.printf("utility:: makeTransTemp 003 fee = %s , amtplusfee = %s \n",fee,amtplusfee);
                aliTemp.setRespcode(respcode);
                aliTemp.setReqChannelDtm(reqChannelDtm);
                aliTemp.setReqChannel(reqChannel);
                aliTemp.setService(type);
                aliTemp.setDeviceId(deviceid);
                aliTemp.setMerId(merid);
                aliTemp.setStoreId(storeid);
                aliTemp.setReqid(reqid);
                aliTemp.setReqdt(reqdt);
                aliTemp.setAmt(amt);
                aliTemp.setFee(fee);                    // Paul_20181004
                aliTemp.setAmtplusfee(amtplusfee);      // Paul_20181004
                aliTemp.setTransid(transid);
                aliTemp.setWallettransid(wallettransid);
                aliTemp.setTransdt(transdt);
                aliTemp.setBuyerid(buyerid);
                aliTemp.setForamt(foramt);
                aliTemp.setConvrate(convrate);
                aliTemp.setWalletcurr(walletcurr);
                aliTemp.setExchrateunit(exchrateunit);
                aliTemp.setVoidFlag("N");
            }
            System.out.printf("utility:: makeTransTemp AAAAAAAAAAAAAAAAAAABBBBBBBBBBBB  aliTemp.getInvoice(invoice) = %s , aliTemp.getDeviceId() = %s ,aliTemp.getMerid() = %s  \n",aliTemp.getTrace(), aliTemp.getDeviceId(),aliTemp.getMerid());
// Paul_20181007
            realm.commitTransaction();
            realm.close();
            realm = null;
            System.out.printf("utility:: makeTransTemp DATA DB Save Done 001 \n");

            if(respcode.equals("0")){
                if (insertOrUpdateDatabase != null) {
                    insertOrUpdateDatabase.onInsertSuccess(nextId);
                }
            }
            System.out.printf("utility:: makeTransTemp DATA DB Save Done 002 \n");

        }else if(type.equals( AliConfig.Void)){
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }
            realm.beginTransaction();
            aliTemp = realm.where(QrCode.class).equalTo("trace", invoice).findFirst();      // Paul_20181009
            if (aliTemp != null) {
                aliTemp.setRespcode(respcode);
                aliTemp.setReqdt(reqdt);
                aliTemp.setReqid(reqid);
                aliTemp.setTransid(transid);
                aliTemp.setWallettransid(wallettransid);
                aliTemp.setCanceldt(canceldt);
                aliTemp.setCii(cii);
                aliTemp.setVoidFlag("Y");

                amt = "-" + aliTemp.getAmt();
                System.out.printf("utility:: makeTransTemp void amt = %s \n",amt);
                if(!aliTemp.getAmtplusfee().equals( "null" ))       // Paul_20181009
                {
                    amtplusfee = "-" + aliTemp.getAmtplusfee();
                    System.out.printf("utility:: makeTransTemp void amtplusfee = %s \n",amtplusfee);
                }
            }

            System.out.printf("utility:: makeTransTemp AAAAAAAAAAAAAAAAAAABBBBBBBBBBBB  aliTemp.getInvoice(invoice) = %s , aliTemp.getDeviceId() = %s ,aliTemp.getMerid() = %s  \n",aliTemp.getTrace(), aliTemp.getDeviceId(),aliTemp.getMerid());

            if (insertOrUpdateDatabase != null) {
                insertOrUpdateDatabase.onInsertSuccess(aliTemp.getId());
            }

// Paul_20181007
            realm.commitTransaction();
            realm.close();
            realm = null;
        }
    }

    private void makeTransTemp_timeout() {
        QrCode aliTemp;

        if(type.equals( AliConfig.SubmitSale) || type.equals( AliConfig.Sale) || type.equals( AliConfig.Inquiry)){
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }
            realm.beginTransaction();
            Number currentId = realm.where(QrCode.class).max("id");

            int nextId;

            if(type.equals( AliConfig.SubmitSale) || type.equals( AliConfig.Sale)){
                if (currentId == null) {
                    nextId = 1;
                } else {
                    nextId = currentId.intValue() + 1;
                }
                aliTemp = realm.createObject(QrCode.class, nextId);
            }else{
                nextId = currentId.intValue();
                aliTemp = realm.where(QrCode.class).equalTo( "hostTypeCard", walletcode).equalTo("id", nextId).findFirst();
            }

            if(aliTemp != null) {
                invoice = checkLength(invoice, 6);

                if(type.equals( AliConfig.SubmitSale)){
                    aliTemp.setHostTypeCard(walletcode);
                    aliTemp.setRespcode("1");                                  // Respcode 1, voidflag 1     --->    Timeout
                    aliTemp.setReqBy(reqBy);
                    aliTemp.setReqChannel(reqChannel);
                    aliTemp.setReqChannelDtm(reqChannelDtm);
                    aliTemp.setReqChannelRefId(reqChannelRefId);
                    aliTemp.setService(type);
                    aliTemp.setTrace(invoice);
                    aliTemp.setAmt(amt);
                    aliTemp.setFee(fee);
                    aliTemp.setAmtplusfee(amtplusfee);
                    aliTemp.setToken(token);
                    aliTemp.setDeviceId(deviceid);
                    aliTemp.setMerId(merid);
                    aliTemp.setStoreId(storeid);
                    aliTemp.setVoidFlag("Y");
                    System.out.printf("utility:: makeTransTemp 001 fee = %s , amtplusfee = %s \n",fee,amtplusfee);
                }else if(type.equals( AliConfig.Sale)){
                    aliTemp.setHostTypeCard(walletcode);
                    aliTemp.setRespcode("1");                                  // Respcode 1, voidflag 1     --->    Timeout
                    aliTemp.setReqChannelDtm(reqChannelDtm);
                    aliTemp.setReqChannel(reqChannel);
                    aliTemp.setService(type);
                    aliTemp.setTrace(invoice);
                    aliTemp.setDeviceId(deviceid);
                    aliTemp.setMerId(merid);
                    aliTemp.setStoreId(storeid);
                    aliTemp.setToken(token);
                    aliTemp.setRespcode(respcode);
                    aliTemp.setReqid(reqid);
                    aliTemp.setReqdt(reqdt);
                    aliTemp.setAmt(amt);
                    aliTemp.setFee(fee);
                    aliTemp.setAmtplusfee(amtplusfee);
                    aliTemp.setTransid(transid);
                    aliTemp.setWalletcode(walletcode);
                    aliTemp.setWallettransid(wallettransid);
                    aliTemp.setTransdt(transdt);
                    aliTemp.setBuyerid(buyerid);
                    aliTemp.setForamt(foramt);
                    aliTemp.setConvrate(convrate);
                    aliTemp.setWalletcurr(walletcurr);
                    aliTemp.setExchrateunit(exchrateunit);
                    aliTemp.setVoidFlag("Y");
                }else{ //Inquiry
                    aliTemp.setRespcode("1");                                  // Respcode 1, voidflag 1     --->    Timeout
                    aliTemp.setReqChannelDtm(reqChannelDtm);
                    aliTemp.setReqChannel(reqChannel);
                    aliTemp.setService(type);
                    aliTemp.setDeviceId(deviceid);
                    aliTemp.setMerId(merid);
                    aliTemp.setStoreId(storeid);
                    aliTemp.setReqid(reqid);
                    aliTemp.setReqdt(reqdt);
                    aliTemp.setAmt(amt);
                    aliTemp.setFee(fee);
                    aliTemp.setAmtplusfee(amtplusfee);
                    aliTemp.setTransid(transid);
                    aliTemp.setWalletcode(walletcode);
                    aliTemp.setWallettransid(wallettransid);
                    aliTemp.setTransdt(transdt);
                    aliTemp.setBuyerid(buyerid);
                    aliTemp.setForamt(foramt);
                    aliTemp.setConvrate(convrate);
                    aliTemp.setWalletcurr(walletcurr);
                    aliTemp.setExchrateunit(exchrateunit);
                    aliTemp.setVoidFlag("Y");
                }
                realm.commitTransaction();
                realm.close();
                realm = null;
            }

        }else if(type.equals( AliConfig.Void)){
            if (realm == null) {
                realm = Realm.getDefaultInstance();
            }
            realm.beginTransaction();
            aliTemp = realm.where(QrCode.class).equalTo( "hostTypeCard", walletcode ).equalTo("trace", invoice).findFirst();      // Paul_20181009
            if (aliTemp != null) {
                aliTemp.setRespcode("1");                                  // Respcode 1, voidflag 1     --->    Timeout
                aliTemp.setReqdt(reqdt);
                aliTemp.setReqid(reqid);
                aliTemp.setTransid(transid);
                aliTemp.setWallettransid(wallettransid);
                aliTemp.setCanceldt(canceldt);
                aliTemp.setCii(cii);
                aliTemp.setVoidFlag("Y");
            }

            realm.commitTransaction();
            realm.close();
            realm = null;
        }
    }

    private void getResponseData(InputStream in) {
        final String data = readData(in);


        String tmp;
        JSONObject obj;
        try {
            tmp = cryptoServices.decryptAES(data, cryptoServices.AES_KEY);

            System.out.printf("utility:: getResponseData = %s \n",tmp);

            obj = new JSONObject(tmp);
            status = obj.getString("status");
            result = obj.getString("result");
            resmsg = obj.getString("statusDesc");        // Paul_20181003

            System.out.printf("utility:: getResponseData status = %s result = %s \n",status,result);
            if(!result.equals("null")){
                alipayListener.onSuccessful();
            }else{
//                AliConfig.Fail = resmsg;        // Paul_20181003
               alipayListener.onDenclined();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readData(InputStream is){
        String data = "";
        Scanner s = new Scanner(is);
        while(s.hasNext()) data += s.nextLine() + "\n";
        s.close();
        return data;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setTimer(final long time) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (timer != null) {
                    timer.cancel();
                }

                timer = new CountDownTimer(time, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        if(!status.isEmpty())
                            timer.cancel();
                        Log.d(TAG, "onTick: " + millisUntilFinished + "status : " + status + "  ::");
                    }

                    @Override
                    public void onFinish() {
                        timer.cancel();
                        dialogWaiting.dismiss();
                        if(status.isEmpty())
                            alipayListener.onTimeout();
                    }
                };
                timer.start();
            }
        });
    }


    public void DbListener() {
        setInsertOrUpdateDatabase(new AliDbUpdateDatabase() {
            @Override
            public void onUpdateVoidSuccess(int id) {
                Log.d(TAG, "onUpdateVoidSuccess: " + id);
            }

            @Override
            public void onInsertSuccess(int id) {
                Log.d(TAG, "onInsertSuccess: " + id);
                if (dialogWaiting != null) {
                    dialogWaiting.dismiss();
                }

                Intent slip = new Intent(AliServiceActivity.this, AliSlipActivity.class);
                slip.putExtra("STATUS", status);
                slip.putExtra("INVOICE", invoice);
                slip.putExtra("TYPE", type);
                DecimalFormat decimalFormatShow = new DecimalFormat("#,##0.00");    // Paul_20181006
                DecimalFormat decimalFormat = new DecimalFormat("###0.00");    // Paul_20181006
System.out.printf("utility:: DbListener onInsertSuccess 000000001 \n");
                // Paul_20181009
                double doubleplusfee=0.00;
                if(!amtplusfee.equals( "null" )) {
                    doubleplusfee = Double.parseDouble( decimalFormat.format( Double.valueOf( amtplusfee ) ) );    // Paul_20181006
                }
                else
                {
                    doubleplusfee = Double.parseDouble( decimalFormat.format( Double.valueOf( amt ) ) );    // Paul_20181006
                }
                System.out.printf("utility:: DbListener onInsertSuccess 000000002 \n");
                String amt1 = decimalFormatShow.format((float) (doubleplusfee));    // Paul_20181006
                slip.putExtra("AMOUNT", amt1);
                if(type.equals( AliConfig.Sale) ||  type.equals( AliConfig.Void)){
                    slip.putExtra("RECEIPT", receipttext);
                }
                System.out.printf("utility:: DbListener onInsertSuccess 000000003 \n");
                startActivity(slip);
                finish();
            }
        });
    }

    private void transactionListener() {
        setAlipayListener(new AlipayListener() {

            @Override
            public void onSuccessful() {
                JSONObject obj2;

                try {
                    obj2 = new JSONObject(result);

                    if(type.equals( AliConfig.SubmitSale)){
                        System.out.printf("utility:: transactionListener AA0003\n");
                        amt = obj2.getString("amt");        // Paul_20181004
                        fee = obj2.getString("fee");        // Paul_20181004
                        amtplusfee = obj2.getString("amtplusfee");  // Paul_20181004
                        System.out.printf("utility:: transactionListener 0004 amt = %s , fee = %s , amtplusfee = %s \n",amt,fee,amtplusfee);
                        sale();
                    }else if(type.equals( AliConfig.Sale) || type.equals( AliConfig.Inquiry)){
                        respcode = obj2.getString("respcode");
                        if(respcode.equals("0")){
                            System.out.printf("utility:: transactionListener AA0005\n");
                            System.out.printf("utility:: transactionListener 0005 amt = %s , fee = %s , amtplusfee = %s \n",amt,fee,amtplusfee);
                            reqid = obj2.getString("reqid");
                            reqdt = obj2.getString("reqdt");
                            buyerid = obj2.getString("buyerid");
                            transid = obj2.getString("transid");
                            walletcode = obj2.getString("walletcode");
                            wallettransid = obj2.getString("wallettransid");
                            transdt = obj2.getString("transdt");
                            foramt = obj2.getString("foramt");
                            convrate = obj2.getString("convrate");
                            walletcurr = obj2.getString("walletcurr");
                            exchrateunit = obj2.getString("exchrateunit");

                            if(type.equals( AliConfig.Sale))
                                receipttext = obj2.getString("receipttext");

                        }else if(respcode.equals("1")){

                            reqid = obj2.getString("reqid");
                            reqdt = obj2.getString("reqdt");
                            buyerid = obj2.getString("buyerid");
                            walletcode = obj2.getString("walletcode");
                            dialogWaiting.dismiss();
                            alipayListener.onInquiry();

                        }else{
                            dialogWaiting.dismiss();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    Utility.customDialogAlert(AliServiceActivity.this, resmsg, new Utility.OnClickCloseImage() {
                                    ////SINN 20181127 Add dialog error goto main menu.
                                    Utility.customDialogAlert_gotomain(AliServiceActivity.this, resmsg, new Utility.OnClickCloseImage() {
                                        @Override
                                        public void onClickImage(Dialog dialog) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    });
                                }
                            });
                        }
                        insertTransaction();
                    }else{
                        System.out.printf("utility:: transactionListener AA0006\n");
                        System.out.printf("utility:: transactionListener 0006 amt = %s , fee = %s , amtplusfee = %s \n",amt,fee,amtplusfee);
                        respcode = obj2.getString("respcode");
                        reqid = obj2.getString("origreqid");
                        reqdt = obj2.getString("origreqdt");
                        transid = obj2.getString("transid");
                        wallettransid = obj2.getString("wallettransid");
                        canceldt = obj2.getString("canceldt");
                        cii = obj2.getString("cii");
                        receipttext = obj2.getString("receipttext");

                        if(respcode.equals("0"))
                            insertTransaction();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDenclined() {
                dialogWaiting.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ////SINN 20181127 Add dialog error goto main menu.
//                        Utility.customDialogAlert(AliServiceActivity.this, resmsg, new Utility.OnClickCloseImage() {
                        Utility.customDialogAlert_gotomain(AliServiceActivity.this, resmsg, new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                    }
                });
            }

            @Override
            public void onTimeout() {
                insertTransaction_timeout();
                dialogWaiting.dismiss();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Utility.customDialogAlert(AliServiceActivity.this, "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้", new Utility.OnClickCloseImage() {
                        Utility.customDialogAlert_gotomain(AliServiceActivity.this, "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                    }
                });
            }

            @Override
            public void onInquiry() {

                status = AliConfig.Hold;
                Intent slip = new Intent(AliServiceActivity.this, AliSlipActivity.class);
                slip.putExtra("STATUS", status);
                slip.putExtra("INVOICE", invoice);
                startActivity(slip);
                finish();
            }

            @Override
            public void onServerError() {
                dialogWaiting.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utility.customDialogAlert_gotomain(AliServiceActivity.this, "ไม่สามารถเชื่อมต่อเซิฟเวอร์ได้", new Utility.OnClickCloseImage() {
                            @Override
                            public void onClickImage(Dialog dialog) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }

    private String delcomma(String amount) {
        String result_amount;
        result_amount = amount.toString().replaceAll(",", "");
        return result_amount;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_backmain:
                finish();
                overridePendingTransition(0, 0);
                break;
        }
    }


    //Listener
    public interface AlipayListener {
        public void onSuccessful();

        public void onDenclined();

        public void onTimeout();

        public void onInquiry();

        public void onServerError();
    }

    public interface AliDbUpdateDatabase {
        public void onUpdateVoidSuccess(int id);

        public void onInsertSuccess(int id);
    }

    public void setAlipayListener(AlipayListener alipayListener) {
        this.alipayListener = alipayListener;
    }

    public void setInsertOrUpdateDatabase(AliDbUpdateDatabase insertOrUpdateDatabase) {
        this.insertOrUpdateDatabase = insertOrUpdateDatabase;
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}
