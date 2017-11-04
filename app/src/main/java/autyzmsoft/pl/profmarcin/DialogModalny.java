package autyzmsoft.pl.profmarcin;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import java.io.File;

/**
 * Wyswietla okienko modalne.
 * Zrodlo - stackoverflow
 * Dotakowo jest jeszcze potrzebny wpis w manifest.xml:
 * action android:name="autyzmsoft.pl.profmarcin.DialogModalny"/>   --> it gives the activity the dialog look...
 * Uzywane do startowania aplikacji
 */

public class DialogModalny extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setFinishOnTouchOutside (false);  //to make it behave like a modal dialog

        setContentView(R.layout.activity_dialog_modalny);

        //Ustawienie szerokosci okna DialogModalny:
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int szer = displaymetrics.widthPixels;
        View layoutSki = findViewById(R.id.sv_DialogModalny);
        layoutSki.getLayoutParams().width = (int) (szer*0.80f);
        layoutSki.requestLayout(); //teraz nastepuje zaaplikowanie zmian

        //Pobranie zapisanych ustawien i zaladowanie do -> ZmiennychGlobalnych, (if any) gdy startujemy aplikacje :
        if (savedInstanceState == null) { //ten warunek oznacza, ze to nie obrot, tylko startujemy odpoczatku
            pobierzSharedPreferences();
        }

        czyscDlaKrzyska(); //jezeli wysylam do Testerow, to zacieram namiary na moje www

    }  //koniec Metody()


    @Override public void onBackPressed() { //to make it behave like a modal dialog
        // prevent "back" from leaving this activity
        //zwroc uwage, ze tutaj nie ma super() - nadpisanie/zlikwidowanie metody macierzystej i potomnej
     }

     public void onClickbtnOK(View v) {
         //zamkniecie activity, zeby przejsc do MainActivity (wywolywacza)
         finish();
     }

    private void pobierzSharedPreferences() {
    /* ******************************************************** */
    /* Zapisane ustawienia wczytywane sa do ZmiennychGlobalnych */
    /* ******************************************************** */

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); //na zapisanie ustawien na next. sesję

        ZmienneGlobalne.getInstance().POZIOM           =  sharedPreferences.getInt("POZIOM",  4); //2-gi parametr na wypadek, gdyby w SharedPref. nic jeszcze nie bylo
        ZmienneGlobalne.getInstance().WSZYSTKIE_ROZNE  = sharedPreferences.getBoolean("WSZYSTKIE_ROZNE",true);
        ZmienneGlobalne.getInstance().ROZNICUJ_OBRAZKI = sharedPreferences.getBoolean("ROZNICUJ_OBRAZKI",true);

        //Ponizej zapewniamy, ze apka obudzi sie zawsze z obrazkiem i dzwiekiem (inaczej user bylby zdezorientowany):
        ZmienneGlobalne.getInstance().BEZ_OBRAZKOW = false;
        ZmienneGlobalne.getInstance().BEZ_DZWIEKU  = false;

        ZmienneGlobalne.getInstance().BEZ_KOMENT    = sharedPreferences.getBoolean("BEZ_KOMENT",false);
        ZmienneGlobalne.getInstance().TYLKO_OKLASKI = sharedPreferences.getBoolean("TYLKO_OKLASKI", false);
        ZmienneGlobalne.getInstance().CISZA         = sharedPreferences.getBoolean("CISZA", false);

        ZmienneGlobalne.getInstance().TRYB_TRENING  = sharedPreferences.getBoolean("TRYB_TRENING", false);
        ZmienneGlobalne.getInstance().TRYB_PODP     = sharedPreferences.getBoolean("TRYB_PODP", false);
        ZmienneGlobalne.getInstance().DELAYED       = sharedPreferences.getBoolean("DELAYED", true);
        ZmienneGlobalne.getInstance().ODMOWA_DOST   = sharedPreferences.getBoolean("ODMOWA_DOST", false);

        ZmienneGlobalne.getInstance().ZRODLEM_JEST_KATALOG = sharedPreferences.getBoolean("ZRODLEM_JEST_KATALOG", false);

        //Jesli zrodlem miałby byc katalog, to potrzebne dotatkowe sprawdzenie,bo gdyby pomiedzy uruchomieniami
        // zlikwidowano wybrany katalog to mamy problem, i wtedy przelaczamy sie na zrodlo z zasobow aplikacji:
        if (ZmienneGlobalne.getInstance().ZRODLEM_JEST_KATALOG) {
            String katalog = sharedPreferences.getString("WYBRANY_KATALOG", "*^5%dummy");
            File file = new File(katalog);
            if (!file.exists()) {
                ZmienneGlobalne.getInstance().ZRODLEM_JEST_KATALOG = false;
            }
            //gdyby nie zlikwidowano katalogu, ale tylko 'wycieto' obrazki - przelaczenie na Zasoby applikacji:
            else {
                if (SplashKlasa.policzObrazki(katalog) == 0) {
                    ZmienneGlobalne.getInstance().ZRODLEM_JEST_KATALOG = false;
                }
                else {
                    ZmienneGlobalne.getInstance().WYBRANY_KATALOG = katalog;
                }
            }
        }
    } //koniec Metody()

    public void czyscDlaKrzyska() {
    /* Ukrywanie obrazkow i 'śladów' do strony www - przed przekazanie do Krzyska; Potem usunac */
        if (ZmienneGlobalne.getInstance().DLA_KRZYSKA) {
            //ImageView obrazek = (ImageView) findViewById(R.id.imageView1);
            //if (obrazek != null) obrazek.setVisibility(View.INVISIBLE);
            TextView link = (TextView) findViewById(R.id.autyzmsoftpl); //bo na niektorych konfiguracjach nie pokazuje tego linku
            if (link != null) link.setVisibility(View.INVISIBLE);
        }
    } //koniec Metody


}
