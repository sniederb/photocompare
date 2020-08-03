# Manual test cases

This document describes a series of tests to perform manually, as the implementation of automated
tests seems overly complicated

## Using two-finger movements in emulator

1. move your mouse to where you want the centre of the pinch zoom. (do NOT press the mouse button)
2. press SHIFT (do not press the mouse button)
3. press CTRL (do not press the mouse button)
4. Press left mouse button
5. Drag 'n' drop

## Test case 1 - Simple sync

### Given

- Compare image activity
- Pan/zoom sync enabled

### When

1. Zoom lower image
2. Pan lower image

### Then
- Top image is zoomed accordingly
- Top image is panned accordingly

## Test case 1b - Simple sync with different orientation

- Run "test case 1" with images "image_landscape.JPG" and "image_portrait.JPG"

## Test case 2 - Sync and page

### Given
- Test case 1 executed

### When
1. Page to next image on bottom pager

### Then
- Bottom image has zoom and pan adjusted to that of previous image

## Test case 3 - Offset sync

### Given

- Test case 1 executed
 
### When 

1. Disable pan/zoom sync 
2. Pan lower image
3. Enable pan/zoom sync
4. Pan lower image
 
### Then

- In step (2), top image is not moved at all
- In step (4), top image is panned relative to movement in (4)
  - It would be a bug if the top image snaps to mirror the pan movement in step (2)
