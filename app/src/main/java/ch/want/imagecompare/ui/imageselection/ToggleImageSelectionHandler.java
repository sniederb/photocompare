package ch.want.imagecompare.ui.imageselection;

import android.view.View;

import androidx.appcompat.widget.AppCompatCheckBox;
import ch.want.imagecompare.data.ImageBean;

class ToggleImageSelectionHandler implements View.OnClickListener {

    private final ImageBean imageBean;

    ToggleImageSelectionHandler(final ImageBean imageBean) {
        this.imageBean = imageBean;
    }

    @Override
    public void onClick(final View view) {
        imageBean.setSelected(((AppCompatCheckBox) view).isChecked());
    }
}
