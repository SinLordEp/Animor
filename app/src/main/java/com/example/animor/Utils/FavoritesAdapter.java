package com.example.animor.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animor.Model.FavoriteItem;
import com.example.animor.R;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavViewHolder> {

    private List<FavoriteItem> favoritesList;

    public FavoritesAdapter(List<FavoriteItem> favoritesList) {
        this.favoritesList = favoritesList;
    }

    @NonNull
    @Override
    public FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fav, parent, false);
        return new FavViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavViewHolder holder, int position) {
        FavoriteItem item = favoritesList.get(position);

        holder.txtName.setText(item.getName());
        holder.txtCity.setText(item.getCity());
        holder.txtSex.setText(item.getSex());
        holder.txtSpecies.setText(item.getSpecies());
        holder.imgAnimal.setImageResource(item.getImageResId());

        // Cambiar ícono según estado de favorito
        if (item.isFavorite()) {
            holder.btnFavorite.setImageResource(R.drawable.heart);
        } else {
            holder.btnFavorite.setImageResource(R.drawable.heartvacio);
        }

        // Click en el ícono de favorito
        holder.btnFavorite.setOnClickListener(v -> {
            boolean nuevoEstado = !item.isFavorite();
            item.setFavorite(nuevoEstado);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    static class FavViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAnimal, btnFavorite;
        TextView txtName, txtCity, txtSex, txtSpecies;

        public FavViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAnimal = itemView.findViewById(R.id.imgAnimal);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            txtName = itemView.findViewById(R.id.txtName);
            txtCity = itemView.findViewById(R.id.txtCity);
            txtSex = itemView.findViewById(R.id.txtSex);
            txtSpecies = itemView.findViewById(R.id.txtSpecies);
        }
    }
}
