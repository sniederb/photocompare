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

    /**
     * Something like {@code J0091157.jpg}
     */
    private final String displayName;

    /**
     * Something like {@code file:///storage/emulated/0/Download/EOS77D/J0091157.JPG}, ie a file locator
     * indicating the storage location.
     * This valued is stored as String internally so class remains serializable (Uri would violated that)
     */
    private final String filePathToImage;
    /**
     * Something like {@code content://media/external/images/media/52}, ie a reference to the media
     * library.
     * This valued is stored as String internally so class remains serializable (Uri would violated that)
     */
    //
    private final String contentPathToImage;

    private boolean selected;
    private boolean initialForCompare;

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

    public static void copySelectedState(final List<ImageBean> selectedImageBeans, final List<ImageBean> allImageBeans) {
        for (final ImageBean image : selectedImageBeans) {
            final int index = allImageBeans.indexOf(image);
            if (index >= 0) {
                allImageBeans.get(index).setSelected(true);
            }
        }
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

    public boolean isInitialForCompare() {
        return initialForCompare;
    }

    public void setInitialForCompare(final boolean initialForCompare) {
        this.initialForCompare = initialForCompare;
    }

    @Override
    public int compareTo(final ImageBean o) {
        return displayName.compareTo(o.displayName);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ImageBean imageBean = (ImageBean) o;
        return filePathToImage.equals(imageBean.filePathToImage);
    }

    @Override
    public int hashCode() {
        return filePathToImage.hashCode();
    }
}
