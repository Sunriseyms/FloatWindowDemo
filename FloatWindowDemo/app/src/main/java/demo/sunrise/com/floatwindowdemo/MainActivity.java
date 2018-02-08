package demo.sunrise.com.floatwindowdemo;

import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startFloatWindow = findViewById(R.id.start_float_window);
        startFloatWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23){
                    if (!Settings.canDrawOverlays(getApplicationContext())) {
                        Intent intent1 = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        //intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(intent1, 1);
                    } else {
                        //TODO do something you need
                        Intent intent = new Intent(MainActivity.this,FloatWindowService.class);
                        startService(intent);
                        finish();
                    }
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (Settings.canDrawOverlays(getApplicationContext())) {
                Intent intent = new Intent(MainActivity.this,FloatWindowService.class);
                startService(intent);
                finish();
            }else {

            }
        }
    }
}
