package org.centerm.land.activity.menuvoid;

import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.content.ServiceConnection;

import org.centerm.land.R;
import org.centerm.land.adapter.MenuVoidAdapter;
import org.centerm.land.bassactivity.SettingToolbarActivity;

import java.util.ArrayList;

public class MenuActivity extends SettingToolbarActivity {

    private RecyclerView menuRecyclerView;
    private MenuVoidAdapter menuVoidAdapter;
    private ArrayList<String> nameMenuList;
    public static final String KEY_MENU_HOST = MenuActivity.class.getName() + "key_menu_host";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initWidget();
        initBtnExit();
    }

    @Override
    public void initWidget() {
//        super.initWidget();
        menuRecyclerView = findViewById(R.id.menuRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        menuRecyclerView.setLayoutManager(layoutManager);
        setMenuList();

    }

    private void setMenuList() {
        if (menuRecyclerView.getAdapter() == null) {
            menuVoidAdapter = new MenuVoidAdapter(this);
            menuRecyclerView.setAdapter(menuVoidAdapter);
            menuVoidAdapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    if (position == 0) {
                        Intent intent = new Intent(MenuActivity.this,VoidActivity.class);
                        intent.putExtra(KEY_MENU_HOST,"POS");
                        startActivity(intent);
                        overridePendingTransition(0,0);
                    } else if (position == 1) {
                        Intent intent = new Intent(MenuActivity.this,VoidActivity.class);
                        intent.putExtra(KEY_MENU_HOST,"EPS");
                        startActivity(intent);
                        overridePendingTransition(0,0);
                    } else if (position == 2) {
                        Intent intent = new Intent(MenuActivity.this,VoidActivity.class);
                        intent.putExtra(KEY_MENU_HOST,"TMS");
                        startActivity(intent);
                        overridePendingTransition(0,0);
                    }
                }
            });
        } else {
            menuVoidAdapter.clear();
        }
        if (nameMenuList == null) {
            nameMenuList = new ArrayList<>();
        } else {
            nameMenuList.clear();
        }
        nameMenuList.add("POS");
        nameMenuList.add("EPS");
        nameMenuList.add("TMS");
        menuVoidAdapter.setItem(nameMenuList);
        menuVoidAdapter.notifyDataSetChanged();
    }
}
