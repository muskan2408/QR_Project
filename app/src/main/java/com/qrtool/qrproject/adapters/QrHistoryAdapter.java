package com.qrtool.qrproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.qrtool.qrproject.QrHistory;
import com.qrtool.qrproject.R;
import com.qrtool.qrproject.Report;
import com.qrtool.qrproject.util.History;
import com.qrtool.qrproject.util.Status;

import java.util.ArrayList;

public class QrHistoryAdapter extends RecyclerView.Adapter<QrHistoryAdapter.HistoryViewHolder> {

    LayoutInflater inflater;
    ArrayList<History> history;
    Context context;

    public QrHistoryAdapter(Context context, ArrayList<History> history)
    {
        this.inflater=LayoutInflater.from(context);
        this.history=history;
        this.context=context;
    }



    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_qr_history, parent, false);
        return new HistoryViewHolder(v);
       // return  null;

    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, final int position) {

        holder.rname.setText("Reciever's Name "+history.get(position).getRname());
        holder.address.setText("Reciever's Address "+history.get(position).getRaddress());

        holder.date.setText(history.get(position).getDate());

        holder.time.setText(history.get(position).getTime());

        holder.status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                i = new Intent(context,Report.class);
                i.putExtra("id",history.get(position).getId());
                context.startActivity(i);

            }
        });



    }

    @Override
    public int getItemCount() {
        return history.size();
    }

    public  class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView rname,date,time,address;
        TextView status;
        CardView cardView;
        public HistoryViewHolder(View itemView) {
            super(itemView);

            rname=(TextView)itemView.findViewById(R.id.rname);
            date=(TextView)itemView.findViewById(R.id.date);
            time=(TextView)itemView.findViewById(R.id.time);
            address=(TextView)itemView.findViewById(R.id.address);
            status=(TextView)itemView.findViewById(R.id.status);


        }
    }
}
