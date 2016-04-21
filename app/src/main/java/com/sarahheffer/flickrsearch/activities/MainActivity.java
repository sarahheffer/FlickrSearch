package com.sarahheffer.flickrsearch.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.MatrixCursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sarahheffer.flickrsearch.R;
import com.sarahheffer.flickrsearch.adapters.HistoryCursorAdapter;
import com.sarahheffer.flickrsearch.adapters.RecyclerGridAdapter;
import com.sarahheffer.flickrsearch.app.FlickrSearchApp;
import com.sarahheffer.flickrsearch.listeners.EndlessRecyclerViewScrollListener;
import com.sarahheffer.flickrsearch.models.Image;
import com.sarahheffer.flickrsearch.models.ImageResponse;
import com.sarahheffer.flickrsearch.services.ImageService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String HISTORY_SET = "history_set";
    private final static String SEARCH_TERM = "search_term";
//    private final static String IMAGE_LIST = "image_list";

    private static final int FIRST_PAGE = 1;

    @Bind(R.id.imageGrid)
    RecyclerView imageGridView;

    @Bind(R.id.emptyView)
    TextView emptyView;

    @Bind(R.id.load_progress_bar)
    ProgressBar loadingSpinner;

    @Inject
    ImageService imageService;

    @Inject
    Picasso picasso;

    private ArrayList<Image> imageList;
    private GridLayoutManager gridLayoutManager;
    RecyclerGridAdapter gridAdapter;

    SharedPreferences mSettings;
    SharedPreferences.Editor preferenceEditor;

    private Menu menu;
    SearchView searchView;

    String searchTerm;
    private Set<String> historyItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ((FlickrSearchApp) getApplication()).getAppComponent().inject(this);

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        preferenceEditor = mSettings.edit();

        readQueryHistory();
        imageList = new ArrayList<>();

        gridLayoutManager = new GridLayoutManager(this, 3);
        imageGridView.setHasFixedSize(false);
        imageGridView.setLayoutManager(gridLayoutManager);

        gridAdapter = new RecyclerGridAdapter(this, imageList);
        gridAdapter.setOnItemClickListener(new RecyclerGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Context context, Image image) {
                showImage(context, image);
            }
        });
        imageGridView.setAdapter(gridAdapter);
        imageGridView.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                getData(page);
                return true;
            }
        });
        checkShowEmptyView(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        this.menu = menu;

        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        AutoCompleteTextView searchText = (AutoCompleteTextView) searchView.findViewById(R.id.search_src_text);
        searchText.setThreshold(1);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                searchTerm = query;
                addNewQueryHistory(searchTerm);
                imageList.clear();
                getData(FIRST_PAGE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                loadHistory(s);
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(SEARCH_TERM, searchTerm);
//        savedInstanceState.putParcelableArrayList(IMAGE_LIST, imageList);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        searchTerm = savedInstanceState.getString(SEARCH_TERM);
//        ArrayList<Image> images = savedInstanceState.getParcelableArrayList(IMAGE_LIST);
//        updateImageList(images);
        if (searchTerm != null) {
            getData(FIRST_PAGE);
        }
    }

    public void getData(int page) {
        if (searchTerm == null) {
            return;
        }
        displayLoadingSpinner();
        Observable<ImageResponse> call = imageService.getImages(searchTerm, page);
        call.observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ImageResponse>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Failed to get images.", e);
            }

            @Override
            public void onNext(ImageResponse imageResponse) {
                updateImageList(imageResponse.getPhotos());
            }
        });
    }

    private void updateImageList(List<Image> images) {
        imageList.addAll(images);
        gridAdapter.notifyDataSetChanged();
        loadingSpinner.setVisibility(View.GONE);
        checkShowEmptyView(true);
    }

    private void loadHistory(String query) {
        final List<String> filteredHistoryItems = filtered(historyItems, query);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            String[] columns = new String[]{"_id", "text"};
            Object[] temp = new Object[]{0, "default"};

            MatrixCursor cursor = new MatrixCursor(columns);

            for (int i = 0; i < filteredHistoryItems.size(); i++) {
                temp[0] = i;
                temp[1] = filteredHistoryItems.get(i);
                cursor.addRow(temp);
            }

            final SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
            search.setSuggestionsAdapter(new HistoryCursorAdapter(this, cursor, filteredHistoryItems));
            search.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                @Override
                public boolean onSuggestionSelect(int position) {
                    return false;
                }

                @Override
                public boolean onSuggestionClick(int position) {
                    searchView.clearFocus();
                    searchView.setQuery(filteredHistoryItems.get(position), true);
                    return false;
                }
            });
        }
    }

    private List<String> filtered(Set<String> historyItems, String query) {
        List<String> filteredHistoryItems = new ArrayList<>();
        for (String item : historyItems) {
            if (item.startsWith(query)) {
                filteredHistoryItems.add(item);
            }
        }
        return filteredHistoryItems;
    }

    public void showImage(Context context, Image image) {
        ImageView imageView = new ImageView(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(imageView);

        AlertDialog alertDialog = builder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();

        Picasso.with(context).load(image.getImageURL()).into(imageView);
    }

    private void addNewQueryHistory(String searchQuery) {
        historyItems.add(searchQuery);
        writeQueryHistory();
    }

    private void readQueryHistory() {
        historyItems = mSettings.getStringSet(HISTORY_SET, new HashSet<String>());
    }

    private void writeQueryHistory() {
        preferenceEditor.putStringSet(HISTORY_SET, historyItems);
        preferenceEditor.commit();
    }

    private void checkShowEmptyView(boolean didSearch) {
        if (imageList.size() == 0) {
            if (didSearch) {
                emptyView.setText(R.string.no_results);
            }
            emptyView.setVisibility(View.VISIBLE);
            imageGridView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            imageGridView.setVisibility(View.VISIBLE);
        }
    }

    private void displayLoadingSpinner() {
        emptyView.setVisibility(View.GONE);
        loadingSpinner.setVisibility(View.VISIBLE);
    }
}
