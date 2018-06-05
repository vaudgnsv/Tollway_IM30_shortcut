package org.centerm.land.fragment.setting;

import android.app.Dialog;
import android.graphics.Color;
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
import android.widget.LinearLayout;

import org.centerm.land.CardManager;
import org.centerm.land.MainApplication;
import org.centerm.land.R;
import org.centerm.land.database.ReversalTemp;
import org.centerm.land.database.TransTemp;
import org.centerm.land.utility.Utility;

import io.realm.Realm;
import io.realm.RealmResults;

@SuppressWarnings("unused")
public class NMXInfoFragment extends Fragment implements View.OnClickListener {

    private LinearLayout creditLinearLayout = null;
    private Button nmxBtn = null;
    private Button clearReverBtn = null;
    private Button clearBatchBtn = null;
    private CardManager cardManager = null;
    private Realm realm = null;
    private Dialog dialogHost;

    private Button posBtn = null;
    private Button epsBtn = null;
    private Button tmsBtn = null;
    private ImageView closeImage = null;

    private String typeClick = null;

    public NMXInfoFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static NMXInfoFragment newInstance() {
        NMXInfoFragment fragment = new NMXInfoFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_nmx_setup, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        cardManager = MainApplication.getCardManager();
        nmxBtn = rootView.findViewById(R.id.nmxBtn);
        clearReverBtn = rootView.findViewById(R.id.clearReverBtn);
        clearBatchBtn = rootView.findViewById(R.id.clearBatchBtn);
        clearReverBtn.setOnClickListener(this);
        clearBatchBtn.setOnClickListener(this);
        nmxBtn.setOnClickListener(this);
        customDialogHost();
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

    private void deleteReversal() {
        Realm.getDefaultInstance().refresh();
        if (realm != null) {
            if (!realm.isClosed()) {
                realm.close();
            }
        }
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<ReversalTemp> reversalTemp = realm.where(ReversalTemp.class).findAll();
                reversalTemp.deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Utility.customDialogAlertSuccess(getContext(), "ลบข้อมูลสำเร็จ", new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Utility.customDialogAlert(getContext(), "ลบข้อมูลล้มเหลว", new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
            }
        });
        realm.close();
        realm = null;
    }

    private void deleteBatch() {
        Realm.getDefaultInstance().refresh();
        if (realm != null) {
            if (!realm.isClosed()) {
                realm.close();
            }
        }
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<TransTemp> transTemps = realm.where(TransTemp.class).findAll();
                transTemps.deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Utility.customDialogAlertSuccess(getContext(), "ลบข้อมูลสำเร็จ", new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Utility.customDialogAlert(getContext(), "ลบข้อมูลล้มเหลว", new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
            }
        });
        realm.close();
        realm = null;
    }

    private void deleteBatch(final String typeHOst) {
        Realm.getDefaultInstance().refresh();
        if (realm != null) {
            if (!realm.isClosed()) {
                realm.close();
            }
        }
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<TransTemp> transTemps = realm.where(TransTemp.class).equalTo("hostTypeCard",typeHOst).findAll();
                if (transTemps != null)
                    transTemps.deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Utility.customDialogAlertSuccess(getContext(), "ลบข้อมูลสำเร็จ", new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Utility.customDialogAlert(getContext(), "ลบข้อมูลล้มเหลว", new Utility.OnClickCloseImage() {
                    @Override
                    public void onClickImage(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
            }
        });
        realm.close();
        realm = null;
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
                if (typeClick.equals("REVERSAL")) {
                    deleteReversal();
                } else {
                    deleteBatch("POS");
                }
            }
        });
        epsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeClick.equals("REVERSAL")) {
                    deleteReversal();
                } else {
                    deleteBatch("EPS");
                }
            }
        });

        tmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeClick.equals("REVERSAL")) {
                    deleteReversal();
                } else {
                    deleteBatch("TMS");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nmxBtn:
                cardManager.RKIdownload();
                break;
            case R.id.clearReverBtn:
                typeClick = "REVERSAL";
                dialogHost.show();
                break;
            case R.id.clearBatchBtn:
                typeClick = "BATCH";
                dialogHost.show();
                break;
        }
    }
}