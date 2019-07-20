## Basic structure

### ListAllImageFoldersActivity
Lists all image media folders, using 
```java
getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
```
It's important to note that the Media external/internal URI has nothing to do with storage location, 
but actually refer to internal = private to the app, and external = public folders

### ListImagesInFolderActivity

### CompareImagesActivity

* Has two instances of the layout/view_image_details.xml, along with a widget.Switch to en-/disable sync between the two
* Manages a list of selected images per folder (see "Data and file storage overview")
** Image selection is NOT a cache, as it cannot be rebuilt from other information

The main actions are
* Handle image swiping with a ViewPager, disallowing the index currently in use on the other ViewPager
* Sync zoom/pan to other view
* Manage image selection
* Provide actions to share selected images

The "matrix listener" on a PhotoView is toggled in two cases:

* During loading of a new image
** Disabled when starting to load a new high-resolution image
** Re-enabled once in the onApplyZoomPanMatrix() implementation of the ImageDetailViewImpl
* When applying a display matrix
** Disabled at the start of PhotoViewMediator.copyMatrix()
** Re-enabled in the finally block of above method 


### SelectedImagesActivity

The main actions are
* Remove images from selection
* Share images 


## Libraries

The only non-Android library used is [Glide](https://github.com/bumptech/glide) and [PhotoView](https://github.com/chrisbanes/PhotoView).

We're using the 3.x version of Glide for now, because the "Generated API" stuff in version 4 looks complicated
and daunting.

### PhotoView (chrisbanes)

Swipes can be handled using
```java
myPhotoView.setOnSingleFlingListener(new ImageSwipeHandler(viewContext));
```
where the FlingListener needs to do the math on fling distance and velocity to decide whether this was a swipe or not.
(There are various stackoverflow posts to that end.) However, we're using a ViewPager which makes the swipe
handling redundant.

In ImagePageAdapter.instantiateItem():

```java
myPhotoView.setOnScaleChangeListener(new ScaleChangeListener());
```
introduces a listener for zoom changes. The listener receives
* @param scaleFactor the +incremental+ scale factor (less than 1 for zoom out, greater than 1 for zoom in)
* @param focusX      focal point X position
* @param focusY      focal point Y position

To get a total scale factor for an image, do:
```java
private float cumulativeScaleFactor = 1;
photoViewAttacher.setOnScaleChangeListener(new PhotoViewAttacher.OnScaleChangeListener() {
  @Override
  public void onScaleChange(final float scaleFactor, final float focusX, final float focusY) {
    cumulativeScaleFactor = cumulativeScaleFactor * scaleFactor;
  }
});

```
Beware that Photoview#setScale() expects the absolute scale, not an incremental factor.

```java
myPhotoView.setOnMatrixChangeListener(new MatrixChangeListener());
```
introduces a listener for Photoview "matrix" changes.

A "matrix" holds rotation, scale, and pivot properties. A PhotoView instance holds three matrices:

1. 'mBaseMatrix' .. matrix describing the image
2. 'mDrawMatrix' .. returned by PhotoView#getImageMatrix()
3. 'mSuppMatrix' .. delta-matrix to get from mBaseMatrix to mDrawMatrix, set with #setDisplayMatrix() [sic]

Note that PhotoView#getMatrix() doesn't return any of the above elements, but rather the default 
transformation matrix from the View superclass.

The bottom line is that while the photo view will hold an internal base matrix (mBaseMatrix), that
matrix is simply derived from the image. The viewport is derived solely from the mSuppMatrix, so
synchronizing zoom/pan need to
```java
final Matrix newImageMatrix = new Matrix();
currentPhoto.getAttacher().getSuppMatrix(newImageMatrix);
```
and then copy that matrix over with
```java
currentPhoto.setDisplayMatrix(matrix);
```

### Glide / PhotoView / ViewPager and OOM

According to [this github issue](https://github.com/bumptech/glide/issues/974),
> If you want to zoom an image you need to tell Glide to load a bigger image, otherwise it'll be blurry: 
> .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).

However the ViewPager will load 1 off-screen view per side, ie with a top/bottom view that 6 "original size" 
images in memory. An image from a EOS 77D is 7-9MB, ie for images alone a heap of around 50MB is required.
When scrolling, garbage collection of destroyed views might be a bit delayed, thus increasing required
heap size yet even more.

According to [this stackoverflow](https://stackoverflow.com/questions/10747211/how-much-memory-does-each-android-process-get), the
standard heap size on an Android device is in the area of 16-24 MB, while android:largeHeap="true" increases
that to 48-128MB.


## Resources used for this app

* [Data and file storage overview](https://developer.android.com/guide/topics/data/data-storage)
* [Code an Image Gallery Android App With Glide](https://code.tutsplus.com/tutorials/code-an-image-gallery-android-app-with-glide--cms-28207)
