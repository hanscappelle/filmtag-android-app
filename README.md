
# About

Basic Android app for keeping track of photography settings while shooting analog film. As used by
analog photographers who can't rely on exif information when using analog roll film while shooting.

# Release Build Instructions

- use v1 release key
- create tag on git for each release
- for now building with Java 17 for bitbucket pipeline

# Goals

Keep it dead simple and easy to use. The photographer shouldn't have to look into settings or any
complex editing options while shooting. Keep distraction to an absolute minimum.

# Key features

* get a list of film rolls created
* create a new film roll adding film type, nr of frames and ISO exposed at (!film ISO)
* show details (frames) for each of these film rolls (+ delete option on detail screen)
* from details change the aperture, shutter and notes for each frame

# Alternatives

* http://petapixel.com/2012/07/30/exif4film-helps-analog-photographers-infuse-metadata-into-film-photos/
* http://appcrawlr.com/ios/film-rolls

# Other Resources

## play store link

https://play.google.com/store/apps/details?id=be.hcpl.android.filmtag

For devs, not that this app is signed with older keystore format and still released as an apk (no bundle).

## beta testing

https://play.google.com/apps/testing/be.hcpl.android.filmtag

## colors generated with materialpalette

https://www.materialpalette.com/brown/grey

# Version History

## upcoming, issues and features

* in app visualisation of locations on map
* delete from overview with swipe action (?)
* autocomplete for names and tags based on existing
* use recyclerView (technical improvement only)
* filter with search from toolbar
* improve picture storage and preview
* resolve locations into address or place names instead of lat/long?
* easier way to mark film developed (not from edit film)

## 0.21.0

technical release

- update build tools in preparation for compose
- changed from apk to app bundle release

## 0.20.0

technical release

- update target SDK to 36
- update build tools

## v0.19.0

resolved obfuscation issue with release build 

Caused by: java.lang.IllegalStateException: TypeToken must be created with a type argument: new TypeToken<...>() {}; When using code shrinkers (ProGuard, R8, ...) make sure that generic signatures are preserved.

## v0.18.0

* technical code improvements
* UX allow adding location also from selecting the location icon (not only from the menu) + auto save
* fix showing location on map when set
* UX - fixed support for dark mode (using DayNight theme instead of Light)
* UX - marked films developed with checkbox now

## v0.17.0

Created on request of Google to support latest releases. Was rejected for stabiilty reasons, working
on update 0.18.0 to resolve these. Tested on Google 6a, couldn't reproduce the problem. 

* update target SDK to 34

## v0.16.0

* update target SDK to 33
* removed sign config from project
* removed synthetic view imports
* moved to latest compat views
* remove whitespace for preferences
* removed older references from about view

## v0.15.0

* upgraded build tools (internal) to 28
* completed Kotlin migration (Rene Saarsoo)

## v0.14.0

* properly restore last fragment on resume (thanks to Rene Saarsoo or bitbucket user renku)
* Estonian translation added (thanks to Rene Saarsoo or bitbucket user renku)
* update default values to match previous frame (thanks to Rene Saarsoo or bitbucket user renku)

## v0.13.0

* use of default shutterspeed and aperture from settings on frame update (thanks to Rene Saarsoo or bitbucket user renku)
* some internal and formatting changes (thanks to Rene Saarsoo or bitbucket user renku)

## v0.12.0

* update formatting of exposed frames (thanks to Rene Saarsoo or bitbucket user renku)
* update on default values for non exposed frames (thanks to Rene Saarsoo or bitbucket user renku)
* added support for long exposures
* introduced kotlin (technical change only)
* chopped film notes on top of exposures (frames) view (will be animated in the future)

## v0.11.0

* android 6.0 runtime permissions support
* improved back stack handling for editing rolls
* support for custom tags

## v0.10.0

* implemented defaults for required fields
* (technical improvement) added butterknife for view binding using annotations

## v0.9.0

* implemented proper adapter for listviews

## v0.8.0

* example shot for frames added
* geo location for frames added
* improved action icons

## v0.7.0

* optimized back handling and search navigation
* linkified about information

## v0.6.0

* search/filter option implemented on overview of rolls

## v0.5.0

* format numbers to have leading zero on frames
* added film developed option as boolean + indication on overview

## v0.4.0

* add scrolling to forms to show all input behind keyboard
* close keyboard during navigation
* created about page
* enabled beta testing program + added link to about page

## v0.3.0

* dutch translation in store listing also
* no longer prefilling value if value is O on frame editing
* proper fragment resume if editing frames
* import/export option using intent
* bugfix for data input validation
* edit film and notes
* add autocomplete based on already added content

## v0.2.0

* bugfix for missing IDs
* translated to dutch
* minor GUI tweaks

## v 0.1.0

* initial release, open source project
* minimal requirements met (create film rolls and frames)

# Resources

getting images: http://developer.android.com/training/camera/photobasics.html

reading exif info from jpeg files: http://developer.android.com/reference/android/media/ExifInterface.html

Android design guidelines see: https://www.google.com/design/spec/layout/metrics-keylines.html#metrics-keylines-ratio-keylines

