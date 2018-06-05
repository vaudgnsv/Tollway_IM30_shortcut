package org.centerm.land.fragment.setting;

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
import android.widget.TextView;

import org.centerm.land.R;
import org.centerm.land.utility.CustomDialog;
import org.centerm.land.utility.Preference;

@SuppressWarnings("unused")
public class MerchantFragment extends Fragment implements View.OnClickListener {

    private TextView merchantL1Label = null;
    private Button merchantL1Btn = null;
    private TextView merchantL2Label = null;
    private Button merchantL2Btn = null;
    private TextView merchantL3Label = null;
    private Button merchantL3Btn = null;
    private TextView feeLabel = null;
    private Button feeBtn = null;
    private TextView texLabel = null;
    private Button texBtn = null;
    private CustomDialog customDialog;

    public MerchantFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static MerchantFragment newInstance() {
        MerchantFragment fragment = new MerchantFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_merchant, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        merchantL1Label = rootView.findViewById(R.id.merchantL1Label);
        merchantL1Label.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_MERCHANT_1));

        merchantL1Btn = rootView.findViewById(R.id.merchantL1Btn);
        merchantL2Label = rootView.findViewById(R.id.merchantL2Label);
        merchantL2Label.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_MERCHANT_2));

        merchantL2Btn = rootView.findViewById(R.id.merchantL2Btn);
        merchantL3Label = rootView.findViewById(R.id.merchantL3Label);
        merchantL3Label.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_MERCHANT_3));

        merchantL3Btn = rootView.findViewById(R.id.merchantL3Btn);

        feeLabel = rootView.findViewById(R.id.feeLabel);
        feeLabel.setText(Preference.getInstance(getContext()).getValueFloat(Preference.KEY_FEE)+"");
        feeBtn = rootView.findViewById(R.id.feeBtn);

        texLabel = rootView.findViewById(R.id.texLabel);
        texLabel.setText(Preference.getInstance(getContext()).getValueFloat(Preference.KEY_FEE)+"");

        texBtn = rootView.findViewById(R.id.texBtn);

        merchantL1Btn.setOnClickListener(this);
        merchantL2Btn.setOnClickListener(this);
        merchantL3Btn.setOnClickListener(this);
        feeBtn.setOnClickListener(this);
        texBtn.setOnClickListener(this);
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
        if (v == merchantL1Btn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(merchantL1Label.getText().toString());
            customDialog.setMaxLength(50);
            customDialog.setInputText(InputType.TYPE_CLASS_TEXT);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    merchantL1Label.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_MERCHANT_1,sEt);
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        } else if (v == merchantL2Btn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(merchantL2Label.getText().toString());
            customDialog.setMaxLength(50);
            customDialog.setInputText(InputType.TYPE_CLASS_TEXT);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    merchantL2Label.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_MERCHANT_2,sEt);
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        } else if (v == merchantL3Btn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(merchantL3Label.getText().toString());
            customDialog.setMaxLength(50);
            customDialog.setInputText(InputType.TYPE_CLASS_TEXT);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    merchantL3Label.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_MERCHANT_3,sEt);
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        } else if (v == feeBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(feeLabel.getText().toString());
            customDialog.setMaxLength(20);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    feeLabel.setText(sEt);
//                    Preference.getInstance(getContext()).setValueString(Preference.KEY_QR_TERMINAL_ID,sEt);
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        } else if (v == texBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(feeLabel.getText().toString());
            customDialog.setMaxLength(10);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    feeLabel.setText(sEt);
//                    Preference.getInstance(getContext()).setValueString(Preference.KEY_QR_TERMINAL_ID,sEt);
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