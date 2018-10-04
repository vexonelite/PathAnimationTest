package tw.realtime.project.pathanimationtest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public final class DrawPathActivity extends AppCompatActivity {

    private DrawPathView drawPathView;
    private View animationView;

    private final Rect centerTopRect = new Rect();
    private final Rect centerBottomRect = new Rect();
    private final Rect leftBottomRect = new Rect();
    private final Rect rightBottomRect = new Rect();
    private final Rect leftCenterRect = new Rect();
    private final Rect rightCenterRect = new Rect();

    private String getLogTag () {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_path);

        drawPathView = findViewById(R.id.drawPathView);
        drawPathView.setOnClickListener(this::clickHandler);

        animationView = findViewById(R.id.animationView);

        registerGlobalLayoutEvent();
    }

    private void clickHandler(View view) {
        //drawByLineTo();
        drawByCubicTo();
        //drawByQuadTo();
    }

    private PointF getRecCenter (@NonNull Rect rect) {
        final float xPoint = ((float) rect.left) + ( ( ((float)rect.right) - ((float) rect.left) ) / 2f );
        final float yPoint = ((float) rect.top) + ( ( ((float)rect.bottom) - ((float) rect.top) ) / 2f );
        return new PointF(xPoint, yPoint);
    }

    private void playAnimation (@NonNull Path path) {
        final ObjectAnimator animation = ObjectAnimator.ofFloat(animationView, View.X, View.Y, path);
        animation.setDuration(700L);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.addListener(new MyAnimationCallback());
        animation.start();
    }

    private class MyAnimationCallback extends AnimatorListenerAdapter {
        @Override
        public void onAnimationStart(Animator animator) {
            //LogWrapper.showLog(Log.INFO, getLogTag(), "onAnimationStart");
            animationView.setTranslationX(0f);
            animationView.setTranslationY(0f);
            animationView.setAlpha(1f);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            animationView.setAlpha(0f);
        }
    }

    private void drawByLineTo () {
        final Path path = new Path();
        final PointF startPoint = getRecCenter(leftBottomRect);
        path.moveTo(startPoint.x, startPoint.y);
        Log.i(getLogTag(), "startPoint: " + startPoint);

        final PointF stepPoint1 = getRecCenter(leftCenterRect);
        path.lineTo(stepPoint1.x, stepPoint1.y);
        Log.i(getLogTag(), "stepPoint1: " + stepPoint1);

        final PointF stepPoint2 = getRecCenter(centerTopRect);
        path.lineTo(stepPoint2.x, stepPoint2.y);
        Log.i(getLogTag(), "stepPoint2: " + stepPoint2);

        final PointF stepPoint3 = getRecCenter(rightCenterRect);
        path.lineTo(stepPoint3.x, stepPoint3.y);
        Log.i(getLogTag(), "stepPoint3: " + stepPoint3);

        final PointF destination = getRecCenter(rightBottomRect);
        path.lineTo(destination.x, destination.y);
        Log.i(getLogTag(), "destination: " + destination);

        drawPathView.setDrawingPath(path);
        playAnimation(path);
    }

    private void drawByCubicTo () {
        final Path path = new Path();
        final PointF startPoint = getRecCenter(leftCenterRect);
        path.moveTo(startPoint.x, startPoint.y);
        Log.i(getLogTag(), "startPoint: " + startPoint);

        final PointF stepPoint1 = getRecCenter(centerTopRect);
        final PointF stepPoint2 = getRecCenter(rightCenterRect);
        final PointF destination = getRecCenter(centerBottomRect);
        Log.i(getLogTag(), "stepPoint1: " + stepPoint1);
        Log.i(getLogTag(), "stepPoint2: " + stepPoint2);
        Log.i(getLogTag(), "destination: " + destination);
        path.cubicTo(
                stepPoint1.x, stepPoint1.y,
                stepPoint2.x, stepPoint2.y,
                destination.x, destination.y);
        drawPathView.setDrawingPath(path);
        playAnimation(path);
    }

    private void drawByQuadTo () {
        final Path path = new Path();
        final PointF startPoint = getRecCenter(leftCenterRect);
        path.moveTo(startPoint.x, startPoint.y);
        Log.i(getLogTag(), "startPoint: " + startPoint);

        final PointF stepPoint1 = getRecCenter(centerTopRect);
        final PointF destination = getRecCenter(rightBottomRect);
        Log.i(getLogTag(), "stepPoint1: " + stepPoint1);
        Log.i(getLogTag(), "destination: " + destination);
        path.quadTo(
                stepPoint1.x, stepPoint1.y,
                destination.x, destination.y);
        drawPathView.setDrawingPath(path);
        playAnimation(path);
    }


    private void registerGlobalLayoutEvent () {
        final View rootView = findViewById(R.id.drawPathContainer);
        if (null != rootView) {
            rootView.getViewTreeObserver().addOnGlobalLayoutListener(this::viewTreeObserverOnGlobalLayoutCallback);
        }
    }

    // implements ViewTreeObserver.OnGlobalLayoutListener
    private void viewTreeObserverOnGlobalLayoutCallback () {
        final View rootView = findViewById(R.id.drawPathContainer);
        if (null != rootView) {
            rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this::viewTreeObserverOnGlobalLayoutCallback);
        }

        final View centerTopRef = findViewById(R.id.centerTopRef);
        centerTopRef.getHitRect(centerTopRect);
        Log.i(getLogTag(), "centerTopRect: " + centerTopRect);

        final View centerBottomRef = findViewById(R.id.centerBottomRef);
        centerBottomRef.getHitRect(centerBottomRect);
        Log.i(getLogTag(), "centerBottomRect: " + centerBottomRect);

        final View leftBottomRef = findViewById(R.id.leftBottomRef);
        leftBottomRef.getHitRect(leftBottomRect);
        Log.i(getLogTag(), "leftBottomRect: " + leftBottomRect);

        final View rightBottomRef = findViewById(R.id.rightBottomRef);
        rightBottomRef.getHitRect(rightBottomRect);
        Log.i(getLogTag(), "rightBottomRect: " + rightBottomRect);

        final View leftCenterRef = findViewById(R.id.leftCenterRef);
        leftCenterRef.getHitRect(leftCenterRect);
        Log.i(getLogTag(), "leftCenterRect: " + leftCenterRect);

        final View rightCenterRef = findViewById(R.id.rightCenterRef);
        rightCenterRef.getHitRect(rightCenterRect);
        Log.i(getLogTag(), "rightCenterRect: " + rightCenterRect);

        //////

        // about view itself:
        // drawingRect = focusedRect = localVisibleRect

//        final Rect localVisibleRect = new Rect();
//        final boolean isLocalVisible = centerTopRef.getLocalVisibleRect(localVisibleRect);
//        Log.i(getLogTag(), "isLocalVisible: " + isLocalVisible + ", localVisibleRect: " + localVisibleRect);
//
//        final Rect drawingRect = new Rect();
//        centerTopRef.getDrawingRect(drawingRect);
//        Log.i(getLogTag(), "drawingRect: " + drawingRect);
//
//        final Rect focusedRect = new Rect();
//        centerTopRef.getFocusedRect(focusedRect);
//        Log.i(getLogTag(), "focusedRect: " + focusedRect);
//
//        // for touch event to perform hit test
//        final Rect hitRect = new Rect();
//        centerTopRef.getHitRect(hitRect);
//        Log.i(getLogTag(), "hitRect: " + hitRect);
//
//        // isGlobalVisible = globalVisibleRect
//        final Rect globalVisibleRect = new Rect();
//        final boolean isGlobalVisible = centerTopRef.getGlobalVisibleRect(globalVisibleRect);
//        Log.i(getLogTag(), "isGlobalVisible: " + isGlobalVisible + ", globalVisibleRect: " + globalVisibleRect);
//
//        final int[] points = new int[2];
//        centerTopRef.getLocationOnScreen(points);
//        final Rect locationOnScreenRect = new Rect(points[0], points[1], points[0] + centerTopRef.getWidth(), points[1] + centerTopRef.getHeight());
//        Log.i(getLogTag(), "locationOnScreenRect: " + locationOnScreenRect);
    }


}
