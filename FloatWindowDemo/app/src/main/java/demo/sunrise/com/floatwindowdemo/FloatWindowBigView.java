package demo.sunrise.com.floatwindowdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by sunrise on 2/6/18.
 */

public class FloatWindowBigView extends LinearLayout implements View.OnClickListener {

    public static int sViewWidth;

    public static int sViewHeight;

    public FloatWindowBigView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.float_window_big,this);
        View view = findViewById(R.id.big_window_layout);
        sViewWidth = view.getLayoutParams().width;
        sViewHeight = view.getLayoutParams().height;

        Button closebtn = findViewById(R.id.close);
        Button backbtn = findViewById(R.id.back);

        closebtn.setOnClickListener(this);
        backbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch ( view.getId() ) {
            case R.id.close:
                MyWindowManager.removeBigWidow(getContext());
                MyWindowManager.createSmallWindow(getContext());
                break;
            case R.id.back:
                MyWindowManager.removeBigWidow(getContext());
                MyWindowManager.createSmallWindow(getContext());
                break;
            default:
                break;
        }
    }
}
