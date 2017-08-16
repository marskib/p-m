package autyzmsoft.pl.profmarcin;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class DialogModalny extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setFinishOnTouchOutside (false);  //to make it behave like a modal dialog

        setContentView(R.layout.activity_dialog_modalny);
    }



    @Override public void onBackPressed() { //to make it behave like a modal dialog
        // prevent "back" from leaving this activity
     }

     public void onClickbtnOK(View v) {
         //zamkniecie activity, zeby przejsc do MainActivity
         finish();
     }


}
