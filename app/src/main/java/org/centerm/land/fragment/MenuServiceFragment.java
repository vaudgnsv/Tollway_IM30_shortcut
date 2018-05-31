package org.centerm.land.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.centerm.land.R;
import org.centerm.land.activity.MenuServiceListActivity;
import org.centerm.land.activity.SettingActivity;
import org.centerm.land.activity.qr.MenuQrActivity;
import org.centerm.land.bassactivity.SettingToolbarActivity;

@SuppressWarnings("unused")
public class MenuServiceFragment extends Fragment {

    private LinearLayout creditLinearLayout = null;
    private LinearLayout qrLinearLayout = null;
    private LinearLayout settingLinearLayout = null;

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
                Intent intent = new Intent(getActivity(),MenuServiceListActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(0,0);
            }
        });
        qrLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MenuQrActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(0,0);
            }
        });
        settingLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(0,0);
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

}