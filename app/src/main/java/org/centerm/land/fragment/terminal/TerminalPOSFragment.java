package org.centerm.land.fragment.terminal;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.centerm.land.R;
import org.centerm.land.utility.CustomDialog;
import org.centerm.land.utility.Preference;
import org.centerm.land.utility.Utility;

@SuppressWarnings("unused")
public class TerminalPOSFragment extends Fragment implements View.OnClickListener {

    private Button terminalBtn;
    private Button applicationBtn;
    private Button merchantBtn;
    private TextView terminalLabel;
    private TextView applicationLabel;
    private TextView merchantLabel;
    private CustomDialog customDialog;

    private Button traceBtn;
    private Button batchBtn;
    private Button tpduBtn;
    private Button niiBtn;

    private TextView traceLabel;
    private TextView batchLabel;
    private TextView tpduLabel;
    private TextView niiLabel;

    public TerminalPOSFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static TerminalPOSFragment newInstance() {
        TerminalPOSFragment fragment = new TerminalPOSFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_trtminal_pos, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        terminalBtn = rootView.findViewById(R.id.terminalBtn);
        applicationBtn = rootView.findViewById(R.id.applicationBtn);
        merchantBtn = rootView.findViewById(R.id.merchantBtn);
        terminalLabel = rootView.findViewById(R.id.terminalLabel);
        applicationLabel = rootView.findViewById(R.id.applicationLabel);
        merchantLabel = rootView.findViewById(R.id.merchantLabel);
        traceBtn = rootView.findViewById(R.id.traceBtn);
        batchBtn = rootView.findViewById(R.id.batchBtn);
        tpduBtn = rootView.findViewById(R.id.tpduBtn);
        niiBtn = rootView.findViewById(R.id.niiBtn);
        traceLabel = rootView.findViewById(R.id.traceLabel);
        batchLabel = rootView.findViewById(R.id.batchLabel);
        tpduLabel = rootView.findViewById(R.id.tpduLabel);
        niiLabel = rootView.findViewById(R.id.niiLabel);
        terminalLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_TERMINAL_ID_POS));
        merchantLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_MERCHANT_ID_POS));
        traceLabel.setText(Utility.calNumTraceNo(Preference.getInstance(getContext()).getValueString(Preference.KEY_TRACE_NO_POS)));
        batchLabel.setText(Utility.calNumTraceNo(Preference.getInstance(getContext()).getValueString(Preference.KEY_BATCH_NUMBER_POS)));
        tpduLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_TPDU_POS));
        niiLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_NII_POS));
        traceBtn.setOnClickListener(this);
        batchBtn.setOnClickListener(this);
        tpduBtn.setOnClickListener(this);
        niiBtn.setOnClickListener(this);
        terminalBtn.setOnClickListener(this);
        applicationBtn.setOnClickListener(this);
        merchantBtn.setOnClickListener(this);
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
        if (v == terminalBtn){
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog  = new CustomDialog(getContext(),R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(terminalLabel.getText().toString());
            customDialog.setMaxLength(8);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    terminalLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_TERMINAL_ID_POS,sEt);
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        } else if (v == applicationBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog  = new CustomDialog(getContext(),R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(applicationLabel.getText().toString());
            customDialog.setMaxLength(6);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    applicationLabel.setText(sEt);
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();

        } else if (v == merchantBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog  = new CustomDialog(getContext(),R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(merchantLabel.getText().toString());
            customDialog.setMaxLength(15);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    merchantLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_MERCHANT_ID_POS,sEt);
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        } else if (v == traceBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(traceLabel.getText().toString());
            customDialog.setMaxLength(6);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    traceLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_TRACE_NO_POS, String.valueOf(Integer.valueOf(sEt)));
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        } else if (v == batchBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(batchLabel.getText().toString());
            customDialog.setMaxLength(6);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    batchLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_BATCH_NUMBER_POS, String.valueOf(Integer.valueOf(sEt)));
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        } else if (v == tpduBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(tpduLabel.getText().toString());
            customDialog.setMaxLength(10);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    tpduLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_TPDU_POS,sEt);
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        } else if (v == niiBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(niiLabel.getText().toString());
            customDialog.setMaxLength(4);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    niiLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_NII_POS,sEt);
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