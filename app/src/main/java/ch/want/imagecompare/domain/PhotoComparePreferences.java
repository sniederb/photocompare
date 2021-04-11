package ch.want.imagecompare.domain;

import android.app.Activity;
import android.content.SharedPreferences;

import ch.want.imagecompare.BundleKeys;

public class PhotoComparePreferences {

    private final SharedPreferences preferences;

    public PhotoComparePreferences(final Activity activity) {
        preferences = activity.getSharedPreferences(BundleKeys.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
    }

    public PhotoComparePreferences setSortNewestFirst(final boolean newValue) {
        setBoolean(BundleKeys.KEY_SORT_NEWEST_FIRST, newValue);
        return this;
    }

    public boolean isSortNewestFirst() {
        return preferences.getBoolean(BundleKeys.KEY_SORT_NEWEST_FIRST, true);
    }

    public PhotoComparePreferences setFilenamesForSort(final boolean newValue) {
        setBoolean(BundleKeys.KEY_FILENAMES_FOR_SORT, newValue);
        return this;
    }

    boolean isFilenamesForSort() {
        return preferences.getBoolean(BundleKeys.KEY_FILENAMES_FOR_SORT, false);
    }

    public PhotoComparePreferences setShowExifDetails(final boolean newValue) {
        setBoolean(BundleKeys.KEY_SHOW_EXIF_DETAILS, newValue);
        return this;
    }

    boolean isShowExifDetails() {
        return preferences.getBoolean(BundleKeys.KEY_SHOW_EXIF_DETAILS, true);
    }

    public PhotoComparePreferences setCheckboxStyleDark(final boolean newValue) {
        setBoolean(BundleKeys.KEY_CHECKBOX_STYLE_DARK, newValue);
        return this;
    }

    boolean isCheckboxStyleDark() {
        return preferences.getBoolean(BundleKeys.KEY_CHECKBOX_STYLE_DARK, true);
    }

    private void setBoolean(final String key, final boolean newValue) {
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, newValue);
        editor.apply();
    }
}
