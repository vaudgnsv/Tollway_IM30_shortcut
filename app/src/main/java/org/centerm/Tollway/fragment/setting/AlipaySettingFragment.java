package org.centerm.Tollway.fragment.setting;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.centerm.Tollway.R;
import org.centerm.Tollway.utility.CustomDialog;
import org.centerm.Tollway.utility.Preference;

// Paul_20181007

@SuppressWarnings("unused")
public class AlipaySettingFragment extends Fragment implements View.OnClickListener {

    private TextView alipayTerminalIdLabel = null;
    private Button alipayTerminalIdBtn = null;

    private TextView alipayMerchantIdLabel = null;////20180814 SINN  use QR Merchant ID instead biller id.
    private Button alipayMerchantIdBtn = null;   ////20180814 SINN  use QR Merchant ID instead biller id.

    private TextView alipayStoreidLabel = null;
    private Button alipayStoreidBtn = null;

    private TextView alipayUrlLabel = null;
    private Button alipayUrlBtn = null;

    private TextView alipayPubLabel = null;
    private Button alipayPubBtn = null;

    private TextView alipayCerLabel = null;
    private Button alipayCerBtn = null;

    private CustomDialog customDialog;

    public AlipaySettingFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static AlipaySettingFragment newInstance() {
        AlipaySettingFragment fragment = new AlipaySettingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alipay_setting, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here

        alipayTerminalIdLabel = rootView.findViewById(R.id.alipayTerminalIdLabel);
        alipayTerminalIdLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_ALIPAY_TERMINAL_ID));
        alipayTerminalIdBtn = rootView.findViewById(R.id.alipayTerminalIdBtn);

        alipayMerchantIdLabel = rootView.findViewById(R.id.alipayMerchantIdLabel);
        alipayMerchantIdLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_ALIPAY_MERCHANT_ID));
        alipayMerchantIdBtn = rootView.findViewById(R.id.alipayMerchantIdBtn);  //20180814 SINN  use QR Merchant ID instead biller id.

        alipayStoreidLabel = rootView.findViewById(R.id.alipayStoreidLabel);
        alipayStoreidLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_ALIPAY_STORE_ID));
        alipayStoreidBtn = rootView.findViewById(R.id.alipayStoreidKeyBtn);

        alipayUrlLabel = rootView.findViewById(R.id.alipayUrlLabel);
        alipayUrlLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_ALIPAY_URL_ID));
        alipayUrlBtn = rootView.findViewById(R.id.alipayUrlBtn);

        alipayPubLabel = rootView.findViewById(R.id.alipayPubLabel);
        alipayPubLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_ALIPAY_PUBLIC_ID));
        alipayPubBtn = rootView.findViewById(R.id.alipayPubBtn);

        alipayCerLabel = rootView.findViewById(R.id.alipayCerLabel);
        alipayCerLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_ALIPAY_CERTI_ID));
        alipayCerBtn = rootView.findViewById(R.id.alipayCerBtn);

        alipayTerminalIdBtn.setOnClickListener(this);
        alipayMerchantIdBtn.setOnClickListener(this);   //20180814 SINN  use QR Merchant ID instead biller id.
        alipayStoreidBtn.setOnClickListener(this);
        alipayUrlBtn.setOnClickListener(this);
        alipayPubBtn.setOnClickListener(this);
        alipayCerBtn.setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }

    @Override
    public void onClick(View v) {
        if (v == alipayTerminalIdBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(alipayTerminalIdLabel.getText().toString());
            customDialog.setMaxLength(8);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    alipayTerminalIdLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_ALIPAY_TERMINAL_ID,sEt);
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        }
        //---------------------------------------------------------------------------------
        ////20180814 SINN  use QR Merchant ID instead biller id.
        else if (v == alipayMerchantIdBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(alipayMerchantIdLabel.getText().toString());
            customDialog.setMaxLength(18);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    alipayMerchantIdLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_ALIPAY_MERCHANT_ID,sEt);
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        }

        //---------------------------------------------------------------------------------
        else if (v == alipayStoreidBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(alipayStoreidLabel.getText().toString());
            customDialog.setMaxLength(19);
            customDialog.setInputText(InputType.TYPE_CLASS_TEXT);
            customDialog.setCancelable(false);


            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    alipayStoreidLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_ALIPAY_STORE_ID,sEt);
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        }else if(v == alipayUrlBtn){
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(alipayUrlLabel.getText().toString());
            customDialog.setInputText(InputType.TYPE_CLASS_TEXT);
            customDialog.setCancelable(false);


            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    alipayUrlLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_ALIPAY_URL_ID,sEt);
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        }else if(v == alipayPubBtn){
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(alipayPubLabel.getText().toString());
            customDialog.setInputText(InputType.TYPE_CLASS_TEXT);
            customDialog.setCancelable(false);


            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    alipayPubLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_ALIPAY_PUBLIC_ID,sEt);
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        }else if(v == alipayCerBtn){
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(alipayCerLabel.getText().toString());
            customDialog.setInputText(InputType.TYPE_CLASS_TEXT);
            customDialog.setCancelable(false);


            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    alipayCerLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_ALIPAY_CERTI_ID,sEt);
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        }
    }
}