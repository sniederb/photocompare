package ch.want.imagecompare.ui.compareimages;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.R;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.domain.FileImageMediaQuery;
import ch.want.imagecompare.domain.PhotoViewMediator;

/**
 * @link https://www.androidauthority.com/how-to-build-an-image-gallery-app-718976/
 * @link https://blog.fossasia.org/how-to-create-a-basic-gallery-application/
 */
public class CompareImagesActivity extends AppCompatActivity {

    private String currentImageFolder;
    private PhotoViewMediator photoViewMediator;
    private Switch syncToggle;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_images);
        photoViewMediator = buildPhotoViewMediator();
        initInitialState(savedInstanceState);
        initToolbar();
    }

    private PhotoViewMediator buildPhotoViewMediator() {
        final PhotoViewMediator mediator = new PhotoViewMediator(new ImageDetailViewImpl(findViewById(R.id.upperImage)), new ImageDetailViewImpl(findViewById(R.id.bottomImage)));
        syncToggle = findViewById(R.id.toggleZoomPanSync);
        syncToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                mediator.setSyncZoomAndPan(isChecked);
            }
        });
        syncToggle.setChecked(false);
        //
        final ImageButton resetMatrixButton = findViewById(R.id.resetMatrix);
        resetMatrixButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                photoViewMediator.resetMatrix();
            }
        });
        return mediator;
    }

    private void initInitialState(final Bundle savedInstanceState) {
        final int topImageIndex;
        final int bottomIndex;
        final ArrayList<ImageBean> selectedBeansFromState;
        if (savedInstanceState == null) {
            final Intent intent = getIntent();
            currentImageFolder = intent.getStringExtra(BundleKeys.KEY_IMAGE_FOLDER);
            selectedBeansFromState = intent.getParcelableArrayListExtra(BundleKeys.KEY_SELECTION_COLLECTION);
            topImageIndex = intent.getIntExtra(BundleKeys.KEY_TOPIMAGE_INDEX, 0);
            bottomIndex = intent.getIntExtra(BundleKeys.KEY_BOTTOMIMAGE_INDEX, PhotoViewMediator.NO_VALID_IMAGE_INDEX);
        } else {
            currentImageFolder = savedInstanceState.getString(BundleKeys.KEY_IMAGE_FOLDER);
            selectedBeansFromState = savedInstanceState.getParcelableArrayList(BundleKeys.KEY_SELECTION_COLLECTION);
            topImageIndex = savedInstanceState.getInt(BundleKeys.KEY_TOPIMAGE_INDEX);
            bottomIndex = savedInstanceState.getInt(BundleKeys.KEY_BOTTOMIMAGE_INDEX, PhotoViewMediator.NO_VALID_IMAGE_INDEX);
        }
        final List<ImageBean> allImageBeans = new FileImageMediaQuery(getContentResolver(), currentImageFolder).execute();
        if (selectedBeansFromState != null && !selectedBeansFromState.isEmpty()) {
            ImageBean.copySelectedState(selectedBeansFromState, allImageBeans);
        }
        photoViewMediator.initGalleryImageList(allImageBeans, topImageIndex, bottomIndex);
        syncToggle.setChecked(true);
    }

    private void initToolbar() {
        final Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.compare_image_menu, menu);
        return true;
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(BundleKeys.KEY_IMAGE_FOLDER, currentImageFolder);
        savedInstanceState.putInt(BundleKeys.KEY_TOPIMAGE_INDEX, photoViewMediator.getTopIndex());
        savedInstanceState.putInt(BundleKeys.KEY_BOTTOMIMAGE_INDEX, photoViewMediator.getBottomIndex());
        savedInstanceState.putParcelableArrayList(BundleKeys.KEY_SELECTION_COLLECTION, new ArrayList<>(ImageBean.getSelectedImageBeans(photoViewMediator.getGalleryImageList())));
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.showSelection:
                new ShowSelectedImagesTransition(this, currentImageFolder, photoViewMediator).execute();
                return true;
            case R.id.checkboxStyleDark:
                final boolean isNowDarkMode = photoViewMediator.toggleCheckboxStyleDark();
                item.setChecked(isNowDarkMode);
                return true;
            case R.id.showExifDetails:
                final boolean isNowShowingExif = photoViewMediator.toggleExifDisplay();
                item.setChecked(isNowShowingExif);
                return true;
            case android.R.id.home:
                new BackToImageListTransition(this, currentImageFolder, photoViewMediator.getGalleryImageList()).execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
