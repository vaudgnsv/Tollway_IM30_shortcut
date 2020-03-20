package org.centerm.Tollway.healthcare.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.centerm.Tollway.R;

@SuppressWarnings("unused")
public class FamilyFragment extends Fragment implements View.OnClickListener {

    private CardView familyCardView;
    private CardView minSevenCardView;
    private CardView foreignerCardView;
    private CardView noCardCardView;


    public FamilyFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static FamilyFragment newInstance() {
        FamilyFragment fragment = new FamilyFragment();
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
        familyCardView = rootView.findViewById(R.id.familyCardView);
        minSevenCardView = rootView.findViewById(R.id.minSevenCardView);
        foreignerCardView = rootView.findViewById(R.id.foreignerCardView);
        noCardCardView = rootView.findViewById(R.id.noCardCardView);

        familyCardView.setOnClickListener(this);
        minSevenCardView.setOnClickListener(this);
        foreignerCardView.setOnClickListener(this);
        noCardCardView.setOnClickListener(this);

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
            case R.id.familyCardView:

                break;
            case R.id.minSevenCardView:

                break;
            case R.id.foreignerCardView:

                break;
            case R.id.noCardCardView:

                break;
        }
    }
}