package tw.realtime.project.pathanimationtest;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by vexonelite on 2017/01/10.
 *
 *
 * @see <a href="http://blogs.sitepointstatic.com/examples/tech/canvas-curves/bezier-curve.html">Bezier Curve</a>
 * <p></p>
 * @see <a href="https://gist.github.com/romannurik/882650">Custom ViewGroup Reference 1</a>
 * @see <a href="http://stacktips.com/tutorials/android/how-to-create-custom-layout-in-android-by-extending-viewgroup-class">Custom ViewGroup Reference 2</a>
 * <p></p>
 * @see <a href="http://stackoverflow.com/questions/25929820/how-to-calculate-position-on-a-circle-with-a-certain-angle">Node Position on a circle</a>
 * @see <a href="http://www.mathopenref.com/ellipse.html">Node Position on a ellipse</a>
 * <p></p>
 * @see <a href="http://stackoverflow.com/questions/28901744/in-android-how-to-use-objectanimator-to-move-to-point-x-along-a-curve">Path animation Reference 1</a>
 * @see <a href="http://android-coding.blogspot.tw/2015/02/objectanimator-to-animate-coordinates.html">Path animation Reference 2</a>
 * @see <a href="http://stackoverflow.com/questions/21002795/draw-and-fill-custom-shape">Path animation Reference 3</a>
 * @see <a href="http://stackoverflow.com/questions/39612318/objectanimator-on-path-not-working-on-marshmallow">Path animation Reference 4</a>
 * @see <a href="http://stackoverflow.com/questions/6154370/android-move-object-along-a-path">Path animation Reference 5</a>
 */
public class PathMotionMenuLayout extends ViewGroup {

    private RectF mSourcePoint;
    private List<RectF> mDestinationRectList;

    private ImageView mMainButton;
    private List<ImageView> mImageViewHolder;

    private int mChildCount = 4;
    private long mDuration;
    private Interpolator mInterpolator;
    private float mRadius;
    private boolean hasOrbiterClickEffect;
    private OrbiterClickListener mCallback;

    private boolean isExpanded;
    private boolean buttonLock;


    private static String getLogTag () {
        return PathMotionMenuLayout.class.getSimpleName();
    }

    private float getDensity () {
        return getContext().getResources().getDisplayMetrics().density;
    }


    public PathMotionMenuLayout(Context context) {
        super(context);
        init(context);
    }

    public PathMotionMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PathMotionMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }



    private void init (Context context) {
        mDuration = 350;
        //mInterpolator = new LinearInterpolator();
        //mInterpolator = new OvershootInterpolator();
        mInterpolator = new AccelerateDecelerateInterpolator();
        mRadius = context.getResources().getDisplayMetrics().density * 100f;
        addMainButton(context);
        //addIconImageViews(context);
    }

    private void addMainButton (Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mMainButton = (ImageView) inflater.inflate(
                R.layout.path_motion_menu_main_button, PathMotionMenuLayout.this, false);
        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addView(mMainButton, params);
        mMainButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonLock) {
                    return;
                }

                view.setEnabled(false);

                constructAndPlayAnimation();

                view.setEnabled(true);
            }
        });
    }

    private void addIconImageViews (Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if (mImageViewHolder == null) {
            mImageViewHolder = new ArrayList<>();
        }
        else {
            mImageViewHolder.clear();
        }

        for (int i = 0; i < mChildCount; i++) {
            ImageView imageView = (ImageView) inflater.inflate(
                    R.layout.path_motion_menu_orbiter, PathMotionMenuLayout.this, false);
            imageView.setOnClickListener(new OrbiterClicker(i));
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            addView(imageView, params);
            mImageViewHolder.add(imageView);
        }
    }



    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if ( (null == mMainButton) || (null == mImageViewHolder) || (mImageViewHolder.isEmpty()) ) {
            return;
        }

        final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);
        final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST);

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
//            if (child.getVisibility() == GONE) {
//                continue;
//            }
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }

        float mainSize = ((float) mMainButton.getMeasuredWidth()) / 2f;
        ImageView imageView = mImageViewHolder.get(0);
        float ringSize = (float) imageView.getMeasuredWidth();
        float rationX = 1f; // 5f;
        int measureWidth = (int)(mRadius + mainSize + (rationX * ringSize) );
        float rationY = 1f; // 5f;
        int measureHeight = (int)(mRadius + mainSize + (rationY * ringSize) );
        //Log.i(getLogTag(), "onMeasure - mainSize: " + mainSize + ", ringSize: " + ringSize);
        //Log.i(getLogTag(), "onMeasure - measureWidth: " + measureWidth + ", measureHeight: " + measureHeight);
        setMeasuredDimension(
                resolveSize(measureWidth, widthMeasureSpec),
                resolveSize(measureHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.i(getLogTag(), "onLayout - changed: " + changed + ", left: " + left + ", top: " + top + ", right: " + right + ", bottom: " + bottom);

        PointF mainButtonCenter = layoutMainButton(getMeasuredWidth(), getMeasuredHeight());
        layoutImageViews(changed, mainButtonCenter);
        invalidate();
    }

    private PointF layoutMainButton (final int viewGroupWidth, final int viewGroupHeight) {
        if (null == mMainButton) {
            return new PointF(0f, 0f);
        }
        Log.i(getLogTag(), "onLayout - viewGroupWidth: " + viewGroupWidth + ", viewGroupHeight: " + viewGroupHeight);

        float measuredWidth = (float) mMainButton.getMeasuredWidth();
        float measureHeight = (float) mMainButton.getMeasuredHeight();
        Log.i(getLogTag(), "onLayout - mMainButton - measuredWidth: " + measuredWidth + ", measureHeight: " + measureHeight);

        int leftMain = viewGroupWidth - ((int)measuredWidth);
        int topMain = (int)(viewGroupHeight - measureHeight);

        mMainButton.layout(leftMain, topMain, viewGroupWidth, viewGroupHeight);
        Log.i(getLogTag(), "onLayout - mMainButton: " + leftMain + ", " + viewGroupWidth + ", " + topMain + ", " + viewGroupHeight);

        float mainCenterX = ((float) leftMain) + (measuredWidth / 2f);
        float mainCenterY = ((float) topMain) + (measureHeight / 2f);
        Log.i(getLogTag(), "onLayout - mainCenterX: " + mainCenterX + ", mainCenterY: " + mainCenterY);

        return new PointF(mainCenterX, mainCenterY);
    }

    // calculate the destination RectF for each image and save it in the mDestinationRectList
    private void layoutImageViews (final boolean changed, final PointF mainButtonCenter) {
        if ( (null == mImageViewHolder) || (mImageViewHolder.isEmpty()) ||
                    (null == mainButtonCenter) || (mainButtonCenter.x <= 0f) ||
                            (mainButtonCenter.y <= 0f) ) {
            return;
        }

        ImageView imageView1 = mImageViewHolder.get(0);
        float measuredWidth = (float) imageView1.getMeasuredWidth();
        float measureHeight = (float) imageView1.getMeasuredHeight();
        Log.i(getLogTag(), "onLayout - imageView1 - measuredWidth: " + measuredWidth + ", measureHeight: " + measureHeight);

        double refAngle = 180d;
        double refCenterX = mainButtonCenter.x + mRadius * Math.cos(Math.toRadians(refAngle));
        double refCenterY = mainButtonCenter.y + mRadius * Math.sin(Math.toRadians(refAngle));
        double ratioY = 1.5d;
        refCenterY = refCenterY + (ratioY * measureHeight);

        int refLeft = (int)(refCenterX - (measuredWidth / 2f));
        int refTop = (int)(refCenterY - (measureHeight / 2f));
        int refRight = (int)(refCenterX + (measuredWidth / 2f));
        int refBottom = (int)(refCenterY + (measureHeight / 2f));

        if (!changed) {
            if (null == mSourcePoint) {
                mSourcePoint = new RectF();
            }
            if (null == mDestinationRectList) {
                mDestinationRectList = new ArrayList<>();
            }
            else {
                mDestinationRectList.clear();
            }

            mSourcePoint.set(refLeft, refTop, refRight, refBottom);
            Log.i(getLogTag(), "onLayout - mSourcePoint: " + mSourcePoint);
        }

        // ver1
        //double angleDifference = 90d / 10d;
        //double baseAngle = 180d + (angleDifference / 2d) ;
        // ver2
//        double angleDifference = (90d / ((double)(mChildCount + 1))) / ((double)mChildCount);
//        double baseAngle = 180d + (((double)(mChildCount - 1)) * angleDifference);
//        if (mChildCount > 3) {
//            baseAngle = baseAngle - angleDifference;
//        }
        // ver3
        double angleA = 90d / ((double)(mChildCount + 1));
        double angleDifference = (90d - angleA) / ((double)(mChildCount - 1));
        double baseAngle = 180d + (angleA / 2d);
        Log.i(getLogTag(), "onLayout - angleDifference: " + angleDifference + ", baseAngle: " + baseAngle);
        int i = 0;
        for (ImageView imageView : mImageViewHolder) {
            // ver1
            //double angle = baseAngle + (i * 3d * angleDifference);
            // ver2
            //double angle = baseAngle + (i * ((double)(mChildCount + 1)) * angleDifference);
            // ver3
            double angle = baseAngle + (i * angleDifference);
            double centerX = mainButtonCenter.x + mRadius * Math.cos(Math.toRadians(angle));
            double centerY = mainButtonCenter.y + mRadius * Math.sin(Math.toRadians(angle));
            Log.i(getLogTag(), "onLayout - imageView(" + i + ") - angle: " + angle + ", centerX: " + centerX + ", centerY: " + centerY);

            measuredWidth = (float) imageView.getMeasuredWidth();
            measureHeight = (float) imageView.getMeasuredHeight();
            Log.i(getLogTag(), "onLayout - imageView(" + i + ") - measuredWidth: " + measuredWidth + ", measureHeight: " + measureHeight);
            if (!changed) {
                int leftChild = (int)(centerX - (measuredWidth / 2f));
                int topChild = (int)(centerY - (measureHeight / 2f));
                int rightChild = (int)(centerX + (measuredWidth / 2f));
                int bottomChild = (int)(centerY + (measureHeight / 2f));
                RectF rectF = new RectF(leftChild, topChild, rightChild, bottomChild);
                mDestinationRectList.add(rectF);
                Log.i(getLogTag(), "onLayout - imageView(" + i + "): " + rectF);
            }

            //imageView.layout(leftChild, topChild, rightChild, bottomChild);
            imageView.layout(refLeft, refTop, refRight, refBottom);
            imageView.invalidate();

            i = i + 1;
        }
    }


    /**
     * @param target The View that you want to remove its GlobalLayoutListener.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static void removeOnGlobalLayoutEvent(View target, ViewTreeObserver.OnGlobalLayoutListener listener){
        if (Build.VERSION.SDK_INT < 16) {
            target.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            target.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }



    private void constructAndPlayAnimation () {
        if ( (null == mImageViewHolder) || (mImageViewHolder.isEmpty()) ||
                    (null == mDestinationRectList) || (mDestinationRectList.isEmpty()) ||
                    (null == mSourcePoint) ) {
            return;
        }
        //Log.i(getLogTag(), "constructAnimation");

        ArrayList<ValueAnimator> animatorList = new ArrayList<>();

        for (int i = 0; i < mImageViewHolder.size(); i++) {
            ImageView imageView = mImageViewHolder.get(i);
            RectF destRectF = mDestinationRectList.get(i);

            ValueAnimator pathAnimator = createPathAnimator (imageView, destRectF, isExpanded);
            pathAnimator.setDuration(mDuration);
            pathAnimator.setInterpolator(mInterpolator);
            animatorList.add(pathAnimator);

            float rotationAngle = 360f * (i + 1);
            ValueAnimator rotationAnimator = ObjectAnimator.ofFloat(imageView , "rotation", 0f, rotationAngle);
            rotationAnimator.setDuration(mDuration);
            rotationAnimator.setInterpolator(mInterpolator);
            animatorList.add(rotationAnimator);
        }

        ValueAnimator[] animatorArray = animatorList.toArray(new ValueAnimator[animatorList.size()]);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorArray);

        Animator.AnimatorListener callback = (isExpanded)
                ? new ShrinkageAnimationCallback() : new ExpansionAnimationCallback();
        animatorSet.addListener(callback);
        animatorSet.start();
    }

    private ValueAnimator createPathAnimator (ImageView imageView, RectF destRectF, boolean isExpanded) {

        float diffX = Math.abs(destRectF.left - mSourcePoint.left);
        Log.i(getLogTag(), "createPathAnimator - diffX: " + diffX);
        if (diffX > 0f) {
            diffX = diffX / 2f;
            Log.i(getLogTag(), "createPathAnimator - diffX / 2: " + diffX);
        }
        PointF controlPoint1 = new PointF(destRectF.left - diffX, destRectF.top);

        float diffY = Math.abs(destRectF.top - mSourcePoint.top);
        Log.i(getLogTag(), "createPathAnimator - diffY: " + diffY);
        if (diffY > 0f) {
            diffY = diffY / 2f;
            Log.i(getLogTag(), "createPathAnimator - diffY / 2: " + diffY);
        }
        PointF controlPoint2 = new PointF(mSourcePoint.left, mSourcePoint.top - diffY);

        Path path = new Path();
        if (isExpanded) {
            path.moveTo(destRectF.left, destRectF.top);
            path.cubicTo(   controlPoint1.x, controlPoint1.y,
                    controlPoint2.x, controlPoint2.y,
                    mSourcePoint.left, mSourcePoint.top);
            Log.i(getLogTag(), "createPathAnimator - controlPoint1: " + controlPoint1 +
                    ", controlPoint2: " + controlPoint2 + ", endPoint: " + mSourcePoint.left + ", " + mSourcePoint.top);
        }
        else {
            path.moveTo(mSourcePoint.left, mSourcePoint.top);
            path.cubicTo(   controlPoint1.x, controlPoint1.y,
                    controlPoint2.x, controlPoint2.y,
                    destRectF.left, destRectF.top);
            Log.i(getLogTag(), "createPathAnimator - controlPoint1: " + controlPoint1 +
                    ", controlPoint2: " + controlPoint2 + ", endPoint: " + destRectF.left + ", " + destRectF.top);
        }

        ValueAnimator pathAnimator;
        if (Build.VERSION.SDK_INT >= 21) {
            pathAnimator = ObjectAnimator.ofFloat(imageView, View.X, View.Y, path);
        }
        else {
            // Animates a float value from 0 to 1
            pathAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);

            // This listener onAnimationUpdate will be called during every step in the animation
            // Gets called every millisecond in my observation
            pathAnimator.addUpdateListener(new PathAnimatorUpdateCallback(imageView, path));
        }

        return pathAnimator;
    }

    private class PathAnimatorUpdateCallback implements ValueAnimator.AnimatorUpdateListener {

        private float[] mPoint = new float[2];
        private Path mPath;
        private ImageView mImageView;

        private PathAnimatorUpdateCallback (ImageView imageView, Path path) {
            mPath = path;
            mImageView = imageView;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            // Gets the animated float fraction
            float fraction = animation.getAnimatedFraction();

            // Gets the point at the fractional path length
            PathMeasure pathMeasure = new PathMeasure(mPath, true);
            pathMeasure.getPosTan(pathMeasure.getLength() * fraction, mPoint, null);

            // Sets view location to the above point
            mImageView.setX(mPoint[0]);
            mImageView.setY(mPoint[1]);
        }
    }

    private class DefaultAnimationCallback implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animator) {
            buttonLock = true;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            //Log.i(getLogTag(), "DefaultAnimationCallback - onAnimationEnd");
            buttonLock = false;
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }

    private class ExpansionAnimationCallback extends DefaultAnimationCallback {
        @Override
        public void onAnimationEnd(Animator animator) {
            super.onAnimationEnd(animator);
            isExpanded = true;
        }
    }

    private class ShrinkageAnimationCallback extends DefaultAnimationCallback {
        @Override
        public void onAnimationEnd(Animator animator) {
            super.onAnimationEnd(animator);
            isExpanded = false;
        }
    }

    /**
     *  Prepare an Animation set for the set of orbiter buttons.
     *  The Animation set mainly includes the Scale and Alpha animations.
     *  Under the state of Reset, it would includes the path animations.
     *
     *  @param viewClicked	The View that was just clicked by users
     *  @param isToReset 	Indicate the Animation set is either the clicked or reset
     *	@param period 		The time period at which the Animation set takes places
     */
    private AnimatorSet createOrbitersClickResetAnimation (final View viewClicked,
                                                           final boolean isToReset,
                                                           final long period) {

        long duration;
        Interpolator interpolator;
        if (!isToReset) {
            interpolator = new DecelerateInterpolator();
        }
        else {
            interpolator = new AccelerateInterpolator();
        }

        ArrayList<ValueAnimator> compoundAnimatorList = new ArrayList<>();
        ArrayList<ValueAnimator> alphaAnimatorList = new ArrayList<>();

		/* Begin of "For-each Loop" ***********************************************/
        for (ImageView imageView : mImageViewHolder) {

            int imageIndex = mImageViewHolder.indexOf(imageView);
            int clickIndex = mImageViewHolder.indexOf( (ImageView) viewClicked);

            final boolean isClicked = (imageIndex == clickIndex);

            ValueAnimator xScaleAnimator;
            ValueAnimator yScaleAnimator;
            ValueAnimator alphaAnimator;
            ValueAnimator pathAnimator = null;

		    /* Begin of "if (!isToReset)" *********************************************/
            if (!isToReset) { // for the case of Click animation

                if (isClicked) { // for the View that was just clicked by users
                    duration = period;
                    // scale up (enlarge) animations for both x-axis and y-axis
                    xScaleAnimator = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 2f);
                    yScaleAnimator = ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 2f);

                } else { // for the rest of Views
                    // scale down (shrink) animations for both x-axis and y-axis
                    xScaleAnimator = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 0f);
                    yScaleAnimator = ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 0f);
                    duration = period - 100L;
                }
                // disappear animation
                alphaAnimator = ObjectAnimator.ofFloat(imageView, "alpha", 1f, 0f);

            }
            else { // for the case of Reset animations

                duration = period / 2L;

                if (isClicked) { // for the View that was just clicked by users
                    // scale down (restoration) animations for both x-axis and y-axis
                    xScaleAnimator = ObjectAnimator.ofFloat(imageView, "scaleX", 2f, 1f);
                    yScaleAnimator = ObjectAnimator.ofFloat(imageView, "scaleY", 2f, 1f);
                }
                else { // for the rest of Views
                    // scale up (restoration) animations for both x-axis and y-axis
                    xScaleAnimator = ObjectAnimator.ofFloat(imageView, "scaleX", 0f, 1f);
                    yScaleAnimator = ObjectAnimator.ofFloat(imageView, "scaleY", 0f, 1f);
                }

                // re-appear animation
                alphaAnimator = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f);

                RectF destRectF = mDestinationRectList.get(imageIndex);
                pathAnimator = createPathAnimator(imageView, destRectF, true);
            }
		    /* End of "if (!isToReset)" ***********************************************/

            xScaleAnimator.setDuration(duration);
            yScaleAnimator.setDuration(duration);
            alphaAnimator.setDuration(duration);
            xScaleAnimator.setInterpolator(interpolator);
            yScaleAnimator.setInterpolator(interpolator);
            alphaAnimator.setInterpolator(interpolator);

            compoundAnimatorList.add(xScaleAnimator);
            compoundAnimatorList.add(yScaleAnimator);

            if (null != pathAnimator) { // for the case of Reset animations
                pathAnimator.setDuration(duration);
                pathAnimator.setInterpolator(interpolator);
                compoundAnimatorList.add(pathAnimator);
                alphaAnimatorList.add(alphaAnimator);
            }
            else { // for the case of Click animation
                compoundAnimatorList.add(alphaAnimator);
            }
        }
		/* End of "For-each Loop" *************************************************/

        ValueAnimator[] compoundAnimatorArray = compoundAnimatorList.toArray(
                new ValueAnimator[compoundAnimatorList.size()]);
        AnimatorSet animatorSet = new AnimatorSet();

        if (!isToReset) { // for the case of Click animation
            animatorSet.playTogether(compoundAnimatorArray);
        }
        else { // for the case of Reset animations

            ValueAnimator[] alphaAnimatorArray = alphaAnimatorList.toArray(
                    new ValueAnimator[alphaAnimatorList.size()]);

            AnimatorSet alphaAnimatorSet = new AnimatorSet();
            alphaAnimatorSet.playTogether(alphaAnimatorArray);

            AnimatorSet scaleAndPathAnimatorSet = new AnimatorSet();
            scaleAndPathAnimatorSet.playTogether(compoundAnimatorArray);

            // make scaleAndTransAnimeSet and alpahAnimeSet play in order
            animatorSet.playSequentially(scaleAndPathAnimatorSet, alphaAnimatorSet);
        }

        return animatorSet;
    }

    /**
     *
     *  @param viewClicked	The View that was just clicked by users
     */
    private AnimatorSet bindOrbiterClickAnimatorSet (final View viewClicked) {
        final AnimatorSet resetAnimatorSet = createOrbitersClickResetAnimation (viewClicked, true, 100);
        resetAnimatorSet.addListener(new ShrinkageAnimationCallback());
        final AnimatorSet clickAnimatorSet = createOrbitersClickResetAnimation( viewClicked, false, 400);
        clickAnimatorSet.addListener( new OrbiterClickAnimationCallback(resetAnimatorSet));
        return clickAnimatorSet;
    }

    private class OrbiterClickAnimationCallback extends DefaultAnimationCallback {

        final AnimatorSet oAnimatorSet;

        private OrbiterClickAnimationCallback (AnimatorSet animatorSet) {
            oAnimatorSet = animatorSet;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (null != oAnimatorSet) {
                oAnimatorSet.start();
            }
        }
    }


    public void setParameters (Parameters parameters) {
        if (null == parameters) {
            return;
        }
        mChildCount = parameters.pChildCount;
        mDuration = parameters.pDuration;
        mInterpolator = parameters.pInterpolator;
        mRadius = getDensity() * parameters.pRadius;
        hasOrbiterClickEffect = parameters.hasOrbiterClickEffect;
        mCallback = parameters.pCallback;

        if ( (null != mMainButton) && (parameters.mMainButtonResId != Integer.MIN_VALUE) ) {
            try {
                mMainButton.setImageResource(parameters.mMainButtonResId);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        addIconImageViews(getContext());

        if ( (null != mImageViewHolder) && (!mImageViewHolder.isEmpty()) &&
                            (null != parameters.mOrbiterResourceIdList) ) {
            for (int i = 0; i < mImageViewHolder.size(); i++) {
                try {
                    ImageView imageView = mImageViewHolder.get(i);
                    Integer resId = parameters.mOrbiterResourceIdList.get(i);
                    imageView.setImageResource(resId);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        requestLayout();
        invalidate();
    }

    public static class Parameters {
        private int pChildCount = 4;
        private long pDuration;
        private Interpolator pInterpolator;
        private float pRadius;
        private boolean hasOrbiterClickEffect = true;
        private OrbiterClickListener pCallback;
        private int mMainButtonResId = Integer.MIN_VALUE;
        private List<Integer> mOrbiterResourceIdList;

        public Parameters () {
            pDuration = 350;
            //mInterpolator = new OvershootInterpolator();
            pInterpolator = new AccelerateDecelerateInterpolator();
            pRadius = 100f;
        }

        /**
         *
         * @param radius unit: dp
         */
        public Parameters setLayoutRadius (int radius) {
            if (radius > 0) {
                pRadius = radius;
            }
            return this;
        }

        public Parameters setDuration (long duration) {
            if (duration > 0) {
                pDuration = duration;
            }
            return this;
        }

        public Parameters setInterpolator (Interpolator interpolator) {
            if (null != interpolator) {
                pInterpolator = interpolator;
            }
            return this;
        }

        /**
         *
         * @param childCount range from 2 to 6
         * @return
         */
        public Parameters setChildCount (int childCount) {
            if ( (childCount > 1) && (childCount < 7) ) {
                pChildCount = childCount;
            }
            return this;
        }


        public Parameters setOrbiterClickEffectFlag (boolean flag) {
            hasOrbiterClickEffect = flag;
            return this;
        }

        public Parameters setOrbiterClickListener (OrbiterClickListener callback) {
            pCallback = callback;
            return this;
        }

        public Parameters setMainButtonResourceId (int resourceId) {
            mMainButtonResId = resourceId;
            return this;
        }

        public Parameters setOrbiterResourceIdList (List<Integer> resourceIdList) {
            if ( (null != resourceIdList) && (resourceIdList.size() == 4) ) {
                mOrbiterResourceIdList = resourceIdList;
            }
            return this;
        }
    }

    public interface OrbiterClickListener {
        void onOrbiterClicked (int position);
    }

    private class OrbiterClicker implements View.OnClickListener {
        private int mPosition;

        protected OrbiterClicker(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View view) {
            if (buttonLock) {
                return;
            }
            view.setEnabled(false);

            if (hasOrbiterClickEffect) {
                bindOrbiterClickAnimatorSet(view).start();
            }
            else {
                constructAndPlayAnimation();
            }

            if (null != mCallback) {
                mCallback.onOrbiterClicked(mPosition);
            }

            view.setEnabled(true);
        }
    }
}
