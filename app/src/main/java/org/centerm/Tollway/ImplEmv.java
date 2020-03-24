package org.centerm.Tollway;

import android.content.Context;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;

import com.pax.jemv.clcommon.ACType;
import com.pax.jemv.clcommon.ByteArray;
import com.pax.jemv.clcommon.EMV_APPLIST;
import com.pax.jemv.clcommon.EMV_CAPK;
import com.pax.jemv.clcommon.OnlineResult;
import com.pax.jemv.clcommon.RetCode;
import com.pax.jemv.device.DeviceManager;
import com.pax.jemv.emv.api.EMVApi;
import com.pax.jemv.emv.api.EMVCallback;
import com.pax.jemv.emv.model.EmvEXTMParam;
import com.pax.jemv.emv.model.EmvMCKParam;
import com.pax.jemv.emv.model.EmvParam;

import org.centerm.Tollway.activity.ConsumeActivity;
import org.centerm.Tollway.core.ChangeFormat;
import org.centerm.Tollway.jemv.clssentrypoint.model.TransResult;
import org.centerm.Tollway.jemv.clssentrypoint.trans.ClssEntryPoint;
import org.centerm.Tollway.jemv.clssquickpass.trans.ClssQuickPass;
import org.centerm.Tollway.pax.AAction;
import org.centerm.Tollway.pax.ActionEnterPin;
import org.centerm.Tollway.pax.ActionResult;
import org.centerm.Tollway.pax.InputPwdDialog;
import org.centerm.Tollway.utility.FileParse;
import org.centerm.Tollway.utility.Preference;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.centerm.Tollway.core.ChangeFormat.bcd2Str;
import static org.centerm.Tollway.core.ChangeFormat.bytes2String;
import static org.centerm.Tollway.core.ChangeFormat.str2Bcd;
import static org.centerm.Tollway.core.TlvUtils.combine;

//import com.pax.tradepaypw.utils.Utils;

/**
 * Created by yanglj on 2017-06-07.
 */

public class ImplEmv {

    private static SparseArray<byte[]> tags = new SparseArray<>();
    private static final String TAG = "ImplEmv";
    private EMVCallback emvCallback;
    private static Context emvContext;
    private static ConditionVariable cv;
    private CardManager cardManager;

    public String amount;
    public long ulAmntAuth;
    public long ulAmntOther;
    public long ulTransNo;
    public byte ucTransType;
    InputPwdDialog pindialog;
    public boolean reversalflag;
    byte[] aucTransDate = new byte[4];
    byte[] aucTransTime = new byte[4];

    private EmvParam emvParam;
    private EmvMCKParam mckParam;

    private String selectedaid = "";
    private int selectedinedex = -1;
    private boolean selectedflag = false;
    private int transresult;



    public ImplEmv(Context context) {
        emvParam = new EmvParam();
        mckParam = new EmvMCKParam();
        mckParam.extmParam = new EmvEXTMParam();
        emvCallback = EMVCallback.getInstance();
        emvCallback.setCallbackListener(emvCallbackListener);
        emvContext = context;
        cardManager = CardManager.instance;

    }

    public void setdialog(InputPwdDialog pin) {
        this.pindialog = pin;
    }

    private int addCapkIntoEmvLib() {
        int ret;
        ByteArray dataList = new ByteArray();

        ret = EMVCallback.EMVGetTLVData((short) 0x4F, dataList);
        if (ret != 0) {
            ret = EMVCallback.EMVGetTLVData((short) 0x84, dataList);
        }
        if (ret == 0) {
            byte[] rid = new byte[5];
            System.arraycopy(dataList.data, 0, rid, 0, 5);
            ret = EMVCallback.EMVGetTLVData((short) 0x8F, dataList);
            if (ret == 0) {
                byte keyId = dataList.data[0];
                Log.i("log", "keyID=" + keyId);
                //for (EMV_CAPK capk : EmvTestCAPK.genCapks()) {
                for (EMV_CAPK capk : FileParse.getmEmvCapk()) {
                    if (bytes2String(capk.rID).equals(new String(rid))) {
                        //if (keyId < 0 || capk.keyID == keyId) {
                        if (keyId == -1 || capk.keyID == keyId) {
                            // ret = EMVCallback.EMVAddCAPK(capk);
                            EMV_CAPK emv_capk = new EMV_CAPK();
                            Log.i("log", "EMVAddCAPK rid=" + bcd2Str(capk.rID));
                            Log.i("log", "EMVAddCAPK keyID=" + capk.keyID);
                            Log.i("log", "EMVAddCAPK exponentLen=" + capk.exponentLen);
                            Log.i("log", "EMVAddCAPK hashInd=" + capk.hashInd);
                            Log.i("log", "EMVAddCAPK arithInd=" + capk.arithInd);
                            Log.i("log", "EMVAddCAPK modulLen=" + capk.modulLen);
                            Log.i("log", "EMVAddCAPK checkSum=" + bcd2Str(capk.checkSum));
                            emv_capk.rID = capk.rID;
                            emv_capk.keyID = capk.keyID;
                            emv_capk.hashInd = capk.hashInd;
                            emv_capk.arithInd = capk.arithInd;
                            emv_capk.modul = capk.modul;
                            emv_capk.modulLen = (short) capk.modulLen;
                            emv_capk.exponent = capk.exponent;
                            emv_capk.exponentLen = (byte) capk.exponentLen;
                            emv_capk.expDate = capk.expDate;
                            emv_capk.checkSum = capk.checkSum;
                            ret = EMVCallback.EMVAddCAPK(emv_capk);
                            Log.i("log", "EMVAddCAPK ret=" + ret);
                        }
                    }
                }
            }
        }
        return ret;
    }

    public int startContactEmvTrans() {
        int ret = EMVCallback.EMVCoreInit();
        if (ret != RetCode.EMV_OK) {
            return ret;
        }



        EMVCallback.EMVSetCallback();

        EMVCallback.EMVGetParameter(emvParam);
        //emvParam.capability = ChangeFormat.str2Bcd("E0E8C8"); //  UPI
        //emvParam.capability = ChangeFormat.str2Bcd("00B0C8"); //  MASTERCARD
        emvParam.capability = ChangeFormat.str2Bcd("E0F8C8"); //  VISA
        //emvParam.countryCode = Utils.str2Bcd("0840");
        emvParam.countryCode = ChangeFormat.str2Bcd("0764");
        emvParam.exCapability = ChangeFormat.str2Bcd("E000F0A001");
        emvParam.forceOnline = 0;
        emvParam.getDataPIN = ((byte) 1);
        //emvParam.merchCateCode = Utils.str2Bcd("0840");
        emvParam.merchCateCode = ChangeFormat.str2Bcd("0764");
        //emvParam.referCurrCode = Utils.str2Bcd("0840");
        emvParam.referCurrCode = ChangeFormat.str2Bcd("0764");
        emvParam.referCurrCon = 1000;
        emvParam.referCurrExp = (byte) 2;
        emvParam.surportPSESel = (byte) 1;
        //emvParam.terminalType = ((byte) 0x22);
        emvParam.terminalType = ((byte) 0x23);
        //emvParam.transCurrCode = Utils.str2Bcd("0840");
        emvParam.transCurrCode = ChangeFormat.str2Bcd("0764");
        emvParam.transCurrExp = (byte) 2;
        emvParam.transType = ucTransType;
        emvParam.termId = Preference.getInstance(emvContext).getValueString(Preference.KEY_TERMINAL_ID_EPS).getBytes();
        emvParam.merchId = Preference.getInstance(emvContext).getValueString(Preference.KEY_MERCHANT_ID_EPS).getBytes();
        emvParam.merchName = Preference.getInstance(emvContext).getValueString(Preference.KEY_MERCHANT_1).getBytes();

        EMVCallback.EMVSetParameter(emvParam);


        EMVCallback.EMVGetMCKParam(mckParam);
        mckParam.ucBypassPin = 1;
        mckParam.ucBatchCapture = 1;
        mckParam.extmParam.aucTermAIP = ChangeFormat.str2Bcd("0800");
        mckParam.extmParam.ucUseTermAIPFlg = 1;
        mckParam.extmParam.ucBypassAllFlg = 1;
        EMVCallback.EMVSetMCKParam(mckParam);

        EMVCallback.EMVSetPCIModeParam((byte) 1, "0,4,5,6,7,8,9,10,11,12".getBytes(), 1000 * 120);//Set no PCI mode. for input PIN


        EMVCallback.EMVDelAllApp();


        for (EMV_APPLIST i : FileParse.getEmv_applists()) {
            Log.d("kang2","EmvaApiAddApp/aid:" + bcd2Str(i.aid) + ",name:" + bcd2Str(i.appName));
            ret = EMVCallback.EMVAddApp(i);
            if (ret != RetCode.EMV_OK) {
                Log.i(TAG, "EMVAddApp");
                return ret;
            }
            //Log.i(TAG, "EMVAddApp " + Utils.bcd2Str(i.aid));
        }

        EMV_APPLIST test = new EMV_APPLIST();
        int index = 0;
        for (int i = 0; i < FileParse.getEmv_applists().length; ++i) {
            ret = EMVCallback.EMVGetApp(i, test);
            Log.i("kang2", "EmvApiGetApp/ " + i + "/api:" + bcd2Str(test.aid) + ",name:" + bcd2Str(test.appName));

            if (ret != RetCode.EMV_OK) {
                Log.i(TAG, "EMVGetApp err=" + ret);
                return ret;
            }
        }

        Log.d("kang","startcontactemvtrans");



        ret = EMVCallback.EMVAppSelect(0, 1);
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "EMVAppSelect");
            return ret;
        }
        Log.d("EMVAppSelect return",String.valueOf(ret));
        EMVCallback.EMVGetFinalAppPara(test);
        Log.d("kang","selected aid:" + bcd2Str(test.aid) + ", selected aid/floorlimit:" + test.floorLimit + ",selected aid/riskmandata:" + bcd2Str(test.riskManData));


    /*
       String type = bcd2Str(getTlv(0x57));
        switch (type.substring(4).charAt(0)) {
            case '3':
                type = "JCB";
                break;
            case '4':
                type = "VISA";
                break;
            case '5':
                type = "MASTER";
                break;
            case '6':
            case '8':
                type = "UNIONPAY";
                break;
            default:
                type = "";
        }

        Log.d("kang","cardType:" + type);


        EMVApi.EMVSetParameter(emvParam);
        EMVCallback.EMVGetParameter(emvParam);
        setEmvParam(type);
*/


        ret = EMVCallback.EMVReadAppData();
        Log.d("EMVReadAppData return",String.valueOf(ret));
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "EMVReadAppData");
            return ret;
        }

      emvCallback.setCallBackResult(0);




        for (EMV_CAPK i : FileParse.getmEmvCapk())
            EMVCallback.EMVDelCAPK(i.keyID, i.rID);

        addCapkIntoEmvLib();

        ret = EMVCallback.EMVCardAuth();
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "EMVCardAuth");
            return ret;
        }

        byte[] errCode = new byte[10];
        ret = EMVCallback.EMVGetDebugInfo(0, errCode);
        Log.d("EMVGetDebuginfo return",String.valueOf(ret));
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "EMVGetDebugInfo1 ret=" + ret);
            //return ret;
        } else {
            Log.i(TAG, "EMVGetDebugInfo1 ok .ret=" + ret);
        }




        Log.i("EmvApi", "before EMVStartTrans");
        ACType acType = new ACType();
        Log.i(TAG, "AcType 1=" + acType.type + "");
        Log.d("kang", "ulAmntAuth : " + ulAmntAuth);
        Log.d("kang","TVR:" + bcd2Str(getTlv(0x95)));
        Log.d("kang","9f14:" + bcd2Str(getTlv(0x9f14)));
        Log.d("kang","9f36:" + bcd2Str(getTlv(0x9f36)));
        Log.d("kang","9f13:" + bcd2Str(getTlv(0x9f13)));

/*
        int a = EMVApi.EMVSetTLVData((byte)0x8C, ChangeFormat.str2Bcd("9F02069F03069F1A0295055F2A029A039C019F37049F35019F5303"), str2Bcd("9F02069F03069F1A0295055F2A029A039C019F37049F35019F5303").length);
        int b = EMVApi.EMVSetTLVData((byte)0x8D, ChangeFormat.str2Bcd("8A0291109505"), str2Bcd("8A0291109505").length);
*/

        ret = EMVCallback.EMVStartTrans(ulAmntAuth, 0, acType);

        Log.i("EmvApi", "after EMVStartTrans/" + "acType:" + acType.type);
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "EMVStartTrans err = " + ret);
            return ret;
        }
        if (acType.type == ACType.AC_TC)
            return TransResult.EMV_OFFLINE_APPROVED;
        else if (acType.type == ACType.AC_AAC)
            return TransResult.EMV_OFFLINE_DENIED;
        else if (acType.type == ACType.AC_ARQC)
            return TransResult.EMV_ARQC;



//        if(acType.type == ACType.AC_TC)
//        {
//            return RetCode.EMV_OK;
//        }
//        else if(acType.type == ACType.AC_AAC)
//        {
//            return RetCode.EMV_DENIAL;
//        }

//        String authCode = "123456";
//        EMVCallback.EMVSetTLVData((short) 0x89, authCode.getBytes(), 6);
//        EMVCallback.EMVSetTLVData((short) 0x8A, "00".getBytes(), 2);


//        onlineProc();
//
//        byte[] script = Utils.str2Bcd("9F1804AABBCCDD86098424000004AABBCCDD86098418000004AABBCCDD86098416000004AABBCCDD");
//        int rspResult = OnlineResult.ONLINE_APPROVE;
//        ret = EMVCallback.EMVCompleteTrans(rspResult, script, script.length, acType);
//        Log.i(TAG, "EMVCompleteTrans");
//        ByteArray dataList = new ByteArray(5);
//        EMVCallback.EMVGetTLVData((short) 0x95, dataList);
//        Log.i("EMVGetTLVData TVR 0x95", Utils.bcd2Str(dataList.data, 5) + "");
//        EMVCallback.EMVGetTLVData((short) 0x9B, dataList);
//        Log.i("EMVGetTLVData TVR 0x95", Utils.bcd2Str(dataList.data, 2) + "");
//
//        Log.i("AcType", acType.type + "");
//        if(acType.type == ACType.AC_TC)
//        {
//            return RetCode.EMV_OK;
//        }
//        if(acType.type == ACType.AC_AAC)
//        {
//            return RetCode.EMV_DENIAL;
//        }

        Log.d("kang","end startcontactemvtrans");
        return ret;
    }

    public int CompleteContactEmvTrans(String[] mBlockDataReceived) {
        int ret;


        String authCode = "123456";
        ByteArray byteArray = new ByteArray();
        Log.d("kang","getTlv8a:" + bcd2Str(getTlv(0x8a)) + ",4f:" + bcd2Str(getTlv(0x4f)));
        //EMVCallback.EMVGetTLVData((short) 0x8A, byteArray);
        Log.d("kang","39 data :" + mBlockDataReceived[39 - 1]);

        Log.d("kang","BEFORE set8A:" + bcd2Str(getTlv(0x8a)));
        EMVCallback.EMVSetTLVData((short) 0x89, authCode.getBytes(), 6);
        int j;

        j = EMVCallback.EMVSetTLVData((short) 0x8A, mBlockDataReceived[39 -1].replace("3","").getBytes(), 2);
        Log.d("kang","set8a:" + j);
        byteArray = new ByteArray();
        //EMVCallback.EMVGetTLVData((short) 0x8A, byteArray);
        Log.d("kang","AFTER set8A:"  + bcd2Str(getTlv(0x8a)));

        byte[] script = {};
        for(int i = 0; i<mBlockDataReceived.length; i++) {
            System.out.println("kang/completecontactemvtrans/" + (i + 1) + ":" + mBlockDataReceived[i]);
        }
        if(!mBlockDataReceived[55 - 1].isEmpty()) {
            System.out.printf("utility:: %s  CompleteContactEmvTrans mBlockDataReceived[55 -1] = %s\n",TAG,mBlockDataReceived[55 -1].substring(4));

            int num = Integer.parseInt(mBlockDataReceived[55 - 1].substring(6,8), 16);
            byteArray = new ByteArray();
            EMVApi.EMVGetTLVData((short) 0x91, byteArray);
            Log.d("kang","before set 91 data:" + bcd2Str(byteArray.data));
            String data91 = mBlockDataReceived[55 - 1].substring(8, 8 + (num * 2));
            j = EMVCallback.EMVSetTLVData((short) 0x91, str2Bcd(data91), num);
            byteArray = new ByteArray();
            EMVApi.EMVGetTLVData((short) 0x91, byteArray);
            Log.d("kang","after set 91/j:" + j + ",data: " + bcd2Str(byteArray.data));

//        byte[] script = ChangeFormat.str2Bcd("9F1804AABBCCDD86098424000004AABBCCDD86098418000004AABBCCDD86098416000004AABBCCDD");
            script = ChangeFormat.str2Bcd(mBlockDataReceived[55 - 1].substring(8 + (num * 2)));
        }
        Log.d("kang","script:" + bcd2Str(script));

        int rspResult ;
        if(mBlockDataReceived[39 - 1].replace("3","").equals("00")) {
            rspResult = OnlineResult.ONLINE_APPROVE;
        }
        else {
            rspResult = OnlineResult.ONLINE_FAILED;
        }
        //rspResult = OnlineResult.ONLINE_APPROVE;

        ACType acType = new ACType();
        ret = EMVCallback.EMVCompleteTrans(rspResult, script, script.length, acType);
        Log.d("kang","after emvcompletetrans/actype:" + acType.type);
        Log.i(TAG, "EMVCompleteTrans");
        if (ret != RetCode.EMV_OK) {
            if(ret == RetCode.EMV_DENIAL && rspResult == OnlineResult.ONLINE_APPROVE) {
                reversalflag = false;
            }
            Log.i(TAG, "EMVCompleteTrans err = " + ret);
            return ret;
        }
/*        byteArray = new ByteArray();
        ret = EMVApi.EMVGetScriptResult(byteArray);
        if(ret != RetCode.EMV_OK) {
            Log.d("kang","emvgetscriptresult error/ret:" + ret);
            return ret;
        }*/
        ByteArray dataList = new ByteArray(5);
        EMVCallback.EMVGetTLVData((short) 0x95, dataList);
        Log.i("EMVGetTLVData TVR 0x95", bcd2Str(dataList.data, 5) + "");
        EMVCallback.EMVGetTLVData((short) 0x9B, dataList);
        Log.i("EMVGetTLVData TVR 0x9B", bcd2Str(dataList.data, 2) + "");
        //acType.type = ACType.AC_TC;  //this is for demo only;
        Log.i("AcType", acType.type + "");


        if (acType.type == ACType.AC_TC) {
            transresult = TransResult.EMV_ONLINE_APPROVED;
            return TransResult.EMV_ONLINE_APPROVED;
        }

        else if (acType.type == ACType.AC_AAC) {
            if (rspResult == OnlineResult.ONLINE_APPROVE) {
                transresult = TransResult.EMV_ONLINE_CARD_DENIED;
                return TransResult.EMV_ONLINE_CARD_DENIED;
            }
            else {
                transresult = TransResult.EMV_ONLINE_DENIED;
                return TransResult.EMV_ONLINE_DENIED;
            }
        }

        return ret;
    }

    public int getTransresult() {
        return transresult;
    }

    public void emvsetamount(double amt) {
        this.amount = Double.toString(amt);
    }


    private EMVCallback.EmvCallbackListener emvCallbackListener = new EMVCallback.EmvCallbackListener() {
        private boolean selectedflag = false;
        @Override
        public void emvWaitAppSel(int tryCnt, final EMV_APPLIST[] list, int appNum) {
            Log.d("kang", "emvwaitappsel/selectedflag:" + this.selectedflag);
            Log.i(TAG, "emvWaitAppSel : need to call app select page");
            List<EMV_APPLIST> lis = Arrays.asList(list);
            for (EMV_APPLIST i : lis) {
                Log.i(TAG, "AID :" + bcd2Str(i.aid) + ",appname:" + bcd2Str(i.appName));
                if(selectedaid.equals(bcd2Str(i.aid))) {
                    selectedinedex = lis.indexOf(i);
                    Log.d("kang","selectedindex:" + selectedinedex );
                    break;
                }
            }
            if(lis.size() == 1) {
                selectedinedex = 0;
                selectedaid = bcd2Str(lis.get(0).aid);
                this.selectedflag = true;
                emvCallback.setCallBackResult(selectedinedex);
                return;
            }
            if (cardManager != null && cardManager.getCardHelperListener() != null && this.selectedflag == false) {

                cardManager.getCardHelperListener().onMultiApp(appNum, list);
                this.selectedflag = true;
            }

            Log.d("kang","selectedAid:" + selectedaid + ",selectedindex:" + selectedinedex);
            if(!selectedaid.equals("") && selectedinedex != -1) {
                Log.d("kang","selectedAid:" + selectedaid + ",selectedindex:" + selectedinedex);
                emvCallback.setCallBackResult(selectedinedex); //force to select the first app
            }
            else {
                //emvCallback.setCallBackResult(RetCode.EMV_DENIAL);
            }
            //cardManager.startEmvTrans(null, bcd2Str(lis.get(selectedinedex).aid));
        }

        @Override
        public void emvInputAmount(long[] amt) {
            amt[0] = ulAmntAuth; //dummy
            //if (amt[1] != null)
            //amt[1] = 0;
            Log.i(TAG, "emvInputAmount : need to call input amount page");
            emvCallback.setCallBackResult(RetCode.EMV_OK);
        }

        @Override
        public void emvGetHolderPwd(int tryFlag, int remainCnt, byte[] pin) {
            if (pin == null) {
                Log.i("log", "emvGetHolderPwd pin is null, tryFlag=" + tryFlag + " remainCnt:" + remainCnt);
            } else {
                Log.i("log", "emvGetHolderPwd pin is not null, tryFlag=" + tryFlag + " remainCnt:" + remainCnt);            }

            int result = 0;
            int ret = 0;

            if (pin != null && pin[0] != 0) {
                ret = pin[0];
                Log.i("log", "emvGetHolderPwd ret=" + ret);
            } else {
                enterPin(pin == null, remainCnt);


                ret = GetPinEmv.getInstance().GetPinResult();
                Log.i("log", "GetPinEmv GetPinResult = " + ret);
            }
            if (ret == EEmvExceptions.EMV_OK.getErrCodeFromBasement()) {
                result = RetCode.EMV_OK;
                Log.i("log", "GetPinEmv result = EMV_OK");
            } else if (ret == EEmvExceptions.EMV_ERR_TIME_OUT.getErrCodeFromBasement()) {
                result = RetCode.EMV_TIME_OUT;
                Log.i("log", "GetPinEmv result = EMV_ERR_TIME_OUT");
            } else if (ret == EEmvExceptions.EMV_ERR_USER_CANCEL.getErrCodeFromBasement()) {
                result = RetCode.EMV_USER_CANCEL;
                Log.i("log", "GetPinEmv result = EMV_USER_CANCEL");
            } else if (ret == EEmvExceptions.EMV_ERR_NO_PASSWORD.getErrCodeFromBasement()) {
                result = RetCode.EMV_NO_PASSWORD;
                Log.i("log", "GetPinEmv result = EMV_NO_PASSWORD");
            } else {
                result = RetCode.EMV_NO_PINPAD;
                Log.i("log", "GetPinEmv result = EMV_USER_CANCEL");
            }

            emvCallback.setCallBackResult(result);
        }

        @Override
        public void emvAdviceProc() {
            Log.i(TAG, "emvAdviceProc");

        }

        @Override
        public void emvVerifyPINOK() {
            Log.i(TAG, "emvVerifyPINOK");
            if(cardManager != null) {

                cardManager.inputofflinePIN("TEMP");
            }
        }

        @Override
        public int emvVerifyPINfailed(byte[] var1) {
            Log.i(TAG, "emvVerifyPINfailed ret = " + var1);
            return 0;
        }




        @Override
        public int emvUnknowTLVData(short tag, ByteArray data) {
            //Log.i(TAG, "emvUnknowTLVData tag: "+ Integer.toHexString(tag) + " data:" + data.data.length);
            switch ((int) tag) {
                case 0x9A:
                    //String date = TradeApplication.dal.getSys().getDate();
                    //System.arraycopy(Utils.str2Bcd(date.substring(2, 8)), 0, data.data, 0, data.data.length);
                    byte[] date = new byte[7];
                    DeviceManager.getInstance().getTime(date);
                    System.arraycopy(date, 1, data.data, 0, 3);
                    break;
                case 0x9F1E:
                    //String sn = TradeApplication.dal.getSys().getTermInfo().get(ETermInfoKey.SN);
                    //System.arraycopy(sn.getBytes(), 0, data.data, 0, data.data.length);
                    byte[] sn = new byte[10];
                    DeviceManager.getInstance().readSN(sn);
                    System.arraycopy(sn, 0, data.data, 0, Math.min(data.data.length, sn.length));
                    break;
                case 0x9F21:
                    //String time = TradeApplication.dal.getSys().getDate();
                    byte[] time = new byte[7];
                    DeviceManager.getInstance().getTime(time);
                    //System.arraycopy(time, 3, data.data, 0, 3);
                    System.arraycopy(time, 4, data.data, 0, 3);
                    break;
                case 0x9F37:
                    //byte[] random = TradeApplication.dal.getSys().getRandom(data.data.length);
                    //System.arraycopy(random, 0, data.data, 0, data.data.length);
                    byte[] random = new byte[4];
                    DeviceManager.getInstance().getRand(random, 4);
                    System.arraycopy(random, 0, data.data, 0, data.data.length);
                    break;
                case 0xFF01:
                    Arrays.fill(data.data, (byte) 0x00);
                    break;
                default:
                    return RetCode.EMV_PARAM_ERR;
            }
            data.length = data.data.length;
            Log.i(TAG, "emvUnknowTLVData tag: " + Integer.toHexString(tag));
            Log.i(TAG, "emvUnknowTLVData data: " + bcd2Str(data.data) + " length:" + data.data.length);
            return RetCode.EMV_OK;
        }

        @Override
        public void certVerify() {
            Log.i(TAG, "certVerify");
            emvCallback.setCallBackResult(RetCode.EMV_OK);
        }

        @Override
        public int emvSetParam() {
            Log.i(TAG, "emvSetParam");
            return RetCode.EMV_OK;
        }

        @Override
        public int cRFU2() {
            return 0;
        }
    };

    public void enterPin(boolean isOnlinePin, int offlinePinLeftTimes) {
        final boolean onlinePin = isOnlinePin;


        if(pindialog != null) {

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    pindialog.show();
                }
            });

        }
        Log.i("log", "enterPin offlinePinLeftTimes=" + Integer.toHexString(offlinePinLeftTimes));
        cv = new ConditionVariable();
        final String totalAmount = amount;
        final String leftTimes = Integer.toString(offlinePinLeftTimes);

        //showEnterPin(context,pan,isOnlinePin, offlinePinLeftTimes);

        byte[] track2 = getTlv(0x57);
        String strTrack2 = MainApplication.getConvert().bcdToStr(track2).split("F")[0];
        //strTrack2 = strTrack2.split("F")[0];
        String Pan = strTrack2.split("D")[0];
        ActionEnterPin actionEnterPin = new ActionEnterPin(new AAction.ActionStartListener() {

            byte[] track2 = getTlv(0x57);
            String strTrack2 = MainApplication.getConvert().bcdToStr(track2).split("F")[0];
            //strTrack2 = strTrack2.split("F")[0];
            String Pan = strTrack2.split("D")[0];


            @Override
            public void onStart(AAction action) {
                ((ActionEnterPin) action).setParam(emvContext, Pan, onlinePin, totalAmount, leftTimes);
            }

        });
        actionEnterPin.setEndListener(new AAction.ActionEndListener() {
            @Override
            public void onEnd(AAction action, ActionResult result) {
                //InputPwdDialog.OnDismissListener();
                Log.i(TAG, "setEndListener OnEnd");
                //ActivityStack.getInstance().popTo((Activity) context);
                ConsumeActivity.getInstance().onStop();
            }
        });
        Log.d("kang","actionenterpin.excute before");
        actionEnterPin.execute();
        //cv.open();
        cv.block(); // for the Offline pin case, block it for make sure the PIN activity is ready, otherwise, may get the black screen.
    }


    public static byte[] getTlv(int tag) {
        ByteArray byteArray = new ByteArray();
        ByteArrayOutputStream bo = new ByteArrayOutputStream();

        if (EMVCallback.EMVGetTLVData((short) tag, byteArray) == RetCode.EMV_OK) {
            byte[] data = Arrays.copyOfRange(byteArray.data, 0, byteArray.length);
            Log.d("getTlv",bcd2Str(data));
            tags.put(tag, data);
            Log.i("asd", Integer.toHexString(tag) + ":" + data.length);
            try {
                bo.write(intToByteArray(tag));
                bo.write(intToByteArray(byteArray.length));
                bo.write(data);
            } catch(Exception e) {
                e.printStackTrace();
            }

            return bo.toByteArray();
        }
        return tags.get(tag, null);
    }



    public static byte[] getTlv2() {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ByteArray byteArray = new ByteArray();
        int s = 0;
        //System.out.println("tag length : " + tag.size());

        for(int i = 0; i < getF55Taglist_int().size(); i++) {
            int t = getF55Taglist_int().get(i);
            System.out.println("tag = " + t);
            if (EMVCallback.EMVGetTLVData((short) t, byteArray) == RetCode.EMV_OK) {
                byte[] data = Arrays.copyOfRange(byteArray.data, 0, byteArray.length);
                System.out.println("byteArray length : " + byteArray.length);
                System.out.println("data length : " + data.length);
                try {
                    bo.write(intToByteArray(t));
                    bo.write(intToByteArray(byteArray.length));
                    bo.write(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("before s : " + s + ", after s : " + (s + data.length));
                s = s + data.length;
            }
            else if (EMVCallback.EMVGetTLVData((short) t, byteArray) == RetCode.EMV_NO_DATA) {
                System.out.println("emv no data, tag : " + Integer.toHexString(t) + ", byteArray :" + byteArray.toString());
                try {
                    switch(t) {
                        case 40707:
                            bo.write(intToByteArray(t));
                            bo.write(intToByteArray(6));
                            byte[] dummy = new byte[6];
                            for(int j = 0; j < 6; j++) {
                                dummy[j] = 0;
                            }
                            bo.write(dummy);
                            break;
                        default:
                            break;
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return bo.toByteArray();
    }

    public static void pinEnterReady() {
        if (cv != null)
            cv.open();
    }

    public int startClssPBOC(TransResult transResult) {

        transResult.result = TransResult.EMV_ABORT_TERMINATED;

        int ret = EMVCallback.EMVCoreInit();
        if (ret != RetCode.EMV_OK) {
            return ret;
        }
        EMVCallback.EMVSetCallback();

        EMVCallback.EMVGetParameter(emvParam);
        ClssQuickPass clssQuickPass = ClssQuickPass.getInstance();
        emvParam.capability = clssQuickPass.getClss_ReaderParam().aucTmCap;
        Log.i(TAG, " emvParam.countryCode = " + bcd2Str(emvParam.capability));
        emvParam.countryCode = clssQuickPass.getClss_ReaderParam().aucTmCntrCode;
        Log.i(TAG, " emvParam.countryCode = " + bcd2Str(emvParam.countryCode));
        emvParam.exCapability = clssQuickPass.getClss_ReaderParam().aucTmCapAd;
        emvParam.forceOnline = 0;
        emvParam.getDataPIN = ((byte) 1);
        emvParam.merchCateCode = clssQuickPass.getClss_ReaderParam().aucMerchCatCode;
        emvParam.referCurrCode = clssQuickPass.getClss_ReaderParam().aucTmRefCurCode;
        emvParam.referCurrCon = clssQuickPass.getClss_ReaderParam().ulReferCurrCon;
        emvParam.referCurrExp = clssQuickPass.getClss_ReaderParam().ucTmRefCurExp;
        emvParam.surportPSESel = (byte) 1;
        emvParam.terminalType = clssQuickPass.getClss_ReaderParam().ucTmType;
        emvParam.transCurrCode = clssQuickPass.getClss_ReaderParam().aucTmTransCur;
        Log.i(TAG, " emvParam.transCurrCode = " + bcd2Str(emvParam.transCurrCode));
        emvParam.transCurrExp = clssQuickPass.getClss_ReaderParam().ucTmTransCurExp;
        emvParam.transType = clssQuickPass.getClss_ReaderParam().ucTmType;
        emvParam.termId = clssQuickPass.getClss_ReaderParam().acquierId;
        emvParam.merchId = clssQuickPass.getClss_ReaderParam().aucMerchantID;
        emvParam.merchName = clssQuickPass.getClss_ReaderParam().aucMchNameLoc;

        EMVCallback.EMVSetParameter(emvParam);


        EMVCallback.EMVGetMCKParam(mckParam);
        mckParam.ucBypassPin = 1;
        mckParam.ucBatchCapture = 1;
        mckParam.extmParam.aucTermAIP = ChangeFormat.str2Bcd("0800");
        mckParam.extmParam.ucUseTermAIPFlg = 1;
        mckParam.extmParam.ucBypassAllFlg = 1;
        EMVCallback.EMVSetMCKParam(mckParam);

        EMVCallback.EMVSetPCIModeParam((byte) 1, "0,4,5,6,7,8,9,10,11,12".getBytes(), 1000 * 120);//Set no PCI mode. for input PIN

        EMVCallback.EMVDelAllApp();
        for (EMV_APPLIST i : FileParse.getEmv_applists()) {
            ret = EMVCallback.EMVAddApp(i);
            if (ret != RetCode.EMV_OK) {
                Log.i(TAG, "EMVAddApp");
                return ret;
            }
            //Log.i(TAG, "EMVAddApp " + Utils.bcd2Str(i.aid));
        }

        EMV_APPLIST test = new EMV_APPLIST();
        for (int i = 0; i < FileParse.getEmv_applists().length; ++i) {
            ret = EMVCallback.EMVGetApp(i, test);
            Log.i(TAG, "EmvApiGetApp " + bcd2Str(test.aid));
            if (ret != RetCode.EMV_OK) {
                Log.i(TAG, "EMVGetApp err=" + ret);
                return ret;
            }
        }

        EMVCallback.EMVInitTLVData();
        ClssEntryPoint entryPoint = ClssEntryPoint.getInstance();
        ByteArray gpoData = new ByteArray();
        ret = clssQuickPass.getClss_GPOData(gpoData);
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "getClss_GPOData err=" + ret);
            return ret;
        }
        ret = EMVCallback.EMVSwitchClss(entryPoint.getTransParam(), entryPoint.getOutParam().sDataOut, entryPoint.getOutParam().iDataLen, gpoData.data, gpoData.length);
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "EMVSwitchClss err = " + ret);
            return ret;
        }

        ret = EMVCallback.EMVReadAppData();
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "EMVReadAppData err = " + ret);
            return ret;
        }

        for (EMV_CAPK i : EmvTestCAPK.genCapks())
            EMVCallback.EMVDelCAPK(i.keyID, i.rID);

        addCapkIntoEmvLib();

        ret = EMVCallback.EMVCardAuth();
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "EMVCardAuth");
            return ret;
        }

        byte[] errCode = new byte[10];
        ret = EMVCallback.EMVGetDebugInfo(0, errCode);
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "EMVGetDebugInfo1 ret=" + ret);
            //return ret;
        } else {
            Log.i(TAG, "EMVGetDebugInfo1 ok .ret=" + ret);
        }


        Log.i("EmvApi", "before startclsspboc");
        ACType acType = new ACType();
        Log.i(TAG, "AcType 1=" + acType.type + "");
        ulAmntAuth = entryPoint.getTransParam().ulAmntAuth;
        EMV_APPLIST app = new EMV_APPLIST();
        EMVApi.EMVGetFinalAppPara(app);

        Log.i(TAG, "ulAmntAuth = " + Long.toString(ulAmntAuth));

        ret = EMVCallback.EMVStartTrans(ulAmntAuth, 0, acType);
        Log.i("EmvApi", "after EMVStartTrans");
        if (ret != RetCode.EMV_OK) {
            Log.i(TAG, "EMVStartTrans err = " + ret);
            return ret;
        }
        Log.i(TAG, "AcType =" + acType.type + "");
        if (acType.type == ACType.AC_TC)
            transResult.result = TransResult.EMV_OFFLINE_APPROVED;
        else if (acType.type == ACType.AC_AAC)
            transResult.result = TransResult.EMV_OFFLINE_DENIED;
        else if (acType.type == ACType.AC_ARQC)
            transResult.result = TransResult.EMV_ARQC;
        return ret;
    }

    public static byte[] getF55Taglist() {
        try {
            ByteArrayOutputStream ous = new ByteArrayOutputStream();
            ous.write(combine(159, 38));
            ous.write(combine(159, 39));
            ous.write(combine(159, 16));
            ous.write(combine(159, 55));
            ous.write(combine(159, 54));
            ous.write(combine(149));
            ous.write(combine(154));
            ous.write(combine(156));
            ous.write(combine(159, 2));
            ous.write(combine(95, 42));
            ous.write(combine(130));
            ous.write(combine(159, 26));
            ous.write(combine(159, 3));
            ous.write(combine(159, 51));
            ous.write(combine(159, 52));
            ous.write(combine(159, 53));
            ous.write(combine(159, 30));
            ous.write(combine(132));
            ous.write(combine(159, 9));
            ous.write(combine(159, 65));
            ous.write(combine(159, 99));
            ous.write(combine(223, 50));
            ous.write(combine(223, 51));
            ous.write(combine(223, 52));
            ous.write(combine(79));
            ous.write(combine(223, 49));
            ous.write(combine(138));
            ous.write(combine(95, 40));
            ous.write(combine(159, 116));
            ous.write(combine(155));
            ous.write(combine(80));
            ous.write(combine(159, 18));
            ous.write(combine(159, 78));
            ous.write(combine(159, 123));
            ous.write(combine(159, 119));
            ous.write(combine(143));
            ous.write(combine(90));
            ous.write(combine(95, 52));
            ous.write(combine(87));
            ous.write(combine(145));
            ous.write(combine(113));
            ous.write(combine(114));
            return ous.toByteArray();
        } catch (Exception var1) {
            return null;
        }
    }
    public static ArrayList<Integer> getF55Taglist_int() {
        ArrayList<String> arr = new ArrayList<String>();
        arr.add("82");      // Source from ICC
        arr.add("84");      // Source from ICC ;UL Recommended
        arr.add("95");      // Source from Terminal
        arr.add("9A");      // Source from Terminal
        arr.add("9C");      // Source from Terminal
        arr.add("5F2A");    // Currency code
        arr.add("5F30");    // Currency code
        arr.add("5F34");    // Source from ICC
        arr.add("9F02");    // Source from Terminal
        arr.add("9F03");    // Source from Terminal
        arr.add("9F09");    // Source from Terminal
        arr.add("9F10");    // Source from ICC
        arr.add("9F1A");    // Source from Terminal; Country code
        arr.add("9F1E");    // Source from Terminal
        arr.add("9F26");    // Source from ICC
        arr.add("9F27");    // Source from ICC
        arr.add("9F33");    // Source from Terminal
        arr.add("9F34");    // Source from Terminal
        arr.add("9F35");    // Source from Terminal
        arr.add("9F36");    // Source from ICC
        arr.add("9F37");    // Source from Terminal
        arr.add("9F41");    // Source from Terminal
        arr.add("9F53"); //MC
        ArrayList<Integer> arr2 = new ArrayList<Integer>();
        for(int i = 0; i < arr.size(); i++ ) {
            arr2.add(Integer.parseInt(arr.get(i), 16));
        }
        return arr2;
    }
    public static short toint(byte[] source) {
        System.out.println("toint");
        for(int i=0; i<source.length; i++) {
            System.out.println(source[i]);
        }

        short result =
                (short)((source[0] & 0xff)  |
                        (source[1] & 0xff)<< 8);
        System.out.println("result : " + Short.valueOf(Short.toString(result), 10));


        return result;
    }
    public static byte[] shortToByteArray(short value) {
        byte[] arr = ByteBuffer.allocate(2).putShort(value).array();
        byte[] byteArray = new byte[2];
        System.out.println("value:"+Integer.toBinaryString(value));
        System.out.println("vlaueshift"+Integer.toBinaryString(value >> 8));
        byteArray[0] = (byte)(value >> 8);
        byteArray[1] = (byte)(value);
        for(int i=0;i<2;i++) {
            System.out.println(i + ":"+Integer.toHexString(byteArray[i]));
            System.out.println(i + ":"+Integer.toBinaryString(byteArray[i]));
        }
        //System.out.println("after:"+Integer.toBinaryString(byteArray));
        return byteArray;
    }

    public static byte[] intToByteArray(int value) {
        byte[] byteArray;
        String s = Integer.toHexString(value);
        if(s.length() < 4) {
            byteArray = new byte[1];
            byteArray[0] = (byte)value;
        }
        else {
            byteArray = new byte[2];
            byteArray[0] = (byte)(value >> 8);
            byteArray[1] = (byte)(value);
        }
        return byteArray;
    }


    public void setSelectedaid(String aid) {
        this.selectedaid = aid;

    }

    public void setSelectedinedex(int index) {
        //EMVCallback.EMVAppSelect(index, 1);
        //EMV_APPLIST applist = new EMV_APPLIST();

        Log.d("kang","setselectedindex");
        EMV_APPLIST[] list = FileParse.getEmv_applists();
        List<EMV_APPLIST> lis = Arrays.asList(list);
        for (EMV_APPLIST i : lis) {
            Log.i("kang", "AID :" + bcd2Str(i.aid));
        }
        EMV_APPLIST applist = new EMV_APPLIST();
        EMVApi.EMVGetApp(index, applist);

        emvCallback.setCallBackResult(index);
        Log.d("kang","selected/aid:" + bcd2Str(applist.aid) + ",name:" + bcd2Str(applist.appName));
    }

    public void setSelectedflag(boolean flag) {
        this.selectedflag = flag;
    }


    public boolean getReversalflag() {
        return reversalflag;
    }

    private void setEmvParam(String cardType) {

        EMVCallback.EMVGetParameter(emvParam);
        switch(cardType) {
            case "VISA" :
                emvParam.capability = ChangeFormat.str2Bcd("E0F8C8");
                //emvParam.countryCode = Utils.str2Bcd("0840");
                emvParam.countryCode = ChangeFormat.str2Bcd("0764");
                emvParam.exCapability = ChangeFormat.str2Bcd("E000F0A001");
                emvParam.forceOnline = 0;
                emvParam.getDataPIN = ((byte) 1);
                //emvParam.merchCateCode = Utils.str2Bcd("0840");
                emvParam.merchCateCode = ChangeFormat.str2Bcd("0764");
                //emvParam.referCurrCode = Utils.str2Bcd("0840");
                emvParam.referCurrCode = ChangeFormat.str2Bcd("0764");
                emvParam.referCurrCon = 1000;
                emvParam.referCurrExp = (byte) 2;
                emvParam.surportPSESel = (byte) 1;
                emvParam.terminalType = ((byte) 0x22);
                //emvParam.transCurrCode = Utils.str2Bcd("0840");
                emvParam.transCurrCode = ChangeFormat.str2Bcd("0764");
                emvParam.transCurrExp = (byte) 2;
                emvParam.transType = ucTransType;
                emvParam.termId = Preference.getInstance(emvContext).getValueString(Preference.KEY_TERMINAL_ID_EPS).getBytes();
                emvParam.merchId = Preference.getInstance(emvContext).getValueString(Preference.KEY_MERCHANT_ID_EPS).getBytes();
                emvParam.merchName = Preference.getInstance(emvContext).getValueString(Preference.KEY_MERCHANT_1).getBytes();

                Log.d("kang","termid:" + bcd2Str(emvParam.termId) + ",merchid:" + bcd2Str(emvParam.merchId) + ",merchname:" + bcd2Str(emvParam.merchName));


                int a ;
                EMVCallback.EMVSetParameter(emvParam);
                EMVApi.EMVSetParameter(emvParam);
                EMVCallback.getInstance().cEMVSetParam();



             /*   EMVCallback.EMVGetMCKParam(mckParam);
                mckParam.ucBypassPin = 1;
                mckParam.ucBatchCapture = 1;
                mckParam.extmParam.aucTermAIP = ChangeFormat.str2Bcd("0800");
                mckParam.extmParam.ucUseTermAIPFlg = 1;
                mckParam.extmParam.ucBypassAllFlg = 1;
                EMVCallback.EMVSetMCKParam(mckParam);*/

                EMVCallback.EMVSetPCIModeParam((byte) 1, "0,4,5,6,7,8,9,10,11,12".getBytes(), 1000 * 120);//Set no PCI mode. for input PIN
                break;
            case "MASTER":
                emvParam.capability = ChangeFormat.str2Bcd("00B0C8");
                //emvParam.countryCode = Utils.str2Bcd("0840");
                emvParam.countryCode = ChangeFormat.str2Bcd("0764");
                emvParam.exCapability = ChangeFormat.str2Bcd("E000F0A001");
                emvParam.forceOnline = 0;
                emvParam.getDataPIN = ((byte) 1);
                //emvParam.merchCateCode = Utils.str2Bcd("0840");
                emvParam.merchCateCode = ChangeFormat.str2Bcd("0764");
                //emvParam.referCurrCode = Utils.str2Bcd("0840");
                emvParam.referCurrCode = ChangeFormat.str2Bcd("0764");
                emvParam.referCurrCon = 1000;
                emvParam.referCurrExp = (byte) 2;
                emvParam.surportPSESel = (byte) 1;
                emvParam.terminalType = ((byte) 0x22);
                //emvParam.transCurrCode = Utils.str2Bcd("0840");
                emvParam.transCurrCode = ChangeFormat.str2Bcd("0764");
                emvParam.transCurrExp = (byte) 2;
                emvParam.transType = ucTransType;
                emvParam.termId = Preference.getInstance(emvContext).getValueString(Preference.KEY_TERMINAL_ID_EPS).getBytes();
                emvParam.merchId = Preference.getInstance(emvContext).getValueString(Preference.KEY_MERCHANT_ID_EPS).getBytes();
                emvParam.merchName = Preference.getInstance(emvContext).getValueString(Preference.KEY_MERCHANT_1).getBytes();

                Log.d("kang","termid:" + bcd2Str(emvParam.termId) + ",merchid:" + bcd2Str(emvParam.merchId) + ",merchname:" + bcd2Str(emvParam.merchName));


                EMVCallback.EMVSetParameter(emvParam);
                EMVApi.EMVSetParameter(emvParam);
                EMVCallback.getInstance().cEMVSetParam();


                EMVCallback.EMVGetMCKParam(mckParam);
                mckParam.ucBypassPin = 1;
                mckParam.ucBatchCapture = 1;
                mckParam.extmParam.aucTermAIP = ChangeFormat.str2Bcd("0800");
                mckParam.extmParam.ucUseTermAIPFlg = 1;
                mckParam.extmParam.ucBypassAllFlg = 1;
                EMVCallback.EMVSetMCKParam(mckParam);

                EMVCallback.EMVSetPCIModeParam((byte) 1, "0,4,5,6,7,8,9,10,11,12".getBytes(), 1000 * 120);//Set no PCI mode. for input PIN
                break;
            case "JCB":

                break;
            case "UPI":

                break;
                default:

        }

    }



}
