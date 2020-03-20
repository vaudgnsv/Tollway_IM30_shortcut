package org.centerm.Tollway.fragment.setting;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.centerm.Tollway.BuildConfig;
import org.centerm.Tollway.R;
//import org.centerm.Tollway.helper.printConfig;
import org.centerm.Tollway.utility.CustomDialog;
import org.centerm.Tollway.utility.Preference;

import java.text.DecimalFormat;

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
    private TextView posIdLabel = null;
    private Button posIdBtn = null;
    private CustomDialog customDialog;
    private TextView merchantnametopic;

////20180831 SINN Edit parameter
    private TextView anyParaIdLabel = null;
    private Button anyParaIdBtn = null;
    private TextView valIdLabel = null;
    private Button valIdBtn = null;

    private final String preferenceName = BuildConfig.APPLICATION_ID + "Setting";


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
        DecimalFormat decimalFormat = new DecimalFormat("###0.00");
        feeLabel.setText(decimalFormat.format(Preference.getInstance(getContext()).getValueDouble(Preference.KEY_FEE)));
        feeBtn = rootView.findViewById(R.id.feeBtn);

        texLabel = rootView.findViewById(R.id.texLabel);
        texLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_TAX_ID));

        texBtn = rootView.findViewById(R.id.texBtn);

        posIdLabel = rootView.findViewById(R.id.posIdLabel);
        posIdLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_POS_ID));

        posIdBtn = rootView.findViewById(R.id.posIdBtn);
        //merchantnametopic = rootView.findViewById(R.id.merchantnametopic);
        //merchantnametopic.setOnClickListener(this);


        anyParaIdLabel = rootView.findViewById(R.id.anyParaIdLabel);
      //  anyParaIdLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_POS_ID));
        anyParaIdBtn = rootView.findViewById(R.id.anyParaIdBtn);


        valIdLabel = rootView.findViewById(R.id.valIdLabel);
        valIdBtn = rootView.findViewById(R.id.valIdBtn);

        Log.d("Config", "anyParaIdLabel.getText().toString() ="+anyParaIdLabel.getText().toString()+" value "+Preference.getInstance(getContext()).getValueString("org.centerm.Tollway.utility.Preference"+anyParaIdLabel.getText().toString()));
        //org.centerm.Tollway.utility.Preferencekey_normal_pass_word


        if(!anyParaIdLabel.getText().toString().isEmpty())
            valIdLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_APP_ENABLE).toString());

        merchantnametopic = rootView.findViewById(R.id.merchantnametopic);
        merchantnametopic.setOnClickListener(this);

        merchantL1Btn.setOnClickListener(this);
        merchantL2Btn.setOnClickListener(this);
        merchantL3Btn.setOnClickListener(this);
        feeBtn.setOnClickListener(this);
        texBtn.setOnClickListener(this);
        posIdBtn.setOnClickListener(this);

        //20180831 SINN Edit parameter
        anyParaIdBtn.setOnClickListener(this);
        valIdBtn.setOnClickListener(this);


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
            customDialog.setMaxLength(4);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    feeLabel.setText(sEt);
                    DecimalFormat decimalFormat = new DecimalFormat("##0.00");
                    Preference.getInstance(getContext()).setValueDouble(Preference.KEY_FEE, Double.parseDouble(decimalFormat.format(Double.parseDouble(sEt))));
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
            customDialog.setInitWidgetDialog(texLabel.getText().toString());
            customDialog.setMaxLength(13);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    texLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_TAX_ID,sEt);
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        } else if (v == posIdBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(posIdLabel.getText().toString());
            customDialog.setMaxLength(20);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    posIdLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_POS_ID,sEt);
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        }
        ////20180831 SINN Edit parameter -----------------------------------------------------------
        else if (v == anyParaIdBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(anyParaIdLabel.getText().toString());
            customDialog.setMaxLength(100);
            customDialog.setInputText(InputType.TYPE_CLASS_TEXT);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    anyParaIdLabel.setText(sEt);
                    valIdLabel.setText(Preference.getInstance(getContext()).getValueString("org.centerm.Tollway.utility.Preference"+sEt));
                    //valIdLabel.setText(Preference.getInstance(getContext()).getValueString(sEt));
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        }
        else if (v == valIdBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(valIdLabel.getText().toString());
            customDialog.setMaxLength(100);
            customDialog.setInputText(InputType.TYPE_CLASS_TEXT);
            customDialog.setCancelable(false);

            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    valIdLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString("org.centerm.Tollway.utility.Preference"+anyParaIdLabel.getText().toString(),sEt);
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        }

        //-----------------------------------------------------------
        /*else if(v==merchantnametopic)
        {
            Intent intent = new Intent(getContext(), printConfig.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }*/
    }
}