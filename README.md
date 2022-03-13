<div>
<img src="https://img.shields.io/badge/Android-3DDC84?style=flat&logo=Android&logoColor=white" />
<img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=Kotlin&logoColor=white" />
<img src="https://img.shields.io/badge/writer-kym1924-yellow?&style=flat&logo=Android"/>
</div>

# Palette
Selecting Colors with the *Palette API*.
<br><br>
<img width=360 height=760 src="https://user-images.githubusercontent.com/63637706/157808607-b4d4175a-d4d9-4756-a272-b93c4f9150f8.png"/>

#### 1. Initialize
```groovy
// build.gradle in Module
implementation "androidx.palette:palette-ktx:1.0.0"
```
- The `Palette` library extracts important six colors from *Bitmap* images.
  It can be applied to visual elements in app.
<br>

#### 2. Palette.Builder
Create a `Palette.Builder` from a *Bitmap* using Palette's `from()` method.
```java
public final class Palette {
    ...
    @NonNull
    public static Builder from(@NonNull Bitmap bitmap) {
        return new Builder(bitmap);
    }
    ...
}
```
<br>

#### 2-1. Customize Palette
To customize *Palette*, use the following methods from the `Palette.Builder`.
- `addFilter()`
  - A filter provides a mechanism for exercising fine-grained control over which colors are valid.
  - This method adds a filter that indicates what colors are allowed in the resulting palette.
```java
public static final class Builder {
    ...
    private final List<Filter> mFilters = new ArrayList<>();
    ...
    @NonNull
    public Builder addFilter(Filter filter) {
    	if (filter != null) {
        	mFilters.add(filter);
        }
        return this;
    }
    ...
}
```

- `maximumColorCount()`
  - Sets the maximum number of colors in palette.
  - The default value is *16*.
  - The optimal value depends on the source bitmap image.
  - Optimal values of landscape images are between *8-16*.
  - Optimal values of face images are between *24-32*.
  - *The more count, the longer it takes to generate the palette*.
```java
public static final class Builder {
    ...
    private int mMaxColors = DEFAULT_CALCULATE_NUMBER_COLORS;
    ...
    @NonNull
    public Builder maximumColorCount(int colors) {
        mMaxColors = colors;
        return this;
    }
    ...
    static final int DEFAULT_CALCULATE_NUMBER_COLORS = 16;
}
```

- `setRegion()`
  - Indicates what area of the bitmap the builder uses when creating the palette.
  - It does not affect the original image.
```java
@NonNull
public Builder setRegion(@Px int left, @Px int top, @Px int right, @Px int bottom) {
    if (mBitmap != null) {
        if (mRegion == null) mRegion = new Rect();
        // Set the Rect to be initially the whole Bitmap
        mRegion.set(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        // Now just get the intersection with the region
        if (!mRegion.intersect(left, top, right, bottom)) {
            throw new IllegalArgumentException("The given region must intersect with "
                + "the Bitmap's dimensions.");
        }
    }
    return this;
}
```

- `addTarget()`
  - Can add targets to the *Builder*.
```java
@NonNull
public Builder addTarget(@NonNull final Target target) {
    if (!mTargets.contains(target)) {
        mTargets.add(target);
    }
    return this;
}
```
<br>

#### 3. Create a Palette instance
Create a *Palette* using the generated Builder's `generate()` method.
- The *Palette.Builder* can create the palette *synchronously*.
```kotlin
// Generate palette synchronously and return it
fun createPaletteSync(bitmap: Bitmap): Palette {
    val builder = Palette.from(bitmap)
    return builder.generate()
}
```

- Also, can create *asynchronously*.
```kotlin
// Generate palette asynchronously
fun createPaletteAsync(bitmap: Bitmap) {
    val builder = Palette.from(bitmap)
    builder.generate { palette ->
        // Use generated instance
    }
}
```
<br>

#### 3-1. Asynchronously
- Internally, the [*generate()*](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:palette/palette/src/main/java/androidx/palette/graphics/Palette.java;l=778) function is called asynchronously using AsyncTask.
  - In *doInBackground()* method.
  - If error occurred during creation, *Null* will be passed.
- The provided listener's *onGenerated()* method will be called with the palette when created.
  - In *onPostExecute()* method.
```java
@NonNull
public AsyncTask<Bitmap, Void, Palette> generate(
    @NonNull final PaletteAsyncListener listener) {
    if (listener == null) {
        throw new IllegalArgumentException("listener can not be null");
    }

    return new AsyncTask<Bitmap, Void, Palette>() {
        @Override
        @Nullable
        protected Palette doInBackground(Bitmap... params) {
            try {
                return generate();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Exception thrown during async generate", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(@Nullable Palette colorExtractor) {
            listener.onGenerated(colorExtractor);
        }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mBitmap);
}
```
<br>

#### 3-2. PaletteAsyncListener
- Called when the Palette has been created.
- If error occurred during creation, *Null* will be passed.
```java
public interface PaletteAsyncListener {
    void onGenerated(@Nullable Palette palette);
}
```
<br>

#### 4. Swatch
The Palette library can extract the following six color profiles. Each color profile can be *null*.
<br>
[See how each Target is created here.](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:palette/palette/src/main/java/androidx/palette/graphics/Target.java;l=89)
- `Light Vibrant`
  - A target which has the characteristics of a vibrant color which is light in luminance.
```java
public static final Target LIGHT_VIBRANT;
```
- `Vibrant`
  - A target which has the characteristics of a vibrant color which is neither light or dark.
```java
public static final Target VIBRANT;
```
- `Dark Vibrant`
  - A target which has the characteristics of a vibrant color which is dark in luminance.
```java
public static final Target DARK_VIBRANT;
```
- `Light Muted`
  - A target which has the characteristics of a muted color which is light in luminance.
```java
public static final Target LIGHT_MUTED;
```
- `Muted`
  - A target which has the characteristics of a muted color which is neither light or dark.
```java
public static final Target MUTED;
```
- `Dark Muted`
  - A target which has the characteristics of a muted color which is dark in luminance.
```java
public static final Target DARK_MUTED;
```
<br>

#### 4-1. Initialize Swatch
- Swatch has the following variables to get color information.
```java
public static final class Swatch {
    private final int mRed, mGreen, mBlue;
    private final int mRgb;
    private final int mPopulation;

    private boolean mGeneratedTextColors;
    private int mTitleTextColor;
    private int mBodyTextColor;
    ...
}
```

- *mRgb* is initialized in the constructor.
```java
public Swatch(@ColorInt int color, int population) {
    ...
    mRgb = color;
    ...
}

Swatch(int red, int green, int blue, int population) {
    ...
    mRgb = Color.rgb(red, green, blue);
    ...
}
```

- *mTitleTextColor* and *mBodyTextColor* are initialized when when getter methods are called.
```java
@ColorInt
public int getTitleTextColor() {
    ensureTextColorsGenerated();
    return mTitleTextColor;
}
@ColorInt
public int getBodyTextColor() {
    ensureTextColorsGenerated();
    return mBodyTextColor;
}
```

- ensureTextColorsGenerated()
  - Check *mGeneratedTextColors* to determine if it has been initialized.
```java
private void ensureTextColorsGenerated() {
    if (!mGeneratedTextColors) {
        // First check white, as most colors will be dark
        final int lightBodyAlpha = ColorUtils.calculateMinimumAlpha(
            Color.WHITE, mRgb, MIN_CONTRAST_BODY_TEXT);
        final int lightTitleAlpha = ColorUtils.calculateMinimumAlpha(
            Color.WHITE, mRgb, MIN_CONTRAST_TITLE_TEXT);

        if (lightBodyAlpha != -1 && lightTitleAlpha != -1) {
            // If we found valid light values, use them and return
            mBodyTextColor = ColorUtils.setAlphaComponent(Color.WHITE, lightBodyAlpha);
            mTitleTextColor = ColorUtils.setAlphaComponent(Color.WHITE, lightTitleAlpha);
            mGeneratedTextColors = true;
            return;
        }

        final int darkBodyAlpha = ColorUtils.calculateMinimumAlpha(
            Color.BLACK, mRgb, MIN_CONTRAST_BODY_TEXT);
        final int darkTitleAlpha = ColorUtils.calculateMinimumAlpha(
            Color.BLACK, mRgb, MIN_CONTRAST_TITLE_TEXT);

        if (darkBodyAlpha != -1 && darkTitleAlpha != -1) {
            // If we found valid dark values, use them and return
            mBodyTextColor = ColorUtils.setAlphaComponent(Color.BLACK, darkBodyAlpha);
            mTitleTextColor = ColorUtils.setAlphaComponent(Color.BLACK, darkTitleAlpha);
            mGeneratedTextColors = true;
            return;
        }

        // If we reach here then we can not find title and body values which use the same
        // lightness, we need to use mismatched values
        mBodyTextColor = lightBodyAlpha != -1 ?
            ColorUtils.setAlphaComponent(Color.WHITE, lightBodyAlpha)
            : ColorUtils.setAlphaComponent(Color.BLACK, darkBodyAlpha);
        mTitleTextColor = lightTitleAlpha != -1 ?
            ColorUtils.setAlphaComponent(Color.WHITE, lightTitleAlpha)
            : ColorUtils.setAlphaComponent(Color.BLACK, darkTitleAlpha);
        mGeneratedTextColors = true;
    }
}
```
<br>

#### 4-2. Swatch Method
There are methods for getting information color profile.
- `getRgb()`
  - Return this swatch's *RGB* color value.
```java
@ColorInt
public int getRgb() { return mRgb; }
```
- `getTitleTextColor()`
  - Return an appropriate color to use for any *title* text.
  - This color is guaranteed to have sufficient contrast.
- `getBodyTextColor()`
  - Return an appropriate color to use for any *body* text.
  - This color is guaranteed to have sufficient contrast.
<br>

##### Reference

- https://developer.android.com/training/material/palette-colors
- https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:palette/palette/src/main/java/androidx/palette/graphics/Palette.java
- https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:palette/palette/src/main/java/androidx/palette/graphics/Target.java
- https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:palette/palette/src/main/java/androidx/palette/graphics/ColorCutQuantizer.java
- https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:core/core/src/main/java/androidx/core/graphics/ColorUtils.java
