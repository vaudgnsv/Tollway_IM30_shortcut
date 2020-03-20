package org.centerm.Tollway;

import android.app.Application;
import android.util.Log;

import com.pax.dal.IDAL;
import com.pax.dal.entity.ECheckMode;
import com.pax.dal.entity.EPedDesMode;
import com.pax.dal.entity.EPedKeyType;
import com.pax.dal.entity.EPedType;
import com.pax.neptunelite.api.NeptuneLiteUser;

import org.centerm.Tollway.activity.posinterface.PosInterfaceActivity;
import org.centerm.Tollway.core.ChangeFormat;
import org.centerm.Tollway.manager.Contextor;
import org.centerm.Tollway.pax.Constants;
import org.centerm.Tollway.utility.FileParse;
import org.centerm.Tollway.utility.Preference;

import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainApplication extends Application {
    private static CardManager cardManager;
    private final String TAG = "MainApplication";
    private static IDAL dal;
    private static IConvert convert;

    private static PosInterfaceActivity posInterfaceActivity = null;

    public static IDAL getDal() {
        return dal;
    }

    public static IConvert getConvert() {
        return convert;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        Contextor.getInstance().init(this);
        convert = new ConverterImp();


        NeptuneLiteUser neptuneLiteUser = NeptuneLiteUser.getInstance();

        try {
            if (dal == null) {
                dal = neptuneLiteUser.getDal(this.getApplicationContext());
                Log.i("FinancialApplication:", "dalProxyClient finished.");
            }
        } catch (Exception e) {
            //e.printStackTrace();
            Log.e("dalProxyClient", e.getMessage());
        }


        FileParse.parseAidFromAssets(this, "aid.ini");

        FileParse.parseCapkFromAssets(this, "capk.ini");

        cardManager = CardManager.init(getApplicationContext());
        cardManager.bindService();
        System.out.println("vince20180910");
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfiguration); // Make this Realm the default
        setPreference();
        writekey();

    }

    private void writekey() {
        String EPS_pinblock;
        String OffUs_pinblock;
        String OnUs_pinblock;
        String EPS_applicationkey;
        String OffUs_applicationkey;
        String OnUs_applicationkey;
        int len = 0;
        try {
            File file = new File("/cache/customer/media/pinblock.txt");
            FileInputStream fileInputStream = new FileInputStream(file);

            byte[] readBuffer = new byte[fileInputStream.available()];
            fileInputStream.read(readBuffer);

            String workingkey = new String(readBuffer);

            EPS_pinblock = workingkey.substring(len, len + (17 * 2));
            len += (17 * 2);
            OffUs_pinblock = workingkey.substring(len, len + (17 * 2));
            len += (17 * 2);
            OnUs_pinblock = workingkey.substring(len, len + (17 * 2));
            len += (17 * 2);
            EPS_applicationkey = workingkey.substring(len, len + (17 * 2));
            len += (17 * 2);
            OffUs_applicationkey = workingkey.substring(len, len + (17 * 2));
            len += (17 * 2);
            OnUs_applicationkey = workingkey.substring(len, len + (17 * 2));

            MainApplication.getDal().getPed(EPedType.INTERNAL).erase();


            MainApplication.getDal().getPed(EPedType.INTERNAL).writeKey(EPedKeyType.TDK, (byte)0, EPedKeyType.TDK, Constants.INDEX_TDK,
                    ChangeFormat.hexStringToByte(EPS_applicationkey.substring(0,EPS_applicationkey.length() - 2)), ECheckMode.KCV_NONE, null);
            byte[] decryptedkey = MainApplication.getDal().getPed(EPedType.INTERNAL).calcDes(Constants.INDEX_TDK, ChangeFormat.hexStringToByte(EPS_pinblock.substring(0,EPS_pinblock.length() - 2)), EPedDesMode.DECRYPT);

            //MainApplication.getDal().getPed(EPedType.INTERNAL).writeKey(EPedKeyType.TLK,(byte) 0, EPedKeyType.TMK, Constants.INDEX_TMK, ChangeFormat.hexStringToByte("0000000000000000"), ECheckMode.KCV_NONE, null);
            MainApplication.getDal().getPed(EPedType.INTERNAL).writeKey(EPedKeyType.TPK,(byte)0, EPedKeyType.TPK, Constants.INDEX_TPK,
                    decryptedkey, ECheckMode.KCV_NONE, null);

        } catch ( Exception e) {
            e.printStackTrace();
        }
    }


    static {
        System.loadLibrary("F_DEVICE_LIB_PayDroid");
        System.loadLibrary("F_PUBLIC_LIB_PayDroid");
        System.loadLibrary("F_EMV_LIB_PayDroid");
        System.loadLibrary("F_ENTRY_LIB_PayDroid");
        System.loadLibrary("F_MC_LIB_PayDroid");
        System.loadLibrary("F_WAVE_LIB_PayDroid");
        System.loadLibrary("F_AE_LIB_PayDroid");
        System.loadLibrary("F_QPBOC_LIB_PayDroid");
        System.loadLibrary("F_DPAS_LIB_PayDroid");
        System.loadLibrary("F_JCB_LIB_PayDroid");
        System.loadLibrary("F_PURE_LIB_PayDroid");
        System.loadLibrary("JNI_EMV_v101");
        System.loadLibrary("JNI_ENTRY_v103");
        System.loadLibrary("JNI_MC_v100");
        System.loadLibrary("JNI_WAVE_v100");
        System.loadLibrary("JNI_AE_v101");
        System.loadLibrary("JNI_QPBOC_v100");
        System.loadLibrary("JNI_DPAS_v100");
        System.loadLibrary("JNI_JCB_v100");
        System.loadLibrary("JNI_PURE_v100");
    }

    public static PosInterfaceActivity getPosInterfaceActivity() {
        return posInterfaceActivity;
    }

    private void setPreference() {
        String adminPassword = Preference.getInstance(this).getValueString(Preference.KEY_ADMIN_PASS_WORD);
        if (adminPassword.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_ADMIN_PASS_WORD, "179191");
        }
        String normalPassword = Preference.getInstance(this).getValueString(Preference.KEY_ADMIN_PIN);
        if (normalPassword.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_ADMIN_PIN, "11111111");
        }
        String posId = Preference.getInstance(this).getValueString(Preference.KEY_POS_ID);
        if (posId.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_POS_ID, "2222");
        }
        if (Preference.getInstance(this).getValueString(Preference.KEY_PIN).isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_PIN, "1111");
        }
        ///SINN 20180713 Set offline pwd
        if (Preference.getInstance(this).getValueString(Preference.KEY_OFFLINE_PASS_WORD).isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_OFFLINE_PASS_WORD, "2561");
        }
//end SINN 20180713 Set offline pwd
        if (Preference.getInstance(this).getValueString(Preference.KEY_MAX_AMT).isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_MAX_AMT, "0");
        }
        //SINN 20180803 APP ENABLE
        if (Preference.getInstance(this).getValueString(Preference.KEY_APP_ENABLE).isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_APP_ENABLE, "111");
        }
        if (Preference.getInstance(this).getValueString(Preference.KEY_APP_GHC_ENABLE).isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_APP_GHC_ENABLE, "111");
        }
        if (Preference.getInstance(this).getValueString(Preference.KEY_SETTING_FOR_USER_PASS_WORD).isEmpty()) {//K.GAME 180925 New Password for setting user
            Preference.getInstance(this).setValueString(Preference.KEY_SETTING_FOR_USER_PASS_WORD, "111111");
        }
//        Preference.getInstance(this).setValueString( Preference.KEY_APP_ENABLE, "111");

        //20181024 SINN MASK expire card
        if (Preference.getInstance(this).getValueString(Preference.KEY_CARDMASK_ID).isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_CARDMASK_ID, "NNNN NNXX XXXX NNNN");
        }

        if (Preference.getInstance(this).getValueString(Preference.KEY_EXPIREMASK_ID).isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_EXPIREMASK_ID, "XXXX");
        }

        if (Preference.getInstance(this).getValueString(Preference.KEY_QR_LAST_TRACE).isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_QR_LAST_TRACE, "0");
        }

        //SINN 20181024 set default check card expire
        if (Preference.getInstance(this).getValueString(Preference.KEY_CARDEXP_ID).isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_CARDEXP_ID, "1");
        }

        //JEFF 20181114
        if (Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_URL_ID).isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_ALIPAY_URL_ID, "https://www.inwc.ktb.co.th/smart-payment/third-party-payment/");
        }

        //JEFF 20181114
        if (Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_PUBLIC_ID).isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_ALIPAY_PUBLIC_ID, "/data/thaivan/edc-smart-payment-public-key-uat.der");
        }

        //JEFF 20181115
        if (Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_CERTI_ID).isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_ALIPAY_CERTI_ID, "/data/thaivan/ktb_alipay_uat.cer");
        }

        //JEFF 20181103
        if (Preference.getInstance(this).getValueString(Preference.KEY_ALIPAY_ID).isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_ALIPAY_ID, "0");
        }
        //JEFF 20181103
        if (Preference.getInstance(this).getValueString(Preference.KEY_WECHATPAY_ID).isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_WECHATPAY_ID, "0");
        }

        //SINN 20181129 Railway project QR ref1
        if(Preference.getInstance(this).getValueString(Preference.KEY_RAILWAY_ID).isEmpty())
            Preference.getInstance(this).setValueString(Preference.KEY_RAILWAY_ID, "0");

        if (Preference.getInstance(this).getValueString(Preference.KEY_RAILWAY_REF1_ID).isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_RAILWAY_REF1_ID, "000000");
        }
        // Paul_20181103 AXA Merge
        if(Preference.getInstance(this).getValueString(Preference.KEY_AXA_ID).isEmpty())
            Preference.getInstance(this).setValueString(Preference.KEY_AXA_ID, "0");

        // //20181218  SINN Print slip enable/disable
        if(Preference.getInstance(this).getValueString(Preference.KEY_PrintSlip_ID).isEmpty())
            Preference.getInstance(this).setValueString(Preference.KEY_PrintSlip_ID, "11");

        //20181218  SINN Void syn date/time
        if(Preference.getInstance(this).getValueString(Preference.KEY_SlipSyncTime_ID).isEmpty())
            Preference.getInstance(this).setValueString(Preference.KEY_SlipSyncTime_ID, "0");



        setTexABB();
// Paul_20181103 WAY 4 + AXA
//        if (!Preference.getInstance(this).getValueString(Preference.KEY_AXA_ID).equalsIgnoreCase("1"))
//        {
//            setFee();
//
        setQr();

        setAlipay(); //20181114Jeff
//        }

//        if (Preference.getInstance(this).getValueString(Preference.KEY_AXA_ID).equalsIgnoreCase("1"))
//        {
//            System.out.printf("utility:: %s KEY_AXA_ID = 111111111111111111111111111111111 \n",TAG);
//        }
        setTransactionCode();

        // setIP();

        setTrace();

        setBatch();

        setTerminal();

        setMerchant();

        setTerminalVersion();

        setMessageVersion();

        setNII();

        setTpdu();

        setInvoice();

        setMessageGHCVersion();


    }

    private void setTexABB() {
        String texAbbPos = Preference.getInstance(this).getValueString(Preference.KEY_TAX_INVOICE_NO_POS);
        String texAbbEps = Preference.getInstance(this).getValueString(Preference.KEY_TAX_INVOICE_NO_EPS);
        String texId = Preference.getInstance(this).getValueString(Preference.KEY_TAX_ID);

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String dateAll = dateFormat.format(date);

        if (texAbbPos.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_TAX_INVOICE_NO_POS, dateAll + "0000");
        }

        if (texAbbEps.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_TAX_INVOICE_NO_EPS, dateAll + "0000");
        }
//
//        if (texId.isEmpty()) {
//            Preference.getInstance(this).setValueString(Preference.KEY_TAX_ID, "3101083187");
//        }
    }
    //
    private void setFee() {
        Double fee = Preference.getInstance(this).getValueDouble(Preference.KEY_FEE);
        if (fee <= 0) {
            Preference.getInstance(this).setValueDouble(Preference.KEY_FEE, 0.00d);
        }

//20180815 SINN implement RATEMC,RATEVI,RATEMCLOCAL,RATEVILOCAL,UPI,TPN,JCB
        fee = Preference.getInstance(this).getValueDouble(Preference.KEY_rateMC_ID);
        if (fee <= 0) {
            Preference.getInstance(this).setValueDouble(Preference.KEY_rateMC_ID, 0.00d);
        }
        fee = Preference.getInstance(this).getValueDouble(Preference.KEY_rateVI_ID);
        if (fee <= 0) {
            Preference.getInstance(this).setValueDouble(Preference.KEY_rateVI_ID, 0.00d);
        }
        fee = Preference.getInstance(this).getValueDouble(Preference.KEY_rateVILocal_ID);
        if (fee <= 0) {
            Preference.getInstance(this).setValueDouble(Preference.KEY_rateVILocal_ID, 0.00d);
        }
        fee = Preference.getInstance(this).getValueDouble(Preference.KEY_rateMCLocal_ID);
        if (fee <= 0) {
            Preference.getInstance(this).setValueDouble(Preference.KEY_rateMCLocal_ID, 0.00d);
        }
        fee = Preference.getInstance(this).getValueDouble(Preference.KEY_rateJCB_ID);
        if (fee <= 0) {
            Preference.getInstance(this).setValueDouble(Preference.KEY_rateJCB_ID, 0.00d);
        }
        fee = Preference.getInstance(this).getValueDouble(Preference.KEY_rateUPI_ID);
        if (fee <= 0) {
            Preference.getInstance(this).setValueDouble(Preference.KEY_rateUPI_ID, 0.00d);
        }
        fee = Preference.getInstance(this).getValueDouble(Preference.KEY_rateTPN_ID);
        if (fee <= 0) {
            Preference.getInstance(this).setValueDouble(Preference.KEY_rateTPN_ID, 0.00d);
        }
//END 20180815 SINN implement RATEMC,RATEVI,RATEMCLOCAL,RATEVILOCAL,UPI,TPN,JCB

    }

    private void setQr() {
        String qrAid = Preference.getInstance(this).getValueString(Preference.KEY_QR_AID);
//        String traceId = Preference.getInstance(this).getValueString(Preference.KEY_QR_TRACE_NO);
        String traceId = Preference.getInstance(this).getValueString(Preference.KEY_INVOICE_NUMBER_ALL);  ////SINN 20180713 QR share invoice number
        String billerKey = Preference.getInstance(this).getValueString(Preference.KEY_BILLER_KEY);
        String qrPort = Preference.getInstance(this).getValueString(Preference.KEY_QR_PORT);
        String billerId = Preference.getInstance(this).getValueString(Preference.KEY_QR_BILLER_ID);
        String merchantName = Preference.getInstance(this).getValueString(Preference.KEY_QR_MERCHANT_NAME);
        String terminalId = Preference.getInstance(this).getValueString(Preference.KEY_QR_TERMINAL_ID);
        String merchantId = Preference.getInstance(this).getValueString(Preference.KEY_QR_MERCHANT_ID);  ////20180814 SINN  use QR Merchant ID instead biller id.

        String batchId = Preference.getInstance(this).getValueString(Preference.KEY_QR_BATCH_NUMBER);
        if (batchId.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_QR_BATCH_NUMBER, "1");
        }
//        if (qrAid.isEmpty()) {
//            Preference.getInstance(this).setValueString(Preference.KEY_QR_AID, "A000000677010112");
//        }

//        if (traceId.isEmpty()) {   //SINN 20180713 QR share invoice number use share inv.
// Preference.getInstance(this).setValueString(Preference.KEY_QR_TRACE_NO, "1");
//        }
//
//        if (billerKey.isEmpty()) {
//            Preference.getInstance(this).setValueString(Preference.KEY_BILLER_KEY, "gov");
//        }
//        if (qrPort.isEmpty()) {
//            Preference.getInstance(this).setValueString(Preference.KEY_QR_PORT, "3840");
//        }
//        if (billerId.isEmpty()) {
//            Preference.getInstance(this).setValueString(Preference.KEY_QR_BILLER_ID, "010352102131870");
//        }
//        if (merchantName.isEmpty()) {
//            Preference.getInstance(this).setValueString(Preference.KEY_QR_MERCHANT_NAME, "NAKHONRATCHASIMA PCG.");
//        }
//        if (terminalId.isEmpty()) {
//            Preference.getInstance(this).setValueString(Preference.KEY_QR_TERMINAL_ID, "00025120");
//        }
//////20180814 SINN  use QR Merchant ID instead biller id.
//        if (merchantId.isEmpty()) {
//            Preference.getInstance(this).setValueString(Preference.KEY_QR_MERCHANT_ID, "000000000001688");
//        }
    }
    //
    private void setAlipay() //20181114Jeff
    {
        String batchId = Preference.getInstance(this).getValueString(Preference.KEY_ALI_BATCH_NUMBER);
        String batchId2 = Preference.getInstance(this).getValueString(Preference.KEY_WEC_BATCH_NUMBER);

        if (batchId.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_ALI_BATCH_NUMBER, "1");
        }

        if (batchId2.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_WEC_BATCH_NUMBER, "1");
        }
    }
//
//    private void setIP() {
//        String primaryIP = Preference.getInstance(this).getValueString(Preference.KEY_PRIMARY_IP);
//        String primaryPORT = Preference.getInstance(this).getValueString(Preference.KEY_PRIMARY_PORT);
//        String secondaryIP = Preference.getInstance(this).getValueString(Preference.KEY_SECONDARY_IP);
//        String secondaryPORT = Preference.getInstance(this).getValueString(Preference.KEY_SECONDARY_PORT);
//
//        if (primaryIP.isEmpty()) {
//            Preference.getInstance(this).setValueString(Preference.KEY_PRIMARY_IP, "172.22.0.251");
//        }
//        if (primaryPORT.isEmpty()) {
//            Preference.getInstance(this).setValueString(Preference.KEY_PRIMARY_PORT, "3828");
//        }
//        if (secondaryIP.isEmpty()) {
//            Preference.getInstance(this).setValueString(Preference.KEY_SECONDARY_IP, "172.22.0.251");
//        }
//        if (secondaryPORT.isEmpty()) {
//            Preference.getInstance(this).setValueString(Preference.KEY_SECONDARY_PORT, "3828");
//        }
//    }

    private void setTerminal() {
        String terIdPOS = Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_POS);
        String terIdTMS = Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_TMS);
        String terIdEPS = Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_EPS);
        String terIdGHC = Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_GHC);  // Paul_20180620
// Paul_20181103 WAY 4 + AXA
//        if (!Preference.getInstance(this).getValueString(Preference.KEY_AXA_ID).equalsIgnoreCase("1")) {
//            if (terIdPOS.isEmpty()) {
//                Preference.getInstance( this ).setValueString( Preference.KEY_TERMINAL_ID_POS, "61900010" );
//            }
//            if (terIdTMS.isEmpty()) {
//                Preference.getInstance( this ).setValueString( Preference.KEY_TERMINAL_ID_TMS, "00006120" );
//            }
//            if (terIdEPS.isEmpty()) {
//                Preference.getInstance( this ).setValueString( Preference.KEY_TERMINAL_ID_EPS, "31900010" );
//            }
//            if (terIdGHC.isEmpty()) {       // Paul_20180620
//                Preference.getInstance( this ).setValueString( Preference.KEY_TERMINAL_ID_GHC, "40110003" );
//            }
//        }

    }

    private void setTrace() {
        String tracePOS = Preference.getInstance(this).getValueString(Preference.KEY_TRACE_NO_POS);
        String traceTMS = Preference.getInstance(this).getValueString(Preference.KEY_TRACE_NO_TMS);
        String traceEPS = Preference.getInstance(this).getValueString(Preference.KEY_TRACE_NO_EPS);
        String traceGHC = Preference.getInstance(this).getValueString(Preference.KEY_TRACE_NO_GHC);

        if (tracePOS.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_TRACE_NO_POS, "1");
        }
        if (traceTMS.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_TRACE_NO_TMS, "1");
        }
        if (traceEPS.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_TRACE_NO_EPS, "1");
        }
        if (traceGHC.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_TRACE_NO_GHC, "1");
        }
    }

    private void setBatch() {
        String batchPOS = Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS);
        String batchTMS = Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS);
        String batchEPS = Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS);
        String batchGHC = Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_GHC);

        if (batchPOS.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_BATCH_NUMBER_POS, "1");
        }
        if (batchTMS.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_BATCH_NUMBER_TMS, "1");
        }
        if (batchEPS.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_BATCH_NUMBER_EPS, "1");
        }
        if (batchGHC.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_BATCH_NUMBER_GHC, "1");
        }
    }

    private void setMerchant() {
        String merchantIdPOS = Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_POS);
        String merchantIdTMS = Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_POS);
        String merchantIdEPS = Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_POS);
        String merchantIdGHC = Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_GHC); // Paul_20180620
//
//        if (merchantIdPOS.isEmpty()) {
//            Preference.getInstance(this).setValueString(Preference.KEY_MERCHANT_ID_POS, "000001000900003");
//        }
//        if (merchantIdTMS.isEmpty()) {
//            Preference.getInstance(this).setValueString(Preference.KEY_MERCHANT_ID_TMS, "000000000001688");
//        }
//        if (merchantIdEPS.isEmpty()) {
//            Preference.getInstance(this).setValueString(Preference.KEY_MERCHANT_ID_EPS, "000001000900003");
//        }
//        if (merchantIdGHC.isEmpty()) {  // Paul_20180620
//            Preference.getInstance(this).setValueString(Preference.KEY_MERCHANT_ID_GHC, "000010040110000");
//        }
    }

    private void setTransactionCode() {
        String terminalCode = Preference.getInstance(this).getValueString(Preference.KEY_TRANSACTION_CODE);

        if (terminalCode.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_TRANSACTION_CODE, "6010");
        }
    }

    private void setTerminalVersion() {
        String terminalVersion = Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_VERSION);

        if (terminalVersion.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_TERMINAL_VERSION, "00000001");
        }
    }

    private void setMessageVersion() {
        String messageVersion = Preference.getInstance(this).getValueString(Preference.KEY_MESSAGE_VERSION);
        if (messageVersion.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_MESSAGE_VERSION, "0003");
        }
    }

    private void setNII() {
        String niiPOS = Preference.getInstance(this).getValueString(Preference.KEY_NII_POS);
        String niiTMS = Preference.getInstance(this).getValueString(Preference.KEY_NII_TMS);
        String niiEPS = Preference.getInstance(this).getValueString(Preference.KEY_NII_EPS);
        String niiGHC = Preference.getInstance(this).getValueString(Preference.KEY_NII_GHC);    // Paul_20180620
//
//        if (niiPOS.isEmpty()) {
//            Preference.getInstance(this).setValueString(Preference.KEY_NII_POS, "0245");
//        }
//        if (niiTMS.isEmpty()) {
//            Preference.getInstance(this).setValueString(Preference.KEY_NII_TMS, "0246");
//        }
//        if (niiEPS.isEmpty()) {
//            Preference.getInstance(this).setValueString(Preference.KEY_NII_EPS, "0242");
//        }
//        if (niiGHC.isEmpty()) { // Paul_20180620
//            Preference.getInstance(this).setValueString(Preference.KEY_NII_GHC, "0444");
//        }
    }

    private void setTpdu() {
        String tpduPOS = Preference.getInstance(this).getValueString(Preference.KEY_TPDU_POS);
        String tpduTMS = Preference.getInstance(this).getValueString(Preference.KEY_TPDU_TMS);
        String tpduEPS = Preference.getInstance(this).getValueString(Preference.KEY_TPDU_EPS);
        String tpduGHC = Preference.getInstance(this).getValueString(Preference.KEY_TPDU_GHC);  // Paul_20180620
//
//        if (tpduPOS.isEmpty()) {
//            Preference.getInstance(this).setValueString(Preference.KEY_TPDU_POS, "6002450000");
//        }
//        if (tpduTMS.isEmpty()) {
//            Preference.getInstance(this).setValueString(Preference.KEY_TPDU_TMS, "6002460000");
//        }
//        if (tpduEPS.isEmpty()) {
//            Preference.getInstance(this).setValueString(Preference.KEY_TPDU_EPS, "6002420000");
//        }
//        if (tpduGHC.isEmpty()) {    // Paul_20180620
//            Preference.getInstance(this).setValueString(Preference.KEY_TPDU_GHC, "6004440000");
//        }
    }

    private void setInvoice() {
        String invoiceNumber;

        if (Preference.getInstance(this).getValueString(Preference.KEY_INVOICE_NUMBER_ALL).isEmpty()) {
            invoiceNumber = "1";
            Preference.getInstance(this).setValueString(Preference.KEY_INVOICE_NUMBER_ALL, invoiceNumber);
        }

        if (Preference.getInstance(this).getValueString(Preference.KEY_INVOICE_NUMBER_ALL).isEmpty()) {
            invoiceNumber = "1";
            Preference.getInstance(this).setValueString(Preference.KEY_INVOICE_NUMBER_ALL, invoiceNumber);
        }

        if (Preference.getInstance(this).getValueString(Preference.KEY_INVOICE_NUMBER_ALL).isEmpty()) {
            invoiceNumber = "1";
            Preference.getInstance(this).setValueString(Preference.KEY_INVOICE_NUMBER_ALL, invoiceNumber);
        }
        if (Preference.getInstance(this).getValueString(Preference.KEY_INVOICE_NUMBER_ALL).isEmpty()) {
            invoiceNumber = "1";
            Preference.getInstance(this).setValueString(Preference.KEY_INVOICE_NUMBER_ALL, invoiceNumber);
        }
    }

    // Paul_20180620
    private void setMessageGHCVersion() {
        String messageVersion = Preference.getInstance(this).getValueString(Preference.KEY_MESSAGE_GHC_VERSION);
        if (messageVersion.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_MESSAGE_GHC_VERSION, "0008");
        }
    }

    public static CardManager getCardManager() {
        return cardManager;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "onTerminate: ");
        cardManager.unbindService();
    }
}
