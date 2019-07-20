package ch.want.imagecompare.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Subclass catching those pesky IllegalArgumentException on touching.
 * See https://github.com/chrisbanes/PhotoView/issues/31
 */
public class FailsafeViewPager extends androidx.viewpager.widget.ViewPager {
    public FailsafeViewPager(@NonNull final Context context) {
        super(context);
    }

    public FailsafeViewPager(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (final IllegalArgumentException ex) {
            Log.w("FailsafeViewPager#onTouchEvent", "Prevented app crash due to " + ex.getMessage());
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (final IllegalArgumentException ex) {
            Log.w("FailsafeViewPager#onInterceptTouchEvent", "Prevented app crash due to " + ex.getMessage());
        }
        return false;
    }
}
