package mimesic.activities;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.TextView;
 
 
public class Mimesic extends Activity {
    
    Button Atras;
    protected TextView custom;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mimesic);

        
        custom = (TextView) findViewById(R.id.mimesic);
        Typeface font = Typeface.createFromAsset(getAssets(), "silent.ttf");
        custom.setTypeface(font);
              
    }
}