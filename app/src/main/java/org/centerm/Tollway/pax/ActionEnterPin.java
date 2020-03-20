/*
 * ============================================================================
 * COPYRIGHT
 *              Pax CORPORATION PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with Pax Corporation and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *      Copyright (C) 2016 - ? Pax Corporation. All rights reserved.
 * Module Date: 2016-11-25
 * Module Author: Steven.W
 * Description:
 *
 * ============================================================================
 */
package org.centerm.Tollway.pax;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.centerm.Tollway.CardManager;
import org.centerm.Tollway.activity.ConsumeActivity;

public class ActionEnterPin extends AAction {
    private Context context;
    private String title;
    private String pan;
    private String header;
    private String subHeader;
    private String totalAmount;
    private String offlinePinLeftTimes;
    private boolean isOnlinePin;

    private Dialog dialogPin;
    private CardManager cardManager;

    //private EEnterPinType enterPinType;

    public ActionEnterPin(ActionStartListener listener) {
        super(listener);

    }

    /**
     * 脱机pin时返回的结果
     *
     * @author Steven.W
     */
    public static class OfflinePinResult {
        // SW1 SW2
        byte[] respOut;
        int ret;

        public byte[] getRespOut() {
            return respOut;
        }

        public void setRespOut(byte[] respOut) {
            this.respOut = respOut;
        }

        public int getRet() {
            return ret;
        }

        public void setRet(int ret) {
            this.ret = ret;
        }
    }

    public void setParam(Context context, String pan, boolean onlinePin, String totalAmount, String offlinePinLeftTimes) {
        this.context = context;
        this.pan = pan;
        this.isOnlinePin = onlinePin;
        //this.header = header;
        //this.subHeader = subHeader;
        this.totalAmount = totalAmount;
        this.offlinePinLeftTimes = offlinePinLeftTimes; //AET-81
        //this.enterPinType = enterPinType;
    }

    public enum EEnterPinType {
        ONLINE_PIN, // 联机pin
        OFFLINE_PLAIN_PIN, // 脱机明文pin
        OFFLINE_CIPHER_PIN, // 脱机密文pin
        OFFLINE_PCI_MODE, //JEMV PCI MODE, no callback for offline pin
    }

    @Override
    protected void process() {
        //Intent intent = new Intent(context, CalculatePriceActivity.class);
        //intent.putExtra(EUIParamKeys.NAV_TITLE.toString(), title);
        //intent.putExtra(EUIParamKeys.PROMPT_1.toString(), header);
        //intent.putExtra(EUIParamKeys.PROMPT_2.toString(), subHeader);

/*

        byte[] track2 = ImplEmv.getTlv(0x57);
        String strTrack2 = MainApplication.getConvert().bcdToStr(track2).split("F")[0];
        //strTrack2 = strTrack2.split("F")[0];
        String pan = strTrack2.split("D")[0];
        Log.d("kang", "pan:" + pan );
        pan = pan.substring(4);

        String panblock = PanUtils.getPanBlock(pan, PanUtils.EPanMode.X9_8_WITH_PAN);
        Log.d("kang","panblock:" + panblock);
        byte[] pinblock = {};
        try {
           // pinblock = Device.getPinBlock(panblock);
        } catch(Exception e) {
            e.printStackTrace();
        }

        finally {

        }

        Log.d("kang","pinblock:" + ChangeFormat.bcd2Str(pinblock));


*/
        Log.d("kang","actionenterpin/process");
        Intent intent = new Intent(context, ConsumeActivity.class);
        //intent.putExtra(EUIParamKeys.NAV_TITLE.toString(), title);
        //intent.putExtra(EUIParamKeys.PROMPT_1.toString(), header);
        //intent.putExtra(EUIParamKeys.PROMPT_2.toString(), subHeader);
        intent.putExtra("amount", totalAmount);
        if (isOnlinePin == true)
            intent.putExtra("isOnlinePin", 1);
        else
            intent.putExtra("isOnlinePin", 0);
       //intent.putExtra("isOnlinePin",0);
        intent.putExtra("offlinePinLeftTimes", Integer.valueOf(offlinePinLeftTimes));
        intent.putExtra("pan", pan);
        //intent.putExtra(EUIParamKeys.TIP_AMOUNT.toString(), tipAmount); //AET-81
        //intent.putExtra(EUIParamKeys.ENTERPINTYPE.toString(), enterPinType);
        //intent.putExtra(EUIParamKeys.PANBLOCK.toString(), PanUtils.getPanBlock(pan, EPanMode.X9_8_WITH_PAN));
        //intent.putExtra(EUIParamKeys.SUPPORTBYPASS.toString(), isSupportBypass);
        context.startActivity(intent);




    }



}