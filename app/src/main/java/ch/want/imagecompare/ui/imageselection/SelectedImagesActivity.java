package ch.want.imagecompare.ui.imageselection;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Optional;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.R;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.domain.FileImageMediaResolver;
import ch.want.imagecompare.domain.PhotoViewMediator;

public class SelectedImagesActivity extends AppCompatActivity {

    private ListSelectionThumbnailsAdapter adapter;
    private FileImageMediaResolver mediaResolver;
    private final ArrayList<ImageBean> galleryImageList = new ArrayList<>();
    private int topImageIndexForParentActivity;
    private int bottomImageIndexForParentActivity;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_images);
        initInitialState(savedInstanceState);
        initToolbar();
        initRecyclerView();
        initShareAction();
    }

    @Override
    public void onSaveInstanceState(@NonNull final Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList(BundleKeys.KEY_SELECTION_COLLECTION, new ArrayList<>(ImageBean.getSelectedImageBeans(galleryImageList)));
        savedInstanceState.putInt(BundleKeys.KEY_TOPIMAGE_INDEX, topImageIndexForParentActivity);
        savedInstanceState.putInt(BundleKeys.KEY_BOTTOMIMAGE_INDEX, bottomImageIndexForParentActivity);
        mediaResolver.saveInstanceState(savedInstanceState);
    }

    private void initInitialState(final Bundle savedInstanceState) {
        final ArrayList<ImageBean> selectedBeansFromState;
        if (savedInstanceState == null) {
            final Intent intent = getIntent();
            selectedBeansFromState = intent.getParcelableArrayListExtra(BundleKeys.KEY_SELECTION_COLLECTION);
            topImageIndexForParentActivity = intent.getIntExtra(BundleKeys.KEY_TOPIMAGE_INDEX, PhotoViewMediator.NO_VALID_IMAGE_INDEX);
            bottomImageIndexForParentActivity = intent.getIntExtra(BundleKeys.KEY_BOTTOMIMAGE_INDEX, PhotoViewMediator.NO_VALID_IMAGE_INDEX);
        } else {
            selectedBeansFromState = savedInstanceState.getParcelableArrayList(BundleKeys.KEY_SELECTION_COLLECTION);
            topImageIndexForParentActivity = savedInstanceState.getInt(BundleKeys.KEY_TOPIMAGE_INDEX, PhotoViewMediator.NO_VALID_IMAGE_INDEX);
            bottomImageIndexForParentActivity = savedInstanceState.getInt(BundleKeys.KEY_BOTTOMIMAGE_INDEX, PhotoViewMediator.NO_VALID_IMAGE_INDEX);
        }
        mediaResolver = FileImageMediaResolver.create(this, savedInstanceState);
        loadImagesForCurrentImageFolder();
        if (selectedBeansFromState != null && !selectedBeansFromState.isEmpty()) {
            ImageBean.copySelectedState(selectedBeansFromState, galleryImageList);
        }
    }

    private void loadImagesForCurrentImageFolder() {
        galleryImageList.clear();
        galleryImageList.addAll(mediaResolver.execute());
    }

    private void initToolbar() {
        final Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        Optional.ofNullable(getSupportActionBar()).ifPresent(actionBar -> actionBar.setDisplayHomeAsUpEnabled(true));
    }

    @Override
    @SuppressLint("RestrictedApi")
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.with_selected_images_menu, menu);
        if (menu instanceof MenuBuilder) {
            final MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    private void initShareAction() {
        final FloatingActionButton fab = findViewById(R.id.shareImagesButton);
        fab.setOnClickListener(new ShareSelectedImagesHandler(this, galleryImageList));
    }

    private void initRecyclerView() {
        adapter = new ListSelectionThumbnailsAdapter(galleryImageList);
        final RecyclerView recyclerView = findViewById(R.id.selectionThumbnails);
        final FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.removeUnselected:
                adapter.notifySelectionChanged();
                return true;
            case R.id.deleteUnselected:
                new DeleteUnselectedHandler(this, mediaResolver, galleryImageList)//
                        .execute();
                return true;
            case R.id.invertSelection:
                new InvertSelectionHandler(galleryImageList)//
                        .execute();
                adapter.notifySelectionChanged();
                return true;
            case R.id.deleteSelected:
                new DeleteSelectedHandler(this, mediaResolver, galleryImageList)//
                        .execute();
                return true;
            case android.R.id.home:
                new BackToCompareImagesTransition(this, mediaResolver, galleryImageList, topImageIndexForParentActivity, bottomImageIndexForParentActivity)//
                        .execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
