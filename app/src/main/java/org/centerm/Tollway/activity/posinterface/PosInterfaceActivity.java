package org.centerm.Tollway.activity.posinterface;

import android.os.CountDownTimer;
import android.os.RemoteException;
import android.util.Log;

import com.centerm.smartpos.aidl.serialport.AidlSerialPort;
import com.centerm.smartpos.util.HexUtil;

import org.centerm.Tollway.CardManager;
import org.centerm.Tollway.MainApplication;

import java.nio.charset.StandardCharsets;

import static org.centerm.Tollway.utility.Utility.BCDtoInt;
import static org.centerm.Tollway.utility.Utility.IntToBCD;
import static org.centerm.Tollway.utility.Utility.JavaHexDump;


// Make class Paul Start 2018-06-23
public class PosInterfaceActivity {

    String TAG = "utility:: PosInterfaceActivity ";

    AidlSerialPort serialPort1 = null;
    AidlSerialPort serialPort2 = null;

    public static int PosInterfaceExistFlg=0;
    public static String PosInterfaceReceiveData = null;
    public static String PosInterfaceReserve = null;
    public static String PosInterfaceFormatVersion = null;
    public static String PosInterfaceReqResIndicator = null;
    public static String PosInterfaceTransactionCode = null;
    public static String PosInterfaceResponseCode = null;
    public static String PosInterfaceMoreDataIndicator = null;
    public static int PosInterfaceTotalFieldCnt=0;
    public static String[] PosInterfaceFieldType = new String[64];
    public static int[] PosInterfaceFieldLength = new int[64];
    public static String[] PosInterfaceFieldData = new String[64];

    public static int PosInterfaceSnedTotalFieldCnt=0;
    public static String[] PosInterfaceSnedFieldType = new String[64];
    public static int[] PosInterfaceSnedFieldLength = new int[64];
    public static String[] PosInterfaceSnedFieldData = new String[64];

    public static byte[] RealSerialRecBuf = new byte[2048+1];

    private CardManager cardManager = null;
    private int SuccessFlg;
    private byte[] SerialReceiveBuf;
    private CountDownTimer countDownTimer;
    private int count;
    private String SendString;
    private boolean isReading;

    public void PosInterfaceOpen() throws RemoteException {
        cardManager = MainApplication.getCardManager();
       // serialPort1 = cardManager.getInstancesSerial1();

        //serialPort1.open( 9600 );
//20180724 SINN cash rs232
        try {
            if(serialPort1 != null)             // Paul_20181127
                serialPort1.open(9600);
        }catch (Exception e){

 //Jeff20181214
//            cardManager = MainApplication.getCardManager();
//            serialPort1 = cardManager.getInstancesSerial1();
//            if(serialPort1 != null)
//            serialPort1.close();
//            if(serialPort1 != null)  //SINN 20180918 fix reboot fail
//            serialPort1.open(9600); // Paul_20180730



//            cardManager = MainApplication.getCardManager();
//            serialPort1 = cardManager.getInstancesSerial1();
//            serialPort1.open(9600);
        }


        // Paul_20180711_new
//        byte[] aaa = new byte[1];
//        aaa[0] = 0x06;
//        serialPort1.sendData( aaa );
    }

    public void PosInterfaceClose() throws RemoteException {
        cardManager = MainApplication.getCardManager();
        //serialPort1 = cardManager.getInstancesSerial1();
        if(serialPort1 != null) {
            serialPort1.close();
        }
    }

    public void POSInterfaceInit() {
        int i;

        PosInterfaceExistFlg = 0;
        PosInterfaceReceiveData = null;
        PosInterfaceReserve = null;
        PosInterfaceFormatVersion = null;
        PosInterfaceReqResIndicator = null;
        PosInterfaceTransactionCode = null;
        PosInterfaceResponseCode = null;
        PosInterfaceMoreDataIndicator = null;
        PosInterfaceTotalFieldCnt = 0;
        for(i=0;i<64;i++)
        {
            PosInterfaceFieldType[i] = null;
            PosInterfaceFieldLength[i] = 0;
            PosInterfaceFieldData[i] = null;
        }
        PosInterfaceSnedTotalFieldCnt = 0;
        for(i=0;i<64;i++)
        {
            PosInterfaceSnedFieldType[i] = null;
            PosInterfaceSnedFieldLength[i] = 0;
            PosInterfaceSnedFieldData[i] = null;
        }
    }

    //    @SuppressLint("LongLogTag")
    // return 0 : success
    // return 1 : Format Error
    // return 2 : no matching
    public int CheckPOSInterface(String InputPOSInterfaceData)
    {
        String szRecBuf = null;
        int inSize = 0;
        byte CheckSum = 0;
        byte[] RecBuf = new byte[2048 + 1];
        int i;
        int InLength = 0;
        int PosiCnt = 0;
        byte[] TempBuf = new byte[2048 + 1];
        int rv;

        System.out.printf("utility:: CheckPOSInterface 000 Start \n");

        szRecBuf = InputPOSInterfaceData;
//        szRecBuf = "02003530303030303030303030313031313030301C343000123030303030303031303030301C0311";

        inSize = szRecBuf.length();
        CheckSum = 0;
        RecBuf = HexUtil.hexStringToByte(szRecBuf);
        System.out.printf("utility:: ");
        inSize = inSize / 2;
        for (i = 0; i < inSize; i++) {
            System.out.printf("[%02X]", RecBuf[i]);
        }
        System.out.printf("\n");
//        if (!szRecBuf.substring(0, 2).equalsIgnoreCase("02"))
        if (RecBuf[0] != (byte) 0x02) {
            System.out.printf("utility:: CheckPOSInterface 001 STX Error \n");
            return 1;
        }
        if (inSize < 5) {
            System.out.printf("utility:: CheckPOSInterface 002 total Length Error \n");
            return 1;
        }
        InLength = BCDtoInt(RecBuf[1],RecBuf[2]);
//        System.out.printf("utility:: CheckPOSInterface 003 InLength = %d \n", InLength);
//        InLength = (((RecBuf[1] & (byte) 0xF0) >> 4) * 1000) + ((RecBuf[1] & (byte) 0x0F) * 100) + (((RecBuf[2] & (byte) 0xF0) >> 4) * 10) + ((RecBuf[2] & (byte) 0x0F) * 1);
        System.out.printf("utility:: inTescoReceiveAnalyse 003 InLength = %d \n", InLength);
        if (InLength > inSize) {
            System.out.printf("utility:: CheckPOSInterface 004 Length Error \n");
            return 1;
        }
        CheckSum = (byte) 0x00;
        CheckSum = (byte)(CheckSum ^ RecBuf[0]);      // STX    // Paul_20180705
        CheckSum = (byte) (CheckSum ^ RecBuf[1]);
        CheckSum = (byte) (CheckSum ^ RecBuf[2]);
        System.out.printf("utility:: Data= ");
        for (i = 0; i < InLength; i++) {
            System.out.printf("[%02X]",RecBuf[3 + i]);
            CheckSum = (byte) (CheckSum ^ RecBuf[3 + i]);
        }
        System.out.printf("\n");
        if(RecBuf[3 + i] != (byte)0x03)
        {
            System.out.printf("utility:: CheckPOSInterface 005 ETX Error = [%02X] \n",RecBuf[3 + i]);
            return 1;
        }
//        System.out.printf("utility:: ETX [%02X]\n", RecBuf[3 + i]);
        CheckSum = (byte) (CheckSum ^ RecBuf[3 + i]);                  // ETX
//        System.out.printf("utility:: LRC [%02X]\n", RecBuf[3 + i + 1]);
//        System.out.printf("utility:: CheckSum [%02X]\n", CheckSum);
        if(RecBuf[3 + i + 1] != CheckSum)
        {
//            System.out.printf("utility:: CheckPOSInterface 006 LRC Error \n");
//            return 1;
        }
        System.out.printf("utility:: CheckPOSInterface 006 Success \n");

        POSInterfaceInit();


        PosiCnt = 3;
        System.arraycopy(RecBuf, PosiCnt, TempBuf, 0, 10);
        PosInterfaceReserve = new String(TempBuf, 0, 10, StandardCharsets.UTF_8);
        Log.d(TAG,"PosInterfaceReserve = "+PosInterfaceReserve);
        PosiCnt += 10;

        System.out.printf("utility:: PosInterfaceReserve = %s \n", PosInterfaceReserve);
        System.arraycopy(RecBuf, PosiCnt, TempBuf, 0, 1);
        PosInterfaceFormatVersion = new String(TempBuf, 0, 1, StandardCharsets.UTF_8);
        Log.d(TAG,"PosInterfaceFormatVersion = "+PosInterfaceFormatVersion);
        PosiCnt++;

        System.arraycopy(RecBuf, PosiCnt, TempBuf, 0, 1);
        PosInterfaceReqResIndicator = new String(TempBuf, 0, 1, StandardCharsets.UTF_8);
        Log.d(TAG,"PosInterfaceReqResIndicator = "+PosInterfaceReqResIndicator);
        PosiCnt++;

        System.arraycopy(RecBuf, PosiCnt, TempBuf, 0, 2);
        PosInterfaceTransactionCode = new String(TempBuf, 0, 2, StandardCharsets.UTF_8);
        Log.d(TAG,"PosInterfaceTransactionCode = "+PosInterfaceTransactionCode);
        PosiCnt += 2;

        System.arraycopy(RecBuf, PosiCnt, TempBuf, 0, 2);
        PosInterfaceResponseCode = new String(TempBuf, 0, 2, StandardCharsets.UTF_8);
        Log.d(TAG,"PosInterfaceResponseCode = "+PosInterfaceResponseCode);
        PosiCnt += 2;

        System.arraycopy(RecBuf, PosiCnt, TempBuf, 0, 1);
        PosInterfaceMoreDataIndicator = new String(TempBuf, 0, 1, StandardCharsets.UTF_8);
        Log.d(TAG,"PosInterfaceMoreDataIndicator = "+PosInterfaceMoreDataIndicator);
        PosiCnt++;

        if (RecBuf[PosiCnt] != (byte) 0x1C) {
            System.out.printf("utility:: PosInterfaceMoreDataIndicator 007 Field Separator Error \n");
            return 2;
        }
        PosiCnt++;
//        System.out.printf("utility:: FieldSeparator = [%02X] \n", FieldSeparator);
//        System.out.printf("utility:: ");
//        byte[] FieldType = new byte[2];
//        int FieldLength;
//        byte[] FieldData = new byte[1024+1];

//        inSize = PosiCnt-3;
//        System.out.printf("utility:: InLength = %d \n",InLength);
//        System.out.printf("utility:: PosiCnt = %d \n",PosiCnt);

//        byte[] ReceiveBuf=new byte[];
//        System.out.printf("utility:: ");
//        for (i = 0; i < (InLength-PosiCnt); i++) {
//            System.out.printf("[%02X]", RecBuf[i+PosiCnt]);
//        }
//        System.out.printf("\n");

        System.arraycopy(RecBuf, PosiCnt, TempBuf, 0,InLength-PosiCnt+3);
//        PosInterfaceReceiveData = new String(RecBuf,PosiCnt,InLength-PosiCnt-2,StandardCharsets.UTF_8  );

        PosInterfaceReceiveData = HexUtil.bytesToHexString(TempBuf).substring( 0,(InLength-PosiCnt+3)*2 );
        Log.d(TAG,"PosInterfaceReceiveData = "+PosInterfaceReceiveData);
        rv = PosInterfaceFieldPadding();
        if(rv != 0)
        {
            return 2;
        }
        return 0;
    }

    public int PosInterfaceFieldPadding()
    {
        int inSize;
        String szRecBuf;
        byte[] RecBuf = new byte[2048 + 1];
        int i;
        int PosiCnt;
        byte[] TempBuf = new byte[2048 + 1];

        szRecBuf = PosInterfaceReceiveData;
        inSize = szRecBuf.length();
        RecBuf = HexUtil.hexStringToByte(szRecBuf);

        System.out.printf("utility:: ");
        inSize = inSize / 2;
        for (i = 0; i < inSize; i++) {
            System.out.printf("[%02X]", RecBuf[i]);
        }
        System.out.printf("\n");

        PosiCnt = 0;
        PosInterfaceTotalFieldCnt = 0;
        for(i=0;(i<64) && (PosiCnt<inSize);i++)
        {
            System.arraycopy(RecBuf, PosiCnt, TempBuf, 0, 2);
            PosInterfaceFieldType[PosInterfaceTotalFieldCnt] = new String(TempBuf, 0, 2, StandardCharsets.UTF_8);
            Log.d(TAG,"PosInterfaceFieldType = "+PosInterfaceFieldType[PosInterfaceTotalFieldCnt]);
            PosiCnt += 2;
            PosInterfaceFieldLength[PosInterfaceTotalFieldCnt] = BCDtoInt(RecBuf[PosiCnt],RecBuf[PosiCnt+1]);
            if(PosInterfaceFieldLength[PosInterfaceTotalFieldCnt] > inSize)
            {
                System.out.printf("utility:: PosInterfaceFieldPadding Length Error  Error PosInterfaceFieldLength[PosInterfaceTotalFieldCnt] \n");
                return 1;
            }
            PosiCnt += 2;
            System.arraycopy(RecBuf, PosiCnt, TempBuf, 0, PosInterfaceFieldLength[PosInterfaceTotalFieldCnt]);
            PosInterfaceFieldData[PosInterfaceTotalFieldCnt] = new String(TempBuf, 0, PosInterfaceFieldLength[PosInterfaceTotalFieldCnt], StandardCharsets.UTF_8);
            Log.d(TAG,"PosInterfaceFieldData = "+PosInterfaceFieldData[PosInterfaceTotalFieldCnt]);
            PosiCnt += PosInterfaceFieldLength[PosInterfaceTotalFieldCnt];
            if(PosiCnt>inSize)
            {
                System.out.printf("utility:: PosInterfaceFieldPadding 001 Error \n");
                return 1;
            }
            if(RecBuf[PosiCnt] != (byte)0x1C)
            {
                if(PosiCnt == inSize)
                {
                    PosInterfaceTotalFieldCnt++;
                    return 0;
                }
                System.out.printf("utility:: PosInterfaceFieldPadding FieldSeparator 002 Error \n");
                return 1;
            }
            PosiCnt++;
            PosInterfaceTotalFieldCnt++;
        }
        PosInterfaceSendData("06");     // ACK SEND Paul_20180703
        return 0;
    }

    public void PosInterfaceSendMessage(String TranCode,String ResponseCode)
    {
        SendString=null;
        int TotalLen;
        byte[] SendBuf = new byte[2048+1];
        String TempString=null;
        int i;
        byte CheckSum;

        try {
            PosInterfaceClose();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            PosInterfaceOpen();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if((PosInterfaceReserve == null) || (PosInterfaceExistFlg == 0)) // Paul_20180713
        {
            return;
        }
        cardManager = MainApplication.getCardManager();
        //serialPort1 = cardManager.getInstancesSerial1();
        TotalLen = 0;
        SendBuf[TotalLen++] = 0x02;
        SendBuf[TotalLen++] = 0x00;
        SendBuf[TotalLen++] = 0x00;
        TempString = PosInterfaceReserve;
        System.arraycopy(TempString.getBytes(), 0,SendBuf, TotalLen,  TempString.length());
        TotalLen += TempString.length();

//        JavaHexDump(SendBuf,TotalLen);

        TempString = PosInterfaceFormatVersion;
        System.arraycopy(TempString.getBytes(), 0,SendBuf, TotalLen,  TempString.length());
        TotalLen += TempString.length();
        TempString = "1";   // Response Message
        System.arraycopy(TempString.getBytes(), 0,SendBuf, TotalLen,  TempString.length());
        TotalLen += TempString.length();
        TempString = PosInterfaceTransactionCode;
        System.arraycopy(TempString.getBytes(), 0,SendBuf, TotalLen,  TempString.length());
        TotalLen += TempString.length();

        TempString = ResponseCode;
        System.arraycopy(TempString.getBytes(), 0,SendBuf, TotalLen,  TempString.length());
        TotalLen += TempString.length();

        TempString = "0";   // More Data Indicator
        System.arraycopy(TempString.getBytes(), 0,SendBuf, TotalLen,  TempString.length());
        TotalLen += TempString.length();

        SendBuf[TotalLen++] = (byte)0x1C;   // Field Separator


//        JavaHexDump(SendBuf,TotalLen);

        for(i=0;i<PosInterfaceSnedTotalFieldCnt;i++)
        {
            TempString = PosInterfaceSnedFieldType[i];   // Field Type
            System.arraycopy(TempString.getBytes(), 0,SendBuf, TotalLen,  TempString.length());
            TotalLen += TempString.length();

            System.arraycopy(IntToBCD(PosInterfaceSnedFieldLength[i]), 0,SendBuf, TotalLen,  2);
            TotalLen += 2;

            TempString = PosInterfaceSnedFieldData[i];   // Field Value
            System.arraycopy(TempString.getBytes(), 0,SendBuf, TotalLen,  PosInterfaceSnedFieldLength[i]);
            TotalLen += PosInterfaceSnedFieldLength[i];

            SendBuf[TotalLen++] = (byte)0x1C;   // Field Separator

        }

        System.arraycopy(IntToBCD(TotalLen-3), 0,SendBuf, 1,  2);
        SendBuf[TotalLen++] = (byte)0x03;
//        JavaHexDump(SendBuf,TotalLen);

        CheckSum = (byte) 0x00;
        CheckSum = (byte)(CheckSum ^ SendBuf[0]);      // STX   // Paul_2010704
        CheckSum = (byte) (CheckSum ^ SendBuf[1]);
        CheckSum = (byte) (CheckSum ^ SendBuf[2]);
        for (i = 3; i < TotalLen; i++) {
            CheckSum = (byte) (CheckSum ^ SendBuf[i]);
        }
//        CheckSum = (byte) (CheckSum ^(byte)0x03);                  // ETX
        SendBuf[TotalLen++] = CheckSum;         // LRC

        JavaHexDump(SendBuf,TotalLen);

        SendString = HexUtil.bytesToHexString(SendBuf).substring(0,TotalLen*2);
        System.out.printf("utility:: SendString = %s \n",SendString);
        /*
        ii = 124;

        TotalLen = IntToBCD(ii);
        SendString = HexUtil.bytesToHexString(TotalLen);
        System.out.printf("utility:: PosInterfaceSendMessage SendString = %s\n",SendString);
//        SendString = "02"+"0000"
*/
//        for(i=0;i<3;i++) {

//        ProgressDialog dialog = ProgressDialog.show(PosInterfaceActivity.this, "",
//                "Loading. Please wait...", true);

        System.out.printf("utility:: PosInterfaceSendData = %s \n",SendString);
//        SendString = SendString + "0D";
        PosInterfaceSendData( SendString );
//            PosInterfaceACKWait();
        SuccessFlg = 1;
        count = 0;
        countDownTimer =   new CountDownTimer( 5000, 1 ) {
            @Override
            public void onTick(long millisUntilFinished) {
                try {
                    SerialReceiveBuf = serialPort1.receiveData(1);
                    if(SerialReceiveBuf != null)
                    {
                        int i;
                        for(i=0;i<SerialReceiveBuf.length;i++) {        // Paul_20180705
                            if (SerialReceiveBuf[i] == (byte) 0x06) {
                                System.out.printf( "utility:: PosInterfaceACKWait Success ACK \n" );
                                SuccessFlg = 0;
                                countDownTimer.cancel();
                                if (onDataRec != null) {
                                    onDataRec.success();
                                }
                                break;
                            }
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
//                System.out.printf("utility:: PosInterfaceACKWait onFinish \n");
                if(SuccessFlg != 0) {

                    count++;
                    if (count < 3) {
//                        System.out.printf( "utility:: PosInterfaceSendData = %s \n", SendString );
                        PosInterfaceSendData( SendString );
                        countDownTimer.start();
                    } else {
                        count = 0;
//                        System.out.printf( "utility:: PosInterfaceACKWait Fail \n" );
                        if (onDataRec != null) {
                            onDataRec.success();
                        }
                    }
                }
            }
        };
        countDownTimer.start();
//        if (SuccessFlg == 0) {
//            return SuccessFlg;
//        }
        //       }
/*
// Paul_20180625 Next Delete
        System.out.printf("utility:: POS Interface TEST Result \n");
        int rv;
        rv = CheckPOSInterface(SendString);
        System.out.printf("utility:: CheckPOSInterface = %d \n",rv);
// Paul_20180625 Next Delete
*/
    }

    public void PosInterfaceWriteField(String FieldTypeString,String FieldValueString)
    {
        PosInterfaceSnedFieldType[PosInterfaceSnedTotalFieldCnt] = FieldTypeString;
        PosInterfaceSnedFieldLength[PosInterfaceSnedTotalFieldCnt] = FieldValueString.length();
        PosInterfaceSnedFieldData[PosInterfaceSnedTotalFieldCnt] = FieldValueString;
        if(PosInterfaceSnedTotalFieldCnt < 64) {
            PosInterfaceSnedTotalFieldCnt++;
        }
    }

    public void PosInterfaceWriteInitField()
    {
        int i;

        PosInterfaceSnedTotalFieldCnt = 0;
        for(i=0;i<64;i++)
        {
            PosInterfaceSnedFieldType[i] = null;
            PosInterfaceSnedFieldLength[i] = 0;
            PosInterfaceSnedFieldData[i] = null;
        }
    }

    public void PosInterfaceACKWait()
    {
        SuccessFlg=1;
        SerialReceiveBuf = null;
//        mHandler.sendEmptyMessage( 0 ); // 시작과 동시에 핸들러에 메세지 전달
        cardManager = MainApplication.getCardManager();
        //serialPort1 = cardManager.getInstancesSerial1();

        countDownTimer =   new CountDownTimer( 4000, 1 ) {
            @Override
            public void onTick(long millisUntilFinished) {
                try {
                    SerialReceiveBuf = null;
                    SerialReceiveBuf = serialPort1.receiveData(1);
                    if(SerialReceiveBuf != null)
                    {
                        int i;
                        for(i=0;i<SerialReceiveBuf.length;i++) {
                            if (SerialReceiveBuf[i] == (byte) 0x06) {
                                System.out.printf( "utility:: PosInterfaceACKWait Success ACK \n" );
                                SuccessFlg = 0;
                                countDownTimer.cancel();
                                if (onDataRec != null) {
                                    onDataRec.success();
                                }
                            }
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                if (onDataRec != null) {
                    onDataRec.success();
                }
                System.out.printf("utility:: PosInterfaceACKWait onFinish \n");
            }
        };
        countDownTimer.start();
    }

    // return Receive Length
    public int PosInterfaceDataWait()
    {
        byte[] RealSerialRecBuf1 = new byte[2048+1];
        byte[] SerialReceiveBuf1 = null;
//        SerialReceiveBuf = new byte[0];
        int TotCnt=0;
        int i;
        int existFlg;
//        boolean isReading;

        cardManager = MainApplication.getCardManager();
        //serialPort1 = cardManager.getInstancesSerial1();
        if(serialPort1 == null)     // Paul_20181127
        {
            try {
                Thread.sleep( 500 );        // Paul_20181127
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 0;
        }
//        System.out.printf("utility:: 0000000001 \n");
        try {
            SerialReceiveBuf1 = serialPort1.receiveData(100);
//        } catch (RemoteException e) {
        } catch (Exception e) {  // SINN 20180918 fix reboot fail
            e.printStackTrace();
            try {
                Thread.sleep( 500 );        // Paul_20181127
            } catch (InterruptedException f) {
                f.printStackTrace();
            }
            return 0;  //SINN 20180918 fix reboot fail
        }
//        System.out.printf("utility:: 0000000002 \n");
        if(SerialReceiveBuf1 == null)
        {
            try {
                Thread.sleep( 500 );        // Paul_20181127
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 0;
        }
        JavaHexDump( SerialReceiveBuf1,SerialReceiveBuf1.length );
        existFlg = 0;
        for(i=0;i<SerialReceiveBuf1.length;i++)
        {
            if(SerialReceiveBuf1[i] == (byte)0x02)
            {
                existFlg = 1;
                break;
            }
        }
        if(existFlg == 0)
        {
            return 0;
        }
        TotCnt = 0;
        System.arraycopy(SerialReceiveBuf1, i,RealSerialRecBuf1, TotCnt,  SerialReceiveBuf1.length);
        TotCnt += SerialReceiveBuf1.length;
        SerialReceiveBuf1 = null;
 //       JavaHexDump( RealSerialRecBuf,TotCnt );
        isReading = true;

        /*
        new Thread() {
            @Override
            public void run() {
                try {
                    while (isReading) {
                        byte[] data = serialPort1.receiveData(500);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();
*/

//        System.out.printf("utility:: 0000000004 \n");

        SerialReceiveBuf1 = null;
        try {
            SerialReceiveBuf1 = serialPort1.receiveData(1000);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
//        System.out.printf("utility:: 0000000005 \n");
        if(SerialReceiveBuf1 == null)
        {
            System.arraycopy(RealSerialRecBuf1, 0,RealSerialRecBuf, 0,  TotCnt);
            JavaHexDump( RealSerialRecBuf,TotCnt );
            return TotCnt;
        }
//        System.out.printf("utility:: 0000000006 \n");
        System.arraycopy(SerialReceiveBuf1, 0,RealSerialRecBuf1, TotCnt,  SerialReceiveBuf1.length);
        TotCnt += SerialReceiveBuf1.length;

        System.arraycopy(RealSerialRecBuf1, 0,RealSerialRecBuf, 0,  TotCnt);
        JavaHexDump( RealSerialRecBuf,TotCnt );
        System.out.printf("utility:: 0000000007 \n");

        return TotCnt;
    }

    public String PosInterfaceReceiveData()
    {
        byte[] data=null;        //  = new byte[0];

        cardManager = MainApplication.getCardManager();
        //serialPort1 = cardManager.getInstancesSerial1();
/*
    try {
            data = serialPort1.receiveData(1000);
        } catch (RemoteException e) {
            e.printStackTrace();
        }*/
        if (data == null || data.length < 1) {
            return null;
        } else {
            return new String(data);
        }
    }

    public void PosInterfaceSendData(String SendString)
    {
        int i;
        byte[] hexData;
        cardManager = MainApplication.getCardManager();
        //serialPort1 = cardManager.getInstancesSerial1();
        try {
//            hexData = SendString.getBytes();
            hexData = HexUtil.hexStringToByte(SendString);
            if (hexData == null) {
                return;
            }
        }catch (Exception e){
            return;
        }

//        byte hexData[] = SendString.getBytes();
//        hexData = HexUtil.hexStringToByte(SendString);
//        if (hexData == null) {
//            return;
//        }

        try {
//            JavaHexDump( hexData,hexData.length );
//            System.out.printf("utility:: AAAAAAAAAAAAA PosInterfaceSendData0 = [%02X]\n",hexData[0]);
//            System.out.printf("utility:: AAAAAAAAAAAAA PosInterfaceSendData1 = [%02X]\n",hexData[1]);
            //this.serialPort1.sendData(hexData);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public String ResponseMsgPosInterface(String response_code) {
        String szMSG = "Un Define Code";
        switch (response_code) {
            case "00":
                szMSG = "APPROVED";
                break;
            case "01":
                szMSG = "MSG LEN ERR";
                break;
            case "02":
                szMSG = "FORMAT ERR";
                break;
            case "03":
                szMSG = "TER VER ERR";
                break;
            case "04":
                szMSG = "MSG VER ERR";
                break;
            case "05":
                szMSG = "MAC ERR";
                break;
            case "06":
                szMSG = "TX CODE ERR";
                break;
            case "07":
                szMSG = "TER CER ERR";
                break;
            case "08":
                szMSG = "TID NOT FOUND";
                break;
            case "11":
                szMSG = "CARD NOT FOUND";
                break;
            case "12":
                szMSG = "TX NOT FOUND";
                break;
            case "13":
//                szMSG = "VOID NOT MATCH";
                szMSG = "NOT MATCH";
                break;
            case "14":
                szMSG = "EXCEED AMT";
                break;
            case "15":
                szMSG = "EXCEED USE";
                break;
            case "95":
                szMSG = "TXN CODE ERR";
                break;
            case "96":
                szMSG = "TOP TIMEOUT";
                break;
            case "97":
                szMSG = "TOP FAIL";
                break;
            case "98":
                szMSG = "EXCEED AMT DEPOSIT";
                break;
            case "21":
                szMSG = "SERV TIMEOUT";
                break;
            case "22":
                szMSG = "TOO MANY CONN";
                break;
            case "31":
                szMSG = "DATABASE ERR";
                break;
            case "32":
                szMSG = "EMCI ERR";
                break;
            case "33":
                szMSG = "INVALID BATCH";
                break;
            case "34":
                szMSG = "TID NOT FOUND";
                break;
            case "40":
                szMSG = "EMCI ERR ";
                break;
            case "41":
                szMSG = "EMCI TIMEOUT";
                break;
            case "42":
                szMSG = "EMCI MALFUNC";
                break;
            case "43":
                szMSG = "INCORRECT PIN";
                break;
            case "44":
                szMSG = "INVALID CARD";
                break;
            case "45":
                szMSG = "DO NOT HONOR";
                break;
            case "46":
                szMSG = "PIN EXCEED";
                break;
            case "47":
                szMSG = "TXN NOT PERMIT";
                break;
            case "48":
                szMSG = "CARD EXPIRE";
                break;
            case "49":
                szMSG = "PICKUP CARDr";
                break;
            case "50":
                szMSG = "INVALID TXN";
                break;
            case "51":
                szMSG = "INVALID TIN/PIN";
                break;
            case "52":
                szMSG = "INV CARD CATG";
                break;
            case "69":
                szMSG = "INVALID TID";
                break;
            case "ND":
                szMSG = "TXN CANCEL";
                break;
            case "EN":
                szMSG = "CONNECT FAILED";
                break;
            case "NA":
                szMSG = "NOT AVAILABLE";
                break;
        }
        return szMSG;
    }


    public void TerToPosCancel()
    {
        PosInterfaceWriteField("02", ResponseMsgPosInterface("ND"));   // Response Message
        PosInterfaceSendMessage(PosInterfaceTransactionCode, "ND");
    }

    public void TerToPosFormatError()
    {
        System.out.printf("%s  = TerToPosFormatError 00000000001 \n",TAG);
        PosInterfaceWriteField("02", ResponseMsgPosInterface("02"));   // Response Message
        PosInterfaceSendMessage(PosInterfaceTransactionCode, "02");
    }

    private OnAckToPrint onDataRec = null;

    public void setOnDataRec(OnAckToPrint onDataRec ) {
        this.onDataRec = onDataRec;
    }

    public void removeAckToPrint() {
        onDataRec = null;
    }

    public interface OnAckToPrint {
        void success();
    }

    public AidlSerialPort checkRS23open()
    {
        return serialPort1;
    }

}
