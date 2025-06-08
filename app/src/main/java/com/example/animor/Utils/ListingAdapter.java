package com.example.animor.Utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingViewHolder> {

    private List<AnimalListing> animalListingList;
    private OnListingInteractionListener listener;
    private final String TAG = "ListingAdapter";
    private Context context;

    // Enum para definir el tipo de vista
    public enum ViewType {
        INICIO_ACTIVITY,        // Con favoritos y distancia
        SHOW_MY_LISTINGS       // Sin favoritos ni distancia
    }

    private ViewType viewType;

    // Constructor principal con tipo de vista
    public ListingAdapter(List<AnimalListing> animalListingList, OnListingInteractionListener listener, ViewType viewType) {
        this.animalListingList = animalListingList != null ? animalListingList : new ArrayList<>();
        this.listener = listener;
        this.viewType = viewType;
    }

    // Constructor legacy para mantener compatibilidad (por defecto usa INICIO_ACTIVITY)
    public ListingAdapter(List<AnimalListing> animalListingList, OnListingInteractionListener listener) {
        this(animalListingList, listener, ViewType.INICIO_ACTIVITY);
    }

    @NonNull
    @Override
    public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_animal, parent, false);
        return new ListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {
        AnimalListing animalListing = animalListingList.get(position);
        Animal animal = animalListing.getAnimal();

        // Configurar información básica
        holder.txtName.setText(animal.getAnimalName());

        // Configurar especie
        String speciesName = "";
        List<SpeciesDTO> speciesDTOList = PreferenceUtils.getSpeciesList();
        for (SpeciesDTO s : speciesDTOList) {
            if (s.getSpeciesId() == animal.getSpeciesId()) {
                speciesName = s.getSpeciesName();
                break;
            }
        }
        holder.txtSpecies.setText(speciesName);

        // Configurar foto del animal
        loadAnimalPhoto(animal, holder);

        // Configurar sexo
        holder.txtSex.setText(animal.getSex().toString());

        // Establecer la ciudad del listing
        if (holder.txtCity != null && animalListing.getLocation() != null) {
            holder.txtCity.setText(animalListing.getLocation().getCity());
        }

        // Configurar elementos específicos según el tipo de vista
        configureViewByType(animalListing, holder);

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onListingSelected(animalListing);
            }
        });
    }

    private void loadAnimalPhoto(Animal animal, ListingViewHolder holder) {
        String photoUrl = null;
        List<Photo> photoList = animal.getAnimalPhotoList();

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
    }

    private void configureViewByType(AnimalListing animalListing, ListingViewHolder holder) {
        switch (this.viewType) {
            case INICIO_ACTIVITY:
                setupForInicioActivity(animalListing, holder);
                break;
            case SHOW_MY_LISTINGS:
                setupForShowMyListings(holder);
                break;
        }
    }

    private void setupForInicioActivity(AnimalListing animalListing, ListingViewHolder holder) {
        // Mostrar y configurar tvNearMe (distancia)
        if (holder.tvNearMe != null) {
            // Usar el campo distance (int) de AnimalListing
            int distance = animalListing.getDistance();
            if (distance > 0) {
                String distanceText;

                if (distance < 1000) {
                    // Mostrar en metros si es menos de 1000m
                    distanceText = "a " + distance + "m de ti";
                } else {
                    // Convertir a kilómetros y mostrar
                    double distanceKm = distance / 1000.0;
                    distanceText = String.format(Locale.getDefault(), "a %.1f km de ti", distanceKm);
                }

                holder.tvNearMe.setText(distanceText);
                holder.tvNearMe.setVisibility(View.VISIBLE);

                Log.d(TAG, "Distance display: " + distanceText + " (raw: " + distance + "m)");
            } else {
                // Si no hay información de distancia, mostrar texto genérico
                holder.tvNearMe.setText("Ubicación disponible");
                holder.tvNearMe.setVisibility(View.VISIBLE);
            }
        }

        // Mostrar y configurar botón de favoritos
        if (holder.btnFavorite != null) {
            holder.btnFavorite.setVisibility(View.VISIBLE);

            // El estado inicial siempre será corazón vacío ya que no almacenamos el estado localmente
            holder.btnFavorite.setImageResource(R.drawable.heartvacio);

            // Configurar el click listener
            holder.btnFavorite.setOnClickListener(v -> {
                if (listener != null) {
                    // Cambiar temporalmente a corazón lleno para feedback visual
                    holder.btnFavorite.setImageResource(R.drawable.heart);

                    // Llamar al listener que manejará la API
                    listener.onFavoriteClick(animalListing);

                    Log.d(TAG, "Favorite clicked for: " + animalListing.getAnimal().getAnimalName());
                }
            });
        }

        Log.d(TAG, "Configurado para InicioActivity: " + animalListing.getAnimal().getAnimalName());
    }

    private void setupForShowMyListings(ListingViewHolder holder) {
        // Ocultar tvNearMe (distancia)
        if (holder.tvNearMe != null) {
            holder.tvNearMe.setVisibility(View.GONE);
        }

        // Ocultar botón de favoritos
        if (holder.btnFavorite != null) {
            holder.btnFavorite.setVisibility(View.GONE);
        }

        Log.d(TAG, "Configurado para ShowMyListings - elementos ocultados");
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
        Log.d(TAG, "Data updated, new size: " + this.animalListingList.size());
    }

    public void addListing(AnimalListing animalListing) {
        if (animalListing != null) {
            this.animalListingList.add(animalListing);
            notifyItemInserted(animalListingList.size() - 1);
            Log.d(TAG, "Listing added: " + animalListing.getAnimal().getAnimalName());
        }
    }

    public void removeListing(int position) {
        if (position >= 0 && position < animalListingList.size()) {
            AnimalListing removed = animalListingList.remove(position);
            notifyItemRemoved(position);
            Log.d(TAG, "Listing removed: " + (removed != null ? removed.getAnimal().getAnimalName() : "null"));
        }
    }

    // Método para cambiar el tipo de vista dinámicamente (opcional)
    public void setViewType(ViewType viewType) {
        this.viewType = viewType;
        notifyDataSetChanged(); // Refrescar toda la vista
    }

    public ViewType getViewType() {
        return viewType;
    }

    // ViewHolder actualizado con tvNearMe
    public static class ListingViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtCity, txtSpecies, txtSex, tvNearMe;
        ImageView imgAnimal, btnFavorite;

        public ListingViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtSpecies = itemView.findViewById(R.id.txtSpecies);
            txtSex = itemView.findViewById(R.id.txtSex);
            txtCity = itemView.findViewById(R.id.txtCity);
            imgAnimal = itemView.findViewById(R.id.imgUser);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            tvNearMe = itemView.findViewById(R.id.tvNearMe);
        }
    }

    public interface OnListingInteractionListener {
        void onListingSelected(AnimalListing animalListing);
        void onFavoriteClick(AnimalListing animalListing);
    }
}