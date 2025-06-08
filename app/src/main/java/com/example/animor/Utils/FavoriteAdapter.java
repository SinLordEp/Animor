package com.example.animor.Utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animor.Model.dto.SpeciesDTO;
import com.example.animor.Model.entity.Animal;
import com.example.animor.Model.entity.AnimalListing;
import com.example.animor.Model.entity.Photo;
import com.example.animor.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FavoriteAdapter {/*extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private List<AnimalListing> animalListingList;
    private OnListingInteractionListener listener;
    private final String TAG = "ListingAdapter";
    private Context context;
    private SimpleDateFormat dateFormat;

    public FavoriteAdapter(List<AnimalListing> animalListingList, OnListingInteractionListener listener) {
        this.animalListingList = animalListingList != null ? animalListingList : new ArrayList<>();
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_animal, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.FavoriteViewHolder holder, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        AnimalListing animalListing = animalListingList.get(position);
        Animal animal = animalListing.getAnimal();

        holder.txtName.setText(animal.getAnimalName());

        String speciesName = "";
        List<SpeciesDTO> speciesDTOList = PreferenceUtils.getSpeciesList();
        for (SpeciesDTO s : speciesDTOList) {
            if (s.getSpeciesId() == animal.getSpeciesId()) {
                speciesName = s.getSpeciesName();
                break;
            }
        }
        holder.txtSpecies.setText(speciesName);

        // CORRECCIÓN: Declarar photoUrl como variable local y resetearla
        String photoUrl = null;
        List<Photo> photoList = animal.getAnimalPhotoList();

        // Agregar logs para debugging
        Log.d(TAG, "Animal: " + animal.getAnimalName() + " - PhotoList size: " +
                (photoList != null ? photoList.size() : "null"));

        if (photoList != null && !photoList.isEmpty()) {
            for (Photo photo : photoList) {
                Log.d(TAG, "Checking photo - isCoverPhoto: " + photo.getIsCoverPhoto() +
                        ", URL: " + photo.getPhotoUrl());

                if (photo.getIsCoverPhoto()) {
                    photoUrl = photo.getPhotoUrl();
                    Log.d(TAG, "Found cover photo URL: " + photoUrl);
                    break;
                }
            }

            // Si no hay foto de portada, tomar la primera disponible
            if (photoUrl == null && !photoList.isEmpty()) {
                photoUrl = photoList.get(0).getPhotoUrl();
                Log.d(TAG, "No cover photo found, using first photo: " + photoUrl);
            }
        }

        // Cargar imagen
        if (photoUrl != null && !photoUrl.trim().isEmpty()) {
            Log.d(TAG, "Loading photo with Picasso: " + photoUrl);

            Picasso.get()
                    .load(photoUrl)
                    .placeholder(R.drawable.gatoinicio)
                    .error(R.drawable.gatoinicio)
                    .into(holder.imgAnimal);
        } else {
            Log.d(TAG, "No valid photo URL available, using default image");
            holder.imgAnimal.setImageResource(R.drawable.gatoinicio);
        }

        holder.txtSex.setText(animal.getSex().toString());

        // Establecer la ciudad del listing
        if (holder.txtCity != null) {
            holder.txtCity.setText(animalListing.getLocation().getCity());
        }

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onListingSelected(animalListing);
            }
        });

        if (holder.btnFavorite != null) {
            holder.btnFavorite.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFavoriteClick(animalListing);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return animalListingList.size();
    }

    // Métodos públicos para actualizar datos
    public void updateData(List<AnimalListing> newAnimalListings) {
        this.animalListingList.clear();
        if (newAnimalListings != null) {
            this.animalListingList.addAll(newAnimalListings);
        }
        notifyDataSetChanged();
    }

    public void addListing(AnimalListing animalListing) {
        if (animalListing != null) {
            this.animalListingList.add(animalListing);
            notifyItemInserted(animalListingList.size() - 1);
        }
    }

    public void removeListing(int position) {
        if (position >= 0 && position < animalListingList.size()) {
            animalListingList.remove(position);
            notifyItemRemoved(position);
        }
    }

    // ViewHolder
    public static class ListingViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtCity, txtSpecies, txtSex;
        ImageView imgAnimal, btnFavorite;

        public ListingViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtSpecies = itemView.findViewById(R.id.txtSpecies);
            txtSex = itemView.findViewById(R.id.txtSex);
            txtCity = itemView.findViewById(R.id.txtCity);
            imgAnimal = itemView.findViewById(R.id.imgUser);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }

    public interface OnListingInteractionListener {
        void onListingSelected(AnimalListing animalListing);
        void onFavoriteClick(AnimalListing animalListing);
    }*/
}