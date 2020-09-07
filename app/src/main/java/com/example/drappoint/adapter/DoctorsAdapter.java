package com.example.drappoint.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.drappoint.R;
import com.example.drappoint.StaticClass;
import com.example.drappoint.activities.core.DoctorActivity;
import com.example.drappoint.models.Doctor;
import java.util.ArrayList;
import java.util.List;

public class DoctorsAdapter extends RecyclerView.Adapter<DoctorsAdapter.ViewHolder> {

    private List<Doctor> list, copyList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public DoctorsAdapter(Context context, List<Doctor> list) {
        this.mInflater = LayoutInflater.from(context);
        this.list = list;
        copyList = new ArrayList<>();
        copyList.addAll(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.doctor_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.nameTV.setText(list.get(position).getName());
        holder.cityTV.setText(list.get(position).getCity());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameTV, cityTV;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.nameTV);
            cityTV = itemView.findViewById(R.id.cityTV);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());

            itemView.getContext().startActivity(new Intent(itemView.getContext(),
                    DoctorActivity.class)
            .putExtra(StaticClass.DOCTOR_ID, list.get(getAdapterPosition()).getId()));
        }
    }

    Doctor getItem(int id) {
        return list.get(id);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;

    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    public void filter(String queryText){
        list.clear();
        if(queryText.isEmpty()) {
            list.addAll(copyList);
        }else{
            for(Doctor doctor: copyList) {
                if(doctor.getName().toLowerCase().contains(queryText.toLowerCase())
                || doctor.getCity().toLowerCase().contains(queryText.toLowerCase())
                || doctor.getSpecialty().toLowerCase().contains(queryText.toLowerCase())) {
                    list.add(doctor);
                }
            }
        }
        notifyDataSetChanged();
    }
}
