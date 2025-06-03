package com.example.animor.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animor.Model.Animal;
import com.example.animor.Model.AnimalPhoto;
import com.example.animor.Model.Species;
import com.example.animor.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder> {

    private List<Animal> animalList;
    private OnAnimalClickListener listener;

    public AnimalAdapter(List<Animal> animalList, OnAnimalClickListener listener) {
        this.animalList = animalList;
        this.listener = listener;
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
        List<Species> species = PreferenceUtils.getSpeciesList();
        String speciesName="";
        for (Species s : species){
            if(s.getSpeciesId()== animal.getAnimalId()){
                speciesName=s.getSpeciesName();
            }
        }
        holder.txtSpecies.setText(speciesName);
        ArrayList<AnimalPhoto> photoList = animal.getAnimalPhotoList();
        String photoUrl = "";
        for (AnimalPhoto a : photoList){
            if(a.getIsCoverPhoto()){
                photoUrl=a.getPhotoUrl();
            }
        }
        Picasso.get()
                .load(photoUrl)
                .placeholder(R.drawable.gatoinicio)
                .error(R.drawable.gatoinicio)
                .into(holder.imgAnimal);
        holder.txtSex.setText(animal.getSex().toString());  // getSex() es enum, lo convierto a string
       // holder.imgAnimal.setImageResource(animal.getImage());
        // Clicks en el animal completo
        holder.itemView.setOnClickListener(v -> listener.onAnimalClick(animal));

        // Clicks en el icono de favorito
        holder.btnFavorite.setOnClickListener(v -> listener.onFavoriteClick(animal));
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
