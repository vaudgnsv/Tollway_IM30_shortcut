package org.centerm.land.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.centerm.land.CardManager;
import org.centerm.land.MainApplication;
import org.centerm.land.R;
import org.centerm.land.activity.MenuServiceListActivity;
import org.centerm.land.activity.SettingActivity;
import org.centerm.land.activity.qr.MenuQrActivity;
import org.centerm.land.bassactivity.SettingToolbarActivity;
import org.centerm.land.utility.Preference;
import org.centerm.land.utility.Utility;

@SuppressWarnings("unused")
public class MenuServiceFragment extends Fragment {

    private final String TAG = "MenuServiceFragment";

    public static final String KEY_TYPE_PASSWORD = "key_type_password";
    public static String TYPE_NORMAL_PASSWORD = "type_normal_password";
    public static String TYPE_ADMIN_PASSWORD = "type_admin_password";

    private LinearLayout creditLinearLayout = null;
    private LinearLayout qrLinearLayout = null;
    private LinearLayout settingLinearLayout = null;
    private LinearLayout linearLayoutTestHost = null;
    private Dialog dialogPassword;
    private EditText passwordBox;
    private Button okBtn;
    private Button cancelBtn;
    private Dialog dialogHost;
    private Button posBtn;
    private Button epsBtn;
    private Button tmsBtn;
    private ImageView closeImage;

    private CardManager cardManager = null;

    public MenuServiceFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static MenuServiceFragment newInstance() {
        MenuServiceFragment fragment = new MenuServiceFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        creditLinearLayout = rootView.findViewById(R.id.creditLinearLayout);
        qrLinearLayout = rootView.findViewById(R.id.qrLinearLayout);
        settingLinearLayout = rootView.findViewById(R.id.settingLinearLayout);
        creditLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MenuServiceListActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(0, 0);
            }
        });
        qrLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MenuQrActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(0, 0);
            }
        });
        settingLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPassword.show();
                passwordBox.setText("");

            }
        });
        linearLayoutTestHost = rootView.findViewById(R.id.linearLayoutTestHost);
        linearLayoutTestHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHost.show();
            }
        });
        customDialogPassword();
        customDialogHost();
    }

    private void onCheckHost() {
        if (cardManager != null)
            cardManager.setTestHostLister(new CardManager.TestHostLister() {
                @Override
                public void onResponseCodeSuccess() {
                    if (isAdded() && !isHidden()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utility.customDialogAlertSuccess(getContext(), null, new Utility.OnClickCloseImage() {
                                    @Override
                                    public void onClickImage(Dialog dialog) {
                                        dialog.dismiss();
                                    }
                                });
                            }
                        });
                    }
                }

                @Override
                public void onConnectTimeOut() {
                    if (isAdded()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utility.customDialogAlert(getContext(), "ไม่สามารถเชื่อมต่อได้", new Utility.OnClickCloseImage() {
                                    @Override
                                    public void onClickImage(Dialog dialog) {
                                        dialog.dismiss();
                                    }
                                });
                            }
                        });
                    }
                }

                @Override
                public void onTransactionTimeOut() {
                    if (isAdded() && !isHidden()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utility.customDialogAlert(getContext(), "ไม่สามารถเชื่อมต่อได้", new Utility.OnClickCloseImage() {
                                    @Override
                                    public void onClickImage(Dialog dialog) {
                                        dialog.dismiss();
                                    }
                                });
                            }
                        });
                    }
                }
            });
    }

    private void customDialogPassword() {
        dialogPassword = new Dialog(getContext());
        dialogPassword.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPassword.setContentView(R.layout.dialog_custom_input_password);
        dialogPassword.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogPassword.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialogPassword.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        passwordBox = dialogPassword.findViewById(R.id.passwordBox);
        okBtn = dialogPassword.findViewById(R.id.okBtn);
        cancelBtn = dialogPassword.findViewById(R.id.cancelBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordBox.getText().toString().isEmpty()) {
                    passwordBox.setError("กรุณาใส่รหัสผ่าน");
                } else {
                    if (Preference.getInstance(getContext()).getValueString(Preference.KEY_ADMIN_PASS_WORD).equalsIgnoreCase(passwordBox.getText().toString())) {
                        Intent intent = new Intent(getActivity(), SettingActivity.class);
                        intent.putExtra(KEY_TYPE_PASSWORD, TYPE_ADMIN_PASSWORD);
                        startActivity(intent);
                        getActivity().overridePendingTransition(0, 0);
                        dialogPassword.dismiss();
                    } else if (Preference.getInstance(getContext()).getValueString(Preference.KEY_NORMAL_PASS_WORD).equalsIgnoreCase(passwordBox.getText().toString())) {
                        Intent intent = new Intent(getActivity(), SettingActivity.class);
                        intent.putExtra(KEY_TYPE_PASSWORD, TYPE_NORMAL_PASSWORD);
                        startActivity(intent);
                        getActivity().overridePendingTransition(0, 0);
                        dialogPassword.dismiss();
                    } else {
                        passwordBox.setError("รหัสผิดพลาด");
                    }
                }

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPassword.dismiss();
            }
        });
    }

    private void customDialogHost() {
        dialogHost = new Dialog(getContext());
        dialogHost.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogHost.setCancelable(false);
        dialogHost.setContentView(R.layout.dialog_custom_host);
        dialogHost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogHost.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        posBtn = dialogHost.findViewById(R.id.posBtn);
        epsBtn = dialogHost.findViewById(R.id.epsBtn);
        tmsBtn = dialogHost.findViewById(R.id.tmsBtn);
        closeImage = dialogHost.findViewById(R.id.closeImage);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHost.dismiss();
            }
        });
        posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardManager.setDataTestHostPos();
            }
        });
        epsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardManager.setDataTestHostEPS();
            }
        });

        tmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardManager.setDataTestHostTMS();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();

        cardManager = MainApplication.getCardManager();
        onCheckHost();

        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        cardManager = null;
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d(TAG, "onStop: ");
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "setUserVisibleHint: " + isVisibleToUser);
    }
}