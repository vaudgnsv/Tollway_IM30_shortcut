package org.centerm.land.fragment.terminal;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.centerm.land.CardManager;
import org.centerm.land.MainApplication;
import org.centerm.land.R;
import org.centerm.land.utility.CustomDialog;
import org.centerm.land.utility.Preference;
import org.centerm.land.utility.Utility;

@SuppressWarnings("unused")
public class TerminalTMSFragment extends Fragment implements View.OnClickListener {

    private Button fsBtn = null;
    private Button padBtn = null;
    private CardManager cardManager = null;

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
    private Button terminalVersionBtn;
    private Button messageVersionBtn;

    private TextView traceLabel;
    private TextView batchLabel;
    private TextView tpduLabel;
    private TextView niiLabel;
    private TextView terminalVersionLabel;
    private TextView messageVersionLabel;
    private Dialog dialogWaiting;
    private Dialog dialogAlertSuccess;

    public TerminalTMSFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static TerminalTMSFragment newInstance() {
        TerminalTMSFragment fragment = new TerminalTMSFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_trtminal_tms, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        cardManager = MainApplication.getCardManager();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        customDialogWaiting();
        customDialogSuccess();
        fsBtn = rootView.findViewById(R.id.fsBtn);
        padBtn = rootView.findViewById(R.id.padBtn);
        fsBtn.setOnClickListener(this);
        padBtn.setOnClickListener(this);

        terminalBtn = rootView.findViewById(R.id.terminalBtn);
        terminalVersionBtn = rootView.findViewById(R.id.terminalVersionBtn);
        messageVersionBtn = rootView.findViewById(R.id.messageVersionBtn);
        terminalVersionLabel = rootView.findViewById(R.id.terminalVersionLabel);
        messageVersionLabel = rootView.findViewById(R.id.messageVersionLabel);
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
        terminalLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_TERMINAL_ID_TMS));
        merchantLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_MERCHANT_ID_TMS));
        traceLabel.setText(Utility.calNumTraceNo(Preference.getInstance(getContext()).getValueString(Preference.KEY_TRACE_NO_TMS)));
        batchLabel.setText(Utility.calNumTraceNo(Preference.getInstance(getContext()).getValueString(Preference.KEY_BATCH_NUMBER_TMS)));
        tpduLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_TPDU_TMS));
        niiLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_NII_TMS));
        terminalVersionLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_TERMINAL_VERSION));
        messageVersionLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_MESSAGE_VERSION));
        traceBtn.setOnClickListener(this);
        batchBtn.setOnClickListener(this);
        tpduBtn.setOnClickListener(this);
        niiBtn.setOnClickListener(this);
        terminalBtn.setOnClickListener(this);
        applicationBtn.setOnClickListener(this);
        merchantBtn.setOnClickListener(this);

        callBackResponseCode();
    }

    private void callBackResponseCode() {
        cardManager.setResponseCodeListener(new CardManager.ResponseCodeListener() {
            @Override
            public void onResponseCode(String response) {
                dialogWaiting.dismiss();
                if (!getActivity().isFinishing())
                    dialogResponseError(response);
            }

            @Override
            public void onResponseCodeSuccess() {
                dialogWaiting.dismiss();
                if (!getActivity().isFinishing())
                    dialogResponseSuccess();
            }

            @Override
            public void onConnectTimeOut() {
                dialogWaiting.dismiss();
                if (!getActivity().isFinishing())
                    dialogResponseError(null);
            }

            @Override
            public void onTransactionTimeOut() {
                dialogWaiting.dismiss();
                if (!getActivity().isFinishing())
                    dialogResponseError(null);
            }
        });
    }

    private void dialogResponseError(final String response) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utility.customDialogAlert(getContext(), response, new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void dialogResponseSuccess() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialogAlertSuccess != null) {
                    if (!dialogAlertSuccess.isShowing()) {
                        dialogAlertSuccess.show();
                    }
                }
            }
        });
    }

    private void customDialogWaiting() {
        dialogWaiting = new Dialog(getContext());
        dialogWaiting.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWaiting.setCancelable(false);
        dialogWaiting.setContentView(R.layout.dialog_custom_load_process);
        dialogWaiting.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogWaiting.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        ImageView waitingImage = dialogWaiting.findViewById(R.id.waitingImage);
        AnimationDrawable animationDrawable = (AnimationDrawable) waitingImage.getBackground();
        animationDrawable.start();
    }

    private void customDialogSuccess() {
        dialogAlertSuccess = new Dialog(getContext());
        dialogAlertSuccess.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAlertSuccess.setContentView(R.layout.dialog_custom_success);
        dialogAlertSuccess.setCancelable(false);
        dialogAlertSuccess.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAlertSuccess.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        TextView msgLabel = dialogAlertSuccess.findViewById(R.id.msgLabel);
        ImageView closeImage = dialogAlertSuccess.findViewById(R.id.closeImage);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAlertSuccess.dismiss();
            }
        });
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
        switch (v.getId()) {
            case R.id.fsBtn:
                dialogWaiting.show();
                cardManager.setDataFirstSettlement();
                break;
            case R.id.padBtn:
                dialogWaiting.show();
                cardManager.setDataParameterDownload();
                break;
            case R.id.terminalBtn:
                if (customDialog != null) {
                    customDialog = null;
                }
                customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
                customDialog.setInitWidgetDialog(terminalLabel.getText().toString());
                customDialog.setMaxLength(8);
                customDialog.setCancelable(false);
                customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                    @Override
                    public void onClickSave(Dialog dialog, String sEt) {
                        terminalLabel.setText(sEt);
                        Preference.getInstance(getContext()).setValueString(Preference.KEY_TERMINAL_ID_TMS, sEt);
                        dialog.dismiss();
                    }

                    @Override
                    public void onClickCancel(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
                customDialog.show();
                break;
            case R.id.applicationBtn:
                if (customDialog != null) {
                    customDialog = null;
                }
                customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
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
                break;
            case R.id.merchantBtn:
                if (customDialog != null) {
                    customDialog = null;
                }
                customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
                customDialog.setInitWidgetDialog(merchantLabel.getText().toString());
                customDialog.setMaxLength(15);
                customDialog.setCancelable(false);
                customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                    @Override
                    public void onClickSave(Dialog dialog, String sEt) {
                        merchantLabel.setText(sEt);
                        Preference.getInstance(getContext()).setValueString(Preference.KEY_MERCHANT_ID_TMS, sEt);
                        dialog.dismiss();
                    }

                    @Override
                    public void onClickCancel(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
                customDialog.show();
                break;
            case R.id.traceBtn:
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
                        Preference.getInstance(getContext()).setValueString(Preference.KEY_TRACE_NO_TMS, String.valueOf(Integer.valueOf(sEt)));
                        dialog.dismiss();
                    }

                    @Override
                    public void onClickCancel(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
                customDialog.show();
                break;
            case R.id.batchBtn:
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
                        Preference.getInstance(getContext()).setValueString(Preference.KEY_BATCH_NUMBER_TMS, String.valueOf(Integer.valueOf(sEt)));
                        dialog.dismiss();
                    }

                    @Override
                    public void onClickCancel(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
                customDialog.show();
                break;
            case R.id.tpduBtn:
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
                        Preference.getInstance(getContext()).setValueString(Preference.KEY_TPDU_TMS, sEt);
                        dialog.dismiss();
                    }

                    @Override
                    public void onClickCancel(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
                customDialog.show();
                break;
            case R.id.niiBtn:
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
                        Preference.getInstance(getContext()).setValueString(Preference.KEY_NII_TMS, sEt);
                        dialog.dismiss();
                    }

                    @Override
                    public void onClickCancel(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
                customDialog.show();
                break;
            case R.id.terminalVersionBtn:
                if (customDialog != null) {
                    customDialog = null;
                }
                customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
                customDialog.setInitWidgetDialog(terminalVersionLabel.getText().toString());
                customDialog.setMaxLength(8);
                customDialog.setCancelable(false);
                customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                    @Override
                    public void onClickSave(Dialog dialog, String sEt) {
                        terminalVersionLabel.setText(sEt);
                        Preference.getInstance(getContext()).setValueString(Preference.KEY_TERMINAL_VERSION, sEt);
                        dialog.dismiss();
                    }

                    @Override
                    public void onClickCancel(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
                customDialog.show();
                break;
            case R.id.messageVersionBtn:
                if (customDialog != null) {
                    customDialog = null;
                }
                customDialog = new CustomDialog(getContext(), R.layout.dialog_custom_ip);
                customDialog.setInitWidgetDialog(messageVersionLabel.getText().toString());
                customDialog.setMaxLength(8);
                customDialog.setCancelable(false);
                customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                    @Override
                    public void onClickSave(Dialog dialog, String sEt) {
                        messageVersionLabel.setText(sEt);
                        Preference.getInstance(getContext()).setValueString(Preference.KEY_MESSAGE_VERSION, sEt);
                        dialog.dismiss();
                    }

                    @Override
                    public void onClickCancel(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
                customDialog.show();
                break;
        }
    }
}