package com.example.animor.UI.ViewsFrames;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animor.R;
import com.example.animor.UI.CreateActivity;
import com.example.animor.UI.LoginActivity;
import com.example.animor.UI.CreateListingActivity;
import com.example.animor.UI.ShowActivity;
import com.example.animor.Utils.ApiRequests;
import com.example.animor.Model.entity.AnimalListing;
import com.example.animor.Utils.ListingAdapter;

import java.util.ArrayList;
import java.util.List;

public class ShowMyListingsFragment extends Fragment implements ListingAdapter.OnListingInteractionListener {

    private RecyclerView rvListings;
    private ListingAdapter adapter;
    private ArrayList<AnimalListing> listingList;
    private List<AnimalListing> newListingList = new ArrayList<>();
    private final String TAG ="ShowMyListingsFragment";
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("DEBUG", "ShowMyListingsFragment onViewCreated() ejecutado");

        initializeViews(view);
        setupRecyclerView();
        loadListings();
    }

    private void initializeViews(View view) {
        rvListings = view.findViewById(R.id.recyclerViewMyListings);
    }

    private void setupRecyclerView() {
        listingList = new ArrayList<>();
        adapter = new ListingAdapter(listingList, this, ListingAdapter.ViewType.SHOW_MY_LISTINGS);
        rvListings.setLayoutManager(new LinearLayoutManager(getContext()));
        rvListings.setAdapter(adapter);

        // Opcional: añadir separadores
        rvListings.addItemDecoration(new DividerItemDecoration(
                rvListings.getContext(), LinearLayoutManager.VERTICAL));
    }

    private void loadListings() {
        ApiRequests api = new ApiRequests();
        new Thread(() -> {
            Log.d(TAG, "loadListings() llamado");
            newListingList = api.getMyListings();

            for(AnimalListing listing: newListingList) {
                Log.d(TAG, "Listing: " + listing.getAnimal().getAnimalName());
                Log.d(TAG, "Location: " + listing.getLocationRequest());
            }

            // Cambiar a requireActivity().runOnUiThread()
            requireActivity().runOnUiThread(() -> {
                handleListingsResult(newListingList);
            });
        }).start();
    }

    private void handleListingsResult(List<AnimalListing> newListingList) {
        // Verificar si el fragment aún está adjunto antes de proceder
        if (!isAdded() || getView() == null) {
            Log.w(TAG, "Fragment no está adjunto, saliendo de handleListingsResult");
            return;
        }

        // CASO 1: Lista null = Usuario no autenticado o error de conexión grave
        if (newListingList == null) {
            showNoLoginLayout();
            return;
        }

        // CASO 2: Lista vacía = Usuario autenticado pero sin listings
        if (newListingList.isEmpty()) {
            showEmptyListingsLayout();
            return;
        }

        // CASO 3: Lista con listings = Mostrar normalmente
        showListingsLayout(newListingList);
    }

    // Mostrar layout cuando no hay login
    private void showNoLoginLayout() {
        Log.d("DEBUG", "Mostrando layout de no login");

        // Ocultar RecyclerView
        rvListings.setVisibility(View.GONE);

        // Mostrar layout de no login
        LinearLayout layoutNoLogin = getView().findViewById(R.id.layoutNoLogin);
        if (layoutNoLogin != null) {
            layoutNoLogin.setVisibility(View.VISIBLE);

            // Configurar botón de iniciar sesión
            Button btnIniciarSesion = layoutNoLogin.findViewById(R.id.btnIniciarSesion);
            if (btnIniciarSesion != null) {
                btnIniciarSesion.setOnClickListener(v -> navigateToLogin());
            }
        }
    }

    //Mostrar layout cuando no hay listings
    private void showEmptyListingsLayout() {
        Log.d(TAG, "Usuario autenticado pero sin listings");

        // Mostrar RecyclerView vacío
        rvListings.setVisibility(View.GONE);

        // Ocultar layout de no login
        LinearLayout layoutNoLogin = getView().findViewById(R.id.layoutNoLogin);
        if (layoutNoLogin != null) {
            layoutNoLogin.setVisibility(View.GONE);
        }

        // Mostrar layout de sin listings (mantener compatibilidad con el original)
        LinearLayout layoutNoListings = getView().findViewById(R.id.linnolisting);
        if (layoutNoListings != null) {
            layoutNoListings.setVisibility(View.VISIBLE);
            Button btnCrear = getView().findViewById(R.id.btncrear);
            btnCrear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(requireContext(), CreateActivity.class);
                    intent.putExtra("currentTab", 1);
                    startActivity(intent);
                }
            });
        }

        // Limpiar lista y notificar adapter
        listingList.clear();
        adapter.notifyDataSetChanged();
    }

    // Mostrar listings
    private void showListingsLayout(List<AnimalListing> newListingList) {
        Log.d(TAG, "Mostrando " + newListingList.size() + " listings");

        // Mostrar RecyclerView
        rvListings.setVisibility(View.VISIBLE);

        // Ocultar layout de no login
        LinearLayout layoutNoLogin = getView().findViewById(R.id.layoutNoLogin);
        if (layoutNoLogin != null) {
            layoutNoLogin.setVisibility(View.GONE);
        }

        // Ocultar layout de sin listings
        LinearLayout layoutNoListings = getView().findViewById(R.id.linnolisting);
        if (layoutNoListings != null) {
            layoutNoListings.setVisibility(View.GONE);
        }

        // Actualizar lista de forma segura
        listingList.clear();
        listingList.addAll(newListingList);
        adapter.notifyDataSetChanged();
    }

    // Navegar a login
    private void navigateToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    // Navegar a crear listing
    private void navigateToCreateListing() {
        Intent intent = new Intent(getActivity(), CreateListingActivity.class);
        intent.putExtra("mode", "create");
        startActivity(intent);
    }

    // Fallback por si no hay listener
    private void navigateToDetailFallback(AnimalListing listing) {
        // Mantener la navegación original como fallback
        Intent intent = new Intent(getActivity(), CreateListingActivity.class);
        intent.putExtra("animal", listing.getAnimal());
        intent.putExtra("location", listing.getLocationRequest());
        intent.putExtra("animalListing", listing);
        loadListings();
        startActivity(intent);

    }

    // establecer el listener desde la Activity
    public void setListingSelectedListener(OnListingSelectedListener listener) {
        this.listingSelectedListener = listener;
    }

    @Override
    public void onListingSelected(AnimalListing listing) {
        // Usar prioritariamente el interface para comunicarse con la Activity
        if (listingSelectedListener != null) {
            listingSelectedListener.onListingSelected(listing);
            loadListings();

        } else {
            // Fallback: Si no hay listener, intentar navegar directamente
            Log.w("ShowMyListingsFragment", "No listener found, attempting direct navigation");
            navigateToDetailFallback(listing);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("DEBUG", "onResume() - Refrescando lista de animales");
        listingList.clear();
        loadListings();
    }

    @Override
    public void onFavoriteClick(AnimalListing animalListing) {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnListingSelectedListener) {
            listingSelectedListener = (OnListingSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnListingSelectedListener");
        }
    }

    public void updateListingList(ArrayList<AnimalListing> newListingList) {
        if (listingList != null && adapter != null) {
            listingList.clear();
            if (newListingList != null) {
                listingList.addAll(newListingList);
            }
            adapter.notifyDataSetChanged();
        }
    }

    // Refrescar lista de listings
    public void refreshListingList() {
        loadListings();
    }
}