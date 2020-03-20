package org.centerm.Tollway.healthcare.basefragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.centerm.centermposoversealib.tleservice.AidlTleService;
import com.centerm.centermposoversealib.tleservice.TleParamMap;
import com.centerm.smartpos.aidl.sys.AidlDeviceManager;

import org.centerm.Tollway.core.BlockCalculateUtil;
import org.centerm.Tollway.core.ChangeFormat;
import org.centerm.Tollway.core.CustomSocketListener;
import org.centerm.Tollway.core.DataExchanger;
import org.centerm.Tollway.utility.Preference;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.centerm.Tollway.core.ChangeFormat.bcd2Str;

public abstract class BaseHealthCareFragment extends Fragment {
    private final String TAG = "BaseHealthCardActivity";

    private CustomSocketListener customSocketListener;
    private ExecutorService sFixedThreadPool;
    private String PRIMARY_HOST;
    private String PRIMARY_PORT;
    private String SECONDARY_HOST;
    private String SECONDARY_PORT;
    private AidlTleService tleVersionOne;
    private String[] mBlockDataReceived;

    protected String getLength62(String slength62) {
        StringBuilder length = new StringBuilder();
        Log.d(TAG, "getLength62: " + slength62.length());
        for (int i = slength62.length(); i < 4; i++) {
            length.append("0");
        }
        Log.d(TAG, "getLength62: " + length + slength62);
        return length + slength62;
    }

    private void sendStr(final String stringss) {
        if (stringss == null || stringss.isEmpty()) {
            //showMessage("The data to send is null or empty");
            Log.d(TAG, "The data to send is null or empty");
            return;
        }

        try {
            //Log.d(TAG, "DATA TO SEND => "+stringss);
            sFixedThreadPool = Executors.newFixedThreadPool(3);
            sFixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        PRIMARY_HOST = Preference.getInstance(getContext()).getValueString(Preference.KEY_PRIMARY_IP);
                        PRIMARY_PORT = Preference.getInstance(getContext()).getValueString(Preference.KEY_PRIMARY_PORT);
                        SECONDARY_HOST = Preference.getInstance(getContext()).getValueString(Preference.KEY_SECONDARY_IP);
                        SECONDARY_PORT = Preference.getInstance(getContext()).getValueString(Preference.KEY_SECONDARY_PORT);
                        Log.d(TAG, "Host => " + PRIMARY_HOST + " [" + PRIMARY_PORT + "]");

                        Log.d(TAG, "Message Length = " + stringss.length());
                        Log.d(TAG, "Message % 2 = " + (stringss.length() % 2));
                        //Log.d(TAG, "TRACK2 length = "+TRACK2.length());

                        DataExchanger dataExchanger = new DataExchanger(1, PRIMARY_HOST, Integer.valueOf(PRIMARY_PORT), SECONDARY_HOST, Integer.valueOf(SECONDARY_PORT));
                        Log.d(TAG, "pass to new DataExchanger");
                        byte[] clientData = ChangeFormat.writeUTFSpecial(stringss);
                        Log.d(TAG, "pass to ChangeFormat");
                        dataExchanger.doExchange(clientData, customSocketListener);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            //showMessage(e.toString());
            Log.d(TAG, e.toString());
        }
    }

    protected void packageAndSend(String TPDU, String messageType, String[] mBlockData) {
        //processCallback(PROCESS_REQUEST_INSERT_DB);
        Log.d(TAG, "packageAndSend: " + mBlockData.toString());


        String applicationData = BlockCalculateUtil.calculateApplicationData(mBlockData);
        String dataToSend = "";
        dataToSend = dataToSend + TPDU;
        dataToSend = dataToSend + messageType;
        dataToSend = dataToSend + applicationData;
        dataToSend = dataToSend.trim();

        Log.d(TAG, "Raw packageAndSend => " + dataToSend);

        dataToSend = OnUsEncryptionMsg(dataToSend);
// Paul_20180522 End
        if (dataToSend != null) {
            Log.d(TAG, "Encrypted DATATOSEND => " + dataToSend);
            sendStr(dataToSend);
        } else {
            Log.d(TAG, "Encrypted Data is return NULL!!!");
            //sendStr(plainData);
        }
    }

    private String OnUsEncryptionMsg(String InputISOMessage) {
        String OutputISOMessage = null;

        try {
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("ISOMESSAGE", InputISOMessage);
            TleParamMap tleParamMap = new TleParamMap();
            tleParamMap.setParamMap(hashMap);
            OutputISOMessage = tleVersionOne.tleFuncton("OnUstleEncryption", tleParamMap);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return OutputISOMessage;
    }

    private String OnUsDecryptionMsg(String InputISOMessage) {
        String OutputISOMessage = null;

        try {
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("ISOMESSAGE", InputISOMessage);
            TleParamMap tleParamMap = new TleParamMap();
            tleParamMap.setParamMap(hashMap);
            OutputISOMessage = tleVersionOne.tleFuncton("OnUstleDecryption", tleParamMap);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return OutputISOMessage;
    }


    private void connect() {
        customSocketListener = new CustomSocketListener() {
            @Override
            public void ConnectTimeOut() {
                Log.d(TAG, "ConnectTimeOut: ");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectTimeOut();
                    }
                });

            }

            @Override
            public void TransactionTimeOut() {
                Log.d(TAG, "TransactionTimeOut: ");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        transactionTimeOut();
                    }
                });
            }

            @Override
            public void Received(final byte[] data) {
                System.out.printf("utility:: %s received \n",TAG);
                Log.d(TAG, "RECEIVED DATA:" + bcd2Str(data));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        received(dealWithTheResponse(bcd2Str(data)));
                    }
                });


            }

            @Override
            public void Error(final String error) {
                Log.d(TAG, "Error: ");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        error(error);
                    }
                });

            }

            @Override
            public void Other() {
                Log.d(TAG, "Other: ");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        other();
                    }
                });

            }
        };
    }

    private String[] dealWithTheResponse(String response) {
        String raw_data;

        Log.d(TAG, "Encrypted Response Data：" + response);

        response = response.substring(4);
        // Paul_20180522 Start

        raw_data = OnUsDecryptionMsg(response); // send to decrypt no need length

// Paul_20180522 End
//        raw_data = decryptMsg(response); // send to decrypt no need length


        //raw_data = raw_data.substring(4); // already cut length
        mBlockDataReceived = BlockCalculateUtil.getReceivedDataBlock(raw_data);

        Log.d(TAG, "Decrypted Response Data：" + raw_data);

        for (int i = 0; i < mBlockDataReceived.length; i++) {
            //System.out.println((i+1)+":"+mBlockDataReceived[i]);
            Log.d(TAG, (i + 1) + ":" + mBlockDataReceived[i]);
        }


        String result = BlockCalculateUtil.checkResult(mBlockDataReceived[39 - 1]);
        Log.d(TAG, "RETURN INFO OF 37:" + mBlockDataReceived[37 - 1]);
        Log.d(TAG, "RETURN INFO OF 38:" + mBlockDataReceived[38 - 1]);
        Log.d(TAG, "RETURN INFO OF 39:" + mBlockDataReceived[39 - 1]);
        Log.d(TAG, "RETURN INFO OF 55:" + mBlockDataReceived[55 - 1]);
        Log.d(TAG, "RETURN INFO OF 63:" + mBlockDataReceived[63 - 1]);

        return mBlockDataReceived;
    }

    public void bindService() {
        Intent intentTle = new Intent();
        intentTle.setPackage("com.centerm.smartpostestforandroidstudio");
//        intentTle.setPackage("com.centerm.tle");
        intentTle.setAction("com.centerm.TleFunction.MANAGER_SERVICE");
        getActivity().bindService(intentTle, connTle, Context.BIND_AUTO_CREATE);
    }

    private AidlDeviceManager managerTle;
    private ServiceConnection connTle = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            managerTle = AidlDeviceManager.Stub.asInterface(service);
            Log.d(TAG, "Tle 服务绑定成功");
            if (null != managerTle) {
                tle_initialize(managerTle);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            managerTle = null;
            Log.d(TAG, "Tle服务绑定失败");
        }
    };

    private void tle_initialize(AidlDeviceManager deviceManager) {
        try {
            tleVersionOne = AidlTleService.Stub.asInterface(deviceManager.getDevice(999));
            Log.d(TAG, "TLE Service is " + ((tleVersionOne != null) ? "not null" : "null"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void unbindService() {
        getActivity().unbindService(connTle);
    }

    @Override
    public void onResume() {
        super.onResume();
        bindService();
        connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        unbindService();
    }

    @Override
    public void onPause() {
        super.onPause();
        customSocketListener = null;
    }

    protected abstract void connectTimeOut();

    protected abstract void transactionTimeOut();

    protected abstract void received(String[] data);

    protected abstract void error(String error);

    protected abstract void other();
}
