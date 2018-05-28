package org.centerm.land.activity.qr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.centerm.land.R;
import org.centerm.land.adapter.MenuQrAdapter;
import org.centerm.land.bassactivity.SettingToolbarActivity;
import org.centerm.land.model.MenuQr;

import java.util.ArrayList;
import java.util.List;

public class MenuQrActivity extends SettingToolbarActivity {

    private RecyclerView recyclerViewMenuQr = null;
    private MenuQrAdapter menuQrAdapter = null;
    private List<MenuQr> menuList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_qr);
        initWidget();
        initBtnExit();
    }

    @Override
    public void initWidget() {
//        super.initWidget();
        recyclerViewMenuQr = findViewById(R.id.recyclerViewMenuQr);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewMenuQr.setLayoutManager(layoutManager);
        setMenuQr();
    }

    private void setMenuQr() {
        if (recyclerViewMenuQr.getAdapter() == null) {
            menuQrAdapter = new MenuQrAdapter(this);
            recyclerViewMenuQr.setAdapter(menuQrAdapter);
            menuQrAdapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    if (position == 0) {
                        Intent intent = new Intent(MenuQrActivity.this,GenerateQrActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                    } if (position == 1) {
                        Intent intent = new Intent(MenuQrActivity.this,CheckQrActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                    }
                }
            });
        } else {
            menuQrAdapter.clear();
        }
        if (menuList == null) {
            menuList = new ArrayList<>();
        } else {
            menuList.clear();
        }
        MenuQr menuQr1 = new MenuQr();
        menuQr1.setName("Generator QR ");
        menuQr1.setImage(R.drawable.qrscanner96x96);
        MenuQr menuQr2 = new MenuQr();
        menuQr2.setName("Check Complete");
        menuQr2.setImage(R.drawable.qrscanner96x96);

        menuList.add(menuQr1);
        menuList.add(menuQr2);
        menuQrAdapter.addItem(menuList);
        menuQrAdapter.notifyDataSetChanged();

    }
}
