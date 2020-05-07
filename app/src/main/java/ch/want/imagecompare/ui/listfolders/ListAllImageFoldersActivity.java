package ch.want.imagecompare.ui.listfolders;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import ch.want.imagecompare.R;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.domain.FolderImageMediaQuery;
import ch.want.imagecompare.domain.PermissionChecker;
import ch.want.imagecompare.ui.thumbnails.ImageBeanListRecyclerViewAdapter;

/**
 * You do not have to assign a layout to these elements. If you do not define a layout, the activity or fragment contains a single ListView
 * by default
 */
public class ListAllImageFoldersActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final int PERMISSION_REQUEST_STORAGE = 999;
    private final Map<String, Uri> imageBuckets = new HashMap<>();
    private PermissionChecker permissionChecker;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionChecker = new PermissionChecker(this, PERMISSION_REQUEST_STORAGE);
        initContentViews();
        onRefresh();
        final Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        setAppNameAndVersion();
    }

    private void initContentViews() {
        setContentView(R.layout.activity_list_all_image_folders);
        final SwipeRefreshLayout swipeLayout = findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        initImageBuckets();
        updateLayoutFromImageBuckets();
        ((SwipeRefreshLayout) findViewById(R.id.swipe_container)).setRefreshing(false);
    }

    private void initImageBuckets() {
        imageBuckets.clear();
        if (!permissionChecker.hasPermissions()) {
            permissionChecker.askNicely();
        } else {
            imageBuckets.putAll(new FolderImageMediaQuery(getContentResolver()).execute());
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        if ((requestCode == PERMISSION_REQUEST_STORAGE) && (grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            initImageBuckets();
            updateLayoutFromImageBuckets();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void updateLayoutFromImageBuckets() {
        final ImageBeanListRecyclerViewAdapter adapter = new ListFolderThumbnailsAdapter(getImageFolders());
        final RecyclerView recyclerView = findViewById(R.id.folderThumbnails);
        final FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private List<ImageBean> getImageFolders() {
        final List<ImageBean> buckets = new ArrayList<>();
        for (final Map.Entry<String, Uri> entry : imageBuckets.entrySet()) {
            buckets.add(new ImageBean(entry.getKey(), entry.getValue()));
        }
        Collections.sort(buckets);
        return buckets;
    }

    private void setAppNameAndVersion() {
        final TextView txtView = findViewById(R.id.appNameAndVersion);
        try {
            final PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            final String version = pInfo.versionName;
            txtView.setText(getString(R.string.app_name) + " - " + version);
        } catch (final PackageManager.NameNotFoundException e) {
            Log.w("setAppNameAndVersion", "App name and version are not displayed: " + e.getMessage());
        }
    }
}
