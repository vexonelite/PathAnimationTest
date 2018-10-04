package tw.realtime.project.pathanimationtest;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

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

//        PathMotionMenuLayout pathMotionMenu = (PathMotionMenuLayout) findViewById(R.id.pathMotionMenu);
//        if (null != pathMotionMenu) {
//
//            Integer[] resId = {
//                    R.drawable.buddii_nav_personal_wall_button,
//                    R.drawable.buddii_nav_world_wall_button,
//                    R.drawable.buddii_nav_qr_scan_button,
//                    R.drawable.buddii_nav_map_button
//            };
//            pathMotionMenu.setParameters(
//                    new PathMotionMenuLayout.Parameters()
//                            .setChildCount(6)
//                            .setLayoutRadius(150)
//                            .setDuration(300)
//                            .setInterpolator(new AccelerateDecelerateInterpolator())
//                            .setMainButtonResourceId(R.drawable.buddii_nav_ray_button)
//                            //.setOrbiterClickEffectFlag(false)
//                            .setOrbiterResourceIdList(new ArrayList<>(Arrays.asList(resId)))
//                            .setOrbiterClickListener(new MenuOrbiterClickCallback()));
//        }

        RotationMenuLayout rotationMenu = (RotationMenuLayout) findViewById(R.id.rotationMenu);
        if (null != rotationMenu) {

            Integer[] resId = {
                    R.drawable.buddii_nav_personal_wall_button,
                    R.drawable.buddii_nav_world_wall_button,
                    R.drawable.buddii_nav_qr_scan_button,
                    R.drawable.buddii_nav_map_button
            };
            rotationMenu.setParameters(
                    new RotationMenuLayout.Parameters()
                            .setChildCount(6)
                            .setLayoutRadius(150)
                            .setDuration(300)
                            .setMainButtonResourceId(R.drawable.buddii_nav_ray_button)
                            .setOrbiterResourceIdList(new ArrayList<>(Arrays.asList(resId)))
                            .setOrbiterClickListener(new MenuOrbiterClickCallback()));
        }
    }

    private class MenuOrbiterClickCallback implements OrbiterClickListener {
        @Override
        public void onOrbiterClicked(int position) {
            Log.i(getLogTag(), "onOrbiterClicked: " + position);
        }
    }


}
