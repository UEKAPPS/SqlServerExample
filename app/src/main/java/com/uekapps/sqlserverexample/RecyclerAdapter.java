package com.uekapps.sqlserverexample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements Filterable {

    private List<ClassListItems> values;
    private List<ClassListItems> newValues;
    public Context context;
    Activity activity;

    public RecyclerAdapter(List<ClassListItems> myDataSet, Context context, Activity activity) {
        this.values = myDataSet;
        this.context = context;
        this.activity = activity;
        newValues = new ArrayList<>(values);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final ClassListItems classListItems = values.get(position);

        holder.idTxt.setText(classListItems.getId());
        holder.nameTxt.setText(classListItems.getName());
        holder.phoneTxt.setText(classListItems.getPhone());
        holder.emailTxt.setText(classListItems.getEmail());
        holder.addressTxt.setText(classListItems.getAddress());

        boolean isVisible = classListItems.visibility;
        holder.constraintLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("ID", values.get(position).getId());
                intent.putExtra("Name", values.get(position).getName());
                intent.putExtra("Phone", values.get(position).getPhone());
                intent.putExtra("Email", values.get(position).getEmail());
                intent.putExtra("Address", values.get(position).getAddress());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView idTxt;
        public TextView nameTxt;
        public TextView phoneTxt;
        public TextView emailTxt;
        public TextView addressTxt;
        public View layout;
        public ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View v) {
            super(v);

            layout = v;
            idTxt = v.findViewById(R.id.idTxt);
            nameTxt = v.findViewById(R.id.nameTxt);
            phoneTxt = v.findViewById(R.id.phoneTxt);
            emailTxt = v.findViewById(R.id.emailTxt);
            addressTxt = v.findViewById(R.id.addressTxt);
            constraintLayout = v.findViewById(R.id.expandedLayout);

            nameTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClassListItems cl = values.get(getAdapterPosition());
                    cl.setVisibility(!cl.isVisibility());
                    notifyItemChanged(getAdapterPosition());
                }
            });

        }
    }

    @Override
    public Filter getFilter() {
        return myFilter;
    }

    private Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<ClassListItems> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(newValues);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (ClassListItems item : newValues){
                    if (item.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            values.clear();
            values.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };
}
