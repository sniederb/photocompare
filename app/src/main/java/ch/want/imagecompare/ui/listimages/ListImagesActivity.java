package ch.want.imagecompare.ui.listimages;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.R;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.domain.FileImageMediaResolver;
import ch.want.imagecompare.domain.PhotoComparePreferences;
import ch.want.imagecompare.ui.thumbnails.ImageBeanListRecyclerViewAdapter;

/**
 * List images. The layout is basically defined by subclass of {@link ch.want.imagecompare.ui.thumbnails.ImageLayoutSizeParams},
 * used in {@link ImageBeanListRecyclerViewAdapter}
 */
public class ListImagesActivity extends AppCompatActivity {

    private FileImageMediaResolver mediaResolver;

    private final ArrayList<ImageBean> galleryImageList = new ArrayList<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_images_in_folder);
        initInitialState(savedInstanceState);
        initToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.list_images_menu, menu);
        menu.findItem(R.id.sortNewToOld).setChecked(mediaResolver.isSortNewToOld());
        menu.findItem(R.id.sortFilenames).setChecked(mediaResolver.useFilenamesForSort());
        return true;
    }

    private void initToolbar() {
        final Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        Optional.ofNullable(getSupportActionBar()).ifPresent(actionBar -> actionBar.setDisplayHomeAsUpEnabled(true));
    }

    private void initInitialState(final Bundle savedInstanceState) {
        final ArrayList<ImageBean> selectedBeansFromState;
        if (savedInstanceState == null) {
            final Intent intent = getIntent();
            selectedBeansFromState = intent.getParcelableArrayListExtra(BundleKeys.KEY_SELECTION_COLLECTION);
        } else {
            selectedBeansFromState = savedInstanceState.getParcelableArrayList(BundleKeys.KEY_SELECTION_COLLECTION);
        }
        mediaResolver = FileImageMediaResolver.create(this, savedInstanceState);
        // Async loader for images. For large folders, running this on the UI thread might lead to an ANR.
        Executors.newSingleThreadExecutor().execute(() -> loadImagesForCurrentImageFolder(selectedBeansFromState));
    }

    @Override
    protected void onStart() {
        super.onStart();
        initRecyclerImageView();
    }

    /**
     * Called before the instance is killed
     */
    @Override
    public void onSaveInstanceState(@NonNull final Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList(BundleKeys.KEY_SELECTION_COLLECTION, new ArrayList<>(ImageBean.getSelectedImageBeans(galleryImageList)));
        mediaResolver.saveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (hasSelection()) {
                    showAlertLosingSelection();
                    return true;
                }
                new BackToPoolSelectionTransition(this, mediaResolver).execute();
                return true;
            case R.id.sortNewToOld:
                mediaResolver.setSortNewToOld(!mediaResolver.isSortNewToOld());
                item.setChecked(mediaResolver.isSortNewToOld());
                new PhotoComparePreferences(this).setSortNewestFirst(mediaResolver.isSortNewToOld());
                // sort is done by the media query, so need to reload the entire set!
                loadImagesForCurrentImageFolder();
                return true;
            case R.id.sortFilenames:
                mediaResolver.setFilenamesForSort(!mediaResolver.useFilenamesForSort());
                item.setChecked(mediaResolver.useFilenamesForSort());
                new PhotoComparePreferences(this).setFilenamesForSort(mediaResolver.useFilenamesForSort());
                // sort is done by the media query, so need to reload the entire set!
                loadImagesForCurrentImageFolder();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean hasSelection() {
        return !ImageBean.getSelectedImageBeans(galleryImageList).isEmpty();
    }

    private void loadImagesForCurrentImageFolder() {
        loadImagesForCurrentImageFolder(ImageBean.getSelectedImageBeans(galleryImageList));
    }

    private void loadImagesForCurrentImageFolder(final List<ImageBean> currentSelection) {
        setToolbarTitleToLoading();
        galleryImageList.clear();
        galleryImageList.addAll(mediaResolver.execute());
        if (currentSelection != null && !currentSelection.isEmpty()) {
            ImageBean.copySelectedState(currentSelection, galleryImageList);
        }
        notifyAdapterDataSetChanged();
        setToolbarTitleToReady();
    }

    private void initRecyclerImageView() {
        final ImageBeanListRecyclerViewAdapter<SingleImageViewHolder> adapter = new ListImagesThumbnailsAdapter(mediaResolver, galleryImageList);
        final RecyclerView recyclerView = findViewById(R.id.imageThumbnails);
        final FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void setToolbarTitleToLoading() {
        runOnUiThread(() -> {
            final Toolbar toolbar = findViewById(R.id.my_toolbar);
            toolbar.setTitle(R.string.action_loading_images);
        });
    }

    private void setToolbarTitleToReady() {
        runOnUiThread(() -> {
            final Toolbar toolbar = findViewById(R.id.my_toolbar);
            toolbar.setTitle(R.string.action_select_image);
        });
    }

    private void notifyAdapterDataSetChanged() {
        runOnUiThread(() -> Optional.ofNullable(findViewById(R.id.imageThumbnails))//
                .map(view -> (RecyclerView) view)//
                .map(view -> (ImageBeanListRecyclerViewAdapter<?>) view.getAdapter())//
                .ifPresent(ImageBeanListRecyclerViewAdapter::notifyDataSetChanged));
    }

    private void showAlertLosingSelection() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(android.R.string.ok, (dialog, id) -> {
            galleryImageList.clear();
            onBackPressed();
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setMessage(R.string.navigation_will_lose_selection).setTitle(R.string.navigation_will_lose_selection_title);
        builder.show();
    }
}
