package com.qrtool.qrproject.adapters;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qrtool.qrproject.R;
import com.qrtool.qrproject.util.Status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StatusRecyclerAdapter extends RecyclerView.Adapter<StatusRecyclerAdapter.StatusViewHolder>{

    private LayoutInflater inflater;
     ArrayList<Status> statuses;
     Context context;
     String address;
    public StatusRecyclerAdapter(Context context, ArrayList<Status> statuses)
    {
        this.inflater=LayoutInflater.from(context);
        this.statuses=statuses;
        this.context=context;
    }

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_status, parent, false);
        return new StatusViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, int position) {

        double latitude=statuses.get(position).getLatitude();
        double longitude=statuses.get(position).getLongitude();

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if (addresses != null && !addresses.isEmpty()) {
                address = addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.address.setText("Address: "+address);
        holder.date.setText(statuses.get(position).getDate());
        holder.time.setText(statuses.get(position).getTime());
        holder.name.setText(statuses.get(position).getName());
        holder.mobile.setText(statuses.get(position).getContact());
        holder.designation.setText(statuses.get(position).getDesignation());
      //  holder.imageView.setImageBitmap(statuses.get(position).getImageBitmap());
    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }

    public class StatusViewHolder extends RecyclerView.ViewHolder {

        TextView designation,time,date,address,name,mobile;
        ImageView imageView;

        public StatusViewHolder(View view) {
            super(view);
            designation = (TextView) view.findViewById(R.id.source);
            time = (TextView) view.findViewById(R.id.time);
            date = (TextView) view.findViewById(R.id.date);
            address = (TextView) view.findViewById(R.id.address);
            name= (TextView) view.findViewById(R.id.name);
            mobile= (TextView) view.findViewById(R.id.mobile);
            imageView=(ImageView)view.findViewById(R.id.packageimage);
        }
    }


}
