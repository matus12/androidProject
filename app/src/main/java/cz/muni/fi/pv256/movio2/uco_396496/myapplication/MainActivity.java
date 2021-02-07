package cz.muni.fi.pv256.movio2.uco_396496.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static android.R.attr.button;

public class MainActivity extends AppCompatActivity {

    private Button btn;
    private boolean theme1 = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(sharedPref.getString("theme", "").equals("1")){
            setTheme(R.style.Theme);
        } else {
            setTheme(R.style.Theme2);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                f();
                Intent i = getIntent();
                finish();
                startActivity(i);
            }
        });
    }
    void f() {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(sharedPreferences.getString("theme", "").equals("1")){
            editor.putString("theme", "2");
        } else {
            editor.putString("theme", "1");
        }
        editor.commit();
    }


}
