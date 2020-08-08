package ch.want.imagecompare.ui;

public interface ProgressCallback {
    void starting(int maxProgress);

    void progress(int deletedImagesCount);

    void finished();
}
