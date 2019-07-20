package ch.want.imagecompare.ui.compareimages;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.widget.AppCompatCheckBox;
import ch.want.imagecompare.R;
import ch.want.imagecompare.domain.ImageDetailView;

class SelectImageHandler implements View.OnClickListener {

    private final ImageDetailView imageDetailView;

    SelectImageHandler(final ImageDetailView imageDetailView) {
        this.imageDetailView = imageDetailView;
    }

    @Override
    public void onClick(final View view) {
        imageDetailView.getCurrentImageBean().setSelected(((AppCompatCheckBox) view).isChecked());
        if (imageDetailView.getCurrentImageBean().isSelected()) {
            Snackbar.make(view, R.string.confirm_selection, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }
    }
}
