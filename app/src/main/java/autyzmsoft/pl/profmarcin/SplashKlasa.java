package autyzmsoft.pl.profmarcin;

/**
 * Pomimo nazwy (zaszlosc z MPrzegladarki) nie pokazuje sie przy starcie aplikacji.
 * Zawiera ekran z Ustawieniami. Wywolywana na long toucha na obrazku.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


public class SplashKlasa extends Activity implements View.OnClickListener{

    public static final int REQUEST_CODE_WRACAM_Z_APKA_INFO = 222;
    TextView tv_Poziom;
    CheckBox cb_RoznicujKlawisze;
    CheckBox cb_RoznicujObrazki;
    CheckBox cb_Trening;
    CheckBox cb_Podp;
    RadioButton rb_NoPictures;
    RadioButton rb_NoSound;
    RadioButton rb_zAssets;
    RadioButton rb_zKatalogu;
    RadioButton rb_NoComments;
    RadioButton rb_TylkoOklaski;
    RadioButton rb_Cisza;
    TextView sciezka; //informacyjny teksci pokazujacy biezacy katalog i/lub liczbe obrazkow

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Uwaga - wywoluje sie rowniez po wejsciu z MainActivity przez LongClick na obrazku(!)
        //na caly ekran:
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        ustawKontrolki(); //kontrolki<-ZmienneGlobalne
    }  //koniec Metody()




    @Override
    protected void onPause() {
    //*******************************************//
    //Przekazanie ustawien na --> ZmienneGlobalne//
    //*******************************************//
        super.onPause();
        //Przekazanie poziomu trudnosci:
        int poziom = Integer.parseInt(tv_Poziom.getText().toString());
        ZmienneGlobalne.getInstance().POZIOM = poziom;
        //Przekazanie checkboxow:
        boolean isCheckedKlawisze = cb_RoznicujKlawisze.isChecked();
        boolean isCheckedObrazki  = cb_RoznicujObrazki.isChecked();
        boolean isCheckedTrening  = cb_Trening.isChecked();
        boolean isCheckedPodp     = cb_Podp.isChecked();
        ZmienneGlobalne.getInstance().WSZYSTKIE_ROZNE  = isCheckedKlawisze;
        ZmienneGlobalne.getInstance().ROZNICUJ_OBRAZKI = isCheckedObrazki;
        ZmienneGlobalne.getInstance().TRYB_TRENING     = isCheckedTrening;
        ZmienneGlobalne.getInstance().TRYB_PODP        = isCheckedPodp;

        //Komentarze/Nagrody:
        boolean isCheckedNoComments  = rb_NoComments.isChecked();
        boolean isCheckedTylOkl      = rb_TylkoOklaski.isChecked();
        boolean isCheckedCisza       = rb_Cisza.isChecked();
        ZmienneGlobalne.getInstance().BEZ_KOMENT    = isCheckedNoComments;
        ZmienneGlobalne.getInstance().TYLKO_OKLASKI = isCheckedTylOkl;
        ZmienneGlobalne.getInstance().CISZA         = isCheckedCisza;


        //Kwestia bez obrazków/bez dźwieku - tutaj trzeba uważać, żeby nie wyszło coś bez sensu i nie bylo crashu:
        boolean isCheckedNoPictures = rb_NoPictures.isChecked();
        boolean isCheckedNoSound    = rb_NoSound.isChecked();
        if (!isCheckedNoPictures && !isCheckedNoSound) { //z obrazkiem i dzwiekiem
            ZmienneGlobalne.getInstance().BEZ_OBRAZKOW = false;
            ZmienneGlobalne.getInstance().BEZ_DZWIEKU  = false;
        } else {
            if (isCheckedNoPictures) {  //bez obrazkow (ale musimy zapewnic dzwiek no matter what...)
                ZmienneGlobalne.getInstance().BEZ_OBRAZKOW = true;
                ZmienneGlobalne.getInstance().BEZ_DZWIEKU  = false;
            } else {
                if (isCheckedNoSound) {  //bez dzwieku (ale musimy zapewnic obrazki no matter what..)
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


    @Override
    protected void onResume() {
    /* ******************************************************************************************/
    /* Na ekranie (splashScreenie) pokazywane sa aktualne ustawienia.                           */
    /* Wywolywana (nie bezposrednio, ale jako skutek) na long touch na obrazku - wtedy          */
    /* przywolywana jest SplashKlasa z pokazanymi ustawieniami - patrz MAinActivity.onLOngClick */
    /* Wywolywana rowniez przy starcie aplikacji(!)                                             */
    /* **************************************************************************************** */
        super.onResume();



        //Ponizszy kod istotny przy konczeniu wyboru katalogu zewnetrznego (ale wywola sie tez na onCreate):
        if (ZmienneGlobalne.getInstance().ZRODLEM_JEST_KATALOG) {
            String strKatalog = ZmienneGlobalne.getInstance().WYBRANY_KATALOG;
            int liczbaObrazkow = policzObrazki(strKatalog);
            if (liczbaObrazkow>0) {
                if (!ZmienneGlobalne.getInstance().PELNA_WERSJA) {
                    if (liczbaObrazkow>5) {  //werja Demo, a wybrano katalog z wiecej niz 5 obrazkami
                        ostrzegajPowyzej5();
                        //przywrócenie wyboru 'domyslnego' - z zasobów aplikacji:
                        onClick(rb_zAssets);
                        rb_zAssets.setChecked(true);
                    }
                    else { //wersja Demo, wybór OK
                        toast("Liczba obrazków: " + liczbaObrazkow);
                        rb_zKatalogu.setChecked(true);
                        sciezka.setText(strKatalog+"   "+liczbaObrazkow+" szt.");
                    }
                }
                else {  //wersja Pełna, wybór OK
                    toast("Liczba obrazków: " + liczbaObrazkow);
                    rb_zKatalogu.setChecked(true);
                    sciezka.setText(strKatalog+"   "+liczbaObrazkow+" szt.");
                }
            }
            else { //nie ma obrazkow w wybranym katalogu (dot. werski Pelnej i Demo)
                ostrzegajBrakObrazkow();
                //przywrócenie wyboru 'domyslnego' - z zasobów aplikacji:
                onClick(rb_zAssets);
                rb_zAssets.setChecked(true);
            }
        }
        else { //wybrano zasoby aplikacji
            rb_zAssets.setChecked(true);
            int liczbaObrazkow = MainActivity.listaObrazkowAssets.length;
            sciezka.setText(liczbaObrazkow+" szt.");
        }

    } //onResume - koniec


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
        //Toast.makeText(this, "Jeszcze nie zaimplementowane...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ApkaInfo.class);
        this.startActivityForResult(intent, REQUEST_CODE_WRACAM_Z_APKA_INFO);
    }


    private void przywrocUstDomyslne() {
        /**
         * Przywrócenie domyślnych ustawien aplikacji.
         */
        tv_Poziom.setText("4");
        cb_RoznicujKlawisze.setChecked(true);
        cb_RoznicujObrazki.setChecked(true);
        cb_Trening.setChecked(false);
        cb_Podp.setChecked(false);
        rb_NoPictures.setChecked(false);
        rb_NoSound.setChecked(false);
        rb_TylkoOklaski.setChecked(false);
        rb_NoComments.setChecked(false);

        //inicjacja, bo tego nie ma w skladowych klasy:
        RadioButton rb_SoundPicture = (RadioButton) findViewById(R.id.rb_SoundPicture);
        rb_SoundPicture.setChecked(true);
        //inicjacja, bo tego nie ma w skladowych klasy:
        RadioButton rb_GlosOklaski = (RadioButton) findViewById(R.id.rb_GlosOklaski);
        rb_GlosOklaski.setChecked(true);

        ZmienneGlobalne.getInstance().POZIOM = 4;
    }


    private Dialog createAlertDialogWithButtons() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Przywracanie ustawień domyślnych");
        dialogBuilder.setMessage("Czy przywrócić domyślne ustawienia?");
        dialogBuilder.setCancelable(true); //czy można wychodzić przez esc
        dialogBuilder.setPositiveButton("Tak", new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                przywrocUstDomyslne();
                toast("Przywrócono domyślne....");
            }
        });
        dialogBuilder.setNegativeButton("Nie", new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                //nie robimy nic - powrot z dialogu ; toast("You picked negative button");
            }
        });
        return dialogBuilder.create();
    }

    public void bDefaultClick(View v) {
        //toast("bDefaultClick");
        Dialog zapytanie;
        zapytanie = createAlertDialogWithButtons();
        zapytanie.show();
    }


    private void ostrzegajBrakObrazkow(){
    /* **************************************************************** */
    /* Wyswietlany, gdy user wybierze katalog nie zawierajacy obrazkow. */
    /* **************************************************************** */
        wypiszOstrzezenie("Brak obrazków w wybranym katalogu.\nZostanie zastosowany wybór\nz zasobów aplikacji.");
    }


    private void ostrzegajPowyzej5() {
    /* ************************************************************************************ */
    /* Wyswietlany, gdy user wybierze katalog z wiecej niz 5 obrazkami, a wersja jest Demo. */
    /* ************************************************************************************ */
        wypiszOstrzezenie("Uwaga - używasz wersji Demo.\nWybrano katalog zawierający więcej niż 5 obrazków.\nZostanie przywrócony wybór\nz zasobów aplikacji.");
    }


    private void wypiszOstrzezenie(String tekscik) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder1.setMessage(tekscik);
        builder1.setCancelable(true);
        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    } //koniec Metody()

    public void onClick(View arg0) {
    /* ********************************************************************************************** */
    /* Obsluga klikniec na radio buttony 'Obrazki z zasobow aplikacji', 'Obrazki z wlasnego katalogu' */
    /* ********************************************************************************************** */

        if (arg0==rb_zAssets) {
            sciezka.setText(""); //kosmetyka - znika z ekranu
            //jesli kliknieto na "z zasobow aplikacji", to przełączam się na to nowe źródło:
            if (ZmienneGlobalne.getInstance().ZRODLEM_JEST_KATALOG) {
                ZmienneGlobalne.getInstance().ZRODLEM_JEST_KATALOG = false;
                ZmienneGlobalne.getInstance().ZMIENIONO_ZRODLO     = true;
            }

            //policzenie obrazkow w zasobach aplikacji (zeby uswiadomic usera...):
                int liczba = MainActivity.listaObrazkowAssets.length;
                sciezka.setText(liczba+" szt.");
                toast("Liczba obrazków: "+liczba);

            return;
        }

        if (arg0==rb_zKatalogu) {

            if (ZmienneGlobalne.getInstance().ODMOWA_DOST) {
                wypiszOstrzezenie("Opcja nieaktywna. Odmówiłeś dostępu do katalogów na urządzeniu.");
                rb_zAssets.setChecked(true);
                return;
            }

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


    static int policzObrazki(String strKatalog) {
    /* ******************************************************** */
    /* Liczy obrazki (=pliki .jpg .bmp .png) w zadanym katalogu */
    /* zwraca po prostu rozmiar kolekcji                        */
    /* ******************************************************** */

        return MainActivity.findObrazki(new File(strKatalog)).size();

    } //koniec Metody()


    private void toast(String napis) {
        Toast.makeText(getApplicationContext(),napis,Toast.LENGTH_SHORT).show();
    }


    private void ustawKontrolki() {
        /*******************************************************************************************/
        //Ustawienie kontrolek na layoucie splash.xml na wartosci inicjacyjne ze ZmiennychGlobalnych
        /*******************************************************************************************/

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

        cb_Podp = (CheckBox) findViewById(R.id.cb_Podp);
        isChecked = ZmienneGlobalne.getInstance().TRYB_PODP;
        cb_Podp.setChecked(isChecked);

        rb_NoPictures = (RadioButton) findViewById(R.id.rb_noPicture);
        isChecked     = ZmienneGlobalne.getInstance().BEZ_OBRAZKOW;
        rb_NoPictures.setChecked(isChecked);

        rb_NoSound = (RadioButton) findViewById(R.id.rb_noSound);
        isChecked  = ZmienneGlobalne.getInstance().BEZ_DZWIEKU;
        rb_NoSound.setChecked(isChecked);

        rb_NoComments = (RadioButton) findViewById(R.id.rb_No_Comments);
        isChecked = ZmienneGlobalne.getInstance().BEZ_KOMENT;
        rb_NoComments.setChecked(isChecked);

        rb_TylkoOklaski = (RadioButton) findViewById(R.id.rb_TylkoOklaski);
        isChecked = ZmienneGlobalne.getInstance().TYLKO_OKLASKI;
        rb_TylkoOklaski.setChecked(isChecked);

        rb_Cisza = (RadioButton) findViewById(R.id.rb_Cisza);
        isChecked = ZmienneGlobalne.getInstance().CISZA;
        rb_Cisza.setChecked(isChecked);


        rb_zAssets = (RadioButton) findViewById(R.id.rb_zAssets);
        isChecked  = !ZmienneGlobalne.getInstance().ZRODLEM_JEST_KATALOG;
        rb_zAssets.setChecked(isChecked);
        rb_zAssets.setOnClickListener(this);

        rb_zKatalogu = (RadioButton) findViewById(R.id.rb_zKatalogu);
        isChecked    = ZmienneGlobalne.getInstance().ZRODLEM_JEST_KATALOG;
        rb_zKatalogu.setChecked(isChecked);
        rb_zKatalogu.setOnClickListener(this);


        //Wypisanie ewentualnej sciezki i liczby obrazkow:
        sciezka = (TextView) findViewById(R.id.tv_sciezkaKatalog);
        if (ZmienneGlobalne.getInstance().ZRODLEM_JEST_KATALOG) {
            int liczba = MainActivity.myObrazkiSD.size();
            String strLiczba = Integer.toString(liczba);
            sciezka.setText(ZmienneGlobalne.getInstance().WYBRANY_KATALOG+"   "+strLiczba+" szt.");
        } else {
            int liczba = MainActivity.listaObrazkowAssets.length;
            String strLiczba = Integer.toString(liczba);
            sciezka.setText(strLiczba+" szt.");
        }
    } //koniec Metody()

    public void cbTreningPodpClicked(View v) {
        //Kontrolki in question must be mutually exclusive
        if (v==cb_Trening) {
            cb_Podp.setChecked(false);
        } else {
            cb_Trening.setChecked(false);
        }
    }

    @Override
    protected void onDestroy() {
  	/* Zapisanie ustawienia w SharedPreferences na przyszła sesję */
        super.onDestroy();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); //na zapisanie ustawien na next. sesję
        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putInt("POZIOM", ZmienneGlobalne.getInstance().POZIOM);
        edit.putBoolean("WSZYSTKIE_ROZNE", ZmienneGlobalne.getInstance().WSZYSTKIE_ROZNE);
        edit.putBoolean("ROZNICUJ_OBRAZKI", ZmienneGlobalne.getInstance().ROZNICUJ_OBRAZKI);

        edit.putBoolean("BEZ_OBRAZKOW", ZmienneGlobalne.getInstance().BEZ_OBRAZKOW);
        edit.putBoolean("BEZ_DZWIEKU", ZmienneGlobalne.getInstance().BEZ_DZWIEKU);

        edit.putBoolean("BEZ_KOMENT", ZmienneGlobalne.getInstance().BEZ_KOMENT);
        edit.putBoolean("TYLKO_OKLASKI", ZmienneGlobalne.getInstance().TYLKO_OKLASKI);
        edit.putBoolean("CISZA", ZmienneGlobalne.getInstance().CISZA);

        edit.putBoolean("TRYB_TRENING", ZmienneGlobalne.getInstance().TRYB_TRENING);
        edit.putBoolean("TRYB_PODP", ZmienneGlobalne.getInstance().TRYB_PODP);
        edit.putBoolean("ODMOWA_DOST", ZmienneGlobalne.getInstance().ODMOWA_DOST);

        edit.putBoolean("ZRODLEM_JEST_KATALOG", ZmienneGlobalne.getInstance().ZRODLEM_JEST_KATALOG);
        edit.putString("WYBRANY_KATALOG", ZmienneGlobalne.getInstance().WYBRANY_KATALOG);

        edit.apply();
    } //onDestroy

}
