package org.centerm.Tollway.activity;

import android.content.Intent;
import android.os.Bundle;

import org.centerm.Tollway.CardManager;
import org.centerm.Tollway.MainApplication;
import org.centerm.Tollway.activity.posinterface.PosInterfaceActivity;
import org.centerm.Tollway.healthcare.baseavtivity.BaseHealthCardActivity;
import org.centerm.Tollway.utility.Utility;

import static org.centerm.Tollway.activity.MenuServiceListActivity.KEY_TYPE_INTERFACE;

public class PosLinkSendActivity extends BaseHealthCardActivity {

    private CardManager cardManager = null;
    private String typeInterface=null;
    private PosInterfaceActivity posinterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_menu_service_list);
        cardManager = MainApplication.getCardManager();
        posinterface = MainApplication.getPosInterfaceActivity();

        initData();
        if(typeInterface != null)
        {
            Utility.customDialogAlertAuto(PosLinkSendActivity.this, "FORMAT ERROR");
            posinterface.TerToPosFormatError();
//            posinterface.PosInterfaceWriteInitField();
//            posinterface.PosInterfaceWriteField("02", RespCode.ResponseMsgRS232("02"));
//            posinterface.PosInterfaceSendMessage(posinterface.PosInterfaceTransactionCode, "02");
            System.out.printf("utility:: FORMAT ERROR \n");
            posinterface.setOnDataRec(new PosInterfaceActivity.OnAckToPrint() {
                @Override
                public void success() {
                    posinterface.PosInterfaceExistFlg = 0;  // Paul_20180731
//                    Utility.customDialogAlertAutoClear();
                    Intent intent = new Intent(PosLinkSendActivity.this, MenuServiceListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                }
            });
        }
    }
    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString(KEY_TYPE_INTERFACE) != null) {
                typeInterface = bundle.getString(KEY_TYPE_INTERFACE);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void connectTimeOut() {

    }

    @Override
    protected void transactionTimeOut() {

    }

    @Override
    protected void received(String[] data) {

    }

    @Override
    protected void error(String error) {

    }

    @Override
    protected void other() {

    }
}
