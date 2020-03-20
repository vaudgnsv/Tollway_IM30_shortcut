package org.centerm.Tollway.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.centerm.Tollway.R;
import org.centerm.Tollway.adapter.GameAdapter;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    private RecyclerView recyclerViewGame;
    private ArrayList<String> GameList = null;
    private GameAdapter GameAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initData();
        initWidget();
    }

    private void initData() {
    }


    public void initWidget() {
        // super.initWidget();
        GameList = new ArrayList<>();
        GameList.clear();

        GameList.add("1");
        GameList.add("2");
        GameList.add("3");
        GameList.add("4");
        GameList.add("5");


        recyclerViewGame = findViewById(R.id.recyclerViewGame);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);//K.GAME Test
        recyclerViewGame.setLayoutManager(layoutManager);


        setMenuList();

    }

    private void setMenuList() {

        if (recyclerViewGame.getAdapter() == null) {

            GameAdapter = new GameAdapter(this);
            recyclerViewGame.setAdapter(GameAdapter);


            GameAdapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    switch (GameList.get(position)) {
                        case "1":

                            break;
                        case "2":

                            break;
                        case "3":

                            break;
                        case "4":

                            break;
                        case "5":

                    }

                }
            });
        } else {
            GameAdapter.clear();
        }
        GameAdapter.setItem(GameList);
        GameAdapter.notifyDataSetChanged();
    }

}
