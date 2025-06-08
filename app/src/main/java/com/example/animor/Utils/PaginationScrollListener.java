package com.example.animor.Utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PaginationScrollListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager layoutManager;
    private boolean isLoading = false;
    private boolean hasMorePages = true;
    private int currentPage = 0;

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

//        if (!isLoading && hasMorePages) {
//            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
//                    && firstVisibleItemPosition >= 0) {
//                loadMoreItems();
//            }
//        }
    }

//    private void loadMoreItems() {
//        isLoading = true;
//        currentPage++;
//    ApiRequests api = new ApiRequests();
//    api.loadPage(currentPage);
//    }
}
