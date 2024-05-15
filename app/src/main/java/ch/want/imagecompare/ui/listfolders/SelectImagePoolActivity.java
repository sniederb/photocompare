package ch.want.imagecompare.ui.listfolders;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.R;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.domain.FolderImageMediaResolver;
import ch.want.imagecompare.domain.LegacyPermissionChecker;
import ch.want.imagecompare.domain.PermissionChecker;
import ch.want.imagecompare.domain.PhotoComparePreferences;
import ch.want.imagecompare.domain.ScopedPermissionChecker;
import ch.want.imagecompare.ui.thumbnails.ImageBeanListRecyclerViewAdapter;

/**
 * You do not have to assign a layout to these elements. If you do not define a layout, the activity or fragment contains a single ListView
 * by default
 */
public class SelectImagePoolActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    /**
     * Random permission code which will be returned by to {@link #onRequestPermissionsResult(int, String[], int[])}
     */
    private static final int PERMISSION_REQUEST_STORAGE = 999;
    private final Map<String, Uri> imageBuckets = new HashMap<>();
    private PermissionChecker permissionChecker;
    private DateFormat dateFormat;
    private final Calendar cal = Calendar.getInstance();
    private BroadcastReceiver filesDeletedBroadcastReceiver;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateFormat = android.text.format.DateFormat.getMediumDateFormat(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permissionChecker = new ScopedPermissionChecker(this, PERMISSION_REQUEST_STORAGE);
        } else {
            permissionChecker = new LegacyPermissionChecker(this, PERMISSION_REQUEST_STORAGE);
        }
        initContentViews();
        initDateSelection(savedInstanceState);
        initMediaAccessManagement();
        onRefresh();
        final Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        setAppNameAndVersion();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(filesDeletedBroadcastReceiver, new IntentFilter(BundleKeys.FILE_DELETE_COMPLETE));
        if (imageBuckets.isEmpty() && permissionChecker.hasPermissions()) {
            onRefresh();
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(filesDeletedBroadcastReceiver);
        super.onPause();
    }

    private void initContentViews() {
        setContentView(R.layout.activity_select_imagepool);
        final SwipeRefreshLayout swipeLayout = findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
    }

    private void initMediaAccessManagement() {
        if (permissionChecker.supportsReselect()) {
            final Button updateMediaAccessButton = findViewById(R.id.updateMediaAccess);
            if ((updateMediaAccessButton != null) && !updateMediaAccessButton.hasOnClickListeners()) {
                updateMediaAccessButton.setOnClickListener(v -> permissionChecker.askNicely(true));
            }
        } else {
            Optional.ofNullable((View) findViewById(R.id.manageMediaAccess))
                    .ifPresent(manageMediaAccessView -> ((ViewGroup) manageMediaAccessView.getParent()).removeView(manageMediaAccessView));
        }
    }

    private void initDateSelection(final Bundle savedInstanceState) {
        final long savedSelectionDate;
        if (savedInstanceState != null) {
            savedSelectionDate = savedInstanceState.getLong(BundleKeys.KEY_IMAGE_DATE, 0);
        } else {
            final Intent intent = getIntent();
            savedSelectionDate = intent.getLongExtra(BundleKeys.KEY_IMAGE_DATE, 0);
        }
        if (savedSelectionDate > 0) {
            cal.setTime(new Date(savedSelectionDate));
        }
        final EditText dateTextControl = findViewById(R.id.selectByDateValue);
        dateTextControl.setText(dateFormat.format(cal.getTime()));
        dateTextControl.setOnClickListener(view -> {
            final int year = cal.get(Calendar.YEAR);
            final int month = cal.get(Calendar.MONTH);
            final int day = cal.get(Calendar.DAY_OF_MONTH);
            final DatePickerDialog dialog = new DatePickerDialog(SelectImagePoolActivity.this,//
                    new DateSetListener(dateTextControl, cal, dateFormat),//
                    year, month, day);
            dialog.show();
        });
        final ImageButton applyDateSelectionButton = findViewById(R.id.applyDateSelection);
        applyDateSelectionButton.setOnClickListener(v -> new ApplyDateSelectionHandler(cal.getTime()).navigateToListImages(v));
    }

    @Override
    public void onRefresh() {
        initImageBuckets();
        updateLayoutFromImageBuckets();
        ((SwipeRefreshLayout) findViewById(R.id.swipe_container)).setRefreshing(false);
    }

    /**
     * Called before the instance is killed
     */
    @Override
    public void onSaveInstanceState(@NonNull final Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong(BundleKeys.KEY_IMAGE_DATE, cal.getTime().getTime());
    }

    private void initImageBuckets() {
        imageBuckets.clear();
        if (!permissionChecker.hasPermissions()) {
            permissionChecker.askNicely(false);
        } else {
            final boolean sortNewToOld = new PhotoComparePreferences(this).isSortNewestFirst();
            imageBuckets.putAll(new FolderImageMediaResolver(getContentResolver(), sortNewToOld).execute());
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            initImageBuckets();
            updateLayoutFromImageBuckets();
            initMediaAccessManagement();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == PERMISSION_REQUEST_STORAGE) && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)) {
            if (permissionChecker.hasPermissions()) {
                initImageBuckets();
                updateLayoutFromImageBuckets();
                initMediaAccessManagement();
            } else {
                Toast.makeText(this, "Cannot show and manage images without storage access.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateLayoutFromImageBuckets() {
        final ImageBeanListRecyclerViewAdapter<SingleFolderViewHolder> adapter = new ListFolderThumbnailsAdapter(getImageFolders());
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
        buckets.sort((a, b) -> StringUtils.compare(a.getDisplayName(), b.getDisplayName(), true));
        return buckets;
    }

    private void setAppNameAndVersion() {
        final TextView txtView = findViewById(R.id.appNameAndVersion);
        try {
            final PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            final String version = pInfo.versionName;
            txtView.setText(getString(R.string.app_name_with_version, version));
        } catch (final PackageManager.NameNotFoundException e) {
            Log.w("setAppNameAndVersion", "App name and version are not displayed: " + e.getMessage());
        }
    }

    private void initBroadcastReceiver() {
        if (filesDeletedBroadcastReceiver == null) {
            filesDeletedBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(final Context context, final Intent intent) {
                    onRefresh();
                }
            };
        }
    }

    private static class DateSetListener implements DatePickerDialog.OnDateSetListener {

        private final EditText dateTextControl;
        private final Calendar cal;
        private final DateFormat dateFormat;

        DateSetListener(final EditText dateTextControl, final Calendar cal, final DateFormat dateFormat) {
            this.dateTextControl = dateTextControl;
            this.cal = cal;
            this.dateFormat = dateFormat;
        }

        @Override
        public void onDateSet(final DatePicker view, final int year, final int month, final int dayOfMonth) {
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            dateTextControl.setText(dateFormat.format(cal.getTime()));
        }
    }
}
