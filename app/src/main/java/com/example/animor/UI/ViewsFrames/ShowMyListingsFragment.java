package com.example.animor.UI.ViewsFrames;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animor.R;
import com.example.animor.UI.CreateListingActivity;
import com.example.animor.Utils.ApiRequests;
import com.example.animor.Model.entity.AnimalListing;
import com.example.animor.Utils.ListingAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShowMyListingsFragment extends Fragment implements ListingAdapter.OnListingInteractionListener {

    private RecyclerView rvListings;
    private ListingAdapter adapter;
    private List<AnimalListing> listingList;
    private ListingAdapter.OnListingInteractionListener parentListener;


    // Interface para comunicación con la Activity
    public interface OnListingSelectedListener {
        void onListingSelected(AnimalListing listing);
    }

    private OnListingSelectedListener listingSelectedListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_my_listings, container, false);
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupRecyclerView();
        loadListings();

        // Configurar listener si la activity lo implementa
        if (getActivity() instanceof ListingAdapter.OnListingInteractionListener) {
            parentListener = (ListingAdapter.OnListingInteractionListener) getActivity();
        }
    }

    private void initializeViews(View view) {
        rvListings = view.findViewById(R.id.recyclerViewMyListings);
    }

    private void setupRecyclerView() {
        listingList = new ArrayList<>();
        adapter = new ListingAdapter(listingList, this);
        rvListings.setLayoutManager(new LinearLayoutManager(getContext()));
        rvListings.setAdapter(adapter);
        rvListings.addItemDecoration(new DividerItemDecoration(
                rvListings.getContext(), LinearLayoutManager.VERTICAL));
    }

    private void loadListings() {
        ApiRequests api = new ApiRequests();
        new Thread(() -> {
           // List<AnimalListing> newListingList = api.askForMyListingsToDatabase();

            if (getActivity() != null) {
                requireActivity().runOnUiThread(() -> {
                    listingList.clear();
                  //  if (newListingList != null) {
                 //       listingList.addAll(newListingList);
                 //   }
                    adapter.notifyDataSetChanged();

                    if (listingList.isEmpty()) {
                        rvListings.setVisibility(View.GONE);
                        LinearLayout linearLayout = requireView().findViewById(R.id.linnolisting);
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();
    }
    @Override
    public void onListingSelected(AnimalListing listing) {
        // Navegación principal: ir a CreateListingActivity
        Intent intent = new Intent(getActivity(), CreateListingActivity.class);
        intent.putExtra("animal", listing.getAnimal());
        intent.putExtra("location", listing.getLocationRequest());
        intent.putExtra("animalListing", listing);
        startActivity(intent);

        // Notificar a la activity padre si implementa el listener
        if (parentListener != null) {
            parentListener.onListingSelected(listing);
        }
    }
    @Override
    public void onFavoriteClick(AnimalListing animalListing) {
        Toast.makeText(getContext(), "Favorito: " + animalListing.getAnimal().getAnimalName(), Toast.LENGTH_SHORT).show();

        // Notificar a la activity padre si implementa el listener
        if (parentListener != null) {
            parentListener.onFavoriteClick(animalListing);
        }
    }

    // Métodos públicos existentes
    public void updateListingList(ArrayList<AnimalListing> newListingList) {
        if (listingList != null && adapter != null) {
            listingList.clear();
            if (newListingList != null) {
                listingList.addAll(newListingList);
            }
            adapter.notifyDataSetChanged();
        }
    }

    public void refreshListingList() {
        loadListings();
    }
}