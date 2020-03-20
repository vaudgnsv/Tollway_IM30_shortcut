package org.centerm.Tollway.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.centerm.Tollway.R;
import org.centerm.Tollway.database.TransTemp;

import java.text.DecimalFormat;
import java.util.List;

public class SlipSummaryReportCardAdapter extends RecyclerView.Adapter<SlipSummaryReportCardAdapter.ViewHolder> {

    private Context context;
    private List<TransTemp> voidList = null;
//    private ArrayList<String> nameMenuList = null;
    private String CardTypeTemp=null;
    private int AllTotalCount = 0;
    private int TotalCount = 0;
    private Double TotalAmount = 0.00;
    private int TotalVoidCount = 0;
    private Double TotalVoidAmount = 0.00;


    public SlipSummaryReportCardAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate( R.layout.item_report_card_summary, parent, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        AllTotalCount++;
        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");
        System.out.printf("utility:: SlipSummaryReportCardAdapter onBindViewHolder 00000000000 TotalCount = %d , voidList.size() = %d \n",TotalCount,voidList.size());
        if(!voidList.get( position ).getCardTypeHolder().equalsIgnoreCase(CardTypeTemp) && (CardTypeTemp != null)) {
            System.out.printf("utility:: SlipSummaryReportCardAdapter onBindViewHolder 00000000005 CardTypeTemp = %s,TotalAmount = %s,Count = %d \n",CardTypeTemp,decimalFormat.format(TotalAmount),TotalCount);

            CardTypeTemp = voidList.get( position).getCardTypeHolder();
            if (!voidList.get( position ).getVoidFlag().equals( "Y" )) {
                TotalAmount = Double.valueOf(voidList.get(position).getAmount());
                TotalCount++;
            }
            else
            {
                TotalVoidAmount = Double.valueOf(voidList.get(position).getAmount());
                TotalVoidCount++;
            }
            holder.CardTypeNameLabel.setText(CardTypeTemp);
            holder.salecntLabel.setText( TotalCount + "");
            holder.saletotalLabel.setText( decimalFormat.format(TotalAmount) );
            holder.voidcntLabel.setText( TotalVoidCount + "");
            holder.voidtotalLabel.setText( decimalFormat.format(TotalVoidAmount) );

            holder.cardcntLabel.setText( (TotalCount + TotalVoidCount) + "");
            holder.cardtotalLabel.setText( decimalFormat.format(TotalAmount) );


            System.out.printf("utility:: SlipSummaryReportCardAdapter onBindViewHolder 00000000006 CardTypeTemp = %s,TotalAmount = %s,Count = %d \n",CardTypeTemp,decimalFormat.format(TotalAmount),TotalCount);
            if(voidList.size() != AllTotalCount) {
// Paul_20181213
                System.out.printf("utility:: SlipReportCardAdapter onBindViewHolder 00000000015 CardTypeTemp = %s,TotalAmount = %s,Count = %d \n",CardTypeTemp,decimalFormat.format(TotalAmount),TotalCount);
                if(!voidList.get( position+1 ).getCardTypeHolder().equalsIgnoreCase(CardTypeTemp))
                {
                    System.out.printf("utility:: SlipReportCardAdapter onBindViewHolder 00000000016 CardTypeTemp = %s,TotalAmount = %s,Count = %d \n",CardTypeTemp,decimalFormat.format(TotalAmount),TotalCount);
                    holder.CardTypeNameLabel.setVisibility( View.VISIBLE );
                    holder.salecntLabel.setVisibility( View.VISIBLE );
                    holder.saletotalLabel.setVisibility( View.VISIBLE );
                    holder.voidcntLabel.setVisibility( View.VISIBLE );
                    holder.voidtotalLabel.setVisibility( View.VISIBLE );
                    holder.cardcntLabel.setVisibility( View.VISIBLE );
                    holder.cardtotalLabel.setVisibility( View.VISIBLE );
                    TotalCount = 0;
                    TotalAmount = 0.00;
                    TotalVoidCount = 0;
                    TotalVoidAmount = 0.00;
                }
                else {
                    System.out.printf("utility:: SlipReportCardAdapter onBindViewHolder 00000000017 CardTypeTemp = %s,TotalAmount = %s,Count = %d \n",CardTypeTemp,decimalFormat.format(TotalAmount),TotalCount);

//                    holder.CardTypeNameTitleLabel.setVisibility( View.GONE );
//                    holder.acrdTypeSaleTitleLabel.setVisibility( View.GONE );
//                    holder.acrdTypeVoidTitleLabel.setVisibility( View.GONE );
//                    holder.acrdTypeLineTitleLabel.setVisibility( View.GONE );
//                    holder.acrdTypeCardTitleLabel.setVisibility( View.GONE );
//                    holder.countTitleLabel.setVisibility( View.GONE );
//                    holder.totalTitleLabel.setVisibility( View.GONE );
//
//                    holder.CardTypeNameLabel.setVisibility( View.GONE );
//                    holder.salecntLabel.setVisibility( View.GONE );
//                    holder.saletotalLabel.setVisibility( View.GONE );
//                    holder.voidcntLabel.setVisibility( View.GONE );
//                    holder.voidtotalLabel.setVisibility( View.GONE );
//                    holder.cardcntLabel.setVisibility( View.GONE );
//                    holder.cardtotalLabel.setVisibility( View.GONE );

                    holder.cardtypeReportAllLayoutLable.setVisibility( View.GONE );
//                    holder.relativeLayout.setVisibility( View.GONE );
                }


//                holder.cardtypeReportAllLayoutLable.setVisibility( View.GONE );
            }
            else
            {
                System.out.printf("utility:: SlipSummaryReportCardAdapter onBindViewHolder 00000000010 CardTypeTemp = %s,TotalAmount = %s,Count = %d \n",CardTypeTemp,decimalFormat.format(TotalAmount),TotalCount);
                holder.CardTypeNameLabel.setVisibility( View.VISIBLE );
                holder.salecntLabel.setVisibility( View.VISIBLE );
                holder.saletotalLabel.setVisibility( View.VISIBLE );
                holder.voidcntLabel.setVisibility( View.VISIBLE );
                holder.voidtotalLabel.setVisibility( View.VISIBLE );
                holder.cardcntLabel.setVisibility( View.VISIBLE );
                holder.cardtotalLabel.setVisibility( View.VISIBLE );
                TotalCount = 0;
                TotalAmount = 0.00;
                TotalVoidCount = 0;
                TotalVoidAmount = 0.00;
            }
        }
        else
        {
//            TotalCount++;     // Paul_20181211 Fail Count
            if (!voidList.get( position ).getVoidFlag().equals( "Y" )) {
                TotalAmount += Double.valueOf(voidList.get(position).getAmount());
                TotalCount++;       // Paul_20181211 Fail Count
            }
            else
            {
                TotalVoidAmount += Double.valueOf(voidList.get(position).getAmount());
                TotalVoidCount++;
            }
            CardTypeTemp = voidList.get( position).getCardTypeHolder();

            System.out.printf("utility:: SlipSummaryReportCardAdapter onBindViewHolder 00000000037 CardTypeTemp = %s,TotalAmount = %s,Count = %d \n",CardTypeTemp,decimalFormat.format(TotalAmount),TotalCount);
            holder.CardTypeNameLabel.setText(CardTypeTemp);
            holder.salecntLabel.setText( TotalCount + "");
            holder.saletotalLabel.setText( decimalFormat.format(TotalAmount) );
            holder.voidcntLabel.setText( TotalVoidCount + "");
            holder.voidtotalLabel.setText( decimalFormat.format(TotalVoidAmount) );
            holder.cardcntLabel.setText( (TotalCount + TotalVoidCount) + "");
            holder.cardtotalLabel.setText( decimalFormat.format(TotalAmount) );


            System.out.printf("utility:: SlipSummaryReportCardAdapter onBindViewHolder 00000000007 CardTypeTemp = %s,TotalAmount = %s,Count = %d \n",CardTypeTemp,decimalFormat.format(TotalAmount),TotalCount);
            if(voidList.size() != AllTotalCount) {
                System.out.printf("utility:: SlipReportCardAdapter onBindViewHolder 00000000012 CardTypeTemp = %s,TotalAmount = %s,Count = %d \n",CardTypeTemp,decimalFormat.format(TotalAmount),TotalCount);
                if(!voidList.get( position+1 ).getCardTypeHolder().equalsIgnoreCase(CardTypeTemp))
                {
                    System.out.printf("utility:: SlipReportCardAdapter onBindViewHolder 00000000013 CardTypeTemp = %s,TotalAmount = %s,Count = %d \n",CardTypeTemp,decimalFormat.format(TotalAmount),TotalCount);
                    holder.CardTypeNameLabel.setVisibility( View.VISIBLE );
                    holder.salecntLabel.setVisibility( View.VISIBLE );
                    holder.saletotalLabel.setVisibility( View.VISIBLE );
                    holder.voidcntLabel.setVisibility( View.VISIBLE );
                    holder.voidtotalLabel.setVisibility( View.VISIBLE );
                    holder.cardcntLabel.setVisibility( View.VISIBLE );
                    holder.cardtotalLabel.setVisibility( View.VISIBLE );
                    TotalCount = 0;
                    TotalAmount = 0.00;
                    TotalVoidCount = 0;
                    TotalVoidAmount = 0.00;
                }
                else {
                    System.out.printf("utility:: SlipReportCardAdapter onBindViewHolder 00000000014 CardTypeTemp = %s,TotalAmount = %s,Count = %d \n",CardTypeTemp,decimalFormat.format(TotalAmount),TotalCount);

//                    holder.CardTypeNameTitleLabel.setVisibility( View.GONE );
//                    holder.acrdTypeSaleTitleLabel.setVisibility( View.GONE );
//                    holder.acrdTypeVoidTitleLabel.setVisibility( View.GONE );
//                    holder.acrdTypeLineTitleLabel.setVisibility( View.GONE );
//                    holder.acrdTypeCardTitleLabel.setVisibility( View.GONE );
//                    holder.countTitleLabel.setVisibility( View.GONE );
//                    holder.totalTitleLabel.setVisibility( View.GONE );
//
//                    holder.CardTypeNameLabel.setVisibility( View.GONE );
//                    holder.salecntLabel.setVisibility( View.GONE );
//                    holder.saletotalLabel.setVisibility( View.GONE );
//                    holder.voidcntLabel.setVisibility( View.GONE );
//                    holder.voidtotalLabel.setVisibility( View.GONE );
//                    holder.cardcntLabel.setVisibility( View.GONE );
//                    holder.cardtotalLabel.setVisibility( View.GONE );

                    holder.cardtypeReportAllLayoutLable.setVisibility( View.GONE );
//                    holder.relativeLayout.setVisibility( View.GONE );
                }
            }
            else
            {
                System.out.printf("utility:: SlipSummaryReportCardAdapter onBindViewHolder 00000000011 CardTypeTemp = %s,TotalAmount = %s,Count = %d \n",CardTypeTemp,decimalFormat.format(TotalAmount),TotalCount);
                holder.CardTypeNameLabel.setVisibility( View.VISIBLE );
                holder.salecntLabel.setVisibility( View.VISIBLE );
                holder.saletotalLabel.setVisibility( View.VISIBLE );
                holder.voidcntLabel.setVisibility( View.VISIBLE );
                holder.voidtotalLabel.setVisibility( View.VISIBLE );
                holder.cardcntLabel.setVisibility( View.VISIBLE );
                holder.cardtotalLabel.setVisibility( View.VISIBLE );
                TotalCount = 0;
                TotalAmount = 0.00;
                TotalVoidCount = 0;
                TotalVoidAmount = 0.00;
            }
        }
        //////END GAME
    }

    @Override
    public int getItemCount() {
        return voidList != null ? voidList.size() : 0;
    }

//    public void setItem(ArrayList<String> item)
//    {
//        nameMenuList = item;
//    }
    public void setItem(List<TransTemp> item) {
        voidList = item;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout relativeLayout;

        private TextView CardTypeNameTitleLabel;
        private TextView acrdTypeSaleTitleLabel;
        private TextView acrdTypeVoidTitleLabel;
        private TextView acrdTypeLineTitleLabel;
        private TextView acrdTypeCardTitleLabel;
        private TextView countTitleLabel;
        private TextView totalTitleLabel;


        private TextView CardTypeNameLabel;
        private TextView salecntLabel;
        private TextView saletotalLabel;
        private TextView voidcntLabel;
        private TextView voidtotalLabel;

        private TextView cardcntLabel;
        private TextView cardtotalLabel;

        private LinearLayout cardtypeReportAllLayoutLable;

        public ViewHolder(View itemView) {
            super(itemView);
            CardTypeNameTitleLabel = itemView.findViewById( R.id.cardTypeReportTitleLabel);
            acrdTypeSaleTitleLabel = itemView.findViewById( R.id.cardTypeSaleTitleLabel);
            acrdTypeVoidTitleLabel = itemView.findViewById( R.id.voidTotalLabel);
            acrdTypeLineTitleLabel = itemView.findViewById( R.id.lineTitleLabel);
            acrdTypeCardTitleLabel = itemView.findViewById( R.id.cardTitleLabel);
            countTitleLabel = itemView.findViewById( R.id.countTieleLabel);
            totalTitleLabel = itemView.findViewById( R.id.totalTieleLabel);

            relativeLayout = itemView.findViewById( R.id.cardsummaryid );
            CardTypeNameLabel = itemView.findViewById( R.id.cardTypeNameLabel);
            salecntLabel = itemView.findViewById( R.id.saleCountLabel);
            saletotalLabel = itemView.findViewById( R.id.saleTotalLabel);
            voidcntLabel = itemView.findViewById( R.id.voidSaleCountLabel);
            voidtotalLabel = itemView.findViewById( R.id.voidSaleAmountLabel);
            cardcntLabel = itemView.findViewById( R.id.cardCountLabel);
            cardtotalLabel = itemView.findViewById( R.id.cardAmountLabel);

            cardtypeReportAllLayoutLable = itemView.findViewById( R.id.cardTypeReportAllLayout);
        }
    }
}
