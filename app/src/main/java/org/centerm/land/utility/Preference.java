package org.centerm.land.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import org.centerm.land.BuildConfig;


public class Preference {

    public static final String KEY_PRIMARY_IP = Preference.class.getName() + "key_primary_ip";
    public static final String KEY_PRIMARY_PORT = Preference.class.getName() + "key_primary_port";

    public static final String KEY_SECONDARY_IP = Preference.class.getName() + "key_secondary_ip";
    public static final String KEY_SECONDARY_PORT = Preference.class.getName() + "key_secondary_port";

    public static final String KEY_TRACE_NO_TMS = Preference.class.getName() +"key_trace_no_tms";
    public static final String KEY_TRACE_NO_EPS = Preference.class.getName() +"key_trace_no_eps";
    public static final String KEY_TRACE_NO_POS = Preference.class.getName() +"key_trace_no_pos";

    public static final String KEY_BATCH_NUMBER_POS = Preference.class.getName() +"key_batch_number_pos"; //mBlockData 60
    public static final String KEY_BATCH_NUMBER_EPS = Preference.class.getName() +"key_batch_number_ems"; //mBlockData 60
    public static final String KEY_BATCH_NUMBER_TMS = Preference.class.getName() +"key_batch_number_tms"; //mBlockData 60

    public static final String KEY_INVOICE_NUMBER_POS = Preference.class.getName() +"key_invoice_number_pos"; //mBlockData 62
    public static final String KEY_INVOICE_NUMBER_EPS = Preference.class.getName() +"key_invoice_number_ems"; //mBlockData 62
    public static final String KEY_INVOICE_NUMBER_TMS = Preference.class.getName() +"key_invoice_number_tms"; //mBlockData 62

    public static final String KEY_TERMINAL_ID_POS = Preference.class.getName() + "key_terminal_id_pos";
    public static final String KEY_TERMINAL_ID_TMS = Preference.class.getName() + "key_terminal_id_tms";
    public static final String KEY_TERMINAL_ID_EPS = Preference.class.getName() + "key_terminal_id_eps";

    public static final String KEY_MERCHANT_ID_POS = Preference.class.getName() + "key_merchant_id_pos";
    public static final String KEY_MERCHANT_ID_TMS = Preference.class.getName() + "key_merchant_id_tms";
    public static final String KEY_MERCHANT_ID_EPS = Preference.class.getName() + "key_merchant_id_eps";

    public static final String KEY_TERMINAL_VERSION = Preference.class.getName() + "key_terminal_version";

    public static final String KEY_MESSAGE_VERSION = Preference.class.getName() + "key_message_version";

    public static final String KEY_TRANSACTION_CODE = Preference.class.getName() + "key_transaction_code";

    public static final String KEY_PARAMETER_VERSION = Preference.class.getName() +"key_parameter_version";

    public static final String KEY_NII_POS = Preference.class.getName() + "key_nii_pos";
    public static final String KEY_NII_TMS = Preference.class.getName() + "key_nii_tms";
    public static final String KEY_NII_EPS = Preference.class.getName() + "key_nii_eps";

    public static final String KEY_TPDU_POS = Preference.class.getName() + "key_tpdu_pos";
    public static final String KEY_TPDU_TMS = Preference.class.getName() + "key_tpdu_tms";
    public static final String KEY_TPDU_EPS = Preference.class.getName() + "key_tpdu_eps";

    public static final String KEY_PIN = Preference.class.getName() + "key_pin";

    public static final String KEY_TAG_1000 = Preference.class.getName() + "key_tag_1000";
    public static final String KEY_TAG_1001 = Preference.class.getName() + "key_tag_1001";
    public static final String KEY_TAG_1002 = Preference.class.getName() + "key_tag_1002";
    public static final String KEY_TAG_1003 = Preference.class.getName() + "key_tag_1003";
    public static final String KEY_TAG_1004 = Preference.class.getName() + "key_tag_1004";
    public static final String KEY_TAG_1005 = Preference.class.getName() + "key_tag_1005";
    public static final String KEY_TAG_1006 = Preference.class.getName() + "key_tag_1006";
    public static final String KEY_TAG_1007 = Preference.class.getName() + "key_tag_1007";

    public static final String KEY_FEE = Preference.class.getName() + "key_fee_eps";

    public static final String KEY_QR_AID = Preference.class.getName() + "key_qr_aid";
    public static final String KEY_QR_BILLER_ID = Preference.class.getName() + "key_qr_biller_id";
    public static final String KEY_QR_MERCHANT = Preference.class.getName() + "key_qr_merchant";
    public static final String KEY_QR_TERMINAL_ID = Preference.class.getName() + "key_qr_terminal_id";

    public static final String KEY_REF_2 = Preference.class.getName() + "key_ref_2";

    public static final String KEY_TAX_INVOICE_NO = Preference.class.getName() + "key_tax_invoice_no";
    public static final String KEY_TAX_ID = Preference.class.getName() + "key_tax_id";

    private static Preference settingPreference = null;
    private final String preferenceName = BuildConfig.APPLICATION_ID + "Setting";
    private SharedPreferences preference = null;
    private Editor editor = null;
    private Context context = null;

    /**
     * Constructor method
     *
     * @param context
     */
    public Preference(Context context) {
        this.context = context;
        int mode = Context.MODE_PRIVATE;
        this.preference = this.context.getSharedPreferences(this.preferenceName, mode);
        this.editor = this.preference.edit();
    }

    /**
     * Factory method
     *
     * @param context
     * @return
     */
    public static Preference getInstance(Context context) {
        if (settingPreference == null) {
            settingPreference = new Preference(context);
        }
        return settingPreference;
    }

    public void setValueString(String key, String value) {
        this.editor.putString(key, value);
        this.editor.commit();
    }

    public String getValueString(String key) {
        return this.preference.getString(key, "");
    }

    public String getValueString(String key, String defaultValue) {
        return this.preference.getString(key, defaultValue);
    }

    public void setValueInt(String key, int value) {
        this.editor.putInt(key, value);
        this.editor.commit();
    }

    public int getValueInt(String key, int defaultValue) {
        return this.preference.getInt(key, defaultValue);
    }


    public int getValueInt(String key) {
        return this.preference.getInt(key, 0);
    }

    public void setValueLong(String key, long value) {
        this.editor.putLong(key, value);
        this.editor.commit();
    }

    public long getValueLong(String key, long defaultValue) {
        return this.preference.getLong(key, defaultValue);
    }

    public long getValueLong(String key) {
        return this.preference.getLong(key, 0);
    }

    public void setValueDouble(String key, double value) {
        this.editor.putFloat(key, (float) value);
        this.editor.commit();
    }

    public double getValueDouble(String key, float defaultValue) {
        float value = this.preference.getFloat(key, defaultValue);
        return value;
    }

    public double getValueDouble(String key) {
        float value = this.preference.getFloat(key, 0.0f);
        return value;
    }

    public void setValueFloat(String key, float value) {
        this.editor.putFloat(key, value);
        this.editor.commit();
    }

    public float getValueFloat(String key, float defaultValue) {
        float value = this.preference.getFloat(key, defaultValue);
        return value;
    }

    public float getValueFloat(String key) {
        float value = this.preference.getFloat(key, 0.0f);
        return value;
    }

    public void setValueBoolean(String key, boolean value) {
        this.editor.putBoolean(key, value);
        this.editor.commit();
    }

    public boolean getValueBoolean(String key) {
        return this.preference.getBoolean(key, false);
    }

    public void clear() {
        this.editor.clear();
        this.editor.commit();
    }

    public void deleteKey(String key) {
        this.editor.remove(key);
        this.editor.commit();
    }

}
