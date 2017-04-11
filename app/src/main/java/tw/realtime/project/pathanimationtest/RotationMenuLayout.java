package tw.realtime.project.pathanimationtest;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
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

    private RectF mSourcePoint;
    private List<RectF> mDestinationRectList;

    private ImageView mMainButton;
    private List<ViewGroup> mImageViewHolder;

    private int mChildCount = 1;
    private long mDuration;
    private Interpolator mInterpolator;
    private float mRadius;
    private boolean hasOrbiterClickEffect;

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
        //mInterpolator = new LinearInterpolator();
        //mInterpolator = new OvershootInterpolator();
        mInterpolator = new AccelerateDecelerateInterpolator();
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

                //constructAndPlayAnimation();


                ArrayList<ValueAnimator> animatorList = new ArrayList<>();
                ArrayList<ValueAnimator> animatorList2 = new ArrayList<>();
                for (int i = 0; i < mImageViewHolder.size(); i++) {
                    ViewGroup viewGroup = mImageViewHolder.get(i);

                    float rotationAngle = 45f;
                    ValueAnimator rotationAnimator = ObjectAnimator.ofFloat(viewGroup , "rotation", 0f, rotationAngle);
                    rotationAnimator.setDuration(mDuration);
                    rotationAnimator.setInterpolator(mInterpolator);
                    animatorList.add(rotationAnimator);

                    ImageView imageView = (ImageView) viewGroup.findViewById(R.id.iconView);
                    ValueAnimator rotationAnimator2 = ObjectAnimator.ofFloat(imageView , "rotation", 0f, -rotationAngle);
                    rotationAnimator.setDuration(mDuration);
                    rotationAnimator.setInterpolator(mInterpolator);
                    animatorList.add(rotationAnimator2);

                    float measureHeight = (float) imageView.getMeasuredHeight();
                    float ratioY = 1.5f;
                    float refCenterY = (ratioY * measureHeight);

                    ValueAnimator transYAnimator = ObjectAnimator.ofFloat(
                            imageView, "translationY", 0f, -refCenterY);
                    transYAnimator.setDuration(mDuration);
                    transYAnimator.setInterpolator(new LinearInterpolator());
                    animatorList2.add(transYAnimator);
                }

                ValueAnimator[] animatorArray = animatorList.toArray(new ValueAnimator[animatorList.size()]);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(animatorArray);

                ValueAnimator[] animatorArray2 = animatorList.toArray(new ValueAnimator[animatorList2.size()]);
                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorSet2.playTogether(animatorArray2);

                animatorSet2.start();

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
                    R.layout.path_motion_menu_orbiter2, RotationMenuLayout.this, false);
            //imageView.setOnClickListener(new OrbiterClicker(i));
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            addView(viewGroup, params);
            mImageViewHolder.add(viewGroup);
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

        // size of main button
        float mainSize = ((float) mMainButton.getMeasuredWidth()) / 2f;
        ViewGroup viewGroup = mImageViewHolder.get(0);
        // size of orbiter button
        float ringSize = (float) viewGroup.getMeasuredWidth();
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

        ViewGroup viewGroup1 = mImageViewHolder.get(0);
        float measuredWidth = (float) viewGroup1.getMeasuredWidth();
        float measureHeight = (float) viewGroup1.getMeasuredHeight();
        Log.i(getLogTag(), "onLayout - viewGroup1 - measuredWidth: " + measuredWidth + ", measureHeight: " + measureHeight);

        double refAngle = 180d;
        double refCenterX = mainButtonCenter.x + mRadius * Math.cos(Math.toRadians(refAngle));
        double refCenterY = mainButtonCenter.y + mRadius * Math.sin(Math.toRadians(refAngle));
        double ratioY = 1.5d;
        refCenterY = refCenterY + (ratioY * measureHeight);

        int refLeft = (int)(refCenterX - (measuredWidth / 2f));
        int refTop = (int)(refCenterY - (measureHeight / 2f));
        int refRight = (int)(refCenterX + (measuredWidth / 2f));
        int refBottom = (int)(refCenterY + (measureHeight / 2f));

        for (ViewGroup viewGroup : mImageViewHolder) {
            viewGroup.layout(refLeft, refTop, refRight, refBottom);
            viewGroup.invalidate();

            if (!changed) {
                Log.i(getLogTag(), "onLayout - getPivotX [1]: " + viewGroup.getPivotX() + ", getPivotY: " + viewGroup.getPivotY());
                float pivotX = viewGroup.getPivotX();
                viewGroup.setPivotX(pivotX + mRadius);
                Log.i(getLogTag(), "onLayout - getPivotX [2]: " + viewGroup.getPivotX() + ", getPivotY: " + viewGroup.getPivotY());
                ImageView imageView = (ImageView) viewGroup.findViewById(R.id.iconView);
                Log.i(getLogTag(), "onLayout - getPivotX [3]: " + imageView.getPivotX() + ", getPivotY: " + imageView.getPivotY());
            }
        }
    }



}
