package org.centerm.land.helper;

import android.content.Context;
import android.util.Log;

import org.centerm.land.utility.Preference;

import java.math.BigInteger;

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
        if (cardSubstring.equals("9310061") || cardSubstring.equals("6210948")) {
            Log.d(TAG, "getTypeCard: EPS");
            return "EPS";
        }
        Log.d(TAG, "getTypeCard: POS");
        return "POS";
    }

    public static String getInvoice(Context context, String typeCard) {
        String invoiceNumber;
        if (typeCard.equalsIgnoreCase("POS")) {
            invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_POS);
        } else if (typeCard.equalsIgnoreCase("TMS")) {
            invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_TMS);
        } else {
            invoiceNumber = Preference.getInstance(context).getValueString(Preference.KEY_INVOICE_NUMBER_EPS);
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
        }
        Log.d(TAG, "geTraceId traceIdNo: " + traceIdNo);
        return traceIdNo;
    }

    // Paul_20180523
    public static String getNii(String cardNo,Context context) {
        String cardSubstring;
        cardSubstring = cardNo.substring(0, 4);
        if (cardSubstring.equals("0060")) {
            Log.d(TAG, "getTypeCard: TMS");
            return        Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);    // Paul_20180523
//            return "0246";
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
            Log.d(TAG, "getTypeCard: EPS");
            return        Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);    // Paul_20180523
//            return "0246";
        }

        cardSubstring = cardNo.substring(0, 8);
        if (cardSubstring.equals("50436709")) {
            Log.d(TAG, "getTypeCard: TMS");
            return        Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);    // Paul_20180523
//            return "0246";
        }

        cardSubstring = cardNo.substring(0, 6);
        if (cardSubstring.equals("504367") || cardSubstring.equals("990006")) {
            Log.d(TAG, "getTypeCard: TMS");
            return        Preference.getInstance(context).getValueString(Preference.KEY_NII_TMS);    // Paul_20180523
//            return "0246";
        }

        cardSubstring = cardNo.substring(0, 7);
        if (cardSubstring.equals("9310061")) {
            Log.d(TAG, "getTypeCard: EPS");
            return        Preference.getInstance(context).getValueString(Preference.KEY_NII_EPS);    // Paul_20180523
//            return "0242";
        }
        Log.d(TAG, "getTypeCard: POS");
        return        Preference.getInstance(context).getValueString(Preference.KEY_NII_POS);    // Paul_20180523
//        return "0246";
    }

    public static String getBatch(Context context, String typeCard) {
        String batchNumber = null;
        if (typeCard.equalsIgnoreCase("POS")) {
            batchNumber = Preference.getInstance(context).getValueString(Preference.KEY_BATCH_NUMBER_POS);
        } else if (typeCard.equalsIgnoreCase("EPS")) {
            batchNumber = Preference.getInstance(context).getValueString(Preference.KEY_BATCH_NUMBER_EPS);
        } else if (typeCard.equalsIgnoreCase("TMS")) {
            batchNumber  = Preference.getInstance(context).getValueString(Preference.KEY_BATCH_NUMBER_TMS);
        }
        Log.d(TAG, "getBatch batchNumber: " + batchNumber);
        return batchNumber;
    }

    public static String getTPDU(Context context, String typeCard) {
        String TPDU = "";
        if (typeCard.equalsIgnoreCase("POS")) {
            TPDU = Preference.getInstance(context).getValueString(Preference.KEY_TPDU_POS);
        }else if (typeCard.equalsIgnoreCase("EPS")) {
            TPDU = Preference.getInstance(context).getValueString(Preference.KEY_TPDU_EPS);
        } else if (typeCard.equalsIgnoreCase("TMS")) {
            TPDU = Preference.getInstance(context).getValueString(Preference.KEY_TPDU_TMS);
        }
        return TPDU;
    }

    public static String calLen(String l,int number) {
        String no = "";
        for (int i = l.length(); i < number; i++) {
            no += "0";
        }
        Log.d(TAG, "calLen: " + no + l);
        return no + l;
    }
    public static String calSpenLen(String l,int number) {
        String no = "";
        for (int i = l.length(); i < number; i++) {
            no += " ";
        }
        Log.d(TAG, "calSpenLen: " + no + l);
        return l+no ;
    }

    public static String hexadecimalToInt(String panSn) {
        BigInteger value = new BigInteger(panSn != null ? panSn.substring(6, 8) : "1", 16);
        return calLen(String.valueOf(value.toString().length()),4);
    }
}
