package tw.realtime.project.pathanimationtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;


/**
 * Created by vexonelite on 2018/10/04.
 */
public final class DrawPathView extends View {

    private final Paint drawingPaint;
    private Path drawingPath;

    public DrawPathView(Context context) {
        super(context);
        final int color = ContextCompat.getColor(context, R.color.color_012);
        drawingPaint = new Paint();
        drawingPaint.setStyle(Paint.Style.STROKE);
        drawingPaint.setStrokeWidth(2);
        drawingPaint.setColor(color);

//        drawingPath  = new Path();
//        drawingPath.lineTo(200, 400);
//        drawingPath.lineTo(400, 600);
//        drawingPath.rLineTo(400, 600);
    }

    public DrawPathView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        final int color = ContextCompat.getColor(context, R.color.color_012);
        drawingPaint = new Paint();
        drawingPaint.setStyle(Paint.Style.STROKE);
        drawingPaint.setStrokeWidth(2);
        drawingPaint.setColor(color);

//        drawingPath  = new Path();
//        drawingPath.lineTo(200, 400);
//        drawingPath.lineTo(400, 600);
//        drawingPath.rLineTo(400, 600);
    }

    public DrawPathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final int color = ContextCompat.getColor(context, R.color.color_012);
        drawingPaint = new Paint();
        drawingPaint.setStyle(Paint.Style.STROKE);
        drawingPaint.setStrokeWidth(2);
        drawingPaint.setColor(color);

//        drawingPath  = new Path();
//        drawingPath.lineTo(200, 400);
//        drawingPath.lineTo(400, 600);
//        drawingPath.rLineTo(400, 600);
    }

    public DrawPathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        final int color = ContextCompat.getColor(context, R.color.color_012);
        drawingPaint = new Paint();
        drawingPaint.setStyle(Paint.Style.STROKE);
        drawingPaint.setStrokeWidth(2);
        drawingPaint.setColor(color);

//        drawingPath  = new Path();
//        drawingPath.lineTo(200, 400);
//        drawingPath.lineTo(400, 600);
//        drawingPath.rLineTo(400, 600);
    }

    public void setDrawingPath (@NonNull Path path) {
        drawingPath = path;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        Log.i("DrawPathView", "onDraw");
        if (null != drawingPath) {
            canvas.drawPath(drawingPath, drawingPaint);
        }
    }

}
