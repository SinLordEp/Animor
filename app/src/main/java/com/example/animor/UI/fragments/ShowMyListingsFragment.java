package com.example.animor.UI.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animor.R;
import com.example.animor.Utils.ApiRequests;
// import com.example.animor.Model.Listing; // Tu modelo de Listing
// import com.example.animor.Utils.ListingAdapter; // Tu adapter de Listing

import java.util.ArrayList;
import java.util.List;

public class ShowMyListingsFragment extends Fragment {

    private RecyclerView rvListings;
    // private ListingAdapter adapter;
    private ArrayList<Object> listingList; // Cambia Object por tu clase Listing

    // Interface para comunicación con la Activity
    public interface OnListingSelectedListener {
        void onListingSelected(Object listing); // Cambia Object por tu clase Listing
    }

    private OnListingSelectedListener listingSelectedListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_my_listings, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupRecyclerView();
        loadListings();

        // Configurar listener si la activity lo implementa
        if (getActivity() instanceof OnListingSelectedListener) {
            listingSelectedListener = (OnListingSelectedListener) getActivity();
        }
    }

    private void initializeViews(View view) {
        rvListings = view.findViewById(R.id.recyclerViewMyListings);
    }

    private void setupRecyclerView() {
        listingList = new ArrayList<>();
        // adapter = new ListingAdapter(listingList, this::onListingClick);
        rvListings.setLayoutManager(new LinearLayoutManager(getContext()));
        // rvListings.setAdapter(adapter);

        // Opcional: añadir separadores
        rvListings.addItemDecoration(new DividerItemDecoration(
                rvListings.getContext(), LinearLayoutManager.VERTICAL));
    }

    private void loadListings() {
        ApiRequests api = new ApiRequests();
        new Thread(() -> {
            // List<Listing> newListingList = api.askForMyListingsToDatabase();
            List<Object> newListingList = new ArrayList<>(); // Placeholder

            if (getActivity() != null) {
                requireActivity().runOnUiThread(() -> {
                    listingList.clear();
                    listingList.addAll(newListingList);
                    // adapter.notifyDataSetChanged();

                    if (listingList.isEmpty()) {
                        Toast.makeText(getContext(),
                                "No tienes registros", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    public void onListingClick(Object listing) { // Cambia Object por tu clase Listing
        if (listingSelectedListener != null) {
            listingSelectedListener.onListingSelected(listing);
        } else {
            Toast.makeText(getContext(),
                    "Registro seleccionado", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateListingList(ArrayList<Object> newListingList) { // Cambia Object por tu clase Listing
        if (listingList != null) { // && adapter != null
            listingList.clear();
            listingList.addAll(newListingList);
            // adapter.notifyDataSetChanged();
        }
    }

    // Método para refrescar la lista desde la Activity
    public void refreshListingList() {
        loadListings();
    }
}