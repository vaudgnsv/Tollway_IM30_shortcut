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

public class SettingForUserAdapter extends RecyclerView.Adapter<SettingForUserAdapter.ViewHolder> {

    private Context context = null;
    private ArrayList<String> settingForUserList = null;
    private View.OnClickListener onClickListener = null;

    public SettingForUserAdapter(Context context) {
        this.context = context;
    }

    private int inCntItem;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate( R.layout.item_menu_service_list, parent, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingForUserAdapter.ViewHolder holder, int position) {
        if (settingForUserList.get(position).equals("เริ่มต้น\nการเชื่อมต่อ")) {
            holder.img_IconMenu.setImageResource( R.drawable.ic_settings_gray);
        }
        if (settingForUserList.get(position).equals("สรุปยอด\nครั้งแรก")) {
            holder.img_IconMenu.setImageResource( R.drawable.ic_setsummery);
        }
        if (settingForUserList.get(position).equals("ตรวจสอบ\nการอัปเดต")) {
            holder.img_IconMenu.setImageResource( R.drawable.ic_settings_gray);
        }
        if (settingForUserList.get(position).equals("ดาวน์โหลด\nParameter")) {
            holder.img_IconMenu.setImageResource( R.drawable.ic_download);
        }
        if (settingForUserList.get(position).equals("Clear\nReversal")) {
            holder.img_IconMenu.setImageResource( R.drawable.ic_reversal);
        }
        if (settingForUserList.get(position).equals("Clear\nBatch")) {
            holder.img_IconMenu.setImageResource( R.drawable.ic_batch);
        }
        if (settingForUserList.get(position).equals("เวอร์ชั่น")) {
            holder.img_IconMenu.setImageResource( R.drawable.ic_version);
        }
        if (settingForUserList.get(position).equals("ขั้นสูง")) {
            holder.img_IconMenu.setImageResource( R.drawable.ic_setting);
        }
//        inCntItem++;
//        holder.nameMenuLabel.setText(String.valueOf(inCntItem) + ") " + settingForUserList.get(position));
        holder.nameMenuLabel.setText(settingForUserList.get(position));
        holder.frameLayoutMenuServiceList.setTag(position);
    }

    @Override
    public int getItemCount() {
        return settingForUserList != null ? settingForUserList.size() : 0;
    }

    public void setItem(ArrayList<String> item) {
        inCntItem = 0;
        settingForUserList = item;
    }

    public void clear() {
        if (settingForUserList != null) {
            settingForUserList.clear();
            notifyDataSetChanged();
        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public String getItem(int position) {
        return settingForUserList.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_IconMenu; //K.GAME Change Menu
        private TextView nameMenuLabel = null;
        private FrameLayout frameLayoutMenuServiceList = null;


        public ViewHolder(View itemView) {
            super(itemView);
            img_IconMenu = itemView.findViewById( R.id.img_MenuIcon);//K.GAME Change Menu
            nameMenuLabel = itemView.findViewById( R.id.nameMenuLabel);
            frameLayoutMenuServiceList = itemView.findViewById( R.id.frameLayoutMenuServiceList);
            frameLayoutMenuServiceList.setOnClickListener(onClickListener);
        }
    }
}
