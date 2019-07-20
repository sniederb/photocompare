package ch.want.imagecompare.ui.listimages;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.R;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.domain.ImageMediaQuery;
import ch.want.imagecompare.ui.thumbnails.ImageBeanListRecyclerViewAdapter;

public class ListImagesInFolderActivity extends AppCompatActivity {

    private String currentImageFolder;
    private final ArrayList<ImageBean> galleryImageList = new ArrayList<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_images_in_folder);
        initInitialState(savedInstanceState);
        initToolbar();
    }

    private void initToolbar() {
        final Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void initInitialState(final Bundle savedInstanceState) {
        final ArrayList<ImageBean> imageBeansFromState;
        if (savedInstanceState == null) {
            final Intent intent = getIntent();
            currentImageFolder = intent.getStringExtra(BundleKeys.KEY_IMAGE_FOLDER);
            imageBeansFromState = intent.getParcelableArrayListExtra(BundleKeys.KEY_IMAGE_COLLECTION);
        } else {
            currentImageFolder = savedInstanceState.getString(BundleKeys.KEY_IMAGE_FOLDER);
            imageBeansFromState = savedInstanceState.getParcelableArrayList(BundleKeys.KEY_IMAGE_COLLECTION);
        }
        galleryImageList.clear();
        if (imageBeansFromState != null) {
            galleryImageList.addAll(imageBeansFromState);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (galleryImageList.isEmpty()) {
            loadImagesForFolder(currentImageFolder);
        }
        initRecyclerImageView();
    }

    /**
     * Called before the instance is killed
     *
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(BundleKeys.KEY_IMAGE_FOLDER, currentImageFolder);
        savedInstanceState.putParcelableArrayList(BundleKeys.KEY_IMAGE_COLLECTION, galleryImageList);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home && hasSelection()) {
            showAlertLosingSelection(item);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private boolean hasSelection() {
        return !ImageBean.getSelectedImageBeans(galleryImageList).isEmpty();
    }

    private void loadImagesForFolder(@NonNull final String bucketPath) {
        new ImageMediaQuery(getContentResolver(), MediaStore.Images.Media.DATA + " like ?", new String[]{bucketPath + "%"}) {

            @Override
            public void doWithCursor(final String bucketPath, final String imageFilePath, final Uri imageContentUri) {
                final File file = new File(imageFilePath);
                if (file.exists()) {
                    galleryImageList.add(new ImageBean(file.getName(), Uri.fromFile(file), imageContentUri));
                }
            }
        }.execute();
    }

    private void initRecyclerImageView() {
        final ImageBeanListRecyclerViewAdapter adapter = new ListImagesThumbnailsAdapter(galleryImageList);
        final RecyclerView recyclerView = findViewById(R.id.imageThumbnails);
        final FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void showAlertLosingSelection(final MenuItem item) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int id) {
                ListImagesInFolderActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setMessage(R.string.navigation_will_lose_selection).setTitle(R.string.navigation_will_lose_selection_title);
        builder.show();
    }
}
