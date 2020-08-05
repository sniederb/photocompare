package ch.want.imagecompare.data;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Dimension {
    public final int width;
    public final int height;

    public Dimension(final int width, final int height) {
        this.height = height;
        this.width = width;
    }

    public float getDiagonal() {
        return (float) Math.sqrt(height * height + width * width);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("width", width).append("height", height).toString();
    }
}
