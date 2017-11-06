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

    //Ustanowienie listenera uzywanego na klawiszach bInfo i bStart:
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
      }
    };

    //przy takim rozwiazaniu jak wyzej, jeden listener na 2 klawisze:
    bStart.setOnClickListener(sluchacz);
    bOkInfo.setOnClickListener(sluchacz);
    //
    dajInfoWersja();
    ustawGledzenie();

  } //onCreate



  private void ustawGledzenie() {
    //Ustawia drobny druczek na ApkaInfo; robie skladając kod, zeby zautomatyzowac
    //pobieranie liczby obrazków z Assets apkikacji (zeby nie na sztywano).

    String strLiczba = String.valueOf(MainActivity.listaObrazkowAssets.length)+" "; //w Informacjach odnosimy sie tylko do obrazkow w zasobach
    String rob1 = getResources().getString(R.string.apka_info_01);
    String rob2 = getResources().getString(R.string.apka_info_02);
    String rob3 = getResources().getString(R.string.apka_info_03);
    String drobnyDruczek = rob1+ strLiczba +rob2+" "+strLiczba+rob3;
    TextView tvGledzenie = findViewById(R.id.tvGledzenie);
    tvGledzenie.setText(drobnyDruczek);
  }


  private void dajInfoWersja(){
    /*Na ekranie ApkaInfo.xml wypisuje jeden wiersz informacji o wersji aplikacji*/
    TextView tvWersja = findViewById(R.id.tvWersja);
    String str = "  ProfMarcin 1.1 wersja ";
    if (ZmienneGlobalne.getInstance().PELNA_WERSJA)
      str = str + "Pełna";
    else
      str = str + "Demo";
    tvWersja.setText(str);
  }


}
