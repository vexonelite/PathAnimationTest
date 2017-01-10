package tw.realtime.project.pathanimationtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by vexonelite on 2017/1/9.
 */

public class DrawPathView extends View {

    public enum DrawCase {
        LINE,
        CIRCLE,
        ADD_ARC,
        ARC_TO,
        ADD_CIRCLE,
        ADD_PATH,
        QUAD_TO,
        CIBLIC_TO,
        CUSTOM
    }

    private DrawCase mDrawCase = DrawCase.CIRCLE;

    private Path mPath;
    private Paint mPaint;

    private final RectF mPathBounds = new RectF();
    private float mViewArea = 0f;
    private final PathMeasure measure = new PathMeasure();


    private String getLogTag () {
        return this.getClass().getSimpleName();
    }

    public DrawPathView(Context context) {
        super(context);
        init();
    }

    public DrawPathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawPathView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

//    public DrawPathView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    public void refresh (DrawCase drawCase) {
        mDrawCase = drawCase;
        invalidate();
    }

    private void init() {

        mPath = new Path();

        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        //mPaint.setStrokeCap(Paint.Cap.ROUND);
        //mPaint.setStrokeJoin(Paint.Join.ROUND);
        //mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

    }

    private float getDensity () {
        return getContext().getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPath.reset();

        switch (mDrawCase) {
            case LINE:
                drawLinePath();
                break;

            case CIRCLE:
                drawCirclePath();
                break;

            case ADD_CIRCLE:
                drawCircle();
                break;

            case ADD_PATH:
                drawPath();
                break;

            case QUAD_TO:
                drawQuadTo();
                break;

            case CIBLIC_TO:
                drawCublicTo();
                break;

            case CUSTOM:
                drawCustom(canvas);
                break;

            case ADD_ARC:
            case ARC_TO:
                drawArcs(canvas);
                break;

        }
        canvas.drawPath(mPath, mPaint);
    }

    private void drawLinePath () {
        //float density = getContext().getResources().getDisplayMetrics().density;
        //float margin = density * 15f;

        float width = (float)getWidth();
        float height = (float)getHeight();
        float radius = (width > height) ? (height / 4f) : (width / 4f);
        float center_x = width / 4f;
        float center_y = height / 4f;

        mPath.moveTo(center_x, center_y);
        mPath.lineTo(center_x + radius, center_y + radius);
    }

    private void drawCirclePath () {

        float width = (float)getWidth();
        float height = (float)getHeight();
        float radius = (width > height) ? (height / 4f) : (width / 4f);
        float center_x = width / 2f;
        float center_y = height / 2f;

        final RectF oval = new RectF();
        oval.set(center_x - radius,
                center_y - radius,
                center_x + radius,
                center_y + radius);
        mPath.addArc(oval, 0, 360);
    }

    private void drawArcs (Canvas canvas) {
        float width = (float)getWidth();
        float height = (float)getHeight();
        float radius = (width > height) ? (height / 4f) : (width / 4f);
        float center_x = width / 2f;
        float center_y = height / 2f;

        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(2.5f);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);

        RectF rect = new RectF(
                center_x - (2f * radius) ,
                center_y - radius,
                center_x + (2f * radius) ,
                center_y + radius);
        canvas.drawRect(rect, paint);
        canvas.drawOval(new RectF(rect), paint);
        canvas.drawLine(center_x - (2f * radius), center_y, center_x + (2f * radius), center_y, paint);
        canvas.drawLine(center_x, center_y - (2f * radius), center_x, center_y + (2f * radius), paint);

        if (mDrawCase == DrawCase.ADD_ARC) {
            mPath.addArc(new RectF(rect), 30, 60);
            mPath.addArc(new RectF(rect), -90, 60);
        }
        else if (mDrawCase == DrawCase.ARC_TO) {
            mPath.arcTo(new RectF(rect), 30, 90);
            mPath.arcTo(new RectF(rect), -60, 60);
            // it seems that method has suffix "To" will make connection between two method calls
        }
    }

    private void drawCircle () {
        float width = (float)getWidth();
        float height = (float)getHeight();
        float radius = (width > height) ? (height / 4f) : (width / 4f);
        float center_x = width / 2f;
        float center_y = height / 2f;

        //mPath.addCircle(center_x, center_y, radius, Path.Direction.CW);
        mPath.addCircle(center_x, center_y, radius, Path.Direction.CCW);
    }

    private void drawOval () {
        float width = (float)getWidth();
        float height = (float)getHeight();
        float radius = (width > height) ? (height / 4f) : (width / 4f);
        float center_x = width / 2f;
        float center_y = height / 2f;

        RectF rect = new RectF(
                center_x - (2f * radius) ,
                center_y - radius,
                center_x + (2f * radius) ,
                center_y + radius);

        mPath.addOval(rect, Path.Direction.CCW);
    }

    private void drawPath () {

        float width = (float)getWidth();
        float height = (float)getHeight();
        float radius = (width > height) ? (height / 4f) : (width / 4f);

        mPath.moveTo(radius, radius);
        mPath.lineTo(100,100);
        mPath.lineTo(100, 200);
        mPath.lineTo(150, 250);

        //canvas.drawPath(path, paint);
        mPath.addPath(mPath, 100, 0);
    }

    private void drawQuadTo () {

        float width = (float)getWidth();
        float height = (float)getHeight();
        float radius = (width > height) ? (height / 4f) : (width / 4f);

//        mPath.moveTo(radius, radius);
//        mPath.lineTo(50, 200);
//        mPath.quadTo(100, 200, 150, 250);

        mPath.moveTo(100, 500);
        mPath.quadTo(300, 100, 600, 500);
        //where (300, 100) is the control point, and (600, 500) is the end point;
    }

    private void drawCublicTo () {
        mPath.moveTo(100, 500);
        mPath.cubicTo(100, 500, 300, 100, 600, 500);
        //where (100, 500) and (300, 100) are the control point, and (600, 500) is the end point;
    }

    private void drawCustom (Canvas canvas) {
        float width = (float)getWidth();
        float height = (float)getHeight();
        float radius = getDensity() * 100f;
        float center_x = width / 2f;
        float center_y = height / 2f;

        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(2.5f);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        mPath.addCircle(center_x, center_y, radius, Path.Direction.CCW);
        canvas.drawPath(mPath, paint);

        mPath.reset();
        float mainRadius = getDensity() * 30f;
        mPath.addCircle(center_x, center_y, mainRadius, Path.Direction.CCW);

        double angleDifference = 90d / 10d;
        double baseAngle = 180d + (angleDifference / 2d) ;
        float ringRadius = getDensity() * 20f;
        for (int i = 0; i < 4; i++) {
            double angle = baseAngle + (i * 3d * angleDifference);
            double my_center_x = center_x + radius * Math.cos(Math.toRadians(angle));
            double my_center_y = center_y + radius * Math.sin(Math.toRadians(angle));

            mPath.addCircle((float)my_center_x, (float)my_center_y, ringRadius, Path.Direction.CCW);
        }
    }

}
