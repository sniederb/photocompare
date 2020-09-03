package ch.want.imagecompare.ui.listimages;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.Optional;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.R;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.domain.FileImageMediaQuery;
import ch.want.imagecompare.ui.thumbnails.ImageBeanListRecyclerViewAdapter;

public class ListImagesInFolderActivity extends AppCompatActivity {

    private String currentImageFolder;
    private boolean sortNewToOld = true;
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
        menu.findItem(R.id.sortNewToOld).setChecked(sortNewToOld);
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
            currentImageFolder = intent.getStringExtra(BundleKeys.KEY_IMAGE_FOLDER);
            sortNewToOld = intent.getBooleanExtra(BundleKeys.KEY_SORT_NEWEST_FIRST, true);
            selectedBeansFromState = intent.getParcelableArrayListExtra(BundleKeys.KEY_SELECTION_COLLECTION);
        } else {
            currentImageFolder = savedInstanceState.getString(BundleKeys.KEY_IMAGE_FOLDER);
            sortNewToOld = savedInstanceState.getBoolean(BundleKeys.KEY_SORT_NEWEST_FIRST, true);
            selectedBeansFromState = savedInstanceState.getParcelableArrayList(BundleKeys.KEY_SELECTION_COLLECTION);
        }
        loadImagesForCurrentImageFolder();
        if (selectedBeansFromState != null && !selectedBeansFromState.isEmpty()) {
            ImageBean.copySelectedState(selectedBeansFromState, galleryImageList);
        }
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
        savedInstanceState.putString(BundleKeys.KEY_IMAGE_FOLDER, currentImageFolder);
        savedInstanceState.putBoolean(BundleKeys.KEY_SORT_NEWEST_FIRST, sortNewToOld);
        savedInstanceState.putParcelableArrayList(BundleKeys.KEY_SELECTION_COLLECTION, new ArrayList<>(ImageBean.getSelectedImageBeans(galleryImageList)));
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (hasSelection()) {
                    showAlertLosingSelection();
                    return true;
                }
                break;
            case R.id.sortNewToOld:
                sortNewToOld = !sortNewToOld;
                item.setChecked(sortNewToOld);
                loadImagesForCurrentImageFolder();
                notifyAdapterDataSetChanged();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean hasSelection() {
        return !ImageBean.getSelectedImageBeans(galleryImageList).isEmpty();
    }

    private void loadImagesForCurrentImageFolder() {
        galleryImageList.clear();
        galleryImageList.addAll(new FileImageMediaQuery(getContentResolver(), currentImageFolder, sortNewToOld).execute());
    }

    private void initRecyclerImageView() {
        final ImageBeanListRecyclerViewAdapter<SingleImageViewHolder> adapter = new ListImagesThumbnailsAdapter(currentImageFolder, sortNewToOld, galleryImageList);
        final RecyclerView recyclerView = findViewById(R.id.imageThumbnails);
        final FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void notifyAdapterDataSetChanged() {
        Optional.ofNullable(findViewById(R.id.imageThumbnails))//
                .map(view -> (RecyclerView) view)//
                .map(view -> (ImageBeanListRecyclerViewAdapter<?>) view.getAdapter())//
                .ifPresent(adapter -> adapter.notifyDataSetSortChanged());
    }

    private void showAlertLosingSelection() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(android.R.string.ok, (dialog, id) -> ListImagesInFolderActivity.super.onBackPressed());
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setMessage(R.string.navigation_will_lose_selection).setTitle(R.string.navigation_will_lose_selection_title);
        builder.show();
    }
}
