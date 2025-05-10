package Utils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animor.R;

import java.time.format.DateTimeFormatter;
import java.util.List;

import Model.Animal;

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder> {

    private List<Animal> animals;
    private Context context;
    private OnAnimalClickListener listener;

    // Interfaz para manejar clics
    public interface OnAnimalClickListener {
        void onAnimalClick(Animal animal);
    }

    public AnimalAdapter(List<Animal> animals, Context context, OnAnimalClickListener listener) {
        this.animals = animals;
        this.context = context;
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

        // Configurar los datos del animal en las vistas
        holder.txtName.setText(animal.getAnimalName());

        // Mostrar el sexo del animal (traducido si es necesario)
        String sexText = "";
        switch (animal.getSex()) {
            case MALE:
                sexText = "Macho";
                break;
            case FEMALE:
                sexText = "Hembra";
                break;
            case UNKNOWN:
                sexText = "Desconocido";
                break;
        }
        holder.txtSex.setText(sexText);

        // Aquí podrías cargar la imagen del animal si tuvieras una URL o recurso
        // Glide.with(context).load(animal.getImageUrl()).into(holder.imgAnimal);

        // Configurar el clic en el elemento
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAnimalClick(animal);
            }
        });
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
        ImageView imgAnimal;
        TextView txtName;
        TextView txtCity;
        TextView txtSex;
        TextView txtSpecies;

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