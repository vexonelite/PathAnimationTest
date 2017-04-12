package tw.realtime.project.pathanimationtest;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by vexonelite on 2017/04/11.
 * <p>
 * @see <a href="http://blogs.sitepointstatic.com/examples/tech/canvas-curves/bezier-curve.html">Bezier Curve</a>
 * <p>
 * @see <a href="https://gist.github.com/romannurik/882650">Custom ViewGroup Reference 1</a>
 * @see <a href="http://stacktips.com/tutorials/android/how-to-create-custom-layout-in-android-by-extending-viewgroup-class">Custom ViewGroup Reference 2</a>
 * <p>
 * @see <a href="http://stackoverflow.com/questions/25929820/how-to-calculate-position-on-a-circle-with-a-certain-angle">Node Position on a circle</a>
 * @see <a href="http://www.mathopenref.com/ellipse.html">Node Position on a ellipse</a>
 * <p>
 */
public class RotationMenuLayout extends ViewGroup {

    private ImageView mMainButton;
    private List<ViewGroup> mImageViewHolder;

    private int mChildCount = 4;
    private long mDuration;
    private float mRadius;
    private OrbiterClickListener mCallback;

    private boolean isExpanded;
    private boolean buttonLock;


    private static String getLogTag () {
        return RotationMenuLayout.class.getSimpleName();
    }

    private float getDensity () {
        return getContext().getResources().getDisplayMetrics().density;
    }


    public RotationMenuLayout(Context context) {
        super(context);
        init(context);
    }

    public RotationMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RotationMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }



    private void init (Context context) {
        mDuration = 350;
        mRadius = context.getResources().getDisplayMetrics().density * 100f;
        addMainButton(context);
        addIconImageViews(context);
    }

    private void addMainButton (Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mMainButton = (ImageView) inflater.inflate(
                R.layout.path_motion_menu_main_button, RotationMenuLayout.this, false);
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
            ViewGroup viewGroup = (ViewGroup) inflater.inflate(
                    R.layout.rotation_menu_orbiter, RotationMenuLayout.this, false);
            viewGroup.setOnClickListener(new OrbiterClicker(i));
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            addView(viewGroup, params);
            mImageViewHolder.add(viewGroup);
        }
    }

    public void setMainButtonImageResource(int resourceId) {
        if (null != mMainButton) {
            try {
                mMainButton.setImageResource(resourceId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setSubButtonImageResource(int position, int resourceId) {
        if ((null != mImageViewHolder) && (!mImageViewHolder.isEmpty()) && (null != mImageViewHolder.get(position))) {
            try {
                ViewGroup viewGroup = mImageViewHolder.get(position);
                ImageView imageView = (ImageView) viewGroup.findViewById(R.id.iconView);
                imageView.setImageResource(resourceId);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void closeMenu() {
        constructAndPlayAnimation();
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

        // size of main button
        float mainSize = ((float) mMainButton.getMeasuredWidth()) / 2f;
        ViewGroup viewGroup = mImageViewHolder.get(0);
        // size of orbiter button
        float ringSize = (float) viewGroup.getMeasuredWidth();
        float rationX = 1f; //5f;
        int measureWidth = (int)(mRadius + mainSize + (rationX * ringSize) );
        float rationY = 1f; //5f;
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

        mMainButton.layout(
                leftMain,
                topMain,
                leftMain + mMainButton.getMeasuredWidth(),
                topMain + mMainButton.getMeasuredHeight());
        Log.i(getLogTag(), "onLayout - mMainButton: " + leftMain + ", " + mMainButton.getMeasuredWidth()
                + ", " + topMain + ", " + topMain + mMainButton.getMeasuredHeight());

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

        ViewGroup viewGroup1 = mImageViewHolder.get(0);
        float measuredWidth = (float) viewGroup1.getMeasuredWidth();
        float measureHeight = (float) viewGroup1.getMeasuredHeight();
        Log.i(getLogTag(), "onLayout - viewGroup1 - measuredWidth: " + measuredWidth + ", measureHeight: " + measureHeight);

        double refAngle = 180d;
        double refCenterX = mainButtonCenter.x + mRadius * Math.cos(Math.toRadians(refAngle));
        double refCenterY = mainButtonCenter.y + mRadius * Math.sin(Math.toRadians(refAngle));
        double ratioY = 1.5d;
        refCenterY = refCenterY + (ratioY * measureHeight);
        int differenceX = (int)(mRadius * Math.cos(Math.toRadians(refAngle)) );

        int refLeft = (int)(refCenterX - (measuredWidth / 2f));
        int refTop = (int)(refCenterY - (measureHeight / 2f));
        int refRight = (int)(refCenterX + (measuredWidth / 2f));
        int refBottom = (int)(refCenterY + (measureHeight / 2f));

        for (ViewGroup viewGroup : mImageViewHolder) {
            viewGroup.layout(refLeft, refTop, refRight, refBottom);
            viewGroup.invalidate();

            if (changed) {
                Log.i(getLogTag(), "onLayout - getPivotX [1]: " + viewGroup.getPivotX() + ", getPivotY: " + viewGroup.getPivotY());
                viewGroup.setPivotX(viewGroup.getPivotX() - differenceX);
                Log.i(getLogTag(), "onLayout - getPivotX [2]: " + viewGroup.getPivotX() + ", getPivotY: " + viewGroup.getPivotY());
            }
        }
    }


    private void constructAndPlayAnimation () {
        ArrayList<ValueAnimator> phase1AnimatorList = new ArrayList<>();
        ArrayList<ValueAnimator> phase2AnimatorList = new ArrayList<>();
        Interpolator phase1Interpolator = new AccelerateInterpolator();
        Interpolator phase2Interpolator = new AccelerateDecelerateInterpolator();

        float angleA = 90f / ((float)mChildCount);
        float baseAngle = angleA / ((float)(mChildCount + 1));

        long translateDuration = mDuration / 4;
        long rotationDuration = translateDuration * 3;

        for (int i = 0; i < mImageViewHolder.size(); i++) {
            ViewGroup viewGroup = mImageViewHolder.get(i);

            float angle = baseAngle + i * (angleA + baseAngle);
            float parentStartAngle = isExpanded ? angle : 0f;
            float parentEndAngle = isExpanded ? 0f : angle;
            float childStartAngle = isExpanded ? -angle : 0f;
            float childEndAngle = isExpanded ? 0f : -angle;

            ValueAnimator parentRotationAnimator = ObjectAnimator.ofFloat(
                    viewGroup , "rotation", parentStartAngle, parentEndAngle);
            parentRotationAnimator.setDuration(rotationDuration);

            ImageView imageView = (ImageView) viewGroup.findViewById(R.id.iconView);
            ValueAnimator childRotationAnimator = ObjectAnimator.ofFloat(
                    imageView , "rotation", childStartAngle, childEndAngle);
            childRotationAnimator.setDuration(rotationDuration);

            float measureHeight = (float) imageView.getMeasuredHeight();
            float ratioY = 1.5f;
            float translationY = ratioY * measureHeight;
            float startPoint = isExpanded ? -translationY : 0f;
            float endPoint = isExpanded ? 0f : -translationY;

            ValueAnimator transYAnimator = ObjectAnimator.ofFloat(
                    viewGroup, "translationY", startPoint, endPoint);
            transYAnimator.setDuration(translateDuration);

            if (isExpanded) {
                parentRotationAnimator.setInterpolator(phase1Interpolator);
                childRotationAnimator.setInterpolator(phase1Interpolator);
                phase1AnimatorList.add(parentRotationAnimator);
                phase1AnimatorList.add(childRotationAnimator);

                transYAnimator.setInterpolator(phase2Interpolator);
                phase2AnimatorList.add(transYAnimator);
            }
            else {
                transYAnimator.setInterpolator(phase1Interpolator);
                phase1AnimatorList.add(transYAnimator);

                parentRotationAnimator.setInterpolator(phase2Interpolator);
                childRotationAnimator.setInterpolator(phase2Interpolator);
                phase2AnimatorList.add(parentRotationAnimator);
                phase2AnimatorList.add(childRotationAnimator);
            }
        }

        ValueAnimator[] phase2AnimatorArray = phase2AnimatorList.toArray(new ValueAnimator[phase2AnimatorList.size()]);
        AnimatorSet phase2AnimatorSet = new AnimatorSet();
        phase2AnimatorSet.playTogether(phase2AnimatorArray);

        ValueAnimator[] phase1AnimatorArray = phase1AnimatorList.toArray(new ValueAnimator[phase1AnimatorList.size()]);
        AnimatorSet phase1AnimatorSet = new AnimatorSet();
        phase1AnimatorSet.playTogether(phase1AnimatorArray);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(phase1AnimatorSet, phase2AnimatorSet);
        Animator.AnimatorListener callback = (isExpanded)
                ? new ShrinkageAnimationCallback() : new ExpansionAnimationCallback();
        animatorSet.addListener(callback);
        animatorSet.start();
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


    public void setParameters (Parameters parameters) {
        if (null == parameters) {
            return;
        }
        mChildCount = parameters.pChildCount;
        mDuration = parameters.pDuration;
        mRadius = getDensity() * parameters.pRadius;
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
                    ViewGroup viewGroup = mImageViewHolder.get(i);
                    ImageView imageView = (ImageView) viewGroup.findViewById(R.id.iconView);
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
        private float pRadius;
        private OrbiterClickListener pCallback;
        private int mMainButtonResId = Integer.MIN_VALUE;
        private List<Integer> mOrbiterResourceIdList;

        public Parameters () {
            pDuration = 350;
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


        public Parameters setOrbiterClickListener (OrbiterClickListener callback) {
            pCallback = callback;
            return this;
        }

        public Parameters setMainButtonResourceId (int resourceId) {
            mMainButtonResId = resourceId;
            return this;
        }

        public Parameters setOrbiterResourceIdList (List<Integer> resourceIdList) {
            if ( (null != resourceIdList) && (!resourceIdList.isEmpty()) ) {
                mOrbiterResourceIdList = resourceIdList;
            }
            return this;
        }
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

            constructAndPlayAnimation();

            if (null != mCallback) {
                mCallback.onOrbiterClicked(mPosition);
            }

            view.setEnabled(true);
        }
    }
}
