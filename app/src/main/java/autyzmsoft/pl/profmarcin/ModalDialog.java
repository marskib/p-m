package autyzmsoft.pl.profmarcin;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ModalDialog extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setFinishOnTouchOutside (false);  //to make it behave like a modal dialog

        setContentView(R.layout.activity_modal_dialog);
    }



    @Override public void onBackPressed() { //to make it behave like a modal dialog
        // prevent "back" from leaving this activity
     }

     public void onClickbtnOK(View v) {
         finish();
     }


}
