package org.centerm.Tollway.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.centerm.Tollway.R;

import java.util.ArrayList;

public class MenuPaymentAdapter extends RecyclerView.Adapter<MenuPaymentAdapter.ViewHolder> {

    private Context context = null;
    private ArrayList<String> menuPaymentList = null;
    private View.OnClickListener onClickListener = null;

    public MenuPaymentAdapter(Context context) {
        this.context = context;
    }

    private int inCntItem;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.item_menu_service_list, parent, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuPaymentAdapter.ViewHolder holder, int position) {
        holder.nameMenuLabel.setText(menuPaymentList.get(position));
        holder.frameLayoutMenuServiceList.setTag(position);
    }

    @Override
    public int getItemCount() {
        return menuPaymentList != null ? menuPaymentList.size() : 0;
    }

    public void setItem(ArrayList<String> item) {
        inCntItem = 0;
        menuPaymentList = item;
    }

    public void clear() {
        if (menuPaymentList != null) {
            menuPaymentList.clear();
            notifyDataSetChanged();
        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public String getItem(int position) {
        return menuPaymentList.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_IconMenu; //K.GAME Change Menu
        private TextView nameMenuLabel = null;
        private FrameLayout frameLayoutMenuServiceList = null;


        public ViewHolder(View itemView) {
            super(itemView);
            img_IconMenu = itemView.findViewById(R.id.img_MenuIcon);//K.GAME Change Menu
            nameMenuLabel = itemView.findViewById(R.id.nameMenuLabel);
            frameLayoutMenuServiceList = itemView.findViewById(R.id.frameLayoutMenuServiceList);
            frameLayoutMenuServiceList.setOnClickListener(onClickListener);
        }
    }
}
