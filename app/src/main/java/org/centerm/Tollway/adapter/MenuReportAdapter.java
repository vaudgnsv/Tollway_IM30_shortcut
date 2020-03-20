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
import org.centerm.Tollway.utility.Preference;

import java.util.ArrayList;
import java.util.List;

public class MenuReportAdapter extends RecyclerView.Adapter<MenuReportAdapter.ViewHolder> {
    private int inCntItem;
    private List<String> nameMenu;
    private Context context = null;
    private View.OnClickListener onClickListener = null;

    public MenuReportAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MenuReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.item_menu_service_list, parent, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuReportAdapter.ViewHolder holder, int position) {
        //K.GAME 180824 change UI
        if (nameMenu.get(position).equals("พิมพ์\nยอดรวม")) {
            holder.img_MenuIcon.setImageResource(R.drawable.ic_print2);
        }
        if (nameMenu.get(position).equals("พิมพ์\nรายละเอียด")) {
            holder.img_MenuIcon.setImageResource(R.drawable.ic_print_detail);
        }
//        if (nameMenu.get(position).equals("TAX")) {
        //SINN 20181212 TAX report enable by set TAX
        if (nameMenu.get(position).equals("พิมพ์\nรายงานภาษี")){
            holder.img_MenuIcon.setImageResource(R.drawable.ic_tax2);
        }
        //END K.GAME 180824 change UI
        inCntItem++;
        if (Preference.getInstance(context).getValueString(Preference.KEY_APP_ENABLE).substring(2, 3).equalsIgnoreCase("1")) {
            holder.nameMenuLabel.setText(String.valueOf(inCntItem) + ") " + nameMenu.get(position));
        } else {//K.GAME 181016
            holder.nameMenuLabel.setText(nameMenu.get(position));
        }
//        holder.nameMenuLabel.setText(nameMenu.get(position));
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
        private ImageView img_MenuIcon = null;
        private TextView nameMenuLabel = null;
        private FrameLayout frameLayoutMenuServiceList = null;

        public ViewHolder(View itemView) {
            super(itemView);
            img_MenuIcon = itemView.findViewById(R.id.img_MenuIcon);
            nameMenuLabel = itemView.findViewById(R.id.nameMenuLabel);
            frameLayoutMenuServiceList = itemView.findViewById(R.id.frameLayoutMenuServiceList);
            frameLayoutMenuServiceList.setOnClickListener(onClickListener);
        }
    }
}
