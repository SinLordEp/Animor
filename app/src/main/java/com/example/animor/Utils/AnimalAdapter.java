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
import com.example.animor.Model.entity.Photo;
import com.example.animor.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder> {
    List<Animal> animalList;
    private List<SpeciesDTO> speciesDTOList;
    OnAnimalClickListener listener;
    private final String TAG = "AnimalAdapter";
    private Context context;

    public AnimalAdapter(List<Animal> animalList, OnAnimalClickListener listener, Context context) {
        this.animalList = animalList;
        this.speciesDTOList = speciesDTOList;
        this.listener = listener;
        this.context = context;
    }


    @NonNull
    @Override
    public AnimalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_animal, parent, false);
        return new AnimalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AnimalViewHolder holder, int position) {
        Animal animal = animalList.get(position);
        holder.txtName.setText(animal.getAnimalName());

        String speciesName = "";
        List<SpeciesDTO> speciesDTOList=PreferenceUtils.getSpeciesList();
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
        Log.d(TAG, "BIRTHDATE: "+animal.getBirthDate());

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

            // NO necesitas runOnUiThread aquí - onBindViewHolder ya se ejecuta en UI thread
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
        holder.itemView.setOnClickListener(v -> listener.onAnimalClick(animal));
    }

    @Override
    public int getItemCount() {
        return animalList.size();
    }

    public static class AnimalViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtCity, txtSpecies, txtSex;
        ImageView imgAnimal, btnFavorite;

        public AnimalViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtSpecies = itemView.findViewById(R.id.txtSpecies);
            txtSex = itemView.findViewById(R.id.txtSex);
            imgAnimal = itemView.findViewById(R.id.imgUser);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
    public interface OnAnimalClickListener {
        void onAnimalClick(Animal animal);
        void onFavoriteClick(Animal animal);
    }

}
