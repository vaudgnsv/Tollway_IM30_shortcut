package org.centerm.land.core;

import android.text.TextUtils;
import android.util.Log;

import com.centerm.smartpos.aidl.pboc.ParcelableTrackData;
import com.centerm.smartpos.util.HexUtil;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.centerm.land.core.ChangeFormat.binStrToIntHexStr;

/**
 * author:wangwenxun@centerm.com
 * Created by 29622 on 2018/1/18.
 */

public class BlockCalculateUtil {
    public static String getAmount(String amount){
        String amountStr = "000000000000";
        amount = amount.replace(".","").trim();
        int length = amount.length();
        amountStr = amountStr.substring(length)+amount;
        return amountStr;
    }

    public static String getSerialCode(int serialCode){
        String serialStr = "000000";
        String code = serialCode+"";
        code = code.trim();
        int length = code.length();
        serialStr = serialStr.substring(length)+serialCode;
        return serialStr;
    }

    public static String get55Length(int byteLength){
        String lengthStr = "0000";
        String code = byteLength+"";
        code = code.trim();
        int length = code.length();
        lengthStr = lengthStr.substring(length)+byteLength;
        return lengthStr;
    }

    public static String getTheNameFromFirstTrack(String firstTrack){
        if (TextUtils.isEmpty(firstTrack)) {
            return null;
        }

        int index = firstTrack.indexOf("^");
        String name = firstTrack.substring(index + 1, index + 26).trim();
        return name;
    }

    public static int getBankCardType(String cardNo){
        if(TextUtils.isEmpty(cardNo)){
            return -1;
        }
        int type = Config.UNKNOWW;
        String flag = cardNo.substring(0, 2);
        if (flag.startsWith("4")) {
            type = Config.VISA;
        }
        if (flag.equals("62")) {
            type = Config.UNION;
        }

        int flagInt = Integer.parseInt(flag);
        if (flagInt <= 55 && flagInt >= 51) {
            type = Config.MASTER;
        }
        return type;
    }

    //获取第12域的数据:local Time
    public static String get12Data(){
        Calendar calendar = Calendar.getInstance();
        StringBuilder timeBuilder = new StringBuilder();
        int hourStr= calendar.get(Calendar.HOUR_OF_DAY);
        int minuteStr = calendar.get(Calendar.MINUTE);
        int secondStr = calendar.get(Calendar.SECOND);
        if(hourStr<10){
            timeBuilder.append("0");
        }
        timeBuilder.append(hourStr);
        if(minuteStr<10){
            timeBuilder.append("0");
        }
        timeBuilder.append(minuteStr);
        if(secondStr<10){
            timeBuilder.append("0");
        }
        timeBuilder.append(secondStr);
        return timeBuilder.toString().trim();
    }

    //获取第13域的数据:transaction Data
    public static String get13Data(){
        Calendar calendar = Calendar.getInstance();
        StringBuilder dataBuilder = new StringBuilder();
        int monthStr = calendar.get(Calendar.MONTH);
        monthStr = monthStr+1;
        int dayStr = calendar.get(Calendar.DAY_OF_MONTH);
        if(monthStr<10){
            dataBuilder.append("0");
        }
        dataBuilder.append(monthStr);
        if(dayStr<10){
            dataBuilder.append("0");
        }
        dataBuilder.append(dayStr);
        System.out.println("13磁道:"+dataBuilder.toString().trim());
        return dataBuilder.toString().trim();
    }

    //获取60域的数据.当message type = 0320的时候使用
    public static String get60DataOriginalMessageData(String originalMessageType,String originalSystemTrackAuditNumber,String reserved){
        StringBuilder messageDataBuilder = new StringBuilder();
        messageDataBuilder.append("0022");
        for(int i=0;i<originalMessageType.length();i++){
            messageDataBuilder.append(changeCharToHexString(originalMessageType.charAt(i)));
        }
        for(int i=0;i<originalSystemTrackAuditNumber.length();i++){
            messageDataBuilder.append(changeCharToHexString(originalSystemTrackAuditNumber.charAt(i)));
        }
        messageDataBuilder.append(reserved);
        return messageDataBuilder.toString();
    }


    public static String getHexString(String terminalNo){
        StringBuilder terminalNoStr = new StringBuilder();
        terminalNoStr.append("");
        //进来的时候是10进制，然后要转为16进制的
        for (int i=0;i<terminalNo.length();i++){
            terminalNoStr.append(changeCharToHexString(terminalNo.charAt(i)));
        }
        return terminalNoStr.toString();
    }

    /* Vince provide new function at 07-May-2018 */
    public static String get35Data(String string){
        StringBuilder dataStr = new StringBuilder();

        Log.d("CENTERM APP", "get35Data string => "+string);
        //dataStr.append("37");
        dataStr.append(string.substring(4));
        String result = dataStr.toString();
        result = result.substring(0,result.length()-1);
        int track2Length = result.length();
        result = result.length()+result;
        if(track2Length%2 != 0)
        {
            result = result+"0";
        }
        //如果不够38位，那么继续补足

        /*
        int lackLength = 40 - result.length();
        for(int i=0;i<lackLength;i++){
            result = result+"0";
        }
        */
        System.out.println("DATA IN BLOCK 35:："+result);
        return result;
    }

    public static String get35Data(ParcelableTrackData arg0){
        StringBuilder dataStr = new StringBuilder();

        Log.d("CENTERM APP", "get35Data Parcelable => "+(new String(arg0.getSecondTrackData())));
        //dataStr.append("37");
        dataStr.append(new String(arg0.getSecondTrackData()));
        //dataStr = dataStr.length()+dataStr;
        String result = dataStr.toString();
        int track2Length = result.length();
        result = result.length()+dataStr.toString();
        if(track2Length%2 != 0)
        {
            result = result+"0";
        }
        //result = result.substring(0,result.length()-1);
        //如果不够38位，那么继续补足
        /*
        int lackLength = 40 - result.length();
        for(int i=0;i<lackLength;i++){
            result = result+"0";
        }
        */
        result = result.replace("=","D");
        System.out.println("DATA IN BLOCK 35："+result);
        return result;
    }

    /* Original Function
    public static String get35Data(String string){
        StringBuilder dataStr = new StringBuilder();

        Log.d("CENTERM APP", "get35Data string => "+string);
        dataStr.append("37");
        dataStr.append(string.substring(4));
        String result = dataStr.toString();
        result = result.substring(0,result.length()-1);
        result = result+"0";

        System.out.println("DATA IN BLOCK 35:："+result);
        return result;
    }

    public static String get35Data(ParcelableTrackData arg0){
        StringBuilder dataStr = new StringBuilder();

        Log.d("CENTERM APP", "get35Data Parcelable => "+(new String(arg0.getSecondTrackData())));
        dataStr.append("37");
        dataStr.append(new String(arg0.getSecondTrackData()));
        String result = dataStr.toString();
        result = result.substring(0,result.length()-1);
        result = result+"00";
        result = result.replace("=","D");
        System.out.println("DATA IN BLOCK 35："+result);
        return result;
    }
    */

    public static String get45BlockData(String firstCardTrack){
        firstCardTrack = firstCardTrack.substring(2);
        int length = firstCardTrack.length();
        length = length/2;
        return length+firstCardTrack;
    }


    public static String changeStringToHexString(String firstTrackData){
        StringBuilder firstTrackDataBuiler = new StringBuilder();
        for(int i=0;i<firstTrackData.length();i++){
            char temp = firstTrackData.charAt(i);
            firstTrackDataBuiler.append(Integer.toHexString((int)temp));
        }
        return firstTrackDataBuiler.toString().trim().toUpperCase();
    }

    public static String get55Data(String allDataIn,List<String> tagList){
        System.out.println("AllDataIN:"+allDataIn);
        StringBuilder fiveAndFiveData = new StringBuilder();
        Map<String,String > tagMap =  TlvUtils.tlvToMap(allDataIn);
        if(tagList!=null){
            for(String str:tagList){
                fiveAndFiveData.append(TlvUtils.getTag(tagMap,str));
            }
        }
        System.out.println("DATA IN BLOCK 55:"+fiveAndFiveData.toString());
        return fiveAndFiveData.toString();
    }

    public static String get55Data2(String allDataIn,List<String> tagList){
        System.out.println("AllDataIN:"+allDataIn);
        StringBuilder fiveAndFiveData = new StringBuilder();
        Map<String,String > tagMap =  TlvUtils.tlvToMap(allDataIn);
        if(tagList!=null){
            for(String str:tagList){
                fiveAndFiveData.append(TlvUtils.getTag(tagMap,str));
            }
        }
        System.out.println("DATA IN BLOCK 55:"+fiveAndFiveData.toString());
        return fiveAndFiveData.toString();
    }



    public static String getSecondBlock(String track2Data){
        StringBuilder twoData = new StringBuilder();
        track2Data = track2Data.substring(4);

        int position = 0;
        if(track2Data.contains("D")){
            position= track2Data.lastIndexOf("D");
        }else{
            position= track2Data.lastIndexOf("=");
        }
        twoData.append(position+"");
        twoData.append(track2Data.substring(0,position));
        System.out.println("2区域："+twoData.toString().trim());
        return twoData.toString().trim();
    }

    public static String get14BlockData(String track2Data){
        StringBuilder result = new StringBuilder();
        int position = 0 ;
        if(track2Data.contains("D")){
            position= track2Data.lastIndexOf("D");
        }else{
            position = track2Data.lastIndexOf("=");
        }
        result.append(track2Data.substring(position+1,position+5));
        System.out.println("14区域："+result.toString().trim());
        return result.toString().trim();
    }

    /**
     * 固定要求总额传带小数点的数字,要求到小数点后面两位
     * @param validSaleTimesCount
     * @param validSalesMoneyCount
     * @return
     */
    public static String get63BlockData(int validSaleTimesCount,String validSalesMoneyCount,
                                        int validRefundTimesCount,String validRefundMoneyCount,
                                        int validDebitSalesTimesCount,String validDebitSalesMoneyCount,
                                        int validDebitRefundTimesCount,String validDebitRefundMoneyCount,
                                        int validAuthorizeSalesTimesCount,String validAuthorizeSalesMoneyCount,
                                        int validAuthorizeRefundTimesCount,String validAuthorizeRefundMoneyCount){
        StringBuilder result = new StringBuilder();
        //Length
        result.append("0090");
        //Captured Sales Count
        String valiedSalesTimesCountStr = HexUtil.bcd2str(HexUtil.int2bytes(validSaleTimesCount)).substring(5).trim();
        for(int i=0;i<valiedSalesTimesCountStr.length();i++){
            result.append(changeCharToHexString(valiedSalesTimesCountStr.charAt(i)));
        }
        //Sales Amount
        String validSalesMoneyCountStr = getAmount(validSalesMoneyCount);
        for(int i=0;i<validSalesMoneyCountStr.length();i++){
            result.append(changeCharToHexString(validSalesMoneyCountStr.charAt(i)));
        }
        //Refund Count
        String validRefundTimesCountStr = HexUtil.bcd2str(HexUtil.int2bytes(validRefundTimesCount)).substring(5).trim();
        for(int i=0;i<valiedSalesTimesCountStr.length();i++){
            result.append(changeCharToHexString(validRefundTimesCountStr.charAt(i)));
        }
        //Refund Amount
        String validRefundMoneyCountStr = getAmount(validRefundMoneyCount);
        for(int i=0;i<validSalesMoneyCountStr.length();i++){
            result.append(changeCharToHexString(validRefundMoneyCountStr.charAt(i)));
        }
        //Debit Sales Count
        String validDebitSalesTimesCountStr = HexUtil.bcd2str(HexUtil.int2bytes(validDebitSalesTimesCount)).substring(5).trim();
        for(int i=0;i<valiedSalesTimesCountStr.length();i++){
            result.append(changeCharToHexString(validDebitSalesTimesCountStr.charAt(i)));
        }
        //Debit Sales Amount
        String validDebitSalesMoneyCountStr = getAmount(validDebitSalesMoneyCount);
        for(int i=0;i<validSalesMoneyCountStr.length();i++){
            result.append(changeCharToHexString(validDebitSalesMoneyCountStr.charAt(i)));
        }
        //Debit Refund Count
        String validDebitRefundTimesCountStr = HexUtil.bcd2str(HexUtil.int2bytes(validDebitRefundTimesCount)).substring(5).trim();
        for(int i=0;i<valiedSalesTimesCountStr.length();i++){
            result.append(changeCharToHexString(validDebitRefundTimesCountStr.charAt(i)));
        }
        //Debit Refund Amount
        String validDebitRefundMoneyCountStr = getAmount(validDebitRefundMoneyCount);
        for(int i=0;i<validSalesMoneyCountStr.length();i++){
            result.append(changeCharToHexString(validDebitRefundMoneyCountStr.charAt(i)));
        }
        //Authorize Sale Count
        String validAuthorizeSalesTimesCountStr = HexUtil.bcd2str(HexUtil.int2bytes(validAuthorizeSalesTimesCount)).substring(5).trim();
        for(int i=0;i<valiedSalesTimesCountStr.length();i++){
            result.append(changeCharToHexString(validAuthorizeSalesTimesCountStr.charAt(i)));
        }
        //Authorize Sale Amount
        String validAuthorizeSalesMoneyCountStr = getAmount(validAuthorizeSalesMoneyCount);
        for(int i=0;i<validSalesMoneyCountStr.length();i++){
            result.append(changeCharToHexString(validAuthorizeSalesMoneyCountStr.charAt(i)));
        }
        //Author Refund Count
        String validAuthorizeRefundTimesCountStr = HexUtil.bcd2str(HexUtil.int2bytes(validAuthorizeRefundTimesCount)).substring(5).trim();
        for(int i=0;i<valiedSalesTimesCountStr.length();i++){
            result.append(changeCharToHexString(validAuthorizeRefundTimesCountStr.charAt(i)));
        }
        //Author Refund Amount
        String validAuthorizeRefundMoneyCountStr = getAmount(validAuthorizeRefundMoneyCount);
        for(int i=0;i<validSalesMoneyCountStr.length();i++){
            result.append(changeCharToHexString(validAuthorizeRefundMoneyCountStr.charAt(i)));
        }

        return result.toString().trim();
    }

    /**
     * 	对于63域的解释:
     *  举例说明：
     *  sale Count 就是统计之前做过的有效sale的数量（其中被void 撤销的不算）
     *  settle 其实就是统计之前做过的有效sale的总额(我太天才了 言简意赅就懂了)
     *  为了防止以后理解失误，这里做更详细的解释:
     *  比如做了一次sale,10块钱，又做了一次sale，10块钱，这样子，那么Sale Count就填2，然后 sales Amount 就填20
     *  比如做了一次sale,10块钱，又做了一次sale，10块钱，然后做了一次void，10块钱，然后Sale Count 就填1，然后sales Amount 就填10
     *
     * @param validSaleTimesCount
     * @param validSalesMoneyCount
     * @return
     */
    public static String get63BlockData(int validSaleTimesCount,String validSalesMoneyCount){
        StringBuilder result = new StringBuilder();
        //Length
        result.append("0090");
        //Captured Sales Count
        String valiedSalesTimesCountStr = HexUtil.bcd2str(HexUtil.int2bytes(validSaleTimesCount)).substring(5).trim();
        for(int i=0;i<valiedSalesTimesCountStr.length();i++){
            result.append(changeCharToHexString(valiedSalesTimesCountStr.charAt(i)));
        }
        System.out.println("what:"+result.toString());

        //Sales Amount
        String validSalesMoneyCountStr = getAmount(validSalesMoneyCount);
        for(int i=0;i<validSalesMoneyCountStr.length();i++){
            result.append(changeCharToHexString(validSalesMoneyCountStr.charAt(i)));
        }
        System.out.println("what:"+result.toString());
        result.append("303030");
        result.append("303030303030303030303030");
        result.append("303030");
        result.append("303030303030303030303030");
        result.append("303030");
        result.append("303030303030303030303030");
        result.append("303030");
        result.append("303030303030303030303030");
        result.append("303030");
        result.append("303030303030303030303030");
        return result.toString().trim();
    }

    public static String[] getReceivedDataBlock(String receivedData){
        String[] blockReceived = new String[64];
        //delete TUPD , TPDU is 5 bytes
        receivedData = receivedData.substring(5*2);
        //delete Message_Type,Message_Type is 2 bytes
        receivedData = receivedData.substring(2*2);
        //bitmap is always 8 bytes
        String bitmap = receivedData.substring(0,8*2);
        System.out.println("bitmap:"+bitmap);
        //delete bitmap
        receivedData = receivedData.substring(8*2);

        StringBuilder bitmapBuilder = new StringBuilder();
        //we will get a bitmap of bin. So that we can choose the right block;
        for(int i=0;i<bitmap.length();i++){
            bitmapBuilder.append(ChangeFormat.IntHexStrTobinStr(bitmap.charAt(i)+""));
        }
        String binBitmap = bitmapBuilder.toString();
        System.out.println("binBitmap:"+binBitmap);
        for(int j=0;j<binBitmap.length();j++){
            if(binBitmap.charAt(j) == '0'){
                blockReceived[j] = "";
            }else{
                int index = getBlockLength(receivedData,j);
                blockReceived[j] = receivedData.substring(0,index);
                System.out.println("receivedData--"+(j+1)+":"+ blockReceived[j]);
                if(index<receivedData.length()){
                    receivedData = receivedData.substring(index);
                }else{
                    System.out.println("CENTERM TAG:Error in getReceived Data");
                }
            }
        }
        return blockReceived;
    }

    public static int getBlockLength(String receivedData,int blockIndex){
        //IF NOT NEED return 0
        //IF ERROR return -1
        //IF CAN MAKE SURE ,RETURN THE DETAIL LENGTH
        int result = -1;
        switch (blockIndex){
            case (2-1):
                //UNSURE LENGTH
                //PRIMARY ACCOUNT NUMBER
                result = calculateUnSureLength(receivedData,2);
                break;
            case (3-1):
                //n 6
                //PROCESSING CODE
                result = 6;
                break;
            case (4-1):
                //n 12
                //AMOUNT TRANS
                result = 12;
                break;
            case (5-1):
                //n 12
                //AMOUNT SETTLEMENT
                result = 12;
                break;
            case (6-1):
                //n 12
                //AMOUNT
                result = 12;
                break;
            case (7-1):
                //n 10;
                //Transmission data
                result = 10;
                break;
            case (8-1):
                //n 8
                //AMOUNT OR CARDHOLDER FEE
                result = 8;
                break;
            case (9-1):
                //n 8
                //CONVERSATION RATE ,SETTLEMENT
                result = 8;
                break;
            case (10-1):
                //n 8
                //CONVERSION RATE,CARDHOLDER BILLING
                result = 8;
                break;
            case (11-1):
                //n 6
                //SYSTEM TRACE
                result = 6;
                break;
            case (12-1):
                // n 6
                // TIME LOCAL TRANS
                result = 6;
                break;
            case (13-1):
                // n 4
                // DATA LOCAL TRANS
                result = 4;
                break;
            case (14-1):
                // n 4
                // DATA EXPIRATION
                result = 4;
                break;
            case (15-1):
                //n 4
                //SETTLEMENT DATA
                result = 4;
                break;
            case (16-1):
                //n 4
                //CURRENCY CONVERSION DATA
                result = 4;
                break;
            case (17-1):
                //n 4
                //CAPTURE DATE
                result = 4;
                break;
            case (18-1):
                //n 4
                //MERCHANT TYPE PR MERCHANT CATEGORY CODE
                result = 4;
                break;
            case (19-1):
                //n 3，usually will have a zero in left,3+1 = 4
                //ACQUIRING INSTITUTIO(COUNTRY CODE)
                result = 4;
                break;
            case (20-1):
                //n 3，usually will have a zero in left,3+1 = 4
                //PAN EXTENDED(COUNTRY CODE)
                result = 4;
                break;
            case (21-1):
                //n 3，usually will have a zero in left,3+1 = 4
                //FORWARDING INSTITUTION(COUNTRY CODE)
                result = 4;
                break;
            case (22-1):
                //n 3,usually will have a zero in left,3+1 = 4
                //POINT OF SERVICE CONDITIO CODE
                result = 4;
                break;
            case (23-1):
                //n 3,usually will have a zero in left,3+1 = 4
                //APPLICATION PAN SEQUENCE NUMBER
                result = 4;
                break;
            case (24-1):
                // n 3,usually will have a zero in left，3+1 = 4
                //FUNCTION CODE
                result = 4;
                break;
            case (25-1):
                //n 2
                //POINT OF SERVICE CONDITION CODE
                result = 2;
                break;
            case (26-1):
                //n 2
                //POINT OF SERVICE CAPTURE CODE
                result = 2;
                break;
            case (27-1):
                //n 1,usualy will have a zero in left,1+1 = 2
                //AUTHORIZING IDENTIFICATION RESPONSE LENGTH
                result = 2;
                break;
            case (28-1):
                //n x+n 8
                //AMOUNT,TRANSACTION FEE
                result = 0;
                break;
            case (29-1):
                //n x+n 8
                //AMOUNT,SETTLEMENT FEE
                result = 0;
                break;
            case (30-1):
                //n x+8 8
                //AMOUNT,TRANSACTION PROCESSING FEE
                result = 0;
                break;
            case (31-1):
                //n x+n 8
                //AMOUNT,SETTLEMENT PROCESSING FEE
                result = 0;
                break;
            case (32-1):
                //n ...11
                //使用1字节BCD表示的长度 + 用右靠BCD码表示最大11个字节的变长域
                //ACQUIRING INSTITUTION IDENTIFICATION CODE
                //根据我个人的理解，因为用1位表示长度，因此长长左边会补0，因此我们截取两位
                result = calculateUnSureLength(receivedData,2);
                break;
            case (33-1):
                //n ...11
                //使用1字节BCD表示的长度 + 用右靠BCD码表示最大11个字节的变长域
                //FORWARDING INSTITUTION IDENTIFICATIOON CODE
                result = calculateUnSureLength(receivedData,2);
                break;
            case (34-1):
                //PRIMARY ACCOUNT NUMBER EXTENDED
                result = calculateUnSureLength(receivedData,2);
                break;
            case (35-1):
                //UNSURE LENGTH
                //TRACK TWO DATA
                result = calculateUnSureLength(receivedData,2);
                break;
            case (36-1):
                //UNSURE LENGTH
                //TRACK THREE DATA
                result = calculateUnSureLength(receivedData,2);
                break;
            case (37-1):
                //an 12
                //RETRIEVAL REFERENCE NUMBER
                result = 24;
                break;
            case (38-1):
                //an 6
                //AUTHORIZATIO IDENTIFICATION RESPONSE
                result = 12;
                break;
            case (39-1):
                //an 2
                //RESPONSE CODE
                result = 4;
                break;
            case (40-1):
                //an 3
                //SERVICE RESTRICTION CODE
                result = 6;
                break;
            case (41-1):
                //ans 8
                //CARD ACCEPTOR TERMINAL IDENTIFICATION
                result = 16;
                break;
            case (42-1):
                //ans 15;
                //CARD ACCEPTOR IDENTIFICATION CODE
                result = 30;
                break;
            case (43-1):
                //ans 40
                //CARD ACCEPTOR NAME NAME/LOCATION
                result = 80;
                break;
            case (44-1):
                //an...25
                //UNSURE LENGTH
                //ADDTIONAL RESPONSE DATA
                result = calculateUnSureLength(receivedData,2);
                break;
            case (45-1):
                //an...76
                //UNSURE LENGTH
                //TRACK ONE DATA
                //不确定读取两位是否足够
                result = calculateUnSureLength(receivedData,2);
                break;
            case (46-1):
                //an ...999
                //ADDITIONAL DATA（ISO）
                result = calculateUnSureLength(receivedData,4);
                break;
            case (47-1):
                //an ...999
                //ADDITIONAL DATA(NATIONAL)
                result = calculateUnSureLength(receivedData,4);
                break;
            case (48-1):
                //an ...999
                //ADDITIONAL DATA(PRIVATE)
                result = calculateUnSureLength(receivedData,4);
                break;
            case (49-1):
                //a or n3
                //NEED TO CHECK USE A OR N3
                //CURRENCY CODE TRANSACTION，if use n 3.We usually add a zero in left
                result = 4;
                break;
            case (50-1):
                //a or n3
                //NEED TO CHECK USE A OR N3
                //CURRENCY CODE SETTLEMENT，if use n 3.We usually add a zero in left
                result = 4;
                break;
            case (51-1):
                //a or n3
                //NEED TO CHECK USE A OR N3
                //CURRENCY CODE CADHOLDER BILLING，if use n 3.We usually add a zero in left
                result = 4;
                break;
            case (52-1):
                //b 8 b表示8个字节的定长二进制数
                //PERSONAL IDENTIFICATION NUMBER DATA
                result = 8;
                break;
            case (53-1):
                // n 16
                // SECURITY RELATED CONTROL INFORMATION
                result = 16;
                break;
            case (54-1):
                // an ...120
                // ADDITIONAL AMOUNT
                result = calculateUnSureLength(receivedData,4);
                break;
            case (55-1):
                // ans ...999
                // ICC DATA --EMV having multiple tags
                result = calculateUnSureLength(receivedData,4);
                break;
            case (56-1):
                // ans ...999
                // Reserved(ISO)
                result = calculateUnSureLength(receivedData,4);
                break;
            case (57-1):
                //ans ...999
                //UNKNOW DATA
                result = calculateUnSureLength(receivedData,4);
                break;
            case (58-1):
                //ans ...999
                //UNKNOW DATA
                result = calculateUnSureLength(receivedData,4);
                break;
            case (59-1):
                //ans ...999
                //UNKNOW DATA
                result =calculateUnSureLength(receivedData,4);
                break;
            case (60-1):
                //ans ...999
                //RESERVED
                result = calculateUnSureLength(receivedData,4);
                break;
            case (61-1):
                //ans ...999
                //RESERVED
                result = calculateUnSureLength(receivedData,4);
                break;
            case (62-1):
                //ans ...999
                //RESERVED
                result = calculateUnSureLength(receivedData,4);
                break;
            case (63-1):
                //ans ...999
                //RESERVED
                result = calculateUnSureLength(receivedData,4);
                break;
            case (64-1):
                //b 16
                //MESSAGE AUTHENTICATION CODE(MAC)
                result = 16;
                break;
            default:
                break;
        }
        return result;
    }

    private static int calculateUnSureLength(String receivedData,int lengthBit) {
        int length =  0;
        if(receivedData==null){
            return -1;
        }
        if(receivedData.length()<lengthBit){
            return -1;
        }
        String lengthStr = receivedData.substring(0,lengthBit);
        for(int i=0;i<lengthBit;i++){
            int number =  Integer.valueOf(changeCharToHexString(lengthStr.charAt(i)));
            number = number-30;
            for(int j=0;j<(lengthBit-1-i);j++){
                number = number *10;
            }
            length = length + number;
        }
        return length*2+lengthBit;
    }

    /*
    public static String[] getReceivedDataBlock(String receivedData){
        String[] blockReceived = new String[64];
        //delete TUPD , TPDU is 5 bytes
        receivedData = receivedData.substring(5*2);
        //delete Message_Type,Message_Type is 2 bytes
        receivedData = receivedData.substring(2*2);
        //bitmap is always 8 bytes
        String bitmap = receivedData.substring(0,8*2);
        //System.out.println("bitmap:"+bitmap);
        //Log.d("CENTERM APP", "bitmap: "+bitmap);
        //delete bitmap
        receivedData = receivedData.substring(8*2);

        System.out.println("白猴大将军:"+receivedData);
        StringBuilder bitmapBuilder = new StringBuilder();
        //we will get a bitmap of bin. So that we can choose the right block;
        for(int i=0;i<bitmap.length();i++){
            bitmapBuilder.append(ChangeFormat.IntHexStrTobinStr(bitmap.charAt(i)+""));
        }
        String binBitmap = bitmapBuilder.toString();
        System.out.println("binBitmap:"+binBitmap);
        //Log.d("CENTERM APP", "binBitmap: "+binBitmap);
        for(int j=0;j<binBitmap.length();j++){
            if(binBitmap.charAt(j) == '0'){
                blockReceived[j] = "";
            }else{
                int index;
                if(j == (2-1))
                {
                    Log.d("CENTERM APP", "got field 2!!!");
                    int field_len = 2; // 1 byte = 2 characters
                    String length_str = receivedData.substring(0, field_len);
                    Log.d("CENTERM APP", "length string = "+length_str);
                    int length_num = Integer.parseInt(length_str);
                    Log.d("CENTERM APP", "field2 length = "+length_num);
                    blockReceived[j] = receivedData.substring(field_len, length_num+field_len);
                    receivedData = receivedData.substring(length_num+field_len);
                    Log.d("CENTERM APP", "field2 receive data => "+blockReceived[j]);
                }
                else
                {
                    index = getBlockLength(receivedData,j);
                    blockReceived[j] = receivedData.substring(0,index);
                    System.out.println("receivedData--"+(j+1)+":"+ blockReceived[j]);
                    receivedData = receivedData.substring(index);
                }
            }
        }
        return blockReceived;
    }

    public static String[] getReceivedDataBlock(String receivedData){
        String[] blockReceived = new String[64];
        //delete TUPD , TPDU is 5 bytes
        receivedData = receivedData.substring(5*2);
        //delete Message_Type,Message_Type is 2 bytes
        receivedData = receivedData.substring(2*2);
        //bitmap is always 8 bytes
        String bitmap = receivedData.substring(0,8*2);
        System.out.println("bitmap:"+bitmap);
        //delete bitmap
        receivedData = receivedData.substring(8*2);

        StringBuilder bitmapBuilder = new StringBuilder();
        //we will get a bitmap of bin. So that we can choose the right block;
        for(int i=0;i<bitmap.length();i++){
            bitmapBuilder.append(ChangeFormat.IntHexStrTobinStr(bitmap.charAt(i)+""));
        }
        String binBitmap = bitmapBuilder.toString();
        System.out.println("binBitmap:"+binBitmap);
        for(int j=0;j<binBitmap.length();j++){
            if(binBitmap.charAt(j) == '0'){
                blockReceived[j] = "";
            }else{
                int index = getBlockLength(receivedData,j);
                if(index==-1){
                    //IF THE LENGTH IS -1,THAT MEANS THE LENGTH CAN NOT MAKE SURE
                    //WE NEED THIS VALUE IN THE FORMAT OF TLV
                    if(receivedData.startsWith("91")||receivedData.startsWith("71")||receivedData.startsWith("72")){
                        index = receivedData.length();
                    }
                }
                blockReceived[j] = receivedData.substring(0,index);
                System.out.println("receivedData--"+(j+1)+":"+ blockReceived[j]);
                if(receivedData.length()!=index){
                    receivedData = receivedData.substring(index);

                }
            }
        }
        return blockReceived;
    }

    public static int getBlockLength(String receivedData,int blockIndex){
        //IF CAN NOT MAKE SURE THE LENGTH,RETURN -1;
        //IF CAN MAKE SURE ,RETURN THE DETAIL LENGTH
        int result = -1;
        switch (blockIndex){
            case (3-1):
                //n 6
                //PROCESSING CODE
                result = 6;
                break;
            case (4-1):
                //n 12
                //AMOUNT TRANS
                result = 12;
                break;
            case (11-1):
                //n 6
                //SYSTEM TRACE
                result = 6;
                break;
            case (12-1):
                // n 6
                // TIME LOCAL TRANS
                result = 6;
                break;
            case (13-1):
                // n 4
                // DATA LOCAL TRANS
                result = 4;
                break;
            case (14-1):
                // n 6
                // DATA EXPIRATION
                result = 4;
                break;
            case (24-1):
                // n 3,usually will have a zero in left，3+1 = 4
                result = 4;
                break;
            case (37-1):
                //an 12
                result = 24;
                break;
            case (38-1):
                //an 6
                result = 12;
                break;
            case (39-1):
                //an 2
                result = 4;
                break;
            case (41-1):
                //ans 8
                result = 16;
                break;
            default:
                break;
        }
        return result;

    }
    */


    /* Original
    public static int getBlockLength(String receivedData,int blockIndex){
        int result = 0;
        switch (blockIndex){
            case (1-1):
                return 0;
            case (2-1): //不确定长度
                return 0;
            case (3-1):
                //n 6
                result = 6;
                break;
            case (4-1):
                //n 12
                result = 12;
                break;
            case (5-1):
                break;
            case (11-1):
                //n 6
                result = 6;
                break;
            case (12-1):
                // n 6
                result = 6;
                break;
            case (13-1):
                // n 4
                result = 4;
                break;
            case (14-1):
                // n 6
                result = 4;
                break;
            case (23-1):
                // n 23
                result = 4;
                break;
            case (24-1):
                // n 3,usually will have a zero in left，3+1 = 4
                result = 4;
                break;
            case (25-1):
                // n 25
                result = 2;
                break;
            case (37-1):
                //an 12
                result = 24;
                break;
            case (38-1):
                //an 6
                result = 12;
                break;
            case (39-1):
                //an 2
                result = 4;
                break;
            case (41-1):
                //ans 8
                result = 16;
                break;
            case (42-1):
                //ans 15
                result = 30;
                break;
        }
        return result;

    }
    */

    public static String changeBinStringToHexString(String binStr){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<binStr.length();i++){
            stringBuilder.append(changeCharToHexString(binStr.charAt(i)));
        }
        return stringBuilder.toString();

    }

    public static String changeCharToHexString(char c){
        switch (c){
            case '0':
                return "30";
            case '1':
                return "31";
            case '2':
                return "32";
            case '3':
                return "33";
            case '4':
                return "34";
            case '5':
                return "35";
            case '6':
                return "36";
            case '7':
                return "37";
            case '8':
                return "38";
            case '9':
                return "39";
            case 'A':
                return "41";
            case 'B':
                return "42";
            case 'C':
                return "43";
            case 'D':
                return "44";
            case 'E':
                return "45";
            case 'F':
                return "46";
            case ' ':
                return "20";
            default:
                return null;
        }

    }

    /**
     * You can get the tag Value from the kernel Data
     * @param tag
     * @param sourceData
     * @return
     */
    public static String getTagValue(String tag,String sourceData){
        String result = null;
        if(tag == null || TextUtils.isEmpty(tag)){
            Log.d("getTagValue","tag is null or empty");
            return null;
        }
        if(sourceData==null){
            return null;
        }else{
            if(sourceData.contains("null")){
                sourceData = sourceData.replaceAll("null","");
            }
            if(TextUtils.isEmpty(sourceData)){
                Log.d("getTagValue","sourceData is empty");
                return null;
            }
        }

        Map<String,String > tagMap =  TlvUtils.tlvToMap(sourceData);
        Iterator iterator = tagMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();
            String key = (String) entry.getKey();
            String value = (String)entry.getValue();
            if(key.equals(tag)){
                result = value;
            }
        }
        return result;
    }

    /**
     * If the message contain null,delete it
     * @return
     */
    public static String checkMessage(String str){
        return str.replaceAll("null","").trim();
    }

    public static String checkResult(String dataFromThityNine){
        String results = "";
        if(dataFromThityNine.equals("3030")){
            results = "Approved";
        }else if(dataFromThityNine.equals("3031")){
            results = "PLS CALL ISSUER: REFER TO CARD ISSUER";
        }else if(dataFromThityNine.equals("3032")){
            results = "REFERRAL: Refer to card Issuer's special conditions ";
        }else if(dataFromThityNine.equals("3033")){
            results = "CALL HELP – SN: Invalid merchant ";
        }else if(dataFromThityNine.equals("3034")){
            results = "PICKUP CARD: Pick-up : ";
        }else if(dataFromThityNine.equals("3035")){
            results = "DO NOT HONOUR: Do not honour ";
        }else if(dataFromThityNine.equals("3132")){
            results = "CALL HELP – TR: Invalid transaction ";
        }else if(dataFromThityNine.equals("3133")){
            results = "CALL HELP - AM: Invalid amount ";
        }else if(dataFromThityNine.equals("3134")){
            results = "CALL HELP - RE: Invalid card number ";
        }else if(dataFromThityNine.equals("3139")){
            results = "RE-ENTER TRANS: Re-enter transaction";
        }else if(dataFromThityNine.equals("3121")){
            results = "NO TRANSACTIONS: NO TRANSACTIONS";
        }else if(dataFromThityNine.equals("3235")){
            results = "CALL HELP - NT: No Transactions ";
        }else if(dataFromThityNine.equals("3330")){
            results = "CALL HELP - FE: Format error";
        }else if(dataFromThityNine.equals("3331")){
            results = "CALL HELP - NS: Bank not supported by switch ";
        }else if(dataFromThityNine.equals("3431")){
            results = "PLEASE CALL – LC: Lost card ";
        }else if(dataFromThityNine.equals("3433")){
            results = "PLEASE CALL – CC: Stolen card pick up ";
        }else if(dataFromThityNine.equals("3531")){
            results = "DECLINED: Not sufficient funds";
        }else if(dataFromThityNine.equals("3532")){
            results = "NO CHEQUE ACC: No chequing account";
        }else if(dataFromThityNine.equals("3533")){
            results = "NO SAVINGS ACC: No savings account";
        }else if(dataFromThityNine.equals("3534")){
            results = "EXPIRED CARD: Expired card";
        }else if(dataFromThityNine.equals("3535")){
            results = "INCORRECT PIN: Incorrect PIN";
        }else if(dataFromThityNine.equals("3536")){
            results = "NO CARD RECORD: No card record";
        }else if(dataFromThityNine.equals("3538")){
            results = "INVALID TRANS: No card record";
        }else if(dataFromThityNine.equals("3631")){
            results = "EXCEEDS LIMIT: Exceeds withdrawal amount limit";
        }else if(dataFromThityNine.equals("3633")){
            results = "SECURITY VIOLATE: Security violation ";
        }else if(dataFromThityNine.equals("3735")){
            results = "PIN TRIES EXCEED: Allowable number of PIN tries exceeded ";
        }else if(dataFromThityNine.equals("3736")){
            results = "CALL HELP – DC: Invalid product code ";
        }else if(dataFromThityNine.equals("3737")){
            results = "RECONCILE ERROR: Reconcile error (or host text if sent) ";
        }else if(dataFromThityNine.equals("3738")){
            results = "TRANS NOT FOUND: Trans. number not found ";
        }else if(dataFromThityNine.equals("3739")){
            results = "BATCH ALRDY OPEN: Batch already open";
        }else if(dataFromThityNine.equals("3830")){
            results = "BAD BATCH NUMBER: Batch number not found ";
        }else if(dataFromThityNine.equals("3835")){
            results = "BATCH NOT FOUND: Batch not found";
        }else if(dataFromThityNine.equals("3839")){
            results = "BAD TERMINAL ID: Bad Terminal ID";
        }else if(dataFromThityNine.equals("3931")){
            results = "CALL HELP - NA: Issuer or switch inoperative";
        }else if(dataFromThityNine.equals("3934")){
            results = "CALL HELP - SQ: Duplicate transmission ";
        }else if(dataFromThityNine.equals("3935")){
            results = "BATCH TRANSFER:Reconcile error. Batch upload started";
        }else if(dataFromThityNine.equals("3936")){
            results = "CALL HELP - SE: System malfunction";
        }else{
            results = "FAIL ，UNKNOW REASON";
        }
        return results;

    }

    public static String getExpireData(String data)
    {
        int position = 0;
        data = data.replace(" ", "");
        data = data.trim();
        if(data.contains("=")){
            position = data.indexOf("=");
        }
        if(data.contains("D")){
            position = data.indexOf("D");
        }
        String expirate = data.substring(position+1,position+5);
        return expirate;
    }

    public static String calculateApplicationData(String[] mBlockDatas){
        StringBuilder bitmap = new StringBuilder();
        StringBuilder blockData = new StringBuilder();
        StringBuilder hexBitmap = new StringBuilder();
        int j=0;
        if((mBlockDatas!=null)&&(mBlockDatas.length==64)){
            bitmap.append("");
            blockData.append("");
            for(String str:mBlockDatas){
                //Log.d("CENTERM APP", str);
                if(str==null||str.trim().equals("")){
                    bitmap.append("0");
                    //Log.d("CENTERM APP", "mBlock:"+j+"----null");
                }else{
                    //Log.d("CENTERM APP", "mBlock:"+j+"----"+str);
                    bitmap.append("1");
                    blockData.append(str);
                }
                j= j+1;
            }

        }
        String bimapchangeData = bitmap.toString().trim();

        for(int i=0;i<(bimapchangeData.length()/4);i++)
        {
            //Log.d("CENTERM APP", "TAG OF BITMAP："+bimapchangeData.substring(i*4,i*4+4));
            hexBitmap.append(binStrToIntHexStr(bimapchangeData.substring(i*4,i*4+4)));
        }
        return hexBitmap.toString()+blockData.toString();
    }

    public static String ASCII2Str(String ASCIIStr){
        StringBuilder stringBuilder = new StringBuilder();
        if(ASCIIStr.length()%2!=0){
            return null;
        }
        for(int i=0;i<ASCIIStr.length();i=i+2){
            stringBuilder.append(strToHexToChar(ASCIIStr.substring(i,i+2)));
        }
        return stringBuilder.toString();
    }

    public static char strToHexToChar(String twoBitStr){
        char result = '?' ;
        int ascii = charToHexValue(twoBitStr.charAt(0))*16+charToHexValue(twoBitStr.charAt(1));
        result = (char)ascii;
        return result;
    }

    public static int charToHexValue(char c){
        switch (c){
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            case 'A':
                return 10;
            case 'a':
                return 10;
            case 'B':
                return 11;
            case 'b':
                return 11;
            case 'C':
                return 12;
            case 'c':
                return 12;
            case 'D':
                return 13;
            case 'd':
                return 13;
            case 'E':
                return 14;
            case 'e':
                return 14;
            case 'F':
                return 15;
            case 'f':
                return 15;
            default:
                return 0;
        }
    }

    /**
     * The value you input should not including the length of all data
     * @param str
     * @param addValue
     * @return
     */
    public static String checkresponse(String str,String addValue){
        Map<String,String > tagMap =  TlvUtils.tlvToMap(str);
        Iterator iterator = tagMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();
            String key = (String) entry.getKey();
            String value = (String)entry.getValue();
            if(key.equals("91")){
                if(value.length()<20){
                    int lengthofTagNineOne = value.length()/2;
                    // You may ask me why I add 0 here ,because the length is fron 8~16,the length is always enough
                    String tempStr = "91"+"0"+changeInt2Hex(lengthofTagNineOne)+value;
                    int hexLengthofTagNineOne =  lengthofTagNineOne+(addValue.length()/2);
                    String newLength = changeInt2Hex(hexLengthofTagNineOne);
                    String newValue = null;
                    if(newLength.equals("G")){
                        newValue = "91"+ "10"+value+addValue;
                    }else{
                        newValue = "91"+ "0"+ newLength + value+addValue;
                    }
                    str = str.replace(tempStr,newValue);
                }
            }
        }
        return str;
    }


    public static String changeInt2Hex(int number){
        if(number<10){
            return number+"";
        }else{
            switch(number){
                case 10:
                    return "A";
                case 11:
                    return "B";
                case 12:
                    return "C";
                case 13:
                    return "D";
                case 14:
                    return "E";
                case 15:
                    return "F";
                case 16:
                    return "G";
                default:
                    return null;
            }

        }
    }

    public static String hexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        char[] hexData = hex.toCharArray();
        for (int count = 0; count < hexData.length - 1; count += 2) {
            int firstDigit = Character.digit(hexData[count], 16);
            int lastDigit = Character.digit(hexData[count + 1], 16);
            int decimal = firstDigit * 16 + lastDigit;
            sb.append((char)decimal);
        }
        return sb.toString();
    }


}
