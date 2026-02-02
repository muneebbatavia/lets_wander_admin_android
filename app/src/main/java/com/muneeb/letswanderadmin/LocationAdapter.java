package com.muneeb.letswanderadmin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> implements Filterable {

    private List<addLocation> locationList;
    ArrayList<addLocation> listAll;
    private Context context;

    public LocationAdapter(List<addLocation> locationList, Context context) {
        this.locationList = locationList;
        this.context = context;
        listAll = new ArrayList<>(locationList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (holder.getAbsoluteAdapterPosition() >= locationList.size() - 1) {
            holder.itemView.setVisibility(View.INVISIBLE);
        } else {
            addLocation location = locationList.get(holder.getAbsoluteAdapterPosition());

            holder.titleTextView.setText(location.getTitle());
            holder.locationTextView.setText("Location: " + location.getLatitude() + ", " + location.getLongitude());
            holder.descriptionTextView.setText("Description: " + location.getDescription());

            if (location.getStar() != null && location.getStar()) {
                holder.starIcon.setVisibility(View.VISIBLE);
            }

            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EditActivity.class);
                    intent.putExtra("id", location.getId());
                    context.startActivity(intent);
                }
            });
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure you want to delete this location?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String id = location.getId();
                                    if (id != null) {
                                        DatabaseReference locationRef = Constants.databaseReference().child("Markers").child(id);

                                        locationRef.removeValue().addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "Location deleted", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "Failed to delete location", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(context, "Marker ID is missing", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<addLocation> filterList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                filterList.addAll(listAll);
            } else {
                for (addLocation listModel : listAll) {
                    if (listModel.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filterList.add(listModel);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            locationList.clear();
            locationList.addAll((Collection<? extends addLocation>) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;
        private TextView locationTextView;
        private TextView descriptionTextView;
        private ImageView starIcon;
        private Button editButton;
        private ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.textViewTitle);
            locationTextView = itemView.findViewById(R.id.textViewLocation);
            descriptionTextView = itemView.findViewById(R.id.textViewDescription);
            starIcon = itemView.findViewById(R.id.starIcon);
            editButton = itemView.findViewById(R.id.editLocationButton);
            deleteButton = itemView.findViewById(R.id.deleteLocationButton);
        }
    }
}
