package org.centerm.Tollway.fragment.terminal;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.centerm.Tollway.R;
import org.centerm.Tollway.core.BlockCalculateUtil;
import org.centerm.Tollway.healthcare.basefragment.BaseHealthCareFragment;
import org.centerm.Tollway.helper.CardPrefix;
import org.centerm.Tollway.utility.CustomDialog;
import org.centerm.Tollway.utility.Preference;
import org.centerm.Tollway.utility.Utility;

@SuppressWarnings("unused")
public class TerminalHealthCareFragment extends BaseHealthCareFragment implements View.OnClickListener {

    private Button fsBtn = null;
    private Button padBtn = null;

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
    private String[] mBlockDataSend;
    private final String TAG = "TerminalHealthCare";

    public TerminalHealthCareFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static TerminalHealthCareFragment newInstance() {
        TerminalHealthCareFragment fragment = new TerminalHealthCareFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_trtminal_ghc, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
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
        terminalLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_TERMINAL_ID_GHC));
        merchantLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_MERCHANT_ID_GHC));
        traceLabel.setText(Utility.calNumTraceNo(Preference.getInstance(getContext()).getValueString(Preference.KEY_TRACE_NO_GHC)));
        batchLabel.setText(Utility.calNumTraceNo(Preference.getInstance(getContext()).getValueString(Preference.KEY_BATCH_NUMBER_GHC)));
        tpduLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_TPDU_GHC));
        niiLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_NII_GHC));
        terminalVersionLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_TERMINAL_VERSION));
        messageVersionLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_MESSAGE_GHC_VERSION));
        traceBtn.setOnClickListener(this);
        batchBtn.setOnClickListener(this);
        tpduBtn.setOnClickListener(this);
        niiBtn.setOnClickListener(this);
        terminalBtn.setOnClickListener(this);
        applicationBtn.setOnClickListener(this);
        merchantBtn.setOnClickListener(this);
        messageVersionBtn.setOnClickListener(this);
        terminalVersionBtn.setOnClickListener(this);


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
        //K.GAME 180831 chang waitting UI
        ImageView waitingImage = dialogWaiting.findViewById(R.id.waitingImage);
        AnimationDrawable animationDrawable = (AnimationDrawable) waitingImage.getBackground();
        animationDrawable.start();
        Utility.animation_Waiting_new(dialogWaiting);
        //END K.GAME 180831 chang waitting UI
    }

    private void customDialogSuccess() {
        dialogAlertSuccess = new Dialog(getContext(), R.style.ThemeWithCorners);//K.GAME 180821
        View view = dialogAlertSuccess.getLayoutInflater().inflate(R.layout.dialog_custom_success, null);//K.GAME 180821
        dialogAlertSuccess.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
        dialogAlertSuccess.setContentView(view);//K.GAME 180821
        dialogAlertSuccess.setCancelable(false);//K.GAME 180821
//        dialogAlertSuccess = new Dialog(getContext());
//        dialogAlertSuccess.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogAlertSuccess.setContentView(R.layout.dialog_custom_success);
//        dialogAlertSuccess.setCancelable(false);
//        dialogAlertSuccess.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogAlertSuccess.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        TextView msgLabel = dialogAlertSuccess.findViewById(R.id.msgLabel);
        Button btn_success = dialogAlertSuccess.findViewById(R.id.btn_dialog_success);
        btn_success.setOnClickListener(new View.OnClickListener() {
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
                setDataFirstSettlementGHC();
                break;
            case R.id.padBtn:
                dialogWaiting.show();
                setDataParameterDownloadGHC();
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
                        Preference.getInstance(getContext()).setValueString(Preference.KEY_TERMINAL_ID_GHC, sEt);
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
                        Preference.getInstance(getContext()).setValueString(Preference.KEY_MERCHANT_ID_GHC, sEt);
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
                        Preference.getInstance(getContext()).setValueString(Preference.KEY_TRACE_NO_GHC, String.valueOf(Integer.valueOf(sEt)));
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
                        Preference.getInstance(getContext()).setValueString(Preference.KEY_BATCH_NUMBER_GHC, String.valueOf(Integer.valueOf(sEt)));
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
                        Preference.getInstance(getContext()).setValueString(Preference.KEY_TPDU_GHC, sEt);
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
                        Preference.getInstance(getContext()).setValueString(Preference.KEY_NII_GHC, sEt);
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


    private void setDataFirstSettlementGHC() {
        String terminalVersion = Preference.getInstance(getContext()).getValueString(Preference.KEY_TERMINAL_VERSION);
        String messageVersion = Preference.getInstance(getContext()).getValueString(Preference.KEY_MESSAGE_GHC_VERSION);
        String transactionCode = Preference.getInstance(getContext()).getValueString(Preference.KEY_TRANSACTION_CODE);
        String messageLen = "00000106";
        String terminalSN = "88888888";        // Paul_20180522
        String samId = "5555555555555556";      // Paul_20180522
        String samCsn = "4444444444444445";     // Paul_20180522
        String randomData = "00000000";         // Paul_20180522
        String terminalCERT = "              "; // Paul_20180522
        String checkSUM = "00000000";           // Paul_20180522
        mBlockDataSend = new String[64];
        mBlockDataSend[3 - 1] = "920000";
        mBlockDataSend[24 - 1] = Preference.getInstance(getContext()).getValueString(Preference.KEY_NII_GHC);
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(CardPrefix.getTerminalId(getContext(), "GHC"));
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(CardPrefix.getMerchantId(getContext(), "GHC"));
        mBlockDataSend[63 - 1] = getLength62(String.valueOf((messageLen + terminalVersion + messageVersion + transactionCode + terminalSN + samId + samCsn + randomData + checkSUM).length())) + BlockCalculateUtil.getHexString(messageLen + terminalVersion + messageVersion + transactionCode + terminalSN + samId + samCsn + randomData  + checkSUM);
        packageAndSend(Preference.getInstance(getContext()).getValueString(Preference.KEY_TPDU_GHC), "0800", mBlockDataSend);

    }

    private void setDataParameterDownloadGHC() {
        String terminalVersion = Preference.getInstance(getContext()).getValueString(Preference.KEY_TERMINAL_VERSION);
        String messageVersion = Preference.getInstance(getContext()).getValueString(Preference.KEY_MESSAGE_GHC_VERSION);
        String transactionCode = Preference.getInstance(getContext()).getValueString(Preference.KEY_TRANSACTION_CODE);
        String parameterVersion = Preference.getInstance(getContext()).getValueString(Preference.KEY_PARAMETER_VERSION);
        String messageLen = "00000074";
//        String terminalSN = "000025068";        // Paul_20180522
        /*String samId = "                ";      // Paul_20180522
        String samCsn = "                ";     // Paul_20180522*/
        String randomData = "00000000";         // Paul_20180522
        String terminalCERT = "              "; // Paul_20180522
        String checkSUM = "00000000";           // Paul_20180522
        mBlockDataSend = new String[64];
        mBlockDataSend[3 - 1] = "900000";
        mBlockDataSend[24 - 1] = Preference.getInstance(getContext()).getValueString(Preference.KEY_NII_GHC);    // Paul_20180522
        mBlockDataSend[41 - 1] = BlockCalculateUtil.getHexString(CardPrefix.getTerminalId(getContext(), "GHC"));
        mBlockDataSend[42 - 1] = BlockCalculateUtil.getHexString(CardPrefix.getMerchantId(getContext(), "GHC"));
        String s63 = messageLen + terminalVersion + messageVersion + transactionCode + parameterVersion + randomData + terminalCERT + checkSUM;
        mBlockDataSend[63 - 1] = getLength62(String.valueOf((s63).length())) + BlockCalculateUtil.getHexString(s63);
        packageAndSend(Preference.getInstance(getContext()).getValueString(Preference.KEY_TPDU_GHC), "0800", mBlockDataSend);

    }

    private void setDataFS(String[] mBlockDataReceived) {
        if (mBlockDataReceived[63 - 1] != null) {
            Log.d(TAG, "dealWithTheResponse: " + mBlockDataReceived[63 - 1]);
            String paraVersion = mBlockDataReceived[63 - 1].substring(28, 28 + 16);
            String batch = mBlockDataReceived[63 - 1].substring(44, 44 + 16);
            String transactionNo = mBlockDataReceived[63 - 1].substring(60, 60 + 16);
            Log.d(TAG, "dealWithTheResponse: " + paraVersion + "\n batch : " + batch + " \n transactionNo : " + transactionNo);
            Log.d(TAG, "dealWithTheResponse: BlockCalculateUtil.changeStringToHexString(paraVersion) : " + BlockCalculateUtil.hexToString(paraVersion));
            Preference.getInstance(getContext()).setValueString(Preference.KEY_PARAMETER_VERSION, BlockCalculateUtil.hexToString(paraVersion));
            Preference.getInstance(getContext()).setValueString(Preference.KEY_BATCH_NUMBER_GHC, BlockCalculateUtil.hexToString(batch).substring(2, BlockCalculateUtil.hexToString(batch).length()));
            Preference.getInstance(getContext()).setValueString(Preference.KEY_TRACE_NO_GHC, BlockCalculateUtil.hexToString(transactionNo));
            Preference.getInstance(getContext()).setValueString(Preference.KEY_INVOICE_NUMBER_ALL, BlockCalculateUtil.hexToString(transactionNo).substring(2, 8));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void connectTimeOut() {
dialogWaiting.dismiss();
Utility.customDialogAlert(getContext(), "ConnectTimeOut", new Utility.OnClickCloseImage() {
    @Override
    public void onClickImage(Dialog dialog) {
        dialog.dismiss();
    }
});
    }

    @Override
    protected void transactionTimeOut() {
        dialogWaiting.dismiss();
        Utility.customDialogAlert(getContext(), "transactionTimeOut", new Utility.OnClickCloseImage() {
            @Override
            public void onClickImage(Dialog dialog) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void received(final String[] data) {
        System.out.printf("utility:: %s received \n",TAG);
        dialogWaiting.dismiss();
        String de39 =  BlockCalculateUtil.hexToString(data[39 -1 ]);
        String de62 = BlockCalculateUtil.hexToString(data[62 - 1]);
        if (de39.equalsIgnoreCase("00") && mBlockDataSend[3 - 1].equalsIgnoreCase("920000")) {
            setDataFS(data);
            setDataParameterDownloadGHC();
        } else if (de39.equalsIgnoreCase("00")){
            int tagNumber = 54;
            Log.d(TAG, "dealWithTheResponse: ParameterDownload");
            Log.d(TAG, "dealWithTheResponse: " + data[63 - 1]);

            getTag(tagNumber,data);
            Utility.customDialogAlertSuccess(getContext(), null, new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
// Paul_20180718
//                    Toast.makeText(getContext(), "ADADA" + data[39 -1 ], Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        } else {
            Utility.customDialogAlert(getContext(), "ERROR : " + de39 , new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
// Paul_20180718
//                    Toast.makeText(getContext(), "Error" + data[39 -1 ], Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        }
    }

    private void getTag(int tagNumber, String[] data) {
        String tagAll = data[63 - 1];

        String tagId0 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagLen0 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagData0 = tagAll.substring(tagNumber, tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen0)) * 2));
        tagNumber = tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen0)) * 2);
        tagNumber = tagNumber + 16;

        String tagId1 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagLen1 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagData1 = tagAll.substring(tagNumber, tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen1)) * 2));
        tagNumber = tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen1)) * 2);
        tagNumber = tagNumber + 16;

        String tagId2 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagLen2 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagData2 = tagAll.substring(tagNumber, tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen2)) * 2));
        tagNumber = tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen2)) * 2);
        tagNumber += 16;

        String tagId3 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagLen3 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagData3 = tagAll.substring(tagNumber, tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen3)) * 2));
        tagNumber = tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen3)) * 2);
        tagNumber += 16;

        String tagId4 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagLen4 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagData4 = tagAll.substring(tagNumber, tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen4)) * 2));
        tagNumber = tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen4)) * 2);
        tagNumber += 16;

        String tagId5 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagLen5 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagData5 = tagAll.substring(tagNumber, tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen5)) * 2));
        tagNumber = tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen5)) * 2);
        tagNumber += 16;

        String tagId6 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagLen6 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagData6 = tagAll.substring(tagNumber, tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen6)) * 2));
        tagNumber = tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen6)) * 2);
        tagNumber += 16;

        String tagId7 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagLen7 = tagAll.substring(tagNumber, tagNumber + 8);
        tagNumber = tagNumber + 8;
        String tagData7 = tagAll.substring(tagNumber, tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen7)) * 2));
        tagNumber = tagNumber + (Integer.valueOf(BlockCalculateUtil.hexToString(tagLen7)) * 2);
        tagNumber += 16;

        Preference.getInstance(getContext()).setValueString(Preference.KEY_TAG_1000_HC, BlockCalculateUtil.hexToString(tagData0));
        Preference.getInstance(getContext()).setValueString(Preference.KEY_TAG_1001_HC, BlockCalculateUtil.hexToString(tagData1));
        Preference.getInstance(getContext()).setValueString(Preference.KEY_TAG_1002_HC, BlockCalculateUtil.hexToString(tagData2));
        Preference.getInstance(getContext()).setValueString(Preference.KEY_TAG_1003_HC, BlockCalculateUtil.hexToString(tagData3));
        Preference.getInstance(getContext()).setValueString(Preference.KEY_TAG_1004_HC, BlockCalculateUtil.hexToString(tagData4));
        Preference.getInstance(getContext()).setValueString(Preference.KEY_TAG_1005_HC, BlockCalculateUtil.hexToString(tagData5));
        Preference.getInstance(getContext()).setValueString(Preference.KEY_TAG_1006_HC, BlockCalculateUtil.hexToString(tagData6));
        Preference.getInstance(getContext()).setValueString(Preference.KEY_TAG_1007_HC, BlockCalculateUtil.hexToString(tagData7));

        Log.d(TAG, "dealWithTheResponse tagId: " +
                " \n Tag 0 : " + tagId0 +
                " \n tagLen 0 : " + tagLen0 +
                " \n tagData 0 : " + tagData0 +
                " \n =========================================== " +
                " \n Tag 1 : " + tagId1 +
                " \n tagLen 1 : " + tagLen1 +
                " \n tagData 1 : " + tagData1 +
                " \n =========================================== " +
                " \n Tag 2 : " + tagId2 +
                " \n tagLen 2 : " + tagLen2 +
                " \n tagData 2 : " + tagData2 +
                " \n =========================================== " +
                " \n Tag 3 : " + tagId3 +
                " \n tagLen 3 : " + tagLen3 +
                " \n tagData 3 : " + tagData3 +
                " \n =========================================== " +
                " \n Tag 4 : " + tagId4 +
                " \n tagLen 4 : " + tagLen4 +
                " \n tagData 4 : " + tagData4 +
                " \n =========================================== " +
                " \n Tag 5 : " + tagId5 +
                " \n tagLen 5 : " + tagLen5 +
                " \n tagData 5 : " + tagData5 +
                " \n =========================================== " +
                " \n Tag 6 : " + tagId6 +
                " \n tagLen 6 : " + tagLen6 +
                " \n tagData 6 : " + tagData6 +
                " \n =========================================== " +
                " \n Tag 7 : " + tagId7 +
                " \n tagLen 7 : " + tagLen7 +
                " \n tagData 7 : " + tagData7 +
                " \n =========================================== " +
                " \n tagNumber : " + tagNumber);
    }

    @Override
    protected void error(String error) {
        dialogWaiting.dismiss();
        Utility.customDialogAlert(getContext(), "error 002", new Utility.OnClickCloseImage() {
            @Override
            public void onClickImage(Dialog dialog) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void other() {
        dialogWaiting.dismiss();
        Utility.customDialogAlert(getContext(), "other", new Utility.OnClickCloseImage() {
            @Override
            public void onClickImage(Dialog dialog) {
                dialog.dismiss();
            }
        });
    }


}