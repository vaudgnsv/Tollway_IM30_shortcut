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
import java.util.List;

public class MenuReportAdapter extends RecyclerView.Adapter<MenuReportAdapter.ViewHolder> {

    private List<String> nameMenu;
    private Context context = null;
    private View.OnClickListener onClickListener = null;

    public MenuReportAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MenuReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.item_menu_service_list,parent,false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuReportAdapter.ViewHolder holder, int position) {
        holder.nameMenuLabel.setText(nameMenu.get(position));
        holder.frameLayoutMenuServiceList.setTag(position);
    }

    @Override
    public int getItemCount() {
        return nameMenu != null ? nameMenu.size() : 0;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void clear() {
        if (nameMenu != null) {
            nameMenu.clear();
            notifyDataSetChanged();
        }
    }

    public void setItem(List<String> item) {
        if (nameMenu == null) {
            nameMenu = new ArrayList<>();
        }
        nameMenu = item;
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
