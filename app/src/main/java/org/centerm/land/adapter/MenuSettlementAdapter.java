package org.centerm.land.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.centerm.land.R;

import java.util.ArrayList;

public class MenuSettlementAdapter extends RecyclerView.Adapter<MenuSettlementAdapter.ViewHolder> {

    private View.OnClickListener onClickListener;
    private Context context = null;
    private ArrayList<String> nameMenuList = null;

    public MenuSettlementAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.item_menu_service_list,parent,false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nameMenuLabel.setText(nameMenuList.get(position));
        holder.frameLayoutMenuServiceList.setTag(position);
    }

    @Override
    public int getItemCount() {
        return nameMenuList != null ? nameMenuList.size() : 0;
    }

    public void clear() {
        if (nameMenuList != null) {
            nameMenuList.clear();
            notifyDataSetChanged();
        }
    }

    public void setItem(ArrayList<String> item) {
        nameMenuList = item;
    }

    public void setOnClickListener(View.OnClickListener onClickListener ) {
        this.onClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameMenuLabel = null;
        private FrameLayout frameLayoutMenuServiceList = null;
        public ViewHolder(View itemView) {
            super(itemView);
            nameMenuLabel = itemView.findViewById(R.id.nameMenuLabel);
            frameLayoutMenuServiceList = itemView.findViewById(R.id.frameLayoutMenuServiceList);
            frameLayoutMenuServiceList.setOnClickListener(onClickListener);
        }
    }
}
