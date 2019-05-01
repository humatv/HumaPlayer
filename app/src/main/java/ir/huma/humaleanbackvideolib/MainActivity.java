package ir.huma.humaleanbackvideolib;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class MainActivity extends FragmentActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.videoFragment, new TestVideoPlayerFragment(),
                TestVideoPlayerFragment.class.getCanonicalName());
        ft.commit();

//        ImageView imageView = findViewById(R.id.imageview);
//
//        imageView.setImageDrawable(new TextDrawable(getResources(),"Hello"));

    }

}
