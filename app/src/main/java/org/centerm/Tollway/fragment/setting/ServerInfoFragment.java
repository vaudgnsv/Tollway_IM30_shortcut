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

@SuppressWarnings("unused")
public class ServerInfoFragment extends Fragment implements View.OnClickListener {

    private Button primaryIpBtn;
    private Button primaryPortBtn;
    private Button secondaryIPBtn;
    private Button secondaryPortBtn;
    private TextView primaryIpLabel;
    private TextView primaryPortLabel;
    private TextView secondaryIPLabel;
    private TextView secondaryPortLabel;
    private CustomDialog customDialog;


    public ServerInfoFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static ServerInfoFragment newInstance() {
        ServerInfoFragment fragment = new ServerInfoFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_server_info, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        primaryIpBtn = rootView.findViewById(R.id.primaryIpBtn);
        primaryPortBtn = rootView.findViewById(R.id.primaryPortBtn);
        secondaryIPBtn = rootView.findViewById(R.id.secondaryIPBtn);
        secondaryPortBtn = rootView.findViewById(R.id.secondaryPortBtn);
        primaryIpLabel = rootView.findViewById(R.id.primaryIpLabel);
        primaryPortLabel = rootView.findViewById(R.id.primaryPortLabel);
        secondaryIPLabel = rootView.findViewById(R.id.secondaryIPLabel);
        secondaryPortLabel = rootView.findViewById(R.id.secondaryPortLabel);
        primaryIpLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_PRIMARY_IP));
        primaryPortLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_PRIMARY_PORT));
        secondaryIPLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_SECONDARY_IP));
        secondaryPortLabel.setText(Preference.getInstance(getContext()).getValueString(Preference.KEY_SECONDARY_PORT));
        primaryIpBtn.setOnClickListener(this);
        primaryPortBtn.setOnClickListener(this);
        secondaryIPBtn.setOnClickListener(this);
        secondaryPortBtn.setOnClickListener(this);
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
        if (v == primaryIpBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog  = new CustomDialog(getContext(),R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(primaryIpLabel.getText().toString());
//            customDialog.setInputFilter();
            customDialog.setInputText(InputType.TYPE_CLASS_PHONE);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    primaryIpLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_PRIMARY_IP,sEt);
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        } else if (v == primaryPortBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog  = new CustomDialog(getContext(),R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(primaryPortLabel.getText().toString());
            customDialog.setMaxLength(4);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    primaryPortLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_PRIMARY_PORT,sEt);
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        } else if (v == secondaryIPBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog  = new CustomDialog(getContext(),R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(secondaryIPLabel.getText().toString());
            customDialog.setInputText(InputType.TYPE_CLASS_PHONE);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    secondaryIPLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_SECONDARY_IP,sEt);
                    dialog.dismiss();
                }

                @Override
                public void onClickCancel(Dialog dialog) {
                    dialog.dismiss();
                }
            });
            customDialog.show();
        } else if (v == secondaryPortBtn) {
            if (customDialog != null) {
                customDialog = null;
            }
            customDialog  = new CustomDialog(getContext(),R.layout.dialog_custom_ip);
            customDialog.setInitWidgetDialog(secondaryPortLabel.getText().toString());
            customDialog.setMaxLength(4);
            customDialog.setCancelable(false);
            customDialog.setOnClickListener(new CustomDialog.OnClickDialog() {
                @Override
                public void onClickSave(Dialog dialog, String sEt) {
                    secondaryPortLabel.setText(sEt);
                    Preference.getInstance(getContext()).setValueString(Preference.KEY_SECONDARY_PORT,sEt);
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