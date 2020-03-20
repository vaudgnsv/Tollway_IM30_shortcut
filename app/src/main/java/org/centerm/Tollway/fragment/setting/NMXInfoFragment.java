package org.centerm.Tollway.fragment.setting;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import org.centerm.Tollway.CardManager;
import org.centerm.Tollway.MainApplication;
import org.centerm.Tollway.R;
import org.centerm.Tollway.database.ReversalHealthCare;
import org.centerm.Tollway.database.ReversalTemp;
import org.centerm.Tollway.database.TCUpload;
import org.centerm.Tollway.database.TransTemp;
import org.centerm.Tollway.utility.Utility;

import io.realm.Realm;
import io.realm.RealmResults;

//import org.centerm.Tollway.healthcare.database.SaleOfflineHealthCare;

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
    private Button ghcBtn = null;
    //    private ImageView closeImage = null; //K.GAME 180828 change dialog UI
    private Button closeImage = null; //K.GAME 180828 change dialog UI

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
                realm = null;   // Paul_20181026 Some time DB Read error solved
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

    // Paul_20180706
    private void removeReversalHealthCareSetup() {
        Realm.getDefaultInstance().refresh();
        if (realm != null) {
            if (!realm.isClosed()) {
                realm.close();
                realm = null;   // Paul_20181026 Some time DB Read error solved
            }
        }
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(ReversalHealthCare.class);
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
                realm = null;   // Paul_20181026 Some time DB Read error solved
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
        System.out.printf("utility:: NMXInfoFragment deleteBatch = %s\n", typeHOst);
        Realm.getDefaultInstance().refresh();
        if (realm != null) {
            if (!realm.isClosed()) {
                realm.close();
                realm = null;   // Paul_20181026 Some time DB Read error solved
            }
        }
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                System.out.printf("utility:: NMXInfoFragment deleteBatch 002 = %s\n", typeHOst);
                final RealmResults<TransTemp> transTemps = realm.where(TransTemp.class).equalTo("hostTypeCard", typeHOst).findAll();
                if (transTemps != null) {
                    System.out.printf("utility:: NMXInfoFragment transTemps.size() = %d\n", transTemps.size());
                    transTemps.deleteAllFromRealm();
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
//                Utility.customDialogAlertSuccess(getContext(), "ลบข้อมูลสำเร็จ", new Utility.OnClickCloseImage() {
//                    @Override
//                    public void onClickImage(Dialog dialog) {
//                        dialog.dismiss();
//                    }
//                });
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

        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                System.out.printf("utility:: NMXInfoFragment deleteBatch 003 = %s\n", typeHOst);
                final RealmResults<TCUpload> transTemps = realm.where(TCUpload.class).findAll();
                if (transTemps != null) {
                    System.out.printf("utility:: NMXInfoFragment transTemps.size() = %d\n", transTemps.size());
                    transTemps.deleteAllFromRealm();
                }
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

    private void deleteBatchGHC(final String typeHOst) {
        System.out.printf("utility:: NMXInfoFragment deleteBatchGHC = %s\n", typeHOst);
        Realm.getDefaultInstance().refresh();
        if (realm != null) {
            if (!realm.isClosed()) {
                realm.close();
                realm = null;   // Paul_20181026 Some time DB Read error solved
            }
        }
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
//                TransTemp transTemps = realm.where(TransTemp.class).equalTo("hostTypeCard","GHC").findFirst();
                final RealmResults<TransTemp> transTemps = realm.where(TransTemp.class).equalTo("hostTypeCard", "GHC").findAll();
//                SaleOfflineHealthCare healthCareDB =  realm.where(SaleOfflineHealthCare.class).findFirst(); //Paul 20180724_OFF
                if (transTemps != null)
                    transTemps.deleteAllFromRealm();    // Paul_20180725_OFF

                //Paul 20180707
//                if (healthCareDB != null)
//                    healthCareDB.deleteFromRealm();
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
        dialogHost = new Dialog(getContext(), R.style.ThemeWithCorners); //K.GAME 180821
        View view = dialogHost.getLayoutInflater().inflate(R.layout.dialog_custom_host, null);//K.GAME 180821
        dialogHost.requestWindowFeature(Window.FEATURE_NO_TITLE);//K.GAME 180821
        dialogHost.setContentView(view);//K.GAME 180821
        dialogHost.setCancelable(false);//K.GAME 180821

//        dialogHost = new Dialog(getContext());
//        dialogHost.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogHost.setCancelable(false);
//        dialogHost.setContentView(R.layout.dialog_custom_host);
//        dialogHost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogHost.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        posBtn = dialogHost.findViewById(R.id.posBtn);
        epsBtn = dialogHost.findViewById(R.id.epsBtn);
        tmsBtn = dialogHost.findViewById(R.id.tmsBtn);
        ghcBtn = dialogHost.findViewById(R.id.ghcBtn);
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

        ghcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeClick.equals("REVERSAL")) {
                    System.out.printf("utility:: GHC Reversal Clear 001 \n");
                    removeReversalHealthCareSetup();        // Paul_20180706
                    deleteReversal();
//                    deleteReversal();
                } else {
                    System.out.printf("utility:: GHC Reversal Clear 002 \n");
                    deleteBatch("GHC");
//                    deleteBatchGHC("GHC");
                }
            }
        });
    }

    private void deleteBatchOfflineGHC() {

        Realm.getDefaultInstance().refresh();
        if (realm != null) {
            if (!realm.isClosed()) {
                realm.close();
                realm = null;   // Paul_20181026 Some time DB Read error solved
            }
        }
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                TransTemp healthCareDB = realm.where(TransTemp.class).findFirst();
//                TransTemp transTemps = realm.where(TransTemp.class).equalTo("hostTypeCard","GHC").findFirst();
                if (healthCareDB != null)
                    healthCareDB.deleteFromRealm();
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nmxBtn:
//                cardManager.SetToJsonSmartSerialNo();   //Paul_20181114 No Need // Paul_20181029 Json Add to SmartSerial No
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