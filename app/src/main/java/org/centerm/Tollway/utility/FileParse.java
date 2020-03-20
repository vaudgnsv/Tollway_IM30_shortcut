package org.centerm.Tollway.utility;

import android.content.Context;
import android.util.Log;

import com.pax.jemv.amex.model.CLSS_AEAIDPARAM;
import com.pax.jemv.clcommon.ClssTmAidList;
import com.pax.jemv.clcommon.Clss_MCAidParam;
import com.pax.jemv.clcommon.Clss_PreProcInfo;
import com.pax.jemv.clcommon.EMV_APPLIST;
import com.pax.jemv.clcommon.EMV_CAPK;
import com.pax.jemv.clcommon.KernType;
import com.pax.jemv.qpboc.model.Clss_PbocAidParam;

import org.centerm.Tollway.jemv.clssjspeedy.model.Clss_JcbAidParam;
import org.centerm.Tollway.jemv.clsspure.trans.model.Clss_PureAidParam;
import org.dtools.ini.BasicIniFile;
import org.dtools.ini.IniFile;
import org.dtools.ini.IniFileReader;
import org.dtools.ini.IniSection;

import java.io.File;
import java.io.IOException;

import static org.centerm.Tollway.core.ChangeFormat.str2Bcd;

/**
 * Created by Administrator on 2017/3/16 0016.
 */

public class FileParse {
    private static final String TAG = "FileParse";
    private static final String RID_VISA = "A000000003";
    private static final String RID_MC = "A000000004";
    private static final String RID_PBOC = "A000000333";
    private static final String RID_AE = "A000000025";
    private static final String RID_DPAS1 = "A000000152";
    private static final String RID_DPAS2 = "A000000324";
    private static final String RID_JCB = "A000000065";
    private static final String RID_PURE = "D999999999";
    private static final String RID_TPN = "A000000677";

    public static final int PARSE_ERR = -1;
    public static final int PARSE_SUCCESS = 0;

    private static ClssTmAidList[] tmAidLists;
    private static Clss_PreProcInfo[] preProcInfos;
    private static Clss_MCAidParam[] mcAidParams;
    private static CLSS_AEAIDPARAM[] aeAidParams;
    private static Clss_PbocAidParam[] pbocAidParams;
    private static Clss_JcbAidParam[] jcbAidParams;
    private static Clss_PureAidParam[] pureAidParams;
    private static EMV_CAPK[] mEmvCapk;
    private static EMV_APPLIST[] emv_applists;

    public static ClssTmAidList[] getTmAidLists() {
        return tmAidLists;
    }

    public static EMV_CAPK[] getmEmvCapk() {
        return mEmvCapk;
    }

    public static Clss_MCAidParam[] getMcAidParams() {
        return mcAidParams;
    }

    public static CLSS_AEAIDPARAM[] getAeAidParams() {
        return aeAidParams;
    }

    public static Clss_PbocAidParam[] getPbocAidParams() {
        return pbocAidParams;
    }

    public static Clss_JcbAidParam[] getJcbAidParams() {
        return jcbAidParams;
    }

    public static Clss_PureAidParam[] getPureAidParams() {
        return pureAidParams;
    }


    public static Clss_PreProcInfo[] getPreProcInfos() {
        return preProcInfos;
    }

    public static EMV_APPLIST[] getEmv_applists() { return emv_applists; }

    public static int parseAidFromAssets(Context context, String fileName) {
        String file = context.getFilesDir().getPath() + "/" + fileName;
        boolean flag = false;

        flag = FileUtils.copyFileFromAssert(context, fileName, file);

        if (flag) {
            IniFile iniFile = new BasicIniFile();
            File aidFile = new File(file);
            IniFileReader rad = new IniFileReader(iniFile, aidFile);
            try {
                //读取item
                rad.read();
                IniSection iniSection = iniFile.getSection(0);
                String aidNum = iniSection.getItem(0).getValue();
                tmAidLists = new ClssTmAidList[Integer.parseInt(aidNum)];
                preProcInfos = new Clss_PreProcInfo[Integer.parseInt(aidNum)];
                mcAidParams = new Clss_MCAidParam[Integer.parseInt(aidNum)];
                aeAidParams = new CLSS_AEAIDPARAM[Integer.parseInt(aidNum)];
                pbocAidParams = new Clss_PbocAidParam[Integer.parseInt(aidNum)];
                jcbAidParams = new Clss_JcbAidParam[Integer.parseInt(aidNum)];
                pureAidParams = new Clss_PureAidParam[Integer.parseInt(aidNum)];

                emv_applists = new EMV_APPLIST[Integer.parseInt(aidNum)];

                for (int i = 0; i < Integer.parseInt(aidNum); i++) {
                    IniSection iniSection1 = iniFile.getSection(i + 1);
                    //for (int j = 0; j < iniSection1.getNumberOfItems(); j++) {
                    tmAidLists[i] = new ClssTmAidList();


                    tmAidLists[i].ucAidLen = (byte) Integer.parseInt(iniSection1.getItem("AIDLEN").getValue());


                    emv_applists[i] = new EMV_APPLIST();

                    //if(iniSection1.getItem("AIDLEN") != null )
                    emv_applists[i].aidLen = iniSection1.getItem("AIDLEN") != null ?  (byte) Integer.parseInt(iniSection1.getItem("AIDLEN").getValue()) : 0;
                    System.arraycopy(str2Bcd(iniSection1.getItem("APPNAME").getValue()), 0, emv_applists[i].appName, 0, str2Bcd(iniSection1.getItem("APPNAME").getValue()).length);

                    emv_applists[i].selFlag = iniSection1.getItem("SELFLAG") != null ? str2Bcd(iniSection1.getItem("SELFLAG").getValue())[0] : 0;
                    emv_applists[i].priority = iniSection1.getItem("PRIORITY") != null ? str2Bcd(iniSection1.getItem("PRIORITY").getValue())[0] : 0;


                    emv_applists[i].targetPer = iniSection1.getItem("TARGETPER") != null ? str2Bcd(iniSection1.getItem("TARGETPER").getValue())[0] : 0;




                    emv_applists[i].maxTargetPer = iniSection1.getItem("MAXTARGETPER") != null ? str2Bcd(iniSection1.getItem("MAXTARGETPER").getValue())[0] : 0;
                    emv_applists[i].floorLimitCheck = iniSection1.getItem("FLOORLIMITCHECK") != null ? str2Bcd(iniSection1.getItem("FLOORLIMITCHECK").getValue())[0] : 0;
                    emv_applists[i].randTransSel = iniSection1.getItem("RANDTRANSSEL") != null ? str2Bcd(iniSection1.getItem("RANDTRANSSEL").getValue())[0] : 0;
                    emv_applists[i].velocityCheck = iniSection1.getItem("VELOCITYCHECK") != null ? str2Bcd(iniSection1.getItem("VELOCITYCHECK").getValue())[0] : 0;

                    emv_applists[i].floorLimit = iniSection1.getItem("FLOORLIMIT") != null ? Long.parseLong(iniSection1.getItem("FLOORLIMIT").getValue()) : 0;
                    emv_applists[i].threshold = iniSection1.getItem("THRESHOLD") != null ? Long.parseLong(iniSection1.getItem("THRESHOLD").getValue()) : 0;


                    if(iniSection1.getItem("TACDENIAL") != null)
                    System.arraycopy(str2Bcd(iniSection1.getItem("TACDENIAL").getValue()), 0, emv_applists[i].tacDenial, 0, str2Bcd(iniSection1.getItem("TACDENIAL").getValue()).length);

                    if(iniSection1.getItem("TACONLINE") != null)
                    System.arraycopy(str2Bcd(iniSection1.getItem("TACONLINE").getValue()), 0, emv_applists[i].tacOnline, 0, str2Bcd(iniSection1.getItem("TACONLINE").getValue()).length);

                    if(iniSection1.getItem("TACDEFAULT") != null)
                    System.arraycopy(str2Bcd(iniSection1.getItem("TACDEFAULT").getValue()), 0, emv_applists[i].tacDefault, 0, str2Bcd(iniSection1.getItem("TACDEFAULT").getValue()).length);

                    if(iniSection1.getItem("ACQUIERID") != null)
                    System.arraycopy(str2Bcd(iniSection1.getItem("ACQUIERID").getValue()), 0, emv_applists[i].acquierId, 0, str2Bcd(iniSection1.getItem("ACQUIERID").getValue()).length);

                    if(iniSection1.getItem("DDOL") != null)
                    System.arraycopy(str2Bcd(iniSection1.getItem("DDOL").getValue()), 0, emv_applists[i].dDOL, 0, str2Bcd(iniSection1.getItem("DDOL").getValue()).length);

                    if(iniSection1.getItem("TDOL") != null)
                    System.arraycopy(str2Bcd(iniSection1.getItem("TDOL").getValue()), 0, emv_applists[i].tDOL, 0, str2Bcd(iniSection1.getItem("TDOL").getValue()).length);

                    if(iniSection1.getItem("VERSION") != null)
                    System.arraycopy(str2Bcd(iniSection1.getItem("VERSION").getValue()), 0, emv_applists[i].version, 0, str2Bcd(iniSection1.getItem("VERSION").getValue()).length);


                    Log.d("kang","fileparse/riskmandata:" + iniSection1.getItem("RISKMANDATA").getValue() + ",length:" + iniSection1.getItem("RISKMANDATA").getValue().length() + ",bcd length:" + str2Bcd(iniSection1.getItem("RISKMANDATA").getValue()).length);
                    if(iniSection1.getItem("RISKMANDATA") != null)
                    System.arraycopy(str2Bcd(iniSection1.getItem("RISKMANDATA").getValue()), 0, emv_applists[i].riskManData, 0, str2Bcd(iniSection1.getItem("RISKMANDATA").getValue()).length);


                    if (iniSection1.getItem(1).getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem("AID").getValue()), 0,
                                tmAidLists[i].aucAID, 0, tmAidLists[i].ucAidLen);
                        //Log.d(TAG, "aid: " + Utils.bcd2Str(tmAidLists[i].aucAID));
                        System.arraycopy(str2Bcd(iniSection1.getItem("AID").getValue()), 0,
                                emv_applists[i].aid, 0, emv_applists[i].aidLen);
                    }

                    String ridBuf = iniSection1.getItem("AID").getValue().substring(0, 10);
                    Log.d(TAG, "ridBuf: " + ridBuf);
                    if (ridBuf.equals(RID_PBOC))
                        tmAidLists[i].ucKernType = KernType.KERNTYPE_PBOC;
                    else if (ridBuf.equals(RID_VISA))
                        tmAidLists[i].ucKernType = KernType.KERNTYPE_VIS;
                    else if (ridBuf.equals(RID_MC))
                        tmAidLists[i].ucKernType = KernType.KERNTYPE_MC;
                    else if (ridBuf.equals(RID_AE))
                        tmAidLists[i].ucKernType = KernType.KERNTYPE_AE;
                    else if (ridBuf.equals(RID_DPAS1))
                        tmAidLists[i].ucKernType = KernType.KERNTYPE_ZIP;
                    else if (ridBuf.equals(RID_DPAS2))
                        tmAidLists[i].ucKernType = KernType.KERNTYPE_ZIP;
                    else if (ridBuf.equals(RID_JCB))
                        tmAidLists[i].ucKernType = KernType.KERNTYPE_JCB;
                    else if (ridBuf.equals(RID_PURE))
                        tmAidLists[i].ucKernType = KernType.KERNTYPE_PURE;
                    else if (ridBuf.equals(RID_TPN))
                        tmAidLists[i].ucKernType = KernType.KERNTYPE_PBOC;

                    else
                        tmAidLists[i].ucKernType = KernType.KERNTYPE_DEF;
                    tmAidLists[i].ucSelFlg = (byte) Integer.parseInt(iniSection1.getItem("SELFLAG").getValue());

                    preProcInfos[i] = new Clss_PreProcInfo();
                    preProcInfos[i].ucAidLen = (byte) Integer.parseInt(iniSection1.getItem("AIDLEN").getValue());
                    if (iniSection1.getItem("AID").getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem("AID").getValue()), 0,
                                preProcInfos[i].aucAID, 0, tmAidLists[i].ucAidLen);
                    }
                    //????
//                    System.arraycopy(DeviceImpl.str2Bcd(iniSection1.getItem(29).getValue()), 0,
//                            preProcInfos[i].aucReaderTTQ, 0, 4);
//????????????????????????????????????????????????
//                  preProcInfos[j].ucCrypto17Flg = ;

                    if (ridBuf.equals(RID_PBOC))
                        preProcInfos[i].ucKernType = KernType.KERNTYPE_PBOC;
                    else if (ridBuf.equals(RID_VISA))
                        preProcInfos[i].ucKernType = KernType.KERNTYPE_VIS;
                    else if (ridBuf.equals(RID_MC))
                        preProcInfos[i].ucKernType = KernType.KERNTYPE_MC;
                    else if (ridBuf.equals(RID_AE))
                        preProcInfos[i].ucKernType = KernType.KERNTYPE_AE;
                    else if (ridBuf.equals(RID_DPAS1))
                        preProcInfos[i].ucKernType = KernType.KERNTYPE_ZIP;
                    else if (ridBuf.equals(RID_DPAS2))
                        preProcInfos[i].ucKernType = KernType.KERNTYPE_ZIP;
                    else if (ridBuf.equals(RID_JCB))
                        preProcInfos[i].ucKernType = KernType.KERNTYPE_JCB;
                    else if (ridBuf.equals(RID_PURE))
                        preProcInfos[i].ucKernType = KernType.KERNTYPE_PURE;
                    else if(ridBuf.equals(RID_TPN))
                        preProcInfos[i].ucKernType = KernType.KERNTYPE_PBOC;
                    else
                        preProcInfos[i].ucKernType = KernType.KERNTYPE_DEF;
                    preProcInfos[i].ucRdClssFLmtFlg = (byte) Integer.parseInt(iniSection1.getItem("RDCLSSFLMTFLG").getValue());
                    preProcInfos[i].ucRdClssTxnLmtFlg = (byte) Integer.parseInt(iniSection1.getItem("RDCLSSTXNMTFLG").getValue());
                    preProcInfos[i].ucRdCVMLmtFlg = (byte) Integer.parseInt(iniSection1.getItem("RDCVMLMTFLG").getValue());
                    //????
                    preProcInfos[i].ucStatusCheckFlg = (byte) Integer.parseInt(iniSection1.getItem("ucStatusCheckFlg").getValue());
                    preProcInfos[i].ucTermFLmtFlg = (byte) Integer.parseInt(iniSection1.getItem("TERMFLMT").getValue());
                    //?????
                   // Log.d("fileparse",iniSection1.getItem(30).getValue());
                    preProcInfos[i].ucZeroAmtNoAllowed = (byte) Integer.parseInt(iniSection1.getItem("ucZeroAmtNoAllowed").getValue());
                    preProcInfos[i].ulRdClssFLmt = Long.parseLong(iniSection1.getItem("RDCLSSFLMT").getValue());
                    preProcInfos[i].ulRdClssTxnLmt = Long.parseLong(iniSection1.getItem("RDCLSSTXNLIMIT").getValue());
                    preProcInfos[i].ulRdCVMLmt = Long.parseLong(iniSection1.getItem("RDCVMLIMIT").getValue());
                    preProcInfos[i].ulTermFLmt = Long.parseLong(iniSection1.getItem("TERMFLMT").getValue());
                    //preProcInfos[i].ucOnlinePin = (byte) Integer.parseInt(iniSection1.getItem(5).getValue());

                    mcAidParams[i] = new Clss_MCAidParam();
                    aeAidParams[i] = new CLSS_AEAIDPARAM();
                    pbocAidParams[i] = new Clss_PbocAidParam();
                    jcbAidParams[i] = new Clss_JcbAidParam();
                    pureAidParams[i] = new Clss_PureAidParam();

                    if (iniSection1.getItem("ACQUIREID").getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem("ACQUIREID").getValue()), 0,
                                mcAidParams[i].acquierId, 0, str2Bcd(iniSection1.getItem("ACQUIREID").getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem("ACQUIREID").getValue()), 0,
                                aeAidParams[i].AcquierId, 0, str2Bcd(iniSection1.getItem("ACQUIREID").getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem("ACQUIREID").getValue()), 0,
                                jcbAidParams[i].acquierId, 0, str2Bcd(iniSection1.getItem("ACQUIREID").getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem("ACQUIREID").getValue()), 0,
                                pureAidParams[i].acquierId, 0, str2Bcd(iniSection1.getItem("ACQUIREID").getValue()).length);
                    }
                    //Log.d(TAG, "AcquierId: " + str2Bcd(iniSection1.getItem(24).getValue()));

                    if (iniSection1.getItem("DDOL").getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem("DDOL").getValue()), 0,
                                mcAidParams[i].dDOL, 0, str2Bcd(iniSection1.getItem("DDOL").getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem("DDOL").getValue()), 0,
                                aeAidParams[i].dDOL, 0, str2Bcd(iniSection1.getItem("DDOL").getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem("DDOL").getValue()), 0,
                                pureAidParams[i].dDOL, 0, str2Bcd(iniSection1.getItem("DDOL").getValue()).length);
                        pureAidParams[i].dDolLen = str2Bcd(iniSection1.getItem("DDOL").getValue()).length;
                    }

                    mcAidParams[i].floorLimit = Long.parseLong(iniSection1.getItem("FLOORLIMIT").getValue());
                    aeAidParams[i].FloorLimit = Long.parseLong(iniSection1.getItem("FLOORLIMIT").getValue());
                    pbocAidParams[i].ulTermFLmt = Long.parseLong(iniSection1.getItem("FLOORLIMIT").getValue());

                    mcAidParams[i].floorLimitCheck = (byte) Integer.parseInt(iniSection1.getItem("FLOORLIMITCHECK").getValue());
                    aeAidParams[i].FloorLimitCheck = (byte) Integer.parseInt(iniSection1.getItem("FLOORLIMITCHECK").getValue());
                    //Log.d(TAG, "record 1 ");

                    //mcAidParams[j].forceOnline;
                    //mcAidParams[j].magAvn;
                    mcAidParams[i].maxTargetPer = (byte) Integer.parseInt(iniSection1.getItem("MAXTARGETPER").getValue());
                    jcbAidParams[i].maxTargetPer = (byte) Integer.parseInt(iniSection1.getItem("MAXTARGETPER").getValue());

                    mcAidParams[i].randTransSel = (byte) Integer.parseInt(iniSection1.getItem("RANDTRANSSEL").getValue());

                    if (iniSection1.getItem("TACDEFAULT").getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem("TACDEFAULT").getValue()), 0,
                                mcAidParams[i].tacDefault, 0, str2Bcd(iniSection1.getItem("TACDEFAULT").getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem("TACDEFAULT").getValue()), 0,
                                aeAidParams[i].TACDefault, 0, str2Bcd(iniSection1.getItem("TACDEFAULT").getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem("TACDEFAULT").getValue()), 0,
                                jcbAidParams[i].tacDefault, 0, str2Bcd(iniSection1.getItem("TACDEFAULT").getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem("TACDEFAULT").getValue()), 0,
                                pureAidParams[i].tacDefault, 0, str2Bcd(iniSection1.getItem("TACDEFAULT").getValue()).length);
                    }

                    if (iniSection1.getItem("TACDENIAL").getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem("TACDENIAL").getValue()), 0,
                                mcAidParams[i].tacDenial, 0, str2Bcd(iniSection1.getItem("TACDENIAL").getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem("TACDENIAL").getValue()), 0,
                                aeAidParams[i].TACDenial, 0, str2Bcd(iniSection1.getItem("TACDENIAL").getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem("TACDENIAL").getValue()), 0,
                                jcbAidParams[i].tacDenial, 0, str2Bcd(iniSection1.getItem("TACDENIAL").getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem("TACDENIAL").getValue()), 0,
                                pureAidParams[i].tacDenial, 0, str2Bcd(iniSection1.getItem("TACDENIAL").getValue()).length);
                    }

                    if (iniSection1.getItem("TACONLINE").getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem("TACONLINE").getValue()), 0,
                                mcAidParams[i].tacOnline, 0, str2Bcd(iniSection1.getItem("TACONLINE").getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem("TACONLINE").getValue()), 0,
                                aeAidParams[i].TACOnline, 0, str2Bcd(iniSection1.getItem("TACONLINE").getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem("TACONLINE").getValue()), 0,
                                jcbAidParams[i].tacOnline, 0, str2Bcd(iniSection1.getItem("TACONLINE").getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem("TACONLINE").getValue()), 0,
                                pureAidParams[i].tacOnline, 0, str2Bcd(iniSection1.getItem("TACONLINE").getValue()).length);
                    }
                    //Log.d(TAG, "record 2 ");
                    if (ridBuf.equals(RID_AE) || ridBuf.equals(RID_JCB)) {
                        mcAidParams[i].targetPer = (byte) Integer.parseInt(iniSection1.getItem("TARGETPER").getValue());
                        jcbAidParams[i].targetPer = (byte) Integer.parseInt(iniSection1.getItem("TARGETPER").getValue());
                    } else if (ridBuf.equals(RID_PURE)) {
                        if (!iniSection1.getItem("TARGETPER").getValue().equals("")) {
                            pureAidParams[i].ioOption[0] = str2Bcd(iniSection1.getItem("TARGETPER").getValue())[0];//16进制
                        }
                    }

                    if (ridBuf.equals(RID_AE) || ridBuf.equals(RID_JCB)) {
                        mcAidParams[i].threshold = (byte) Integer.parseInt(iniSection1.getItem("THRESHOLD").getValue());
                        jcbAidParams[i].threshold = (byte) Integer.parseInt(iniSection1.getItem("THRESHOLD").getValue());
                    } else if (ridBuf.equals(RID_PURE)) {
                        if (!iniSection1.getItem("THRESHOLD").getValue().equals("")) {
                            if (iniSection1.getItem("THRESHOLD").getValue().length() == 2)
                                pureAidParams[i].appAuthType[0] = str2Bcd(iniSection1.getItem("THRESHOLD").getValue())[0];//16进制
                        }
                    }

                    if (iniSection1.getItem("TDOL").getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem("TDOL").getValue()), 0,
                                mcAidParams[i].tDOL, 0, str2Bcd(iniSection1.getItem("TDOL").getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem("TDOL").getValue()), 0,
                                aeAidParams[i].tDOL, 0, str2Bcd(iniSection1.getItem("TDOL").getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem("TDOL").getValue()), 0,
                                pureAidParams[i].mtDOL, 0, str2Bcd(iniSection1.getItem("TDOL").getValue()).length);
                        pureAidParams[i].mtDolLen = str2Bcd(iniSection1.getItem("TDOL").getValue()).length;
                    }

                    //Log.d(TAG, "record 4 ");
                    //mcAidParams[j].ucMagSupportFlg;
                    //mcAidParams[j].uDOL;
                    //mcAidParams[j].usUDOLLen;
                    mcAidParams[i].velocityCheck = (byte) Integer.parseInt(iniSection1.getItem("VELOCITYCHECK").getValue());

                    if (iniSection1.getItem("VERSION").getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem("VERSION").getValue()), 0,
                                mcAidParams[i].version, 0, str2Bcd(iniSection1.getItem("VERSION").getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem("VERSION").getValue()), 0,
                                aeAidParams[i].Version, 0, str2Bcd(iniSection1.getItem("VERSION").getValue()).length);
                        System.arraycopy(str2Bcd(iniSection1.getItem("VERSION").getValue()), 0,
                                pureAidParams[i].Version, 0, str2Bcd(iniSection1.getItem("VERSION").getValue()).length);
                    }
        /*            //Log.d(TAG, "record 3 ");
                    if (iniSection1.getItem(28).getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem(28).getValue()), 0,
                                pureAidParams[i].aTOL, 0, str2Bcd(iniSection1.getItem(28).getValue()).length);
                        pureAidParams[i].aTolLen = str2Bcd(iniSection1.getItem(28).getValue()).length;
                    }

                    if (!iniSection1.getItem(31).getValue().equals("")) {
                        aeAidParams[i].ucAETermCap = str2Bcd(iniSection1.getItem(31).getValue())[0];//16进制
                        System.arraycopy(str2Bcd(iniSection1.getItem(31).getValue()), 0,
                                pureAidParams[i].atdTOL, 0, str2Bcd(iniSection1.getItem(31).getValue()).length);
                        pureAidParams[i].atdTolLen = str2Bcd(iniSection1.getItem(31).getValue()).length;
                    }*/
                    //Log.d(TAG, "aid param:  " + i + "Finish");

                    //}
                }
//			            iniItem.setValue("Konan");
//			            iniSection.addItem(iniItem);
//			            iniFile.addSection(iniSection);
//			            wir.write();
            } catch (IOException e) {// TODO Auto-generated catch block
                Log.e("parseAidFromAssets", e.getMessage());
                //e.printStackTrace();
                return PARSE_ERR;
            }
            return PARSE_SUCCESS;
        }
        return PARSE_ERR;
    }

    public static int parseCapkFromAssets(Context context, String fileName) {
        String file = context.getFilesDir().getPath() + "/" + fileName;
        boolean flag = false;

        flag = FileUtils.copyFileFromAssert(context, fileName, file);

        if (flag) {
            IniFile iniFile = new BasicIniFile();
            File capkFile = new File(file);
            IniFileReader rad = new IniFileReader(iniFile, capkFile);
            try {
                //读取item
                rad.read();
                IniSection iniSection = iniFile.getSection(0);
                String capkNum = iniSection.getItem(0).getValue();
                mEmvCapk = new EMV_CAPK[Integer.parseInt(capkNum)];

                for (int i = 0; i < Integer.parseInt(capkNum); i++) {
                    IniSection iniSection1 = iniFile.getSection(i + 1);
                    mEmvCapk[i] = new EMV_CAPK();

                    if (iniSection1.getItem(0).getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem(0).getValue()), 0,
                                mEmvCapk[i].rID, 0, str2Bcd(iniSection1.getItem(0).getValue()).length);
                    }
                    if (!iniSection1.getItem(1).getValue().equals("")) {
                        //mEmvCapk[i].keyID = (byte) Integer.parseInt(iniSection1.getItem(1).getValue());
                        mEmvCapk[i].keyID = str2Bcd(iniSection1.getItem(1).getValue())[0];//16进制
                    }

                    if (!iniSection1.getItem(2).getValue().equals("")) {
                        mEmvCapk[i].hashInd = str2Bcd(iniSection1.getItem(2).getValue())[0];
                    }

                    if (!iniSection1.getItem(3).getValue().equals("")) {
                        mEmvCapk[i].arithInd = str2Bcd(iniSection1.getItem(3).getValue())[0];
                    }

                    if (!iniSection1.getItem(4).getValue().equals("")) {
                        mEmvCapk[i].modulLen = (short) Integer.parseInt(iniSection1.getItem(4).getValue());
                    }


                    if (iniSection1.getItem(5).getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem(5).getValue()), 0,
                                mEmvCapk[i].modul, 0, str2Bcd(iniSection1.getItem(5).getValue()).length);
                    }

                    if (!iniSection1.getItem(6).getValue().equals("")) {
                        mEmvCapk[i].exponentLen = (byte) Integer.parseInt(iniSection1.getItem(6).getValue());
                    }

                    if (iniSection1.getItem(7).getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem(7).getValue()), 0,
                                mEmvCapk[i].exponent, 0, str2Bcd(iniSection1.getItem(7).getValue()).length);
                    }

                    if (iniSection1.getItem(8).getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem(8).getValue()), 0,
                                mEmvCapk[i].expDate, 0, str2Bcd(iniSection1.getItem(8).getValue()).length);
                    }

                    if (iniSection1.getItem(9).getValue() != null) {
                        System.arraycopy(str2Bcd(iniSection1.getItem(9).getValue()), 0, mEmvCapk[i].checkSum, 0,
                                str2Bcd(iniSection1.getItem(9).getValue()).length);
                    }

                }
//			            iniItem.setValue("Konan");
//			            iniSection.addItem(iniItem);
//			            iniFile.addSection(iniSection);
//			            wir.write();
            } catch (IOException e) {// TODO Auto-generated catch block
                Log.e("parseCapkFromAssets", e.getMessage());
                //e.printStackTrace();
                return PARSE_ERR;
            }
            return PARSE_SUCCESS;
        }
        return PARSE_ERR;
    }

}
