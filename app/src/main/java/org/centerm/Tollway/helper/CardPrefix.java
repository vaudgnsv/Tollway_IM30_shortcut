package org.centerm.Tollway.helper;

import android.content.Context;
import android.util.Log;

import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class CardPrefix {

    private static final String TAG = "CardPrefix";

    public static String getTypeCard(String cardNo) {

        String cardSubstring;
        cardSubstring = cardNo.substring(0, 4);
        if (cardSubstring.equals("0060")) {
            Log.d(TAG, "getTypeCard: TMS");
            return "TMS";
        }
        cardSubstring = cardNo.substring(0, 6);
        if (cardSubstring.equals("449932") ||
                cardSubstring.equals("453215") ||
                cardSubstring.equals("453216") ||
                cardSubstring.equals("473252") ||
                cardSubstring.equals("473254") ||
                cardSubstring.equals("473256") ||
                cardSubstring.equals("484830") ||
                cardSubstring.equals("484831") /*||
                cardSubstring.equals("621654") ||
                cardSubstring.equals("931006")*/) {
            Log.d(TAG, "getTypeCard: TMS");
            return "TMS";
        }

        cardSubstring = cardNo.substring(0, 8);
        if (cardSubstring.equals("50436709")) {
            Log.d(TAG, "getTypeCard: TMS");
            return "TMS";
        }

        cardSubstring = cardNo.substring(0, 6);
        if (cardSubstring.equals("504367")) {
            Log.d(TAG, "getTypeCard: TMS");
            return "TMS";
        }

        cardSubstring = cardNo.substring(0, 6);
        if (cardSubstring.equals("621654")) {
            Log.d(TAG, "getTypeCard: TMS");
            return "TMS";
        }
        cardSubstring = cardNo.substring(0, 6);
        if (cardSubstring.equals("931006")) {
            Log.d(TAG, "getTypeCard: TMS");
            return "TMS";
        }

        cardSubstring = cardNo.substring(0, 7);
        if (cardSubstring.equals("9310061")) {
            Log.d(TAG, "getTypeCard: TMS");
            return "TMS";
        }

        cardSubstring = cardNo.substring(0, 6);
        if (cardSubstring.equals("990006")) {
            Log.d(TAG, "getTypeCard: TMS");
            return "TMS";
        }
        cardSubstring = cardNo.substring(0, 6);
        if (cardSubstring.equals("522230")) {
            Log.d(TAG, "getTypeCard: TMS");
            return "TMS";
        }
        cardSubstring = cardNo.substring(0, 3);
        if (cardSubstring.equals("621")) {
            Log.d(TAG, "getTypeCard: EPS");
            return "EPS";
        }
        Log.d(TAG, "getTypeCard: POS");
        return "POS";

        /*String cardSubstring;
        cardSubstring = cardNo.substring(0, 4);
        if (cardSubstring.equals("0060")) {
            Log.d(TAG, "getTypeCard: TMS");
            return "TMS";
        }
        cardSubstring = cardNo.substring(0, 6);
        if (cardSubstring.equals("449932") ||
                cardSubstring.equals("453215") ||
                cardSubstring.equals("453216") ||
                cardSubstring.equals("473252") ||
                cardSubstring.equals("473254") ||
                cardSubstring.equals("473256") ||
                cardSubstring.equals("484830") ||
                cardSubstring.equals("484831") ||
                cardSubstring.equals("621654") ||
                cardSubstring.equals("931006")) {
            Log.d(TAG, "getTypeCard: TMS");
            return "TMS";
        }

        cardSubstring = cardNo.substring(0, 8);
        if (cardSubstring.equals("50436709")) {
            Log.d(TAG, "getTypeCard: TMS");
            return "TMS";
        }

        cardSubstring = cardNo.substring(0, 6);
        if (cardSubstring.equals("504367") || cardSubstring.equals("990006")) {
            Log.d(TAG, "getTypeCard: TMS");
            return "TMS";
        }

        cardSubstring = cardNo.substring(0, 7);
        if (cardSubstring.equals("9310061")) {
            Log.d(TAG, "getTypeCard: TMS");
            return "TMS";
        }
        cardSubstring = cardNo.substring(0, 7);
        if (cardSubstring.equals("6210948")) {
            Log.d(TAG, "getTypeCard: EPS");
            return "EPS";
        }
        Log.d(TAG, "getTypeCard: POS");
        return "POS";*/
    }

//    public static String getTypeCardTMS(String cardNo) {
//        String cardSubstring;
//        cardSubstring = cardNo.substring(0, 4);
//        if (cardSubstring.equals("0060")) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return "TMS";
//        }
//        cardSubstring = cardNo.substring(0, 6);
//        if (cardSubstring.equals("449932") ||
//                cardSubstring.equals("453215") ||
//                cardSubstring.equals("453216") ||
//                cardSubstring.equals("473252") ||
//                cardSubstring.equals("473254") ||
//                cardSubstring.equals("473256") ||
//                cardSubstring.equals("484830") ||
//                cardSubstring.equals("484831")) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return "TMS";
//        }
//
//        cardSubstring = cardNo.substring(0, 8);
//        if (cardSubstring.equals("50436709")) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return "TMS";
//        }
//
//        cardSubstring = cardNo.substring(0, 6);
//        if (cardSubstring.equals("504367")) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return "TMS";
//        }
//
//        cardSubstring = cardNo.substring(0, 6);
//        if (cardSubstring.equals("621654")) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return "TMS";
//        }
//        cardSubstring = cardNo.substring(0, 6);
//        if (cardSubstring.equals("931006")) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return "TMS";
//        }
//
//        cardSubstring = cardNo.substring(0, 7);
//        if (cardSubstring.equals("9310061")) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return "TMS";
//        }
//
//        cardSubstring = cardNo.substring(0, 6);
//        if (cardSubstring.equals("990006")) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return "TMS";
//        }
//        cardSubstring = cardNo.substring(0, 6);
//        if (cardSubstring.equals("522230")) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return "TMS";
//        }
//        cardSubstring = cardNo.substring(0, 3);
//        if (cardSubstring.equals("621")) {
//            Log.d(TAG, "getTypeCard: EPS");
//            return "EPS";
//        }
//        Log.d(TAG, "getTypeCard: POS");
//        return null;
//        /*String cardSubstring;
//        cardSubstring = cardNo.substring(0, 4);
//        if (cardSubstring.equals("0060")) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return "TMS";
//        }
//        cardSubstring = cardNo.substring(0, 6);
//        if (cardSubstring.equals("449932") ||
//                cardSubstring.equals("453215") ||
//                cardSubstring.equals("453216") ||
//                cardSubstring.equals("473252") ||
//                cardSubstring.equals("473254") ||
//                cardSubstring.equals("473256") ||
//                cardSubstring.equals("484830") ||
//                cardSubstring.equals("484831") ||
//                cardSubstring.equals("621654") ||
//                cardSubstring.equals("931006")) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return "TMS";
//        }
//
//        cardSubstring = cardNo.substring(0, 8);
//        if (cardSubstring.equals("50436709")) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return "TMS";
//        }
//
//        cardSubstring = cardNo.substring(0, 6);
//        if (cardSubstring.equals("504367") || cardSubstring.equals("990006")) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return "TMS";
//        }
//
//        cardSubstring = cardNo.substring(0, 7);
//        if (cardSubstring.equals("9310061")) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return "TMS";
//        }
//        cardSubstring = cardNo.substring(0, 3);
//        if (cardSubstring.equals("621")) {
//            Log.d(TAG, "getTypeCard: EPS");
//            return "EPS";
//        }
//        Log.d(TAG, "getTypeCard: POS");
//        return null;*/
//    }
//
//    public static String getTypeCardName(String cardNo) {
//        String cardSubstring;
//        cardSubstring = cardNo.substring(0, 4);
//        if (cardSubstring.equals("0060")) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return "ATM";
//        }
//        cardSubstring = cardNo.substring(0, 6);
//        if (cardSubstring.equals("449932") ||
//                cardSubstring.equals("453215") ||
//                cardSubstring.equals("453216") ||
//                cardSubstring.equals("473252") ||
//                cardSubstring.equals("473254") ||
//                cardSubstring.equals("473256") ||
//                cardSubstring.equals("484830") ||
//                cardSubstring.equals("484831") /*||
//                cardSubstring.equals("621654") ||
//                cardSubstring.equals("931006")*/) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return "Visa Debit";
//        }
//
//        cardSubstring = cardNo.substring(0, 8);
//        if (cardSubstring.equals("50436709")) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return "ATM Corporate";
//        }
//
//        cardSubstring = cardNo.substring(0, 6);
//        if (cardSubstring.equals("504367") /*|| cardSubstring.equals("990006")*/) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return "ATM";
//        }
//
//        cardSubstring = cardNo.substring(0, 6);
//        if (cardSubstring.equals("621654")) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return "UnionPay";
//        }
//        cardSubstring = cardNo.substring(0, 6);
//        if (cardSubstring.equals("931006")) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return "Debit PromptCard";
//        }
//
//        cardSubstring = cardNo.substring(0, 7);
//        if (cardSubstring.equals("9310061")) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return "Welfare Card";
//        }
//
//        cardSubstring = cardNo.substring(0, 6);
//        if (cardSubstring.equals("990006")) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return "ATM Thai Std";
//        }
//        cardSubstring = cardNo.substring(0, 6);
//        if (cardSubstring.equals("522230")) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return "KTB MASTER";
//        }
//        cardSubstring = cardNo.substring(0, 3);
//        if (cardSubstring.equals("621")) {
//            Log.d(TAG, "getTypeCard: EPS");
//            return "UPI-CARD";
//        }
//        Log.d(TAG, "getTypeCard: POS");
//
//        cardSubstring = cardNo.substring(0, 1);
//        if (cardSubstring.equals("4")) {
//            Log.d(TAG, "Visa CARD");
//            return "Visa CARD";
//        }
//
//        cardSubstring = cardNo.substring(0, 1);
//        if (cardSubstring.equals("5")) {
//            Log.d(TAG, "MASTER CARD");
//            return "MASTER CARD";
//        }
//
//        return "Visa, MCD";
//    }

    public static String getInvoice(Context context, String typeCard) {
        String invoiceNumber;
        if (typeCard.equalsIgnoreCase("POS")) {
            invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_ALL);
        } else if (typeCard.equalsIgnoreCase("TMS")) {
            invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_ALL);
        } else if (typeCard.equalsIgnoreCase("GHC")) {  // Paul_20180620
            invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_ALL);
        } else {
            invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_ALL);
        }
        Log.d(TAG, "getInvoice invoiceNumber: " + invoiceNumber);
        return invoiceNumber;
    }

    public static String getTerminalId(Context context, String typeCard) {
        String terminalId;
        if (typeCard.equalsIgnoreCase("POS")) {
            terminalId = Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_ID_POS);
        } else if (typeCard.equalsIgnoreCase("TMS")) {
            terminalId = Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_ID_TMS);
        } else if (typeCard.equalsIgnoreCase("GHC")) {     // Paul_20180620
            terminalId = Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_ID_GHC);
        } else {
            terminalId = Preference.getInstance(context).getValueString(Preference.KEY_TERMINAL_ID_EPS);
        }
        Log.d(TAG, "getTerminalId terminalId: " + terminalId);
        return terminalId;
    }

    public static String getMerchantId(Context context, String typeCard) {
        String merchantId;
        if (typeCard.equalsIgnoreCase("POS")) {
            merchantId = Preference.getInstance(context).getValueString(Preference.KEY_MERCHANT_ID_POS);
        } else if (typeCard.equalsIgnoreCase("TMS")) {
            merchantId = Preference.getInstance(context).getValueString(Preference.KEY_MERCHANT_ID_TMS);
        } else if (typeCard.equalsIgnoreCase("GHC")) {  // Paul_20180620
            merchantId = Preference.getInstance(context).getValueString(Preference.KEY_MERCHANT_ID_GHC);
        } else {
            merchantId = Preference.getInstance(context).getValueString(Preference.KEY_MERCHANT_ID_EPS);
        }

        Log.d(TAG, "getMerchantId merchantId: " + merchantId);
        return merchantId;
    }

    public static String geTraceIdPlus(Context context, String typeCard) {
        String traceIdNo = "";
        if (typeCard.equalsIgnoreCase("POS")) {
            traceIdNo = String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_TRACE_NO_POS)) + 1);
        } else if (typeCard.equalsIgnoreCase("EPS")) {
            traceIdNo = String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_TRACE_NO_EPS)) + 1);
        } else if (typeCard.equalsIgnoreCase("TMS")) {
            traceIdNo = String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_TRACE_NO_TMS)) + 1);
        }
        Log.d(TAG, "geTraceIdPlus traceIdNo: " + traceIdNo);
        return traceIdNo;
    }

    public static String geTraceId(Context context, String typeCard) {
        String traceIdNo = "";
        if (typeCard.equalsIgnoreCase("POS")) {
            traceIdNo = String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_TRACE_NO_POS)));
        } else if (typeCard.equalsIgnoreCase("EPS")) {
            traceIdNo = String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_TRACE_NO_EPS)));
        } else if (typeCard.equalsIgnoreCase("TMS")) {
            traceIdNo = String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_TRACE_NO_TMS)));
        } else if (typeCard.equalsIgnoreCase("GHC")) {
            traceIdNo = String.valueOf(Integer.valueOf(Preference.getInstance(context).getValueString(Preference.KEY_TRACE_NO_GHC)));
        }
        Log.d(TAG, "geTraceId traceIdNo: " + traceIdNo);
        return traceIdNo;
    }
//
//    // Paul_20180523
//    public static String getNii(String cardNo, Context context) {
//        String cardSubstring;
//        cardSubstring = cardNo.substring(0, 4);
//        if (cardSubstring.equals("0060")) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);    // Paul_20180523
////            return "0246";
//        }
//        cardSubstring = cardNo.substring(0, 6);
//        if (cardSubstring.equals("449932") ||
//                cardSubstring.equals("453215") ||
//                cardSubstring.equals("453216") ||
//                cardSubstring.equals("473252") ||
//                cardSubstring.equals("473254") ||
//                cardSubstring.equals("473256") ||
//                cardSubstring.equals("484830") ||
//                cardSubstring.equals("484831") ||
//                cardSubstring.equals("621654") ||
//                cardSubstring.equals("931006")) {
//            Log.d(TAG, "getTypeCard: EPS");
//            return Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);    // Paul_20180523
////            return "0246";
//        }
//
//        cardSubstring = cardNo.substring(0, 8);
//        if (cardSubstring.equals("50436709")) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);    // Paul_20180523
////            return "0246";
//        }
//
//        cardSubstring = cardNo.substring(0, 6);
//        if (cardSubstring.equals("504367") || cardSubstring.equals("990006")) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);    // Paul_20180523
////            return "0246";
//        }
//
//        cardSubstring = cardNo.substring(0, 7);
//        if (cardSubstring.equals("9310061")) {
//            Log.d(TAG, "getTypeCard: EPS");
//            return Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);    // Paul_20180523
////            return "0242";
//        }
//        cardSubstring = cardNo.substring(0, 6);
//        if (cardSubstring.equals("522230")) {
//            Log.d(TAG, "getTypeCard: TMS");
//            return Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);    // Paul_20180523;
//        }
//
//        Log.d(TAG, "getTypeCard: POS");
//        cardSubstring = cardNo.substring(0, 3);
//        if (cardSubstring.equals("621")) {
//            Log.d(TAG, "getTypeCard: EPS");
//            return Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
//        }
//        return Preference.getInstance(context).getValueString(Preference.KEY_NII_POS);    // Paul_20180523
////        return "0246";
//    }

    public static String getBatch(Context context, String typeCard) {
        String batchNumber = null;
        if (typeCard.equalsIgnoreCase("POS")) {
            batchNumber = Preference.getInstance(context).getValueString(Preference.KEY_BATCH_NUMBER_POS);
        } else if (typeCard.equalsIgnoreCase("EPS")) {
            batchNumber = Preference.getInstance(context).getValueString(Preference.KEY_BATCH_NUMBER_EPS);
        } else if (typeCard.equalsIgnoreCase("TMS")) {
            batchNumber = Preference.getInstance(context).getValueString(Preference.KEY_BATCH_NUMBER_TMS);
        } else if (typeCard.equalsIgnoreCase("GHC")) {
            batchNumber = Preference.getInstance(context).getValueString(Preference.KEY_BATCH_NUMBER_GHC);
        }
        Log.d(TAG, "getBatch batchNumber: " + batchNumber);
        return batchNumber;
    }

    public static String getTPDU(Context context, String typeCard) {
        String TPDU = "";
        if (typeCard.equalsIgnoreCase("POS")) {
            TPDU = Preference.getInstance(context).getValueString(Preference.KEY_TPDU_POS);
        } else if (typeCard.equalsIgnoreCase("EPS")) {
            TPDU = Preference.getInstance(context).getValueString(Preference.KEY_TPDU_EPS);
        } else if (typeCard.equalsIgnoreCase("TMS")) {
            TPDU = Preference.getInstance(context).getValueString(Preference.KEY_TPDU_TMS);
        } else if (typeCard.equalsIgnoreCase("GHC")) {
            TPDU = Preference.getInstance(context).getValueString(Preference.KEY_TPDU_GHC);
        }
        return TPDU;
    }

    public static String calLen(String l, int number) {
        String no = "";
        if (l == null) {
            l = "";
        }
        for (int i = l.length(); i < number; i++) {
            no += "0";
        }
        Log.d(TAG, "calLen: " + no + l);
        return no + l;
    }

    public static String calSpenLen(String l, int number) {
        String no = "";
        for (int i = l.length(); i < number; i++) {
            no += " ";
        }
        Log.d(TAG, "calSpenLen: " + no + l);
        return l + no;
    }

    public static String hexadecimalToInt(String panSn) {
//        System.out.printf("utility:: YYYYYYYYY panSn.substring(6, 8) = %s \n",panSn.substring(6, 8));
        BigInteger value = new BigInteger(panSn != null ? panSn.substring(6, 8) : "1", 16);
//        BigInteger value = new BigInteger(panSn.substring(6, 8), 16);     // Paul_20180714 Emv Tag 5F34
//        System.out.printf("utility:: YYYYYYYYY value.toString = %s \n",value.toString());
//        System.out.printf("utility:: YYYYYYYYY calLen(String.valueOf(value.toString().length()), 4) = %s \n",calLen(String.valueOf(value.toString().length()), 4));
//        return calLen(String.valueOf(value.toString().length()), 4);     // Paul_20180714 Emv Tag 5F34
        return calLen(String.valueOf(value.toString()), 4);     // Paul_20180714 Emv Tag 5F34
    }


    //20180806 JSON Card range Config
    public static void getStringJson(Context context) {

        if (Preference.getInstance(context).getValueString(Preference.KEY_JSONBYPASS_ID).equalsIgnoreCase("1"))
            return;

//        File file = new File("sdcard/print_param.json");
        String para = "";
        File file = new File("/cache/customer/media/print_param.json");   //20180821 Joe centerm chang JSON file location

        Log.d(TAG, "Call getStringJson(Context context)");
        String getDirectoryPath = String.valueOf(file.length());
        FileInputStream stream = null;
        try {
            String jString = null;
            stream = new FileInputStream(file);
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            /* Instead of using default, pass in a decoder. */
            jString = Charset.defaultCharset().decode(bb).toString();
            Log.d(TAG, "onCreate: " + jString);
            try {
                JSONObject jsonObject = new JSONObject(jString);
                JSONObject ObjParam = jsonObject.getJSONObject("param");
                para = ObjParam.getString("merchantNameLine1");
                Preference.getInstance(context).setValueString(Preference.KEY_MERCHANT_1, para);//Game 03/08/2018

                para = ObjParam.getString("merchantNameLine2");
                Preference.getInstance(context).setValueString(Preference.KEY_MERCHANT_2, para);//Game 03/08/2018

                para = ObjParam.getString("merchantNameLine3");
                Preference.getInstance(context).setValueString(Preference.KEY_MERCHANT_3, para);//Game 03/08/2018

                para = ObjParam.getString("taxId");
                Preference.getInstance(context).setValueString(Preference.KEY_TAX_ID, para);

                para = ObjParam.getString("posId");
                Preference.getInstance(context).setValueString(Preference.KEY_POS_ID, para);


                Double fee = ObjParam.getDouble("fee");//Game 03/08/2018 No data in json
                Preference.getInstance(context).setValueDouble(Preference.KEY_FEE, fee);//K.GAME 20180803

                para = ObjParam.getString("qrAid");
                Preference.getInstance(context).setValueString(Preference.KEY_QR_AID, para);//Game 03/08/2018

                para = ObjParam.getString("qrBillerId");
                Preference.getInstance(context).setValueString(Preference.KEY_QR_BILLER_ID, para);//Game 03/08/2018

                para = ObjParam.getString("qrMerchantName");
                Preference.getInstance(context).setValueString(Preference.KEY_QR_MERCHANT_NAME, para);//Game 03/08/2018

                para = ObjParam.getString("qrMerchantNameTHAI");
                Preference.getInstance(context).setValueString(Preference.KEY_QR_MERCHANT_NAME_THAI, para);

                para = ObjParam.getString("qrTerminalId");
                Preference.getInstance(context).setValueString(Preference.KEY_QR_TERMINAL_ID, para);//Game 03/08/2018

                para = ObjParam.getString("qrMerchantId");
                Preference.getInstance(context).setValueString(Preference.KEY_QR_MERCHANT_ID, para);

                para = ObjParam.getString("qrBillerkey");
                Preference.getInstance(context).setValueString(Preference.KEY_BILLER_KEY, para);//Game 03/08/2018

                para = ObjParam.getString("primaryIp");
                Preference.getInstance(context).setValueString(Preference.KEY_PRIMARY_IP, para);//Game 03/08/2018

                para = ObjParam.getString("primaryPort");
                Preference.getInstance(context).setValueString(Preference.KEY_PRIMARY_PORT, para);//Game 03/08/2018

                para = ObjParam.getString("secondaryIp");
                Preference.getInstance(context).setValueString(Preference.KEY_SECONDARY_IP, para);//Game 03/08/2018

                para = ObjParam.getString("secondaryPort");
                Preference.getInstance(context).setValueString(Preference.KEY_SECONDARY_PORT, para);//Game 03/08/2018

                para = ObjParam.getString("qrPort");
                Preference.getInstance(context).setValueString(Preference.KEY_QR_PORT, para);//Game 03/08/2018

                String ip = Preference.getInstance(context).getValueString(Preference.KEY_PRIMARY_IP);
                String port = Preference.getInstance(context).getValueString(Preference.KEY_QR_PORT);
                String ipAndPort = "https://" + ip +":"+ port+"/";
                String BASE_URL = ipAndPort + "transaction/services/v.2/" ;//"https://172.22.0.251:3840/transaction/services/v.2/";
                Preference.getInstance(context).setValueString(Preference.KEY_QR_URL_ID, BASE_URL);

                para = ObjParam.getString("aliTerid");
                Preference.getInstance(context).setValueString(Preference.KEY_ALIPAY_TERMINAL_ID, para);

                para = ObjParam.getString("aliMerid");
                Preference.getInstance(context).setValueString(Preference.KEY_ALIPAY_MERCHANT_ID, para);

                para = ObjParam.getString("aliStoreid");
                Preference.getInstance(context).setValueString(Preference.KEY_ALIPAY_STORE_ID, para);

                para = ObjParam.getString("posTerminalId");
                Preference.getInstance(context).setValueString(Preference.KEY_TERMINAL_ID_POS, para);//Game 03/08/2018

                para = ObjParam.getString("posMerchantId");
                Preference.getInstance(context).setValueString(Preference.KEY_MERCHANT_ID_POS, para);//Game 03/08/2018

                para = ObjParam.getString("posTpdu");
                Preference.getInstance(context).setValueString(Preference.KEY_TPDU_POS, para);//Game 03/08/2018

                para = ObjParam.getString("posNii");
                Preference.getInstance(context).setValueString(Preference.KEY_NII_POS, para);//Game 03/08/2018

                para = ObjParam.getString("epsTerminalId");
                Preference.getInstance(context).setValueString(Preference.KEY_TERMINAL_ID_EPS, para);//Game 03/08/2018

                para = ObjParam.getString("epsMerchantId");
                Preference.getInstance(context).setValueString(Preference.KEY_MERCHANT_ID_EPS, para);//Game 03/08/2018

                para = ObjParam.getString("epsTpdu");
                Preference.getInstance(context).setValueString(Preference.KEY_TPDU_EPS, para);//Game 03/08/2018

                para = ObjParam.getString("epsNii");
                Preference.getInstance(context).setValueString(Preference.KEY_NII_EPS, para);//Game 03/08/2018

                para = ObjParam.getString("tmsTerminalId");
                Preference.getInstance(context).setValueString(Preference.KEY_TERMINAL_ID_TMS, para);//Game 03/08/2018

//                String tmsMerchantId = ObjParam.getString("tmsMerchantId");
                para = ObjParam.getString("tmsMerchantId");
                Preference.getInstance(context).setValueString(Preference.KEY_MERCHANT_ID_TMS, para);//Game 03/08/2018

//                String tmsTpdu = ObjParam.getString("tmsTpdu");
                para = ObjParam.getString("tmsTpdu");
                Preference.getInstance(context).setValueString(Preference.KEY_TPDU_TMS, para);//Game 03/08/2018

//                String tmsNii = ObjParam.getString("tmsNii");
                para = ObjParam.getString("tmsNii");
                Preference.getInstance(context).setValueString(Preference.KEY_NII_TMS, para);//Game 03/08/2018

//                String tmsTerminaversion = ObjParam.getString("tmsTerminaversion");
                para = ObjParam.getString("tmsTerminaversion");
                Preference.getInstance(context).setValueString(Preference.KEY_TERMINAL_VERSION, para);//Game 03/08/2018

//                String tmsMsgVersion = ObjParam.getString("tmsMsgVersion");
                para = ObjParam.getString("tmsMsgVersion");
                Preference.getInstance(context).setValueString(Preference.KEY_MESSAGE_VERSION, para);//Game 03/08/2018

//                String paramVersion = ObjParam.getString("paramVersion");//Game 03/08/2018
                para = ObjParam.getString("paramVersion");//Game 03/08/2018
                Preference.getInstance(context).setValueString(Preference.KEY_PARAMETER_VERSION, para);//Game 03/08/2018

//                String ghcTerminalId = ObjParam.getString("ghcTerminalId");
                para = ObjParam.getString("ghcTerminalId");
                Preference.getInstance(context).setValueString(Preference.KEY_TERMINAL_ID_GHC, para);//Game 03/08/2018

//                String ghcMerchantId = ObjParam.getString("ghcMerchantId");
                para = ObjParam.getString("ghcMerchantId");
                Preference.getInstance(context).setValueString(Preference.KEY_MERCHANT_ID_GHC, para);//Game 03/08/2018

//                String ghcTpdu = ObjParam.getString("ghcTpdu");
                para = ObjParam.getString("ghcTpdu");
                Preference.getInstance(context).setValueString(Preference.KEY_TPDU_GHC, para);//Game 03/08/2018

//                String ghcNii = ObjParam.getString("ghcNii");
                para = ObjParam.getString("ghcNii");
                Preference.getInstance(context).setValueString(Preference.KEY_NII_GHC, para);//Game 03/08/2018

//                String ghcTerminaversion = ObjParam.getString("ghcTerminaversion");
//                Preference.getInstance(context).setValueString(Preference.KEY_TERMINAL_VERSION, ghcTerminaversion);// same TMS KEY_TERMINAL_VERSION

//                String ghcMsgVersion = ObjParam.getString("ghcMsgVersion");
                para = ObjParam.getString("ghcMsgVersion");
                Preference.getInstance(context).setValueString(Preference.KEY_MESSAGE_GHC_VERSION, para);

                para = ObjParam.getString("KTBNORMAL");
                Preference.getInstance(context).setValueString(Preference.KEY_KTBNORMAL_ID, para);

                para = ObjParam.getString("AXA");
                Preference.getInstance(context).setValueString(Preference.KEY_AXA_ID, para);

                para = ObjParam.getString("Contactless");
                if(para == null) {
                    Preference.getInstance(context).setValueString(Preference.KEY_CONTACTLESS_ID, "0");
                }else {
                    Preference.getInstance(context).setValueString(Preference.KEY_CONTACTLESS_ID, para);
                }

                //20190701_NAMTAN
                para = ObjParam.getString("MAX_Contactless");

                if(para == null) {
                    Preference.getInstance(context).setValueString(Preference.KEY_MAX_CONTACTLESS_ID, "1500.00");
                }else {
                    Preference.getInstance(context).setValueString(Preference.KEY_MAX_CONTACTLESS_ID, para);
                }

                para = ObjParam.getString("Swipe");

                if(para == null)
                    Preference.getInstance(context).setValueString(Preference.KEY_SWIPE_ID, "0");
                else
                    Preference.getInstance(context).setValueString(Preference.KEY_SWIPE_ID, para);

                para = ObjParam.getString("Manual");

                if(para == null)
                    Preference.getInstance(context).setValueString(Preference.KEY_MANUAL_ID, "0");
                else
                    Preference.getInstance(context).setValueString(Preference.KEY_MANUAL_ID, para);

                if ( ObjParam.getString("AXA").equalsIgnoreCase("1")||ObjParam.getString("KTBNORMAL").equalsIgnoreCase("1"))
                {
                    para = ObjParam.getString("para_enable");
                    Preference.getInstance(context).setValueString(Preference.KEY_TAG_1000_HC, para);
                    Preference.getInstance(context).setValueString(Preference.KEY_TAG_1000, para);


                    para = ObjParam.getString("para_com");
                    Preference.getInstance(context).setValueString(Preference.KEY_TAG_1001_HC, para);
                    Preference.getInstance(context).setValueString(Preference.KEY_TAG_1001, para);

                    para = ObjParam.getString("para_ref1");
                    Preference.getInstance(context).setValueString(Preference.KEY_TAG_1002_HC, para);
                    Preference.getInstance(context).setValueString(Preference.KEY_TAG_1002, para);

                    para = ObjParam.getString("para_ref2");
                    Preference.getInstance(context).setValueString(Preference.KEY_TAG_1003_HC, para);
                    Preference.getInstance(context).setValueString(Preference.KEY_TAG_1003, para);

                    para = ObjParam.getString("para_ref3");
                    Preference.getInstance(context).setValueString(Preference.KEY_TAG_1004_HC, para);
                    Preference.getInstance(context).setValueString(Preference.KEY_TAG_1004, para);
                }
//-----------------------------------------------------------------------------------------------


//                String MERCHANT_PIN = ObjParam.getString("MERCHANT_PIN");
                para = ObjParam.getString("MERCHANT_PIN");
                Preference.getInstance(context).setValueString(Preference.KEY_PIN, para);

//                String MAX_AMT = ObjParam.getString("MAX_AMT");
                para = ObjParam.getString("MAX_AMT");
                Preference.getInstance(context).setValueString(Preference.KEY_MAX_AMT, para);

//                String App_enable = ObjParam.getString("App_enable");
                para = ObjParam.getString("App_enable");
                Preference.getInstance(context).setValueString(Preference.KEY_APP_ENABLE, para);
//20181105 JEFF
                para = ObjParam.getString("Ali_enable");
                Preference.getInstance(context).setValueString(Preference.KEY_ALIPAY_ID, para);
                para = ObjParam.getString("Wec_enable");
                Preference.getInstance(context).setValueString(Preference.KEY_WECHATPAY_ID, para);
                para = ObjParam.getString("Rail_enable");
                Preference.getInstance(context).setValueString(Preference.KEY_RAILWAY_ID, para);

//                String Rs232_enable = ObjParam.getString("Rs232_enable");
                para = ObjParam.getString("Rs232_enable");
                Preference.getInstance(context).setValueString(Preference.KEY_RS232_Enable_ID, para);

//                String KEY_FixRATE_ID = ObjParam.getString("FixRate");
                para = ObjParam.getString("FixRate");
                Preference.getInstance(context).setValueString(Preference.KEY_FixRATE_ID, para);


                Double KEY_rateMC_ID = ObjParam.getDouble("rateMC");
                Preference.getInstance(context).setValueDouble(Preference.KEY_rateMC_ID, KEY_rateMC_ID);

                Double KEY_rateVI_ID = ObjParam.getDouble("rateVI");
                Preference.getInstance(context).setValueDouble(Preference.KEY_rateVI_ID, KEY_rateVI_ID);

                Double KEY_rateVILocal_ID = ObjParam.getDouble("rateVILocal");
                Preference.getInstance(context).setValueDouble(Preference.KEY_rateVILocal_ID, KEY_rateVILocal_ID);

                Double KEY_rateMCLocal_ID = ObjParam.getDouble("rateMCLocal");
                Preference.getInstance(context).setValueDouble(Preference.KEY_rateMCLocal_ID, KEY_rateMCLocal_ID);

                Double KEY_rateJCB_ID = ObjParam.getDouble("rateJCB");
                Preference.getInstance(context).setValueDouble(Preference.KEY_rateJCB_ID, KEY_rateJCB_ID);

                Double KEY_rateUPI_ID = ObjParam.getDouble("rateUPI");
                Preference.getInstance(context).setValueDouble(Preference.KEY_rateUPI_ID, KEY_rateUPI_ID);

                Double KEY_rateTPN_ID = ObjParam.getDouble("rateTPN");
                Preference.getInstance(context).setValueDouble(Preference.KEY_rateTPN_ID, KEY_rateTPN_ID);

                para = ObjParam.getString("MerchantSupportRate");
                Preference.getInstance(context).setValueString(Preference.KEY_MerchantSupportRate_ID, para);

//20181213  SINN  KTB CR request set default ref text.
                para = ObjParam.getString("Ref1text");
                Preference.getInstance(context).setValueString(Preference.KEY_Ref1text_ID, para);
                para = ObjParam.getString("Ref2text");
                Preference.getInstance(context).setValueString(Preference.KEY_Ref2text_ID, para);
                para = ObjParam.getString("Ref3text");
                Preference.getInstance(context).setValueString(Preference.KEY_Ref3text_ID, para);
                para = ObjParam.getString("QrRef1text");
                Preference.getInstance(context).setValueString(Preference.KEY_QrRef1text_ID, para);
                para = ObjParam.getString("QrRef2text");
                Preference.getInstance(context).setValueString(Preference.KEY_QrRef2text_ID, para);

//20181218  SINN Void syn date/time
                para = ObjParam.getString("print_slip");
                Preference.getInstance(context).setValueString(Preference.KEY_PrintSlip_ID, para);
//20181218  SINN Print slip enable/disable
                para = ObjParam.getString("SlipVoidSync");
                Preference.getInstance(context).setValueString(Preference.KEY_SlipSyncTime_ID, para);

                para = ObjParam.getString("AdminPIN");
                Preference.getInstance(context).setValueString(Preference.KEY_ADMIN_PIN, para);

                para = ObjParam.getString("SettingPIN");
                Preference.getInstance(context).setValueString(Preference.KEY_SETTING_FOR_USER_PASS_WORD, para);


                // TOLLWAY
                para = ObjParam.getString("ratePrice");
                Preference.getInstance(context).setValueString(Preference.KEY_RATE_PRICE, para);

                para = ObjParam.getString("duplicateTime");
                Preference.getInstance(context).setValueString(Preference.KEY_DUPLICATE_TIME, para);

                para = ObjParam.getString("JsonVersion");
                Preference.getInstance(context).setValueString(Preference.KEY_JSON_CONTROL_VERSION, para);


            } catch (JSONException e) {
                Utility.customDialogAlertAuto(context, "Json ไม่ถูกต้อง:" + para);
                e.printStackTrace();
            }
        } catch (IOException e) {
            Utility.customDialogAlertAuto(context, "Json ไม่ถูกต้อง:" + para);
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "onCreate: " + getDirectoryPath);
    }

    public static String getJsonTypeCARD(String cardNo) {
        String szCardNO;
//        File file = new File("sdcard/print_param.json");
        File file = new File("/cache/customer/media/print_param.json");   //20180821 Joe centerm chang JSON file location
        String getDirectoryPath = String.valueOf(file.length());
        FileInputStream stream = null;

        Log.d(TAG, "getJsonTypeCARD:" + cardNo);

        try {
            String jString = null;
            stream = new FileInputStream(file);
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            /* Instead of using default, pass in a decoder. */
            jString = Charset.defaultCharset().decode(bb).toString();
//            Log.d(TAG, "onCreate: " + jString);
            try {
                JSONObject jsonObject = new JSONObject(jString);
                String param = jsonObject.getString("param");
                JSONArray jArray = jsonObject.getJSONArray("cardFee");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject object3 = jArray.getJSONObject(i);
                    String cardName = object3.getString("cardName");
                    String hostIndex = object3.getString("hostIndex");
                    String cardRange = object3.getString("cardRange");

                    Log.d(TAG, "cardName:" + cardName);
                    Log.d(TAG, "hostIndex:" + hostIndex);
                    Log.d(TAG, "cardRange:" + cardRange);


                    szCardNO = cardNo.substring(0, cardRange.length());
                    Log.d(TAG, "szCardNO:" + szCardNO + " cardRange:" + cardRange);

                    if (cardRange.equalsIgnoreCase(szCardNO)) {
                        Log.d(TAG, " return hostIndex:" + hostIndex);
                        return hostIndex;
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        Log.d(TAG, "onCreate: " + getDirectoryPath);
//        return "POS";
        //SINN 20181119 KTB order no define will  reject.
        return "reject";
    }


    public static String getJsonTypeCARDPINCHECK(String cardNo) {
        String szCardNO;
//        File file = new File("sdcard/print_param.json");
        File file = new File("sdcard/print_param.json");   //20180821 Joe centerm chang JSON file location
        String getDirectoryPath = String.valueOf(file.length());
        FileInputStream stream = null;

        Log.d(TAG, "getJsonTypeCARDPINCHECK:" + cardNo);

        try {
            String jString = null;
            stream = new FileInputStream(file);
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            /* Instead of using default, pass in a decoder. */
            jString = Charset.defaultCharset().decode(bb).toString();
//            Log.d(TAG, "onCreate: " + jString);
            try {
                JSONObject jsonObject = new JSONObject(jString);
                String param = jsonObject.getString("param");
                JSONArray jArray = jsonObject.getJSONArray("cardFee");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject object3 = jArray.getJSONObject(i);
                    String cardName = object3.getString("cardName");
                    String pinReq = object3.getString("pinReq");
                    String cardRange = object3.getString("cardRange");

                    Log.d(TAG, "cardName:" + cardName);
                    Log.d(TAG, "pinReq:" + pinReq);
                    Log.d(TAG, "cardRange:" + cardRange);


                    szCardNO = cardNo.substring(0, cardRange.length());
                    Log.d(TAG, "szCardNO:" + szCardNO + " cardRange:" + cardRange);

                    if (cardRange.equalsIgnoreCase(szCardNO)) {
                        Log.d(TAG, " return pinReq:" + pinReq);
                        return pinReq;
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        Log.d(TAG, "onCreate: " + getDirectoryPath);
        return "NOPIN";
    }




    public static String getJsonTypeCARDTMS(String cardNo) {
        String szCardNO;
//        File file = new File("sdcard/print_param.json");
        File file = new File("/cache/customer/media/print_param.json");   //20180821 Joe centerm chang JSON file location
        String getDirectoryPath = String.valueOf(file.length());
        FileInputStream stream = null;
        try {
            String jString = null;
            stream = new FileInputStream(file);
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            /* Instead of using default, pass in a decoder. */
            jString = Charset.defaultCharset().decode(bb).toString();
            Log.d(TAG, "onCreate: " + jString);
            try {
                JSONObject jsonObject = new JSONObject(jString);
                String param = jsonObject.getString("param");
                JSONArray jArray = jsonObject.getJSONArray("cardFee");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject object3 = jArray.getJSONObject(i);
                    String cardName = object3.getString("cardName");
                    String hostIndex = object3.getString("hostIndex");
                    String cardRange = object3.getString("cardRange");

                    Log.d(TAG, "getJsonTypeCARDTMS cardName:" + cardName);
                    Log.d(TAG, "getJsonTypeCARDTMS hostIndex:" + hostIndex);
                    Log.d(TAG, "getJsonTypeCARDTMS cardRange:" + cardRange);

                    szCardNO = cardNo.substring(0, cardRange.length());

                    Log.d(TAG, "getJsonTypeCARDTMS szCardNO:" + szCardNO);
                    if (cardRange.equalsIgnoreCase(szCardNO) && !hostIndex.equalsIgnoreCase("POS")) {

                        Log.d(TAG, " return hostIndex:" + hostIndex);
                        return hostIndex;
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "onCreate: " + getDirectoryPath);
        return null;
    }

    public static String getJSONNii(String cardNo, Context context) {
        {
            String szCardNO;
//            File file = new File("/cache/customer/media/print_param.json");
            File file = new File("/cache/customer/media/print_param.json");//K.GAME 180921
            String getDirectoryPath = String.valueOf(file.length());
            FileInputStream stream = null;
            try {
                String jString = null;
                stream = new FileInputStream(file);
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                /* Instead of using default, pass in a decoder. */
                jString = Charset.defaultCharset().decode(bb).toString();
                Log.d(TAG, "onCreate: " + jString);
                try {
                    JSONObject jsonObject = new JSONObject(jString);
                    String param = jsonObject.getString("param");
                    JSONArray jArray = jsonObject.getJSONArray("cardFee");
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject object3 = jArray.getJSONObject(i);
                        String cardName = object3.getString("cardName");
                        String hostIndex = object3.getString("hostIndex");
                        String cardRange = object3.getString("cardRange");

                        szCardNO = cardNo.substring(0, cardRange.length());
                        if (cardRange.equalsIgnoreCase(szCardNO)) {
                            if (hostIndex.equalsIgnoreCase("TMS"))
                                return Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);
                            else if (hostIndex.equalsIgnoreCase("EPS"))
                                return Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);
                            else
                                return Preference.getInstance(context).getValueString(Preference.KEY_NII_POS);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (stream != null) {
                        stream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return Preference.getInstance(context).getValueString(Preference.KEY_NII_POS);
        }
    }

    public static String getJSONTypeCardName(String cardNo) {

        String szCardNO;
//        File file = new File("/cache/customer/media/print_param.json");
        File file = new File("/cache/customer/media/print_param.json");   //20180821 Joe centerm chang JSON file location
        String getDirectoryPath = String.valueOf(file.length());
        FileInputStream stream = null;
        try {
            String jString = null;
            stream = new FileInputStream(file);
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            /* Instead of using default, pass in a decoder. */
            jString = Charset.defaultCharset().decode(bb).toString();
            Log.d(TAG, "onCreate: " + jString);
            try {
                JSONObject jsonObject = new JSONObject(jString);
                String param = jsonObject.getString("param");
                JSONArray jArray = jsonObject.getJSONArray("cardFee");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject object3 = jArray.getJSONObject(i);
                    String cardName = object3.getString("cardName");
                    String hostIndex = object3.getString("hostIndex");
                    String cardRange = object3.getString("cardRange");

                    szCardNO = cardNo.substring(0, cardRange.length());
                    if (cardRange.equalsIgnoreCase(szCardNO)) {
                        return cardName;
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "UNKNOW";
    }
    //END 20180806 JSON Card range Config


    public static String getJSONTypeCardFee(String cardNo) {

        String szCardNO;
        File file = new File("/cache/customer/media/print_param.json");   //20180821 Joe centerm chang JSON file location
        String getDirectoryPath = String.valueOf(file.length());
        FileInputStream stream = null;
        try {
            String jString = null;
            stream = new FileInputStream(file);
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            /* Instead of using default, pass in a decoder. */
            jString = Charset.defaultCharset().decode(bb).toString();
            Log.d(TAG, "onCreate: " + jString);
            try {
                JSONObject jsonObject = new JSONObject(jString);
                String param = jsonObject.getString("param");
                JSONArray jArray = jsonObject.getJSONArray("cardFee");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject object3 = jArray.getJSONObject(i);
                    String cardName = object3.getString("cardName");
                    String hostIndex = object3.getString("hostIndex");
                    String cardRange = object3.getString("cardRange");
                    String cardfee = object3.getString("cardFee");

                    szCardNO = cardNo.substring(0, cardRange.length());
                    if (cardRange.equalsIgnoreCase(szCardNO)) {
                        return cardfee;
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "UNKNOW";
    }



    public static String maskcard(String szPara, String szCard) {
        Log.d(TAG, "maskcard = " + szCard+" szPara = "+szPara);
        String szOutPAN="";   //nnnn nnxx xxxx nnnnn   //4444555544445555 //nnnn nnxx xxxx nnnnnnn
        Integer cardpos=0;
        try {
/*
            for (int i = 0; i < szPara.length(); i++) {
                if (szCard.substring(cardpos, cardpos + 1).equalsIgnoreCase(null))
                    break;

                if (szPara.substring(i, i + 1).equalsIgnoreCase(" "))
                    szOutPAN = szOutPAN + szPara.substring(i, i + 1);
                else if (szPara.substring(i, i + 1).equalsIgnoreCase("N")) {
                    szOutPAN = szOutPAN + szCard.substring(cardpos, cardpos + 1);
                    cardpos++;
                } else {
                    szOutPAN = szOutPAN + szPara.substring(i, i + 1);
                    cardpos++;
                }
               // Log.d(TAG, "for szOutPAN = " + String.valueOf(i) + " " + szOutPAN);
            }
*/
            //NNNN NNXX XXXX XXXNNNN

            for (int i = 0; i < szPara.length(); i++)
            {
                if (szCard.substring(cardpos, cardpos + 1).equalsIgnoreCase(null))
                    break;

                if (szPara.substring(i, i + 1).equalsIgnoreCase(" "))
                    szOutPAN = szOutPAN + szPara.substring(i, i + 1);
                else if (szPara.substring(i, i + 1).equalsIgnoreCase("N")) {
                    szOutPAN = szOutPAN + szCard.substring(cardpos, cardpos + 1);
                    cardpos++;
                } else {

                    if(cardpos<(szCard.length()-4))
                        szOutPAN = szOutPAN + szPara.substring(i, i + 1);
                    else
                        szOutPAN = szOutPAN + szCard.substring(cardpos, cardpos + 1);

                    cardpos++;

                }
            }

            //xxxx4444


        }catch (Exception e)
        {
        }
        Log.d(TAG + "utility:: ", "maskcard = " + szOutPAN);
        return szOutPAN;
    }

    // Paul_20190314 last 4 digts after 1111222233334444555 = 1111-2222-3XXX-XXX4-555
    // Paul_20190320 first 6 digts last 4 digts after 1111222233334444555 = 1111-22XX-XXXX-XXX4-555
    public static String maskviewcard(String szPara,String szCard) {
        String szOutPAN="";
        Integer cardpos=0;
        String PadXXXXXX="";

        if(szCard.length() < 11)
        {
            return szCard;
        }
        System.out.printf("utility:: %s maskviewcard szCard = %s\n",TAG,szCard);
        cardpos = szCard.length()-4;
        String cutCardStart = szCard.substring(0, 6);
        System.out.printf("utility:: %s maskviewcard cutCardStart = %s\n",TAG,cutCardStart);
        String cutCardEnd = szCard.substring(cardpos, cardpos+4);
        System.out.printf("utility:: %s maskviewcard cutCardEnd = %s\n",TAG,cutCardEnd);
        for(int i=6;i<cardpos;i++)
        {
            PadXXXXXX = PadXXXXXX + "X";
        }
        System.out.printf("utility:: %s PadXXXXXX = %s\n",TAG,PadXXXXXX);
//        if(cutCardStart.length() > PadXXXXXX.length())
//        {
//            cutCardStart = cutCardStart.substring( 0, cutCardStart.length()-PadXXXXXX.length());
//        }
//        else
//        {
//            cutCardStart  = "";
//        }
//        System.out.printf("utility:: %s cutCardStart = %s\n",TAG,cutCardStart);
//        System.out.printf("utility:: %s cutCardEnd = %s\n",TAG,cutCardEnd);
        cutCardStart = cutCardStart + PadXXXXXX + cutCardEnd;
        System.out.printf("utility:: %s cutCardStart = %s\n",TAG,cutCardStart);
        for(int i=0;i<cutCardStart.length();i++)
        {
            int j = i % 4;
            if((i != 0) && (j == 0))
            {
                szOutPAN = szOutPAN + szPara;
            }
            szOutPAN = szOutPAN + cutCardStart.substring( i,i+1 );
        }
        System.out.printf("utility:: %s maskviewcard szOutPAN = %s\n",TAG,szOutPAN);
        return szOutPAN;
    }

    public static String maskexpire(String szPara, String szCard) {
        String szOutPAN="";   //xxxx
        Integer cardpos=0;
        for (int i = 0; i < szPara.length(); i++) {
            if (szPara.substring(i, i + 1).equalsIgnoreCase(" "))
                szOutPAN = szOutPAN + szPara.substring(i, i + 1);
            else if (szPara.substring(i, i + 1).equalsIgnoreCase("N")) {
                szOutPAN = szOutPAN + szCard.substring(cardpos, cardpos + 1);
                cardpos++;
            }
            else {
                szOutPAN = szOutPAN + szPara.substring(i, i + 1);
                cardpos++;
            }
        }
        Log.d(TAG, "maskexpire = " + szOutPAN);
        return szOutPAN;

    }


    //SINN 20181112 CARD BLOCK
    public static String getJsonTypeCARD_block(String cardNo) {
        String szCardNO;
        File file = new File("sdcard/print_param.json");
        String getDirectoryPath = String.valueOf(file.length());
        FileInputStream stream = null;

        Log.d(TAG, "getJsonTypeCARD_block:" + cardNo);

        try {
            String jString = null;
            stream = new FileInputStream(file);
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            /* Instead of using default, pass in a decoder. */
            jString = Charset.defaultCharset().decode(bb).toString();
//            Log.d(TAG, "onCreate: " + jString);
            try {
                JSONObject jsonObject = new JSONObject(jString);
                String param = jsonObject.getString("param");
                JSONArray jArray = jsonObject.getJSONArray("cardbacklist");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject object3 = jArray.getJSONObject(i);
                    String cardName = object3.getString("cardName");
//                    String pinReq = object3.getString("pinReq");
                    String cardRange = object3.getString("cardRange");

                    Log.d(TAG + "utility:: ", "cardName:" + cardName);
//                    Log.d(TAG, "pinReq:" + pinReq);
                    Log.d(TAG + "utility:: ", "cardRange:" + cardRange);

                    // Paul_20190313
                    if(cardNo.length() >= cardRange.length()) {
                        szCardNO = cardNo.substring(0, cardRange.length());
                    } else {
                        szCardNO = cardNo;
                    }
                    Log.d(TAG + "utility:: ", "szCardNO:" + szCardNO + " cardRange:" + cardRange);

                    if (cardRange.equalsIgnoreCase(szCardNO)) {
//                        Log.d(TAG, " return pinReq:" + pinReq);
                        return "Reject";
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "NoReject";
    }
    //SINN 20181112 CARD BLOCK

}
















