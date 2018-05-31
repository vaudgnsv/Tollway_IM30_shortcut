package org.centerm.land;

import android.app.Application;

import org.centerm.land.manager.Contextor;
import org.centerm.land.utility.Preference;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainApplication extends Application {
    private static CardManager cardManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Contextor.getInstance().init(this);
        cardManager = CardManager.init(getApplicationContext());
        cardManager.bindService();
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfiguration); // Make this Realm the default
        setPreference();

    }

    private void setPreference() {
        setTexABB();

        setFee();

        setQr();

        setTransactionCode();

        setIP();

        setTrace();

        setBatch();

        setTerminal();

        setMerchant();

        setTerminalVersion();

        setMessageVersion();

        setNII();

        setTpdu();

        setInvoice();

    }

    private void setTexABB() {
        String texAbb = Preference.getInstance(this).getValueString(Preference.KEY_TAX_INVOICE_NO);
        String texId = Preference.getInstance(this).getValueString(Preference.KEY_TAX_ID);

        if (texAbb.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_TAX_INVOICE_NO,"61900020201805250004");
        }

        if (texId.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_TAX_ID,"3101083187");
        }
    }

    private void setFee() {
        Double fee = Preference.getInstance(this).getValueDouble(Preference.KEY_FEE);
        if (fee <= 0) {
            Preference.getInstance(this).setValueDouble(Preference.KEY_FEE,2.00d);
        }
    }

    private void setQr() {
        String qrAid = Preference.getInstance(this).getValueString(Preference.KEY_QR_AID);
        String traceId = Preference.getInstance(this).getValueString(Preference.KEY_QR_TRACE_NO);
        String billerKey = Preference.getInstance(this).getValueString(Preference.KEY_BILLER_KEY);
        String qrPort = Preference.getInstance(this).getValueString(Preference.KEY_QR_PORT);
        String billerId = Preference.getInstance(this).getValueString(Preference.KEY_QR_BILLER_ID);
        String merchantName = Preference.getInstance(this).getValueString(Preference.KEY_QR_MERCHANT_NAME);
        String terminalId = Preference.getInstance(this).getValueString(Preference.KEY_QR_TERMINAL_ID);
        if (qrAid.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_QR_AID, "A000000677010112");
        }

        if (traceId.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_QR_TRACE_NO, "1");
        }

        if (billerKey.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_BILLER_KEY, "gov");
        }
        if (qrPort.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_QR_PORT, "3840");
        }
        if (billerId.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_QR_BILLER_ID, "010352102131870");
        }
        if (merchantName.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_QR_MERCHANT_NAME, "NAKHONRATCHASIMA PCG.");
        }
        if (terminalId.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_QR_TERMINAL_ID, "00025120");
        }
    }

    private void setIP() {
        String primaryIP = Preference.getInstance(this).getValueString(Preference.KEY_PRIMARY_IP);
        String primaryPORT = Preference.getInstance(this).getValueString(Preference.KEY_PRIMARY_PORT);
        String secondaryIP = Preference.getInstance(this).getValueString(Preference.KEY_SECONDARY_IP);
        String secondaryPORT = Preference.getInstance(this).getValueString(Preference.KEY_SECONDARY_PORT);

        if (primaryIP.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_PRIMARY_IP, "172.22.0.251");
        }
        if (primaryPORT.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_PRIMARY_PORT, "3828");
        }
        if (secondaryIP.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_SECONDARY_IP, "172.22.0.251");
        }
        if (secondaryPORT.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_SECONDARY_PORT, "3828");
        }
    }

    private void setTerminal() {
        String terIdPOS = Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_POS);
        String terIdTMS = Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_TMS);
        String terIdEPS = Preference.getInstance(this).getValueString(Preference.KEY_TERMINAL_ID_EPS);

        if (terIdPOS.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_TERMINAL_ID_POS, "61900010");
        }
        if (terIdTMS.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_TERMINAL_ID_TMS, "00006120");
        }
        if (terIdEPS.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_TERMINAL_ID_EPS, "31900010");
        }
    }
    private void setTrace() {
        String tracePOS = Preference.getInstance(this).getValueString(Preference.KEY_TRACE_NO_POS);
        String traceTMS = Preference.getInstance(this).getValueString(Preference.KEY_TRACE_NO_TMS);
        String traceEPS = Preference.getInstance(this).getValueString(Preference.KEY_TRACE_NO_EPS);

        if (tracePOS.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_TRACE_NO_POS, "1");
        }
        if (traceTMS.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_TRACE_NO_TMS, "1");
        }
        if (traceEPS.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_TRACE_NO_EPS, "1");
        }
    }
    private void setBatch() {
        String batchPOS = Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_POS);
        String batchTMS = Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_TMS);
        String batchEPS = Preference.getInstance(this).getValueString(Preference.KEY_BATCH_NUMBER_EPS);

        if (batchPOS.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_BATCH_NUMBER_POS, "1");
        }
        if (batchTMS.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_BATCH_NUMBER_TMS, "1");
        }
        if (batchEPS.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_BATCH_NUMBER_EPS, "1");
        }
    }

    private void setMerchant() {
        String merchantIdPOS = Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_POS);
        String merchantIdTMS = Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_POS);
        String merchantIdEPS = Preference.getInstance(this).getValueString(Preference.KEY_MERCHANT_ID_POS);

        if (merchantIdPOS.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_MERCHANT_ID_POS, "000001000900003");
        }
        if (merchantIdTMS.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_MERCHANT_ID_TMS, "000000000001688");
        }
        if (merchantIdEPS.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_MERCHANT_ID_EPS, "000001000900003");
        }
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


        if (niiPOS.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_NII_POS, "0245");
        }
        if (niiTMS.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_NII_TMS, "0246");
        }
        if (niiEPS.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_NII_EPS, "0242");
        }
    }

    private void setTpdu() {
        String tpduPOS = Preference.getInstance(this).getValueString(Preference.KEY_TPDU_POS);
        String tpduTMS = Preference.getInstance(this).getValueString(Preference.KEY_TPDU_TMS);
        String tpduEPS = Preference.getInstance(this).getValueString(Preference.KEY_TPDU_EPS);

        if (tpduPOS.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_TPDU_POS, "6002450000");
        }
        if (tpduTMS.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_TPDU_TMS, "6002460000");
        }
        if (tpduEPS.isEmpty()) {
            Preference.getInstance(this).setValueString(Preference.KEY_TPDU_EPS, "6002420000");
        }
    }

    private void setInvoice() {
        String invoiceNumber;

        if (Preference.getInstance(this).getValueString(Preference.KEY_INVOICE_NUMBER_POS).isEmpty()) {
            invoiceNumber = "1";
            Preference.getInstance(this).setValueString(Preference.KEY_INVOICE_NUMBER_POS, invoiceNumber);
        }

        if (Preference.getInstance(this).getValueString(Preference.KEY_INVOICE_NUMBER_TMS).isEmpty()) {
            invoiceNumber = "1";
            Preference.getInstance(this).setValueString(Preference.KEY_INVOICE_NUMBER_TMS, invoiceNumber);
        }

        if (Preference.getInstance(this).getValueString(Preference.KEY_INVOICE_NUMBER_EPS).isEmpty()) {
            invoiceNumber = "1";
            Preference.getInstance(this).setValueString(Preference.KEY_INVOICE_NUMBER_EPS, invoiceNumber);
        }
    }

    public static CardManager getCardManager() {
        return cardManager;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        cardManager.unbindService();
    }
}
