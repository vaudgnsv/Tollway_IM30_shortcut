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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.centerm.Tollway.R;
import org.centerm.Tollway.utility.CustomDialog;
import org.centerm.Tollway.utility.Preference;

@SuppressWarnings("unused")
public class QrSettingFragment extends Fragment implements View.OnClickListener {

    private CheckBox ref1CB = null;
    private CheckBox ref2CB = null;
    private TextView aidLabel = null;
    private Button aidBtn = null;
    private TextView billerIdLabel = null;
    private Button  billerIdBtn = null;
    private TextView merchantNameLabel = null;
    private Button merchantNameBtn = null;
    private TextView qrTerminalIdLabel = null;
    private Button qrTerminalIdBtn = null;

    private TextView qrMerchantIdLabel = null;////20180814 SINN  use QR Merchant ID instead biller id.
    private Button qrMerchantIdBtn = null;   ////20180814 SINN  use QR Merchant ID instead biller id.


    private TextView billerKeyLabel = null;
    private Button billerKeyBtn = null;
    private TextView qrPortLabel = null;
    private Button qrPortBtn = null;
    private TextView merchantNameThaiLabel = null;
    private Button merchantNameThaiBtn = null;
    private CustomDialog customDialog;

    public QrSettingFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static QrSettingFragment newInstance() {
        QrSettingFragment fragment = new QrSettingFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_qr_setting, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        ref1CB = rootView.findViewById(R.id.ref1CB);
        ref2CB = rootView.findViewById(R.id.ref2CB);

        aidLabel = rootView.findViewById(R.id.aidLabel);
        aidLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_QR_AID));
        aidBtn = rootView.findViewById(R.id.aidBtn);

        billerIdLabel = rootView.findViewById(R.id.billerIdLabel);
        billerIdLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_QR_BILLER_ID));
        billerIdBtn = rootView.findViewById(R.id.billerIdBtn);

        merchantNameLabel = rootView.findViewById(R.id.merchantNameLabel);
        merchantNameLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_QR_MERCHANT_NAME));
        merchantNameBtn = rootView.findViewById(R.id.merchantNameBtn);

        qrTerminalIdLabel = rootView.findViewById(R.id.qrTerminalIdLabel);
        qrTerminalIdLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_QR_TERMINAL_ID));
        qrTerminalIdBtn = rootView.findViewById(R.id.qrTerminalIdBtn);


        qrMerchantIdLabel = rootView.findViewById(R.id.qrMerchantIdLabel);
        qrMerchantIdLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_QR_MERCHANT_ID));
        qrMerchantIdBtn = rootView.findViewById(R.id.qrMerchantIdBtn);  //20180814 SINN  use QR Merchant ID instead biller id.

        billerKeyLabel = rootView.findViewById(R.id.billerKeyLabel);
        billerKeyLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_BILLER_KEY));
        billerKeyBtn = rootView.findViewById(R.id.billerKeyBtn);

        qrPortLabel = rootView.findViewById(R.id.qrPortLabel);
        qrPortLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_QR_PORT));
        qrPortBtn = rootView.findViewById(R.id.qrPortBtn);

        merchantNameThaiLabel = rootView.findViewById(R.id.merchantNameThaiLabel);
        merchantNameThaiLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_QR_MERCHANT_NAME_THAI));
        merchantNameThaiBtn = rootView.findViewById(R.id.merchantNameThaiBtn);

        aidBtn.setOnClickListener(this);
        billerIdBtn.setOnClickListener(this);
        merchantNameBtn.setOnClickListener(this);
        qrTerminalIdBtn.setOnClickListener(this);
        qrMerchantIdBtn.setOnClickListener(this);   //20180814 SINN  use QR Merchant ID instead biller id.

        billerKeyBtn.setOnClickListener(this);
        qrPortBtn.setOnClickListener(this);
        merchantNameThaiBtn.setOnClickListener(this);


        ref2CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Preference.getInstance(getContext()).setValueBoolean(Preference.KEY_REF_2,true);
                } else {
                    Preference.getInstance(getContext()).setValueBoolean(Preference.KEY_REF_2,false);
                }
            }
        });
        if (Preference.getInstance(getContext()).getValueBoolean(Preference.KEY_REF_2)) {
            ref2CB.setChecked(true);
        } else {
            ref2CB.setChecked(false);
        }
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
        if (v == aidBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(aidLabel.getText().toString());
            customDialog.setMaxLength(16);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    aidLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_QR_AID,sEt);
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        } else if (v == billerIdBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(billerIdLabel.getText().toString());
            customDialog.setMaxLength(15);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    billerIdLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_QR_BILLER_ID,sEt);
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        } else if (v == merchantNameBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(merchantNameLabel.getText().toString());
            customDialog.setMaxLength(99);
            customDialog.setInputText(InputType.TYPE_CLASS_TEXT);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    merchantNameLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_QR_MERCHANT_NAME,sEt);
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        } else if (v == qrTerminalIdBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(qrTerminalIdLabel.getText().toString());
            customDialog.setMaxLength(20);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    qrTerminalIdLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_QR_TERMINAL_ID,sEt);
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
        else if (v == qrMerchantIdBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(qrMerchantIdLabel.getText().toString());
            customDialog.setMaxLength(15);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    qrMerchantIdLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_QR_MERCHANT_ID,sEt);
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
        else if (v == billerKeyBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(billerKeyLabel.getText().toString());
            customDialog.setMaxLength(20);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    qrTerminalIdLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_BILLER_KEY,sEt);
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        }else if (v == qrPortBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(qrPortLabel.getText().toString());
            customDialog.setMaxLength(20);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    qrTerminalIdLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_QR_PORT,sEt);
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        }else if (v == merchantNameThaiBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(merchantNameThaiLabel.getText().toString());
            customDialog.setMaxLength(50);
            customDialog.setInputText(InputType.TYPE_CLASS_TEXT);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    merchantNameThaiLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_QR_MERCHANT_NAME_THAI,sEt);
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