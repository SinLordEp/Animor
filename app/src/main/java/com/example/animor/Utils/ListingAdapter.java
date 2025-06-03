package com.example.animor.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animor.App.MyApplication;
import com.example.animor.Model.Animal;
import com.example.animor.Model.AnimalPhoto;
import com.example.animor.Model.AnimalListing;
import com.example.animor.Model.Species;
import com.example.animor.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingViewHolder> {

    private List<AnimalListing> animalListingList;
    private OnListingClickListener listener;
    private SimpleDateFormat dateFormat;

    public ListingAdapter(List<AnimalListing> animalListingList, OnListingClickListener listener) {
        this.animalListingList = animalListingList != null ? animalListingList : new ArrayList<>();
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_animal, parent, false);
        return new ListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {
        AnimalListing animalListing = animalListingList.get(position);
        Animal animal = animalListing.getAnimal();

            holder.txtName.setText(animalListing.getAnimal().getAnimalName());

        // Mostrar ciudad
        holder.txtCity.setVisibility(View.VISIBLE);
        holder.txtCity.setText(animalListing.getLocationRequest().getCity() != null ? animalListing.getLocationRequest().getCity() : "Sin especificar");

        // Configurar datos del animal si existe
        if (animal != null) {
            // Especie
            String speciesName = getSpeciesName(animal.getSpeciesId());
            holder.txtSpecies.setText(speciesName);

            // Sexo
            holder.txtSex.setText(animal.getSex() != null ? animal.getSex().toString() : "Sin especificar");

            // Imagen del animal
            loadAnimalImage(animal, holder.imgAnimal);
        } else {
            holder.txtSpecies.setText("Sin animal asociado");
            holder.txtSex.setText("N/A");
            holder.imgAnimal.setImageResource(R.drawable.gatoinicio);
        }

        // Configurar icono de favorito según el tipo de listing
        setupFavoriteIcon(animalListing, holder.btnFavorite);

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onListingClick(animalListing);
            }
        });

        holder.btnFavorite.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFavoriteClick(animalListing);
            }
        });
    }

    @Override
    public int getItemCount() {
        return animalListingList.size();
    }

    // Métodos auxiliares
    private String getSpeciesName(int speciesId) {
        ArrayList<Species> species = MyApplication.getSpecies();
        if (species != null) {
            for (Species s : species) {
                if (s.getSpeciesId() == speciesId) {
                    return s.getSpeciesName();
                }
            }
        }
        return "Especie desconocida";
    }

    private void loadAnimalImage(Animal animal, ImageView imageView) {
        String photoUrl = "";

        if (animal.getAnimalPhotoList() != null) {
            for (AnimalPhoto photo : animal.getAnimalPhotoList()) {
                if (photo.getIsCoverPhoto()) {
                    photoUrl = photo.getPhotoUrl();
                    break;
                }
            }
        }

        if (!photoUrl.isEmpty()) {
            Picasso.get()
                    .load(photoUrl)
                    .placeholder(R.drawable.gatoinicio)
                    .error(R.drawable.gatoinicio)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.gatoinicio);
        }
    }

    private void setupFavoriteIcon(AnimalListing animalListing, ImageView btnFavorite) {

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

    // Interface para callbacks
    public interface OnListingClickListener {
        void onListingClick(AnimalListing animalListing);
        void onFavoriteClick(AnimalListing animalListing);
    }
}