package com.example.animor.Utils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animor.Model.Animal;
import com.example.animor.R;

import java.util.ArrayList;
import java.util.List;

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder> {

    private List<Animal> animals;
    private Context context;
    private OnAnimalClickListener listener;

    // Interfaz para manejar clics
    public interface OnAnimalClickListener {
        void onAnimalClick(Animal animal);
    }

    // En el constructor
    public AnimalAdapter(List<Animal> animals, OnAnimalClickListener listener) {
        this.animals = animals;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_animal, parent, false);
        return new AnimalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalViewHolder holder, int position) {
        Animal animal = animals.get(position);

        // Configuración básica
        holder.txtName.setText(animal.getAnimalName());
        holder.txtSex.setText(getSexText(animal.getSex()));

        // Manejo de clics más seguro
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && position != RecyclerView.NO_POSITION) {
                listener.onAnimalClick(animal);
            }
        });

        // Configuración adicional (ejemplo)
        if (animal.getIsAdopted() != null && animal.getIsAdopted()) {
            holder.itemView.setAlpha(0.6f);
        }
    }

    // Método helper para el texto del sexo
    private String getSexText(Animal.Sex sex) {
        if (sex == null) return "Desconocido";
        switch (sex) {
            case MALE: return "Macho";
            case FEMALE: return "Hembra";
            default: return "Desconocido";
        }
    }

    @Override
    public int getItemCount() {
        return animals != null ? animals.size() : 0;
    }

    // Método para actualizar la lista de animales
    public void updateAnimals(List<Animal> newAnimals) {
        this.animals = newAnimals;
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class AnimalViewHolder extends RecyclerView.ViewHolder {
        // Considera usar ViewBinding para mayor seguridad y rendimiento
        ImageView imgAnimal;
        TextView txtName, txtCity, txtSex, txtSpecies;

        public AnimalViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAnimal = itemView.findViewById(R.id.imgAnimal);
            txtName = itemView.findViewById(R.id.txtName);
            txtCity = itemView.findViewById(R.id.txtCity);
            txtSex = itemView.findViewById(R.id.txtSex);
            txtSpecies = itemView.findViewById(R.id.txtSpecies);
        }
    }
}