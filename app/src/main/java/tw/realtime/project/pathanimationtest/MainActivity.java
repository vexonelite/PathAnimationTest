package tw.realtime.project.pathanimationtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private DrawPathView mDrawPathView;

    private static String getLogTag () {
        return MainActivity.class.getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawPathView = (DrawPathView) findViewById(R.id.drawPath);

        View view = findViewById(R.id.drawLine);
        if (null != view) {
            view.setOnClickListener(new MyViewOnClickListener(DrawPathView.DrawCase.LINE));
        }

        view = findViewById(R.id.drawCicle);
        if (null != view) {
            view.setOnClickListener(new MyViewOnClickListener(DrawPathView.DrawCase.CUSTOM));
        }

        view = findViewById(R.id.drawAddArc);
        if (null != view) {
            view.setOnClickListener(new MyViewOnClickListener(DrawPathView.DrawCase.ADD_ARC));
        }

        view = findViewById(R.id.drawArcTo);
        if (null != view) {
            view.setOnClickListener(new MyViewOnClickListener(DrawPathView.DrawCase.ARC_TO));
        }

        view = findViewById(R.id.drawAddCircle);
        if (null != view) {
            view.setOnClickListener(new MyViewOnClickListener(DrawPathView.DrawCase.ADD_CIRCLE));
        }

        view = findViewById(R.id.drawAddPath);
        if (null != view) {
            view.setOnClickListener(new MyViewOnClickListener(DrawPathView.DrawCase.ADD_PATH));
        }

        view = findViewById(R.id.drawQuadTo);
        if (null != view) {
            view.setOnClickListener(new MyViewOnClickListener(DrawPathView.DrawCase.QUAD_TO));
        }

        view = findViewById(R.id.drawCublicTo);
        if (null != view) {
            view.setOnClickListener(new MyViewOnClickListener(DrawPathView.DrawCase.CIBLIC_TO));
        }

        PathMotionMenuLayout pmmLayout = (PathMotionMenuLayout) findViewById(R.id.pathMotionMenu);
        if (null != pmmLayout) {

            Integer[] resId = {
                    R.drawable.buddii_nav_personal_wall_button,
                    R.drawable.buddii_nav_world_wall_button,
                    R.drawable.buddii_nav_qr_scan_button,
                    R.drawable.buddii_nav_map_button
            };
            pmmLayout.setParameters(
                    new PathMotionMenuLayout.Parameters()
                            .setLayoutRadius(150)
                            .setDuration(300)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .setMainButtonResourceId(R.drawable.buddii_nav_ray_button)
                            .setOrbiterResourceIdList(new ArrayList<>(Arrays.asList(resId)))
                            .setOrbiterClickListener(new MenuOrbiterClickCallback()));
        }
    }

    private class MenuOrbiterClickCallback implements PathMotionMenuLayout.OrbiterClickListener {
        @Override
        public void onOrbiterClicked(int position) {
            Log.i(getLogTag(), "onOrbiterClicked: " + position);
        }
    }

    private class MyViewOnClickListener implements View.OnClickListener {
        private DrawPathView.DrawCase mDrawCase;

        private MyViewOnClickListener (DrawPathView.DrawCase drawCase) {
            mDrawCase = drawCase;
        }

        @Override
        public void onClick(View view) {
            view.setEnabled(false);
            mDrawPathView.refresh(mDrawCase);
            view.setEnabled(true);
        }
    }
}
