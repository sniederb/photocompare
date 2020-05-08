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

The "pan and zoom listener" on a PhotoView is toggled in two cases:

* During loading of a new image
** Disabled when starting to load a new high-resolution image
** Re-enabled once in the onApplyZoomPanMatrix() implementation of the `ImageDetailViewImpl`
* When applying a display matrix
** Disabled at the start of `PhotoViewMediator.copyPanAndZoom()`
** Re-enabled in the finally block of above method 


### SelectedImagesActivity

The main actions are
* Remove images from selection
* Share images 


## Libraries

The only non-Android library used are [Glide](https://github.com/bumptech/glide) and [Subsampling Scale Image View](https://github.com/davemorrissey/subsampling-scale-image-view).

### Subsampling Scale Image View

Based on [06. State of the documentation](https://github.com/davemorrissey/subsampling-scale-image-view/wiki/06.-State), view state synchronization
is based on OnImageEventListener (basic image readiness) and OnStateChangedListener (pan and zoom). The class
```
ImageViewListener implements SubsamplingScaleImageView.OnImageEventListener, SubsamplingScaleImageView.OnStateChangedListener 
```

wraps these two, and passes the events to our own `ImageViewEventListener`, so that multiple listeners can react to event.

#### Fling

`SubsamplingScaleImageView` sets a `GestureDetector` internally, which runs if `panEnabled` is true. The resulting pan has `ORIGIN_FLING`,
thus it's important that `ImageViewListener` does **not** restrict event handling on "origin".

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
