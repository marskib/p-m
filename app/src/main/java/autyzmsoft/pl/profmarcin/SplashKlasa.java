package autyzmsoft.pl.profmarcin;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


public class SplashKlasa extends Activity implements View.OnClickListener{

    TextView tv_Poziom;
    CheckBox cb_RoznicujKlawisze;
    CheckBox cb_RoznicujObrazki;
    CheckBox cb_Trening;
    RadioButton rb_NoPictures;
    RadioButton rb_NoSound;
    RadioButton rb_zAssets;
    RadioButton rb_zKatalogu;
    TextView sciezka; //informacyjny teksci pokazujacy biezacy katalog (if any)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //na caly ekran:
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);


        //Ustawienie kontrolek na layoucie splash.xml na wartosci inicjacyjne ze ZmiennychGlobalnych:
        tv_Poziom = (TextView) findViewById(R.id.tv_Poziom);
        String strPoziom = Integer.toString(ZmienneGlobalne.getInstance().POZIOM);
        tv_Poziom.setText(strPoziom);

        cb_RoznicujKlawisze = (CheckBox) findViewById(R.id.cb_RoznicujKlawisze);
        boolean isChecked = ZmienneGlobalne.getInstance().WSZYSTKIE_ROZNE;
        cb_RoznicujKlawisze.setChecked(isChecked);

        cb_RoznicujObrazki = (CheckBox) findViewById(R.id.cb_RoznicujObrazki);
        isChecked = ZmienneGlobalne.getInstance().ROZNICUJ_OBRAZKI;
        cb_RoznicujObrazki.setChecked(isChecked);

        cb_Trening = (CheckBox) findViewById(R.id.cb_Trening);
        isChecked = ZmienneGlobalne.getInstance().TRYB_TRENING;
        cb_Trening.setChecked(isChecked);

        rb_NoPictures = (RadioButton) findViewById(R.id.rb_noPicture);
        isChecked     = ZmienneGlobalne.getInstance().BEZ_OBRAZKOW;
        rb_NoPictures.setChecked(isChecked);

        rb_NoSound = (RadioButton) findViewById(R.id.rb_noSound);
        isChecked  = ZmienneGlobalne.getInstance().BEZ_DZWIEKU;
        rb_NoSound.setChecked(isChecked);

        rb_zAssets = (RadioButton) findViewById(R.id.rb_zAssets);
        isChecked  = !ZmienneGlobalne.getInstance().ZRODLEM_JEST_KATALOG;
        rb_zAssets.setChecked(isChecked);
        rb_zAssets.setOnClickListener(this);

        rb_zKatalogu = (RadioButton) findViewById(R.id.rb_zKatalogu);
        isChecked    = ZmienneGlobalne.getInstance().ZRODLEM_JEST_KATALOG;
        rb_zKatalogu.setChecked(isChecked);
        rb_zKatalogu.setOnClickListener(this);

        sciezka = (TextView) findViewById(R.id.tv_sciezkaKatalog);

    }  //koniec Metody()


    @Override
    protected void onPause() {
        super.onPause();
        //Przekazanie poziomu trudnosci:
        int poziom = Integer.parseInt(tv_Poziom.getText().toString());
        ZmienneGlobalne.getInstance().POZIOM = poziom;
        //Przekazanie checkboxow:
        boolean isCheckedKlawisze = cb_RoznicujKlawisze.isChecked();
        boolean isCheckedObrazki  = cb_RoznicujObrazki.isChecked();
        boolean isCheckedTrening  = cb_Trening.isChecked();
        ZmienneGlobalne.getInstance().WSZYSTKIE_ROZNE  = isCheckedKlawisze;
        ZmienneGlobalne.getInstance().ROZNICUJ_OBRAZKI = isCheckedObrazki;
        ZmienneGlobalne.getInstance().TRYB_TRENING     = isCheckedTrening;

        //Kwestia bez obrazków/bez dźwieku - tutaj trzeba uważać, żeby nie wyszło coś b3ez sensu i nie bylo crashu:
        boolean isCheckedNoPictures = rb_NoPictures.isChecked();
        boolean isCheckedNoSound    = rb_NoSound.isChecked();
        if (!isCheckedNoPictures && !isCheckedNoSound) { //z obrazkiem i dzwiekiem
            ZmienneGlobalne.getInstance().BEZ_OBRAZKOW = false;
            ZmienneGlobalne.getInstance().BEZ_DZWIEKU  = false;
        } else {
            if (isCheckedNoPictures) {  //bez obrazkow (musimy zapewnic dzwiek no matter what...)
                ZmienneGlobalne.getInstance().BEZ_OBRAZKOW = true;
                ZmienneGlobalne.getInstance().BEZ_DZWIEKU  = false;
            }     else {
                if (isCheckedNoSound) {  //bez dzwieku - musimy zapewnic obrazki no matter what..
                    ZmienneGlobalne.getInstance().BEZ_OBRAZKOW = false;
                    ZmienneGlobalne.getInstance().BEZ_DZWIEKU  = true;
                } else { //na wszelki wypadek...
                    ZmienneGlobalne.getInstance().BEZ_OBRAZKOW = false;
                    ZmienneGlobalne.getInstance().BEZ_DZWIEKU  = false;
                }
            }
        }
        //
    } //koniec Metody()


    public void bStartClick(View v) {
        //Przejscie do MainActivity
        //i wywola sie onPause... :)
        finish();
    }

    public void  btn_Poziom_Click(View v) {
        /**
         * Zwiekszenie/Zmniejszenie poziomu (liczby klawiszy) poprzez klikniecie na odpowiednich buttonach na ekranie ustawien
         */
        int level = Integer.parseInt(tv_Poziom.getText().toString());
        Button bPlus  = (Button) findViewById(R.id.btn_Plus);
        Button bMinus = (Button) findViewById(R.id.btn_Minus);
        if (v==bPlus) {
            level++;
        } else if (v==bMinus) {
            level--;
        }
        switch (level) {  //zapewniam zakres od 1..6
            case 7: level=6; return;
            case 0: level=1; return;
        }
        String strLevel = Integer.toString(level);
        tv_Poziom.setText(strLevel);
    }  //koniec Metody()

    
    public void bInfoClick(View v) {
        Toast.makeText(this, "Jeszcze nie zaimplementowane...", Toast.LENGTH_SHORT).show();
    }


    public void onClick(View arg0) {
    /* ********************************************************************************************** */
    /* Obsluga klikniec na radio buttony 'Obrazki z zasobow aplikacji', 'Obrazki z wlasnego katalogu' */
    /* ********************************************************************************************** */

        if (arg0==rb_zAssets) {
            sciezka.setText(""); //kosmetyka - znika z ekranu
            //jesli kliknieto na "z zasobow aplikacji", to przełączam się na to nowe źródło:
            if (ZmienneGlobalne.getInstance().ZRODLEM_JEST_KATALOG==true) {
                ZmienneGlobalne.getInstance().ZRODLEM_JEST_KATALOG = false;
                ZmienneGlobalne.getInstance().ZMIENIONO_ZRODLO     = true;
            }

            //policzenie obrazkow w zasobach aplikacji (zeby uswiadomic usera...):
            AssetManager mgr = getAssets();
            try {
                toast("Liczba obrazków: "+Integer.toString(mgr.list(MainActivity.katalog).length));
            } catch (Exception io) {};

            return;
        }
        if (arg0==rb_zKatalogu) {
            /*Wywolanie activity do wyboru miedzy karta zewnetrzna SD, a pamiecia urzadzenia:*/

            if (ZmienneGlobalne.getInstance().PELNA_WERSJA) {
                Intent intent = new Intent(this, InternalExternalKlasa.class);
                this.startActivity(intent);
            }
            //Wersja demo::
            else {
                Intent intent = new Intent(this, WersjaDemoOstrzez.class);
                this.startActivity(intent); //w srodku zostanie wywolana InternalExternalKlasa
            }

            return;
        }
    } //koniec Metody()

    private void toast(String napis) {
        Toast.makeText(getApplicationContext(),napis,Toast.LENGTH_LONG).show();
    }


}
