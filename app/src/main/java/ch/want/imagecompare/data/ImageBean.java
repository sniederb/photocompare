package ch.want.imagecompare.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean class holding information for an image
 */
public class ImageBean implements Comparable<ImageBean>, Parcelable {

    private final String displayName;
    // store Uri as String internally so class remains serializable
    private final String filePathToImage;
    private final String contentPathToImage;

    private boolean selected;

    public ImageBean(final String displayName, final Uri fileUri) {
        this.displayName = displayName;
        filePathToImage = fileUri.toString();
        contentPathToImage = "";
    }

    public ImageBean(final String displayName, final Uri fileUri, final Uri contentUri) {
        this.displayName = displayName;
        filePathToImage = fileUri.toString();
        contentPathToImage = contentUri.toString();
    }

    private ImageBean(final Parcel in) {
        displayName = in.readString();
        filePathToImage = in.readString();
        contentPathToImage = in.readString();
        selected = Boolean.parseBoolean(in.readString());
    }

    public static List<ImageBean> getSelectedImageBeans(final List<ImageBean> allImageBeans) {
        final List<ImageBean> selectedImages = new ArrayList<>();
        for (final ImageBean image : allImageBeans) {
            if (image.isSelected()) {
                selectedImages.add(image);
            }
        }
        return selectedImages;
    }

    public static List<ImageBean> getUnselectedImageBeans(final List<ImageBean> allImageBeans) {
        final List<ImageBean> selectedImages = new ArrayList<>();
        for (final ImageBean image : allImageBeans) {
            if (!image.isSelected()) {
                selectedImages.add(image);
            }
        }
        return selectedImages;
    }

    public static final Creator<ImageBean> CREATOR = new Creator<ImageBean>() {
        @Override
        public ImageBean createFromParcel(final Parcel in) {
            return new ImageBean(in);
        }

        @Override
        public ImageBean[] newArray(final int size) {
            return new ImageBean[size];
        }
    };

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(displayName);
        dest.writeString(filePathToImage);
        dest.writeString(contentPathToImage);
        dest.writeString(Boolean.toString(selected));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Uri getFileUri() {
        return Uri.parse(filePathToImage);
    }

    public Uri getContentUri() {
        return Uri.parse(contentPathToImage);
    }

    public File getImageFile() {
        return new File(Uri.parse(filePathToImage).getPath());
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(final boolean selected) {
        this.selected = selected;
    }

    @Override
    public int compareTo(final ImageBean o) {
        return displayName.compareTo(o.displayName);
    }
}
