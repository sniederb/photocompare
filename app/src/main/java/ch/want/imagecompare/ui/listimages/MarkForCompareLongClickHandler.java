package ch.want.imagecompare.ui.listimages;

import android.view.View;

import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.ui.thumbnails.ImageBeanListRecyclerViewAdapter;

class MarkForCompareLongClickHandler implements View.OnLongClickListener {

    private final ImageBeanListRecyclerViewAdapter viewAdapter;
    private final int selectedIndex;

    MarkForCompareLongClickHandler(final ImageBeanListRecyclerViewAdapter viewAdapter, final int selectedIndex) {
        this.viewAdapter = viewAdapter;
        this.selectedIndex = selectedIndex;
    }

    @Override
    public boolean onLongClick(final View v) {
        for (int i = 0; i < viewAdapter.getItemCount(); i++) {
            final ImageBean imageBean = viewAdapter.getImageAndTitleBean(i);
            if (imageBean.isInitialForCompare() != (i == selectedIndex)) {
                imageBean.setInitialForCompare(i == selectedIndex);
                viewAdapter.notifyItemChanged(i);
            }
        }
        return true;
    }
}
