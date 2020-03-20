package org.centerm.Tollway.healthcare.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import org.centerm.Tollway.R;

@SuppressWarnings("unused")
public class MenuPatientTypeListFragment extends Fragment implements View.OnClickListener {

    private CardView personOutCardView;
    private CardView kidneyCardView;
    private CardView cancerPatientsCardView;
    /**
     * Dialog Family
     */
    private Dialog dialogMenuFamily;
    private Button familyBtn;
    private Button minSevenBtn;
    private Button foreignerBtn;
    private Button noCardBtn;
    private ImageView closeImage;

    public MenuPatientTypeListFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static MenuPatientTypeListFragment newInstance() {
        MenuPatientTypeListFragment fragment = new MenuPatientTypeListFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_menu_patient_type_list, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        customDialogMenuFamily();
        personOutCardView = rootView.findViewById(R.id.personOutCardView);
        kidneyCardView = rootView.findViewById(R.id.kidneyCardView);
        cancerPatientsCardView = rootView.findViewById(R.id.cancerPatientsCardView);
        personOutCardView.setOnClickListener(this);
        kidneyCardView.setOnClickListener(this);
        cancerPatientsCardView.setOnClickListener(this);
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


    private void customDialogMenuFamily() {
        dialogMenuFamily = new Dialog(getContext());
        dialogMenuFamily.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogMenuFamily.setCancelable(false);
        dialogMenuFamily.setContentView(R.layout.dialog_custom_family);
        dialogMenuFamily.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogMenuFamily.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        familyBtn = dialogMenuFamily.findViewById(R.id.familyBtn);
        minSevenBtn = dialogMenuFamily.findViewById(R.id.minSevenBtn);
        foreignerBtn = dialogMenuFamily.findViewById(R.id.foreignerBtn);
        noCardBtn = dialogMenuFamily.findViewById(R.id.noCardBtn);
        closeImage = dialogMenuFamily.findViewById(R.id.closeImage);

        familyBtn.setOnClickListener(this);
        minSevenBtn.setOnClickListener(this);
        foreignerBtn.setOnClickListener(this);
        noCardBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personOutCardView:
                dialogMenuFamily.show();
                break;
            case R.id.kidneyCardView:
                dialogMenuFamily.show();
                break;
            case R.id.cancerPatientsCardView:
                dialogMenuFamily.show();
                break;
            /**
             * Click ฺButton Dialog
             */
            case R.id.familyBtn:

                break;
            case R.id.minSevenBtn:

                break;
            case R.id.foreignerBtn:

                break;
            case R.id.noCardBtn:

                break;
        }
    }

}