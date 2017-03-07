package tw.realtime.project.pathanimationtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static String getLogTag () {
        return MainActivity.class.getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                            .setChildCount(6)
                            .setLayoutRadius(150)
                            .setDuration(300)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .setMainButtonResourceId(R.drawable.buddii_nav_ray_button)
                            //.setOrbiterClickEffectFlag(false)
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


}
