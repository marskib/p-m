package autyzmsoft.pl.profmarcin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



/**
 * Created by developer on 2017-11-02
 * Informacje o aplikacji - licencja, instrukcja itp...
 */

public class ApkaInfo extends Activity {

  public Button bOkInfo;
  public Button bStart;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.apka_info);

    bOkInfo = (Button) findViewById(R.id.bOkInfo);
    bStart = (Button) findViewById(R.id.bStart);

    //Ustanowienie listenera:
    View.OnClickListener sluchacz = new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        Intent returnIntent = new Intent();

        if (v == bStart)
          returnIntent.putExtra("MESSAGE", "KL_START");
        else
          returnIntent.putExtra("MESSAGE", "KL_OK");

        setResult(Activity.RESULT_OK, returnIntent);

        finish();
        return;
      }
    };

    //przy takim rozwiazaniu jak wyzej, jeden listener na 2 klawisze:
    bStart.setOnClickListener(sluchacz);
    bOkInfo.setOnClickListener(sluchacz);
    //
    dajInfoWersja();
  } //onCreate

  private void dajInfoWersja(){
    /*Na ekranie ApkaInfo.xml wypisuje jeden wiersz informacji o wersji aplikacji*/
    TextView tvWersja = (TextView) findViewById(R.id.tvWersja);
    String str = "MPrzegladarka 1.0 wersja ";
    if (ZmienneGlobalne.getInstance().PELNA_WERSJA)
      str = str + "Pełna";
    else
      str = str + "Demo";
    tvWersja.setText(str);
    return;
  }


}
