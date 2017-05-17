package compony.lcxj.com.ratingviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import compony.lcxj.com.ratingviewdemo.views.RatingView;

public class MainActivity extends AppCompatActivity {
    private RatingView ratingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ratingView = (RatingView) findViewById(R.id.ratingBar);
        float f = (float) 4.5;
        ratingView.setRating(f);
    }
}
