package org.centerm.Tollway.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import org.centerm.Tollway.BuildConfig;


public class Preference {

    public static final String KEY_PRIMARY_IP = Preference.class.getName() + "key_primary_ip";
    public static final String KEY_PRIMARY_PORT = Preference.class.getName() + "key_primary_port";

    public static final String KEY_SECONDARY_IP = Preference.class.getName() + "key_secondary_ip";
    public static final String KEY_SECONDARY_PORT = Preference.class.getName() + "key_secondary_port";

    public static final String KEY_TRACE_NO_TMS = Preference.class.getName() + "key_trace_no_tms";
    public static final String KEY_TRACE_NO_EPS = Preference.class.getName() + "key_trace_no_eps";
    public static final String KEY_TRACE_NO_POS = Preference.class.getName() + "key_trace_no_pos";

    public static final String KEY_BATCH_NUMBER_POS = Preference.class.getName() + "key_batch_number_pos"; //mBlockData 60
    public static final String KEY_BATCH_NUMBER_EPS = Preference.class.getName() + "key_batch_number_ems"; //mBlockData 60
    public static final String KEY_BATCH_NUMBER_TMS = Preference.class.getName() + "key_batch_number_tms"; //mBlockData 60
/*
    public static final String KEY_INVOICE_NUMBER_POS = Preference.class.getName() +"key_invoice_number_pos"; //mBlockData 62
    public static final String KEY_INVOICE_NUMBER_EPS = Preference.class.getName() +"key_invoice_number_ems"; //mBlockData 62
    public static final String KEY_INVOICE_NUMBER_TMS = Preference.class.getName() +"key_invoice_number_tms"; //mBlockData 62*/

    public static final String KEY_INVOICE_NUMBER_ALL = Preference.class.getName() + "key_invoice_number_all"; //mBlockData 62

    public static final String KEY_TERMINAL_ID_POS = Preference.class.getName() + "key_terminal_id_pos";
    public static final String KEY_TERMINAL_ID_TMS = Preference.class.getName() + "key_terminal_id_tms";
    public static final String KEY_TERMINAL_ID_EPS = Preference.class.getName() + "key_terminal_id_eps";

    public static final String KEY_MERCHANT_ID_POS = Preference.class.getName() + "key_merchant_id_pos";
    public static final String KEY_MERCHANT_ID_TMS = Preference.class.getName() + "key_merchant_id_tms";
    public static final String KEY_MERCHANT_ID_EPS = Preference.class.getName() + "key_merchant_id_eps";

    public static final String KEY_TERMINAL_VERSION = Preference.class.getName() + "key_terminal_version";

    public static final String KEY_MESSAGE_VERSION = Preference.class.getName() + "key_message_version";

    public static final String KEY_TRANSACTION_CODE = Preference.class.getName() + "key_transaction_code";

    public static final String KEY_PARAMETER_VERSION = Preference.class.getName() + "key_parameter_version";

    public static final String KEY_NII_POS = Preference.class.getName() + "key_nii_pos";
    public static final String KEY_NII_TMS = Preference.class.getName() + "key_nii_tms";
    public static final String KEY_NII_EPS = Preference.class.getName() + "key_nii_eps";

    public static final String KEY_TPDU_POS = Preference.class.getName() + "key_tpdu_pos";
    public static final String KEY_TPDU_TMS = Preference.class.getName() + "key_tpdu_tms";
    public static final String KEY_TPDU_EPS = Preference.class.getName() + "key_tpdu_eps";

    public static final String KEY_PIN = Preference.class.getName() + "key_pin";

    public static final String KEY_TAG_1000 = Preference.class.getName() + "key_tag_1000";  //para enable
    public static final String KEY_TAG_1001 = Preference.class.getName() + "key_tag_1001";  //com
    public static final String KEY_TAG_1002 = Preference.class.getName() + "key_tag_1002";   //ref1
    public static final String KEY_TAG_1003 = Preference.class.getName() + "key_tag_1003";   //ref2
    public static final String KEY_TAG_1004 = Preference.class.getName() + "key_tag_1004";   //ref3
    public static final String KEY_TAG_1005 = Preference.class.getName() + "key_tag_1005";   //fix amt
    public static final String KEY_TAG_1006 = Preference.class.getName() + "key_tag_1006";   // merhant id
    public static final String KEY_TAG_1007 = Preference.class.getName() + "key_tag_1007";   //max trans

    public static final String KEY_FEE = Preference.class.getName() + "key_fee_eps";

    public static final String KEY_QR_AID = Preference.class.getName() + "key_qr_aid";
    public static final String KEY_QR_BILLER_ID = Preference.class.getName() + "key_qr_biller_id";
    public static final String KEY_QR_MERCHANT_NAME = Preference.class.getName() + "key_qr_merchant_name";
    public static final String KEY_QR_TERMINAL_ID = Preference.class.getName() + "key_qr_terminal_id";

    public static final String KEY_QR_MERCHANT_ID = Preference.class.getName() + "key_qr_merchant_id";  ////20180814 SINN  use QR Merchant ID instead biller id.

    public static final String KEY_QR_TRACE_NO = Preference.class.getName() + "key_qr_trace_no";
    public static final String KEY_QR_BATCH_NUMBER = Preference.class.getName() + "key_qr_batch_number";
    public static final String KEY_ALI_BATCH_NUMBER = Preference.class.getName() + "key_ali_batch_number";
    public static final String KEY_ALI_BATCH_NUMBER_LAST = Preference.class.getName() + "key_ali_batch_number_last";
    public static final String KEY_WEC_BATCH_NUMBER = Preference.class.getName() + "key_wec_batch_number";
    public static final String KEY_WEC_BATCH_NUMBER_LAST = Preference.class.getName() + "key_wec_batch_number_last";
    public static final String KEY_QR_MERCHANT_NAME_THAI = Preference.class.getName() + "key_qr_merchant_name_THAI";

    public static final String KEY_REF_2 = Preference.class.getName() + "key_ref_2";

    public static final String KEY_BILLER_KEY = Preference.class.getName() + "key_biller_key";
    public static final String KEY_QR_PORT = Preference.class.getName() + "key_qr_port";
    public static final String KEY_QR_URL_ID = Preference.class.getName() + "key_qr_url";


    public static final String KEY_ALIPAY_TERMINAL_ID = Preference.class.getName() + "key_alipay_terminal_id";  // Paul_20181007
    public static final String KEY_ALIPAY_MERCHANT_ID = Preference.class.getName() + "key_alipay_merchant_id";  // Paul_20181007
    public static final String KEY_ALIPAY_STORE_ID = Preference.class.getName() + "key_alipay_store_id";  // Paul_20181007
    public static final String KEY_ALIPAY_URL_ID = Preference.class.getName() + "key_alipay_url"; //Jeff20181114
    public static final String KEY_ALIPAY_PUBLIC_ID = Preference.class.getName() + "key_alipay_public"; //Jeff20181114
    public static final String KEY_ALIPAY_CERTI_ID = Preference.class.getName() + "key_alipay_certi"; //Jeff20181115

    public static final String KEY_MERCHANT_1 = Preference.class.getName() + "key_merchant_1";
    public static final String KEY_MERCHANT_2 = Preference.class.getName() + "key_merchant_2";
    public static final String KEY_MERCHANT_3 = Preference.class.getName() + "key_merchant_3";

    public static final String KEY_ADMIN_PASS_WORD = Preference.class.getName() + "key_admin_pass_word";
    public static final String KEY_ADMIN_PIN = Preference.class.getName() + "key_admin_pin_word";
    public static final String KEY_OFFLINE_PASS_WORD = Preference.class.getName() + "key_offline_pass_word";  //SINN 20180713 Set offline pwd
    public static final String KEY_SETTING_FOR_USER_PASS_WORD = Preference.class.getName() + "key_setting_for_user_pass_word";  //K.GAME 180925 รหัสผ่านสำหรับ User
    public static final String KEY_NMX_SMARTCARD_SERIAL_NO = Preference.class.getName() + "key_nmx_smartcard_serial_no";  // Paul_20181029 Json Add to SmartSerial No

    public static final String KEY_SALE_VOID_PRINT_ID_POS = Preference.class.getName() + "key_sale_void_print_id_pos";
    public static final String KEY_SALE_VOID_PRINT_ID_EPS = Preference.class.getName() + "key_sale_void_print_id_eps";
    public static final String KEY_SALE_VOID_PRINT_ID_TMS = Preference.class.getName() + "key_sale_void_print_id_tms";
    public static final String KEY_SALE_VOID_PRINT_ID_GHC = Preference.class.getName() + "key_sale_void_print_id_ghc";  // Paul_20180709
//    public static final String KEY_SALE_VOID_PRINT_ID_GHC_OFFLINE = Preference.class.getName() + "key_sale_void_print_id_ghc";  // Paul_20180714

    public static final String KEY_SETTLE_TYPE_POS = Preference.class.getName() + "key_settle_type_pos";
    public static final String KEY_SETTLE_DATE_POS = Preference.class.getName() + "key_settle_date_pos";
    public static final String KEY_SETTLE_TIME_POS = Preference.class.getName() + "key_settle_time_pos";
    public static final String KEY_SETTLE_SALE_COUNT_POS = Preference.class.getName() + "key_settle_sale_count_pos";
    public static final String KEY_SETTLE_SALE_TOTAL_POS = Preference.class.getName() + "key_settle_sale_total_pos";
    public static final String KEY_SETTLE_VOID_COUNT_POS = Preference.class.getName() + "key_settle_void_count_pos";
    public static final String KEY_SETTLE_VOID_TOTAL_POS = Preference.class.getName() + "key_settle_void_total_pos";
    public static final String KEY_SETTLE_BATCH_POS = Preference.class.getName() + "key_settle_batch_pos";
    public static final String KEY_SETTLE_TAX_FEE_SALE_POS = Preference.class.getName() + "key_settle_tax_fee_sale_pos";
    public static final String KEY_SETTLE_TAX_FEE_VOID_POS = Preference.class.getName() + "key_settle_tax_fee_void_pos";

    public static final String KEY_SETTLE_TYPE_EPS = Preference.class.getName() + "key_settle_type_eps";
    public static final String KEY_SETTLE_DATE_EPS = Preference.class.getName() + "key_settle_date_eps";
    public static final String KEY_SETTLE_TIME_EPS = Preference.class.getName() + "key_settle_time_eps";
    public static final String KEY_SETTLE_SALE_COUNT_EPS = Preference.class.getName() + "key_settle_sale_count_eps";
    public static final String KEY_SETTLE_SALE_TOTAL_EPS = Preference.class.getName() + "key_settle_sale_total_eps";
    public static final String KEY_SETTLE_VOID_COUNT_EPS = Preference.class.getName() + "key_settle_void_count_eps";
    public static final String KEY_SETTLE_VOID_TOTAL_EPS = Preference.class.getName() + "key_settle_void_total_eps";
    public static final String KEY_SETTLE_BATCH_EPS = Preference.class.getName() + "key_settle_batch_eps";
    public static final String KEY_SETTLE_TAX_FEE_SALE_EPS = Preference.class.getName() + "key_settle_tax_fee_sale_eps";
    public static final String KEY_SETTLE_TAX_FEE_VOID_EPS = Preference.class.getName() + "key_settle_tax_fee_void_eps";

    public static final String KEY_SETTLE_TYPE_TMS = Preference.class.getName() + "key_settle_type_tms";
    public static final String KEY_SETTLE_DATE_TMS = Preference.class.getName() + "key_settle_date_tms";
    public static final String KEY_SETTLE_TIME_TMS = Preference.class.getName() + "key_settle_time_tms";
    public static final String KEY_SETTLE_SALE_COUNT_TMS = Preference.class.getName() + "key_settle_sale_count_tms";
    public static final String KEY_SETTLE_SALE_TOTAL_TMS = Preference.class.getName() + "key_settle_sale_total_tms";
    public static final String KEY_SETTLE_VOID_COUNT_TMS = Preference.class.getName() + "key_settle_void_count_tms";
    public static final String KEY_SETTLE_VOID_TOTAL_TMS = Preference.class.getName() + "key_settle_void_total_tms";
    public static final String KEY_SETTLE_BATCH_TMS = Preference.class.getName() + "key_settle_batch_tms";

    public static final String KEY_SETTLE_DATE_QR = Preference.class.getName() + "key_settle_date_qr";
    public static final String KEY_SETTLE_TIME_QR = Preference.class.getName() + "key_settle_time_qr";
    public static final String KEY_SETTLE_SALE_COUNT_QR = Preference.class.getName() + "key_settle_sale_count_qr";
    public static final String KEY_SETTLE_SALE_TOTAL_QR = Preference.class.getName() + "key_settle_sale_total_qr";
    public static final String KEY_SETTLE_VOID_COUNT_QR = Preference.class.getName() + "key_settle_void_count_qr";
    public static final String KEY_SETTLE_VOID_TOTAL_QR = Preference.class.getName() + "key_settle_void_total_qr";
    public static final String KEY_SETTLE_BATCH_QR = Preference.class.getName() + "key_settle_batch_qr";             // Paul_20181120 please no remark last settlement reprint problem

    public static final String KEY_SETTLE_DATE_ALI = Preference.class.getName() + "key_settle_date_ali";
    public static final String KEY_SETTLE_TIME_ALI = Preference.class.getName() + "key_settle_time_ali";
    public static final String KEY_SETTLE_SALE_COUNT_ALI = Preference.class.getName() + "key_settle_sale_count_ali";
    public static final String KEY_SETTLE_SALE_TOTAL_ALI = Preference.class.getName() + "key_settle_sale_total_ali";
    public static final String KEY_SETTLE_VOID_COUNT_ALI = Preference.class.getName() + "key_settle_void_count_ali";
    public static final String KEY_SETTLE_VOID_TOTAL_ALI = Preference.class.getName() + "key_settle_void_total_ali";
    public static final String KEY_SETTLE_TAX_SALE_FEE_ALI = Preference.class.getName() + "key_settle_tax_sale_fee_ali";    // Paul_20181219
    public static final String KEY_SETTLE_TAX_VOID_FEE_ALI = Preference.class.getName() + "key_settle_tax_void_fee_ali";    // Paul_20181219

    public static final String KEY_SETTLE_DATE_WEC = Preference.class.getName() + "key_settle_date_wec";
    public static final String KEY_SETTLE_TIME_WEC = Preference.class.getName() + "key_settle_time_wec";
    public static final String KEY_SETTLE_SALE_COUNT_WEC = Preference.class.getName() + "key_settle_sale_count_wec";
    public static final String KEY_SETTLE_SALE_TOTAL_WEC = Preference.class.getName() + "key_settle_sale_total_wec";
    public static final String KEY_SETTLE_VOID_COUNT_WEC = Preference.class.getName() + "key_settle_void_count_wec";
    public static final String KEY_SETTLE_VOID_TOTAL_WEC = Preference.class.getName() + "key_settle_void_total_wec";
    public static final String KEY_SETTLE_TAX_SALE_FEE_WEC = Preference.class.getName() + "key_settle_tax_sale_fee_wec";    // Paul_20181219
    public static final String KEY_SETTLE_TAX_VOID_FEE_WEC = Preference.class.getName() + "key_settle_tax_void_fee_wec";    // Paul_20181219


    public static final String KEY_SETTLE_DATE_GHC = Preference.class.getName() + "key_settle_date_ghc";
    public static final String KEY_SETTLE_TIME_GHC = Preference.class.getName() + "key_settle_time_ghc";
    public static final String KEY_SETTLE_SALE_COUNT_GHC = Preference.class.getName() + "key_settle_sale_count_ghc";
    public static final String KEY_SETTLE_SALE_TOTAL_GHC = Preference.class.getName() + "key_settle_sale_total_ghc";
    public static final String KEY_SETTLE_VOID_COUNT_GHC = Preference.class.getName() + "key_settle_void_count_ghc";
    public static final String KEY_SETTLE_VOID_TOTAL_GHC = Preference.class.getName() + "key_settle_void_total_ghc";
    public static final String KEY_SETTLE_BATCH_GHC = Preference.class.getName() + "key_settle_batch_ghc";


    public static final String KEY_TAX_INVOICE_NO_POS = Preference.class.getName() + "key_tax_invoice_no_pos";
    public static final String KEY_TAX_INVOICE_NO_EPS = Preference.class.getName() + "key_tax_invoice_no_eps";
    public static final String KEY_TAX_ID = Preference.class.getName() + "key_tax_id";

    public static final String KEY_POS_ID = Preference.class.getName() + "key_pos_id";

    public static final String KEY_JSON_CARD_FEE = Preference.class.getName() + "_key_json_card_fee";

    public static final String KEY_TRACE_NO_GHC = Preference.class.getName() + "key_trace_no_ghc"; // Paul_20180620

    public static final String KEY_BATCH_NUMBER_GHC = Preference.class.getName() + "key_batch_number_ghc"; // Paul_20180620 //mBlockData 60
    //    public static final String KEY_INVOICE_NUMBER_GHC = Preference.class.getName() +"key_invoice_number_ghc"; // Paul_20180620 //mBlockData 62
    public static final String KEY_TERMINAL_ID_GHC = Preference.class.getName() + "key_terminal_id_ghc"; // Paul_20180620
    public static final String KEY_MERCHANT_ID_GHC = Preference.class.getName() + "key_merchant_id_ghc";    // Paul_20180620
    public static final String KEY_MESSAGE_GHC_VERSION = Preference.class.getName() + "key_message_ghc_version";
    public static final String KEY_NII_GHC = Preference.class.getName() + "key_nii_ghc";    // Paul_20180620
    public static final String KEY_TPDU_GHC = Preference.class.getName() + "key_tpdu_ghc";  // Paul_20180620

    public static final String KEY_TAG_1000_HC = Preference.class.getName() + "key_tag_1000_hc";  //para enable
    public static final String KEY_TAG_1001_HC = Preference.class.getName() + "key_tag_1001_hc";  //com
    public static final String KEY_TAG_1002_HC = Preference.class.getName() + "key_tag_1002_hc";  //ref1
    public static final String KEY_TAG_1003_HC = Preference.class.getName() + "key_tag_1003_hc";  //ref2
    public static final String KEY_TAG_1004_HC = Preference.class.getName() + "key_tag_1004_hc";  //ref3
    public static final String KEY_TAG_1005_HC = Preference.class.getName() + "key_tag_1005_hc";  //fix amt
    public static final String KEY_TAG_1006_HC = Preference.class.getName() + "key_tag_1006_hc";  //merchant id
    public static final String KEY_TAG_1007_HC = Preference.class.getName() + "key_tag_1007_hc";  //limit amt per transaction

    public static final String KEY_QR_LAST_TRACE = Preference.class.getName() + "key_qr_last_trace";  //SINN //20180706 Add QR print.
    public static final String KEY_ALIPAY_LAST_TRACE = Preference.class.getName() + "key_ali_last_trace";  //JEFF 20181116
    public static final String KEY_WECHAT_LAST_TRACE = Preference.class.getName() + "key_wec_last_trace";  //JEFF 20181116
    public static final String KEY_TEMP = Preference.class.getName() + "key_temp";//SINN //20180709 POS sale
    public static final String KEY_RS232_FLAG = Preference.class.getName() + "key_rs232";//SINN //20180709 POS sale

    public static final String KEY_REF1 = Preference.class.getName() + "key_tag_ref1";
    public static final String KEY_REF2 = Preference.class.getName() + "key_tag_ref2";
    public static final String KEY_REF3 = Preference.class.getName() + "key_tag_ref3";

    public static final String KEY_MAX_AMT = Preference.class.getName() + "key_tag_MAX_AMT";   //SINN 20180716 ADD MAX AMY para
    public static final String KEY_APP_ENABLE = Preference.class.getName() + "key_tag_APP_ENABLE";  //SINN 20180803 APP ENABLE

    public static final String KEY_APP_GHC_ENABLE = Preference.class.getName() + "key_tag_APP_GHC_ENABLE";  //20180828 SINN menu GHC enable
    public static final String KEY_SET_ID = Preference.class.getName() + "key_set_id";  // Paul_20180810
    public static final String KEY_RCPT_LOGO_ID = Preference.class.getName() + "key_rcpt_logo_id";  // Paul_20180810

    ////20180815 SINN implement RATEMC,RATEVI,RATEMCLOCAL,RATEVILOCAL,UPI,TPN,JCB
    public static final String KEY_rateMC_ID = Preference.class.getName() + "key_rateMC_id";
    public static final String KEY_rateVI_ID = Preference.class.getName() + "key_rateVI_id";
    public static final String KEY_rateVILocal_ID = Preference.class.getName() + "key_rateVILocal_id";
    public static final String KEY_rateMCLocal_ID = Preference.class.getName() + "key_rateMCLocal_id";
    public static final String KEY_rateJCB_ID = Preference.class.getName() + "key_rateJCB_id";
    public static final String KEY_rateUPI_ID = Preference.class.getName() + "key_rateUPI_id";
    public static final String KEY_rateTPN_ID = Preference.class.getName() + "key_rateTPN_id";

    public static final String KEY_FixRATE_ID = Preference.class.getName() + "key_FixRATE_id";
    //end 20180815 SINN implement RATEMC,RATEVI,RATEMCLOCAL,RATEVILOCAL,UPI,TPN,JCB

    public static final String KEY_RS232_Enable_ID = Preference.class.getName() + "key_rs232_enable_id";
    public static final String KEY_SETTLEMENT_TRANS_ID = Preference.class.getName() + "key_settlement_trans_id";
    public static final String KEY_JSONBYPASS_ID = Preference.class.getName() + "key_jsonbypass_id";//SINN 180921
    public static final String KEY_KTBNORMAL_ID = Preference.class.getName() + "key_ktbnormal_id";
//    public static final String KEY_WAY4_ID = Preference.class.getName() + "key_ktbway4_id";
    public static final String KEY_SERVICE_PIN_ID = Preference.class.getName() + "key_service_pin_id";
    public static final String KEY_PRINTISO_ID = Preference.class.getName() + "printiso";//SINN 180921
    public static final String KEY_PRINTQR_ID = Preference.class.getName() + "key_printqr_id";//SINN 180921
    public static final String KEY_CARDEXP_ID = Preference.class.getName() + "key_cardexp_id";
    public static final String KEY_CARDMASK_ID = Preference.class.getName() + "key_cardmask_id";
    public static final String KEY_EXPIREMASK_ID = Preference.class.getName() + "key_expiremask_id";  //20181024 SINN MASK expire card

    //    public static final String KEY_ALI_WEC_ENABLE = Preference.class.getName() + "key_tag_ALI_WEC_ENABLE";    //// JEFF 20181103
    public static final String KEY_ALIPAY_ID =  Preference.class.getName() + "key_tag_alipay_id";   //// JEFF 20181103
    public static final String KEY_WECHATPAY_ID =  Preference.class.getName() + "key_tag_wechatpay_id";   //// JEFF 20181103
    public static final String KEY_RAILWAY_ID =  Preference.class.getName() + "key_tag_railway_id";   ////SINN 20181129 Railway project QR ref1
    public static final String KEY_CONTACTLESS_ID =  Preference.class.getName() + "contactless";
    public static final String KEY_SWIPE_ID =  Preference.class.getName() + "swipe";
    public static final String KEY_MANUAL_ID =  Preference.class.getName() + "manual";

    public static final String KEY_MAX_CONTACTLESS_ID =  Preference.class.getName() + "max_contactless"; // NAMTAN_20190701

    public static final String KEY_RAILWAY_REF1_ID =  Preference.class.getName() + "key_tag_railway_ref1_id";   ////SINN 20181129 Railway project QR ref1

    public static final String KEY_AXA_ID = Preference.class.getName() + "key_ktbaxa_id";   // Paul_20181103 Because WAY4 and AXA optional
    public static final String KEY_MerchantSupportRate_ID = Preference.class.getName() + "key_MerchantSupportRate_id";   // SINN merchant support rate.


    //20181213  SINN  KTB CR request set default ref text.
    public static final String KEY_Ref1text_ID = Preference.class.getName() + "key_ref1text_id";
    public static final String KEY_Ref2text_ID = Preference.class.getName() + "key_ref2text_id";
    public static final String KEY_Ref3text_ID = Preference.class.getName() + "key_ref3text_id";
    public static final String KEY_QrRef1text_ID = Preference.class.getName() + "key_qrref1text_id";
    public static final String KEY_QrRef2text_ID = Preference.class.getName() + "key_qrref2text_id";
    public static final String KEY_QrRef3text_ID = Preference.class.getName() + "key_qrref3text_id";
    //20181218  SINN Print slip enable/disable
    public static final String KEY_PrintSlip_ID = Preference.class.getName() + "key_printslip_id";
    public static final String KEY_SlipSyncTime_ID = Preference.class.getName() + "key_slipsynctime_id";


    // TOLLWAY
    public static final String KEY_RATE_PRICE = Preference.class.getName() + "key_rate_price";
    public static final String KEY_BUS_C_CITIZEN_ID = Preference.class.getName() + "key_bus_c_citizen_id";
    public static final String KEY_BUS_C_STAFF_ID = Preference.class.getName() + "key_bus_c_staff_id";
    public static final String KEY_BUS_C_NAME = Preference.class.getName() + "key_bus_c_name";
    public static final String KEY_BUS_LOGIN = Preference.class.getName() + "key_bus_login";

    public static final String KEY_DUPLICATE_TIME = Preference.class.getName() + "key_duplicate_time";

    public static final String KEY_JSON_CONTROL_VERSION = Preference.class.getName() + "key_json_control_version";

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
