package autyzmsoft.pl.profmarcin;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class MainActivity extends Activity implements View.OnClickListener, View.OnLongClickListener {

    public final static int MAX_BTS = 6;  //maxymalna dozwolona liczba klawiszy( =max. poziom trudnosci; =rozmiar tButtons[])
    int lBts = 6;                         //aktualna liczba buttonow (= poziom trudnosci)
    public static
      MojButton[] tButtons = new MojButton[MAX_BTS];   //tablica buttonów z wyrazami

    public Rozdzielacz mRozdzielacz;                   //obiekt sterujacy przydzielaniem zasobow na klawisze

    public static final String katalog  = "obrazki";   //w tym katalogu w Assets trzymane beda obrazki

    public static
      String listaObrazkowAssets[] = null;  //lista obrazkow z Assets/obrazki - dla werski demo )i nie tylko...)

    private int currImage = 0;              //indeks na obrazek

    private int width, height;              //rozmiary urzadzenia
    private int btH;                        //na wysokosc buttona
    private float txSize;                   //na wysokosc tekstu na buttonie

    TextView tvWyraz;       //wyraz pod obrazkiem
    Button bDalej;          //button pod obrazkiem na przechodzenie po kolejne cwiczenie
    Button bAgain;          //button pod obrazkiem umozliwiajacy 'jeszcze raz to samo ćwiczenie"
    LinearLayout  mLayout;  //do tego layoutu bedziemy dorzucac buttony
    ImageView imageView;    //obrazek
    LinearLayout l_obrazek_i_reszta;  //cale pole z lewej strony (bez klawiszy)

    Intent splashKlasa;     //Na przywolanie ekranu z ustawieniami

    Intent intModalDialog;  //Na okienko dialogu 'modalnego' orzy starcie aplikacji

    static MediaPlayer mp = null;

    File dirObrazkiNaSD;                           //katalog z obrazkami na SD (internal i external)
    public static ArrayList<File> myObrazkiSD;     //lista obrazkow w SD



    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    /* Wywolywana po udzieleniu/odmowie zezwolenia na dostęp do karty (od API 23 i wyzej) */
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)  {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //reload my activity with permission granted or use the features what required the permission
                } else  {
                    //toast("Nie udzieliłeś zezwolenia na odczyt. Opcja 'obrazki z mojego katalogu' nie będzie działać. Możesz zainstalować aplikacje ponownie lub zmienić zezwolenie w Menadżerze aplikacji.");
                    wypiszOstrzezenie("Nie udzieliłeś zezwolenia na odczyt. Opcja 'obrazki z mojego katalogu' nie będzie działać. Możesz zainstalować aplikację ponownie lub zmienić zezwolenie w Menadżerze aplikacji.");
                    Button rbKatalog = (RadioButton) findViewById(R.id.rb_zKatalogu);
                    rbKatalog.setEnabled(false);
                }
            }
        }
    } //koniec Metody


    @Override
    protected void onCreate(Bundle savedInstanceState) {

       /* ZEZWOLENIA NA KARTE _ WERSJA na MARSHMALLOW, jezeli dziala na starszej wersji, to ten kod wykona sie jako dummy */
        int jestZezwolenie = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (jestZezwolenie != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
        }
       /* KONIEC **************** ZEZWOLENIA NA KARTE _ WERSJA na MARSHMALLOW */

        //na caly ekran:
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Uchwyty do kontrolek:
        bDalej    = (Button) findViewById(R.id.bDalej);
        tvWyraz   = (TextView) findViewById(R.id.tvWyraz);
        bAgain    = (Button) findViewById(R.id.bAgain);
        mLayout   = (LinearLayout) findViewById(R.id.layoutButtons);

        imageView = (ImageView) findViewById(R.id.imV);
        imageView.setOnLongClickListener(this);

        l_obrazek_i_reszta = (LinearLayout) findViewById(R.id.l_Obrazek_i_Reszta);

        wygenerujButtony();

        ustawListenerNaBDalej();

        pokazModal();   //Okienko modalne z informacjami o aplikacji:

        /* 2017.08.-09 - ski ski - porzejscie na onResume:
        mRozdzielacz.ustaw(lBts,true,true,false);
        mRozdzielacz.dajZestaw(); //na klawiszach pojawia sie wyrazy(=nazwy obrazkow)
        setCurrentImage(); //wyswietlenie obrazka
       */

    } //koniec metody onCreate()


    private void oszacujWysokoscButtonow_i_Tekstu() {
    /* Na podstawie liczby buttonow (=wybranego poziomu trudnosci) szacuje wysokosc buttonow btH i wielkosc tekstów na buttonach txSize */
    /* Wartosci te beda uzywane przy kreowaniu buttonow (wysokosc but.) + wielkosci textu na tvWyraz                                                    */
    /* Algorytm wypracowany doświadczalnie....                                                                                          */
    /* ******************************************************************************************************************************** */

        //Pobieram wymiary ekranu:
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;
        height= displaymetrics.heightPixels;

        //sledzenie - wyrzucic:
        //bDalej.setText(width+"x"+height);

        if (lBts<=4) {
            int lBtsRob = 2;
            btH = height / (lBtsRob + 3); //bylo 2; button height; doswiadczalnie
            btH = btH - 0;
        }
        if (lBts==5) {   // bo dobrze wyglada przy 5-ciu klawiszach:
            int lBtsRob = 4;
            btH = height / (lBtsRob + 2); //button height; doswiadczalnie
        }
        if (lBts==6) {
            btH = height/(lBts+1); //button height; doswiadczalnie
        }
        //txSize = (float) (btH / 1.9); //to 1.9 było ok, ale ponizej 2.0 pozwala zmiescic "dziewczynka" na mniejszych rozdzielczosciach
        txSize = (float) (btH / 2.0);

        //Troche efektow ubocznych - wymiarowanie kontrolek pod obrazkiem i inne:
        float tvWyrazSize = height/10; //doswiadczalnie - duży i wyraźny
        tvWyraz.setTextSize(TypedValue.COMPLEX_UNIT_PX, tvWyrazSize);
        //strzalka na srodku bDalej - doswiadczalnie:
        int szerbDalej = (int) (width/2.2);
        bDalej.setPadding((int)(szerbDalej/2.5)-10 ,1,1,1);
    } //koniec Metody()


    private void ustawListenerNaBDalej() {
        /**
         * Przejscie do nowego cwiczenia:
         */
        bDalej.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                zdejmijButtony();   //zdjecie buttonow starego cwiczenia
                wygenerujButtony(); //generacja buttonow nowego cwiczenia
                for (int i=0; i<lBts; i++) {
                    tButtons[i].setEnabled(true);
                    tButtons[i].setTypeface(null, Typeface.BOLD);
                }
                mRozdzielacz.dajZestaw();
                setCurrentImage();
                bDalej.setVisibility(View.INVISIBLE);
                bAgain.setVisibility(View.INVISIBLE);
            }
        });
    } //koniec metody

    private void zdejmijButtony() {
        /*Zdjecie z ekranu i layoutu 'buttonow 'starego' cwiczenia*/
        for (int i=0; i<lBts; i++) {
            mLayout.removeView(tButtons[i]);
            tButtons[i] = null;
        }
    }  //koniec Metody()

    private void wygenerujButtony() {
        /* Generujemy lBts buttonow; zapamietujemy w tablicy tButtons[] */
        MojButton mb;  //robocza, dla wiekszej czytelnosci
        //
        oszacujWysokoscButtonow_i_Tekstu();
        //
        for (int i=0; i<lBts; i++) {
            mb = new MojButton(this, btH, txSize, "");
            mb.setOnClickListener(this);
            tButtons[i] = mb;
            mLayout.addView(tButtons[i]);
            //Ustawienie marginesow miedzy buttonami (musi byc poza konstruktorem - klawisz musi fizyczne lezec na layoucie, inaczej nie dziala):
            LinearLayout.LayoutParams params;
            params = (LinearLayout.LayoutParams) tButtons[i].getLayoutParams();
            //params.setMargins(0, (int)(btH/10), 10, 0); //substitute parameters for Left, Top, Right, Bottom ( LTRB )
            params.setMargins(0, (int)(btH/7), 10, 0); //substitute parameters for Left, Top, Right, Bottom ( LTRB )
            //params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            tButtons[i].setLayoutParams(params);
        }
    }  //koniec Metody()



    @Override
    public void onClick(View view) {
    /*****************************************************************************
     CO na klik na buttonie z wyrazem (jednym z tButtons[])
     *****************************************************************************/

        String napisNaKl = (String) ((Button) view).getText();
        if (napisNaKl.equals(mRozdzielacz.getAktWybrWyraz())) { //jezeli napis na kliknietym klawiszu taki jaki ustanowil mRozdzielacz, to :
            //1.Wyłaczmy wszystkie listenery (bo efekty uboczne jesli klikanie po 'zwyciestwie'),
            //2.Gasimy i deaktywujemy wszystkie inne, oprócz kliknietego (dobry efekt wizualny):
            //3.Odgrywamy 'aplauz'
            for (int i = 0; i < lBts; i++) {
                view.setOnClickListener(null);  //ad. 1
                if (tButtons[i] != view) {      //ad. 2
                    tButtons[i].setEnabled(false);
                    tButtons[i].setTypeface(null, Typeface.NORMAL);
                }
            }
            //Odblokowanie bDalej, bAgain i wyswietlenie wyrazu pod obrazkiem:
            tvWyraz.setVisibility(View.VISIBLE);
            tvWyraz.setText(mRozdzielacz.getAktWybrWyraz());
            tvWyraz.setTypeface(null, Typeface.BOLD);
            //klawisz na pojscie dale j lekkim opoznieniem (dydaktyka):
            int opozniacz1 = 1000;//1500;  //milisekundy
            Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                public void run() {
                    //Wypisanie nazwy (z lekkim opoznieniem):
                    bDalej.setVisibility(View.VISIBLE);
                    bAgain.setVisibility(View.VISIBLE);
                }
            }, opozniacz1);
            odegrajZAssets("nagrania/oklaski.ogg",5);
        } //if
        else {
            odegrajZAssets("nagrania/zle.ogg",0);
        }
    } //koniec metody


    public void odegrajZAssets(final String sciezka_do_pliku_parametr, int delay_milisek) {
    /* ***************************************************************** */
    // Odegranie dzwieku umieszczonego w Assets (w katalogu 'nagrania'):
    /* ***************************************************************** */

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    if (mp != null) {
                        mp.release();
                        mp = new MediaPlayer();
                    } else {
                        mp = new MediaPlayer();
                    }
                    final String sciezka_do_pliku = sciezka_do_pliku_parametr; //udziwniam, bo klasa wewn. i kompilator sie czepia....
                    AssetFileDescriptor descriptor = getAssets().openFd(sciezka_do_pliku);
                    mp.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                    descriptor.close();
                    mp.prepare();
                    mp.setVolume(1f, 1f);
                    mp.setLooping(false);
                    mp.start();
                    //Toast.makeText(getApplicationContext(),"Odgrywam: "+sciezka_do_pliku,Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    //Toast.makeText(getApplicationContext(), "Nie można odegrać pliku z dźwiękiem.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, delay_milisek);
    } //koniec Metody()



    private void setCurrentImage() {
    /* ************************************ */
    /* metoda wyswietlajaca biezacy obrazek */
    /* ************************************ */

    if (ZmienneGlobalne.getInstance().BEZ_OBRAZKOW) {
        imageView.setVisibility(View.INVISIBLE);
    } else {
        imageView.setVisibility(View.VISIBLE);
    }
        //--------------------------------//
        // Wyswietlenie biezacego obrazka //
        //--------------------------------//

        String imageName = "";
        String nazwaObrazka;

        tvWyraz.setText(""); //nazwa pod obrazkiem - najpierw czyscimy stara nazwe
        //W trybie treningowym od razu pokazujemy czerwopny text pod obrazkiem:
        if (ZmienneGlobalne.getInstance().TRYB_TRENING) {
            tvWyraz.setText(mRozdzielacz.getAktWybrWyraz());
            tvWyraz.setVisibility(View.VISIBLE);
        }

        //Wyswietlanie obrazka :
        nazwaObrazka = mRozdzielacz.getAktWybrZasob();
        try {
            if (!ZmienneGlobalne.getInstance().ZRODLEM_JEST_KATALOG) { //pobranie z Assets
                InputStream stream = getAssets().open(katalog + "/" + nazwaObrazka);
                Drawable drawable = Drawable.createFromStream(stream, null);
                imageView.setImageDrawable(drawable);
            } else  {  //pobranie z directory
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                String robAbsolutePath = dirObrazkiNaSD+"/"+nazwaObrazka;
                Bitmap bm = BitmapFactory.decodeFile(robAbsolutePath, options);
                imageView.setImageBitmap(bm);
            }

          /* ski ski - do wykorzystania(?) 2017.08.05

            int opozniacz1 = 600;  //milisekundy
            Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                public void run() {
                    //ski ski - potem wykorzystac (if any) - 2017.08.05
                    //Wypisanie nazwy (z lekkim opoznieniem):
                    //tvWyraz.setText(robStrFinal);
                }
            }, opozniacz1);
*/

            //ODEGRANIE DŹWIĘKU:
            if (!ZmienneGlobalne.getInstance().ZRODLEM_JEST_KATALOG) {
                //odeggranie z Assets (tam ogg):
                /* ski ski - na razie wylaczam - 2017.08.04
                sciezka_do_pliku_dzwiekowego = nagrania + "/" + robStrFinal + ".ogg"; //ustawienie zmiennej glob. - glob., bo bedzie potrzebna rowniez w onCliknaImage...
                odegrajZAssets(sciezka_do_pliku_dzwiekowego,300);
                */
            };

        } catch (Exception e) {
            Log.e("4321", e.getMessage());
            Toast.makeText(this, "Problem z wyswietleniem obrazka...", Toast.LENGTH_SHORT).show();
        }

    } // koniec metody setCurrentImage


    /**
     * Dotyczy: klikniecie na bAgain
     * Gaszę kontrolki pod obrazkem, odblokowuje buttony z wyrazami.
     * W ten sposob cwiczenie moze byc wykonane raz jeszcze (Again).
     * Dodatkowo mieszam napisy na klawiszach (lepszy efekt)
     */
    public void onClickbAgain(View v) {
        tvWyraz.setVisibility(View.INVISIBLE);
        if (ZmienneGlobalne.getInstance().TRYB_TRENING) {
            tvWyraz.setText(mRozdzielacz.getAktWybrWyraz());
            tvWyraz.setVisibility(View.VISIBLE);
        }
        bAgain.setVisibility(View.INVISIBLE);
        bDalej.setVisibility(View.INVISIBLE);
        //Odblpokowanie buttonow z wyrazami:
        for (int i=0; i<lBts; i++) {
            tButtons[i].setOnClickListener(this);
            tButtons[i].setEnabled(true);
            tButtons[i].setTypeface(null, Typeface.BOLD);
        }
        mRozdzielacz.wymieszajNapisy();
        //Toast.makeText(MainActivity.this, Integer.toString(mRozdzielacz.getAktWybrKl()), Toast.LENGTH_SHORT).show();
    } //koniec Metody()


    @Override
    /**
     * dotyczy: imageView
     * Co na dlugim kliknieciu na obrazku - powolanie ekranu z opcjami
     */
    public boolean onLongClick(View view) {
        //pokazanie splash screena :
        splashKlasa = new Intent("autyzmsoft.pl.profmarcin.SplashKlasa");
        startActivity(splashKlasa);
        return true;
    } //koniec Metody()


    private boolean pokazModal() {
    //Pokazanie modalnego okienka.
    //Okienko realizowane jest jako Activity  o nazwie DialogModalny

        intModalDialog = new Intent("autyzmsoft.pl.profmarcin.DialogModalny");
        startActivity(intModalDialog);
        return true;
    }  //koniec Metody()


    @Override
    protected void onResume() {
    /* ************************************* */
    /*Aplikowanie zmian wprowadzonych w menu */
    /*(Bądż pierwsze uruchomienie)           */
    /* ************************************* */
        super.onResume();
        //Pokazujemy zupelnie nowe cwiczenie z paramatrami ustawionymi na Zmiennych Glob. (np. poprzez splashScreena Ustawienia):
        zdejmijButtony();
        final int poziom = ZmienneGlobalne.getInstance().POZIOM;
        final boolean wszystkieRozne  = ZmienneGlobalne.getInstance().WSZYSTKIE_ROZNE;
        final boolean roznicujObrazki = ZmienneGlobalne.getInstance().ROZNICUJ_OBRAZKI;

        lBts = poziom;

        //Tworzenie listy obrazków z Katalogu: ski ski - na razie tentatywnie 2-017.08.19
        if (ZmienneGlobalne.getInstance().ZRODLEM_JEST_KATALOG == true) {
            dirObrazkiNaSD = new File(ZmienneGlobalne.getInstance().WYBRANY_KATALOG);
            myObrazkiSD = findObrazki(dirObrazkiNaSD);
            mRozdzielacz = new Rozdzielacz(myObrazkiSD.size(), lBts);
        };
        if (ZmienneGlobalne.getInstance().ZRODLEM_JEST_KATALOG == false) {
            //Pobranie listy obrazkow z Assets:
            AssetManager mgr = getAssets();
            try {
                listaObrazkowAssets = mgr.list(katalog);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mRozdzielacz = new Rozdzielacz(listaObrazkowAssets.length, lBts);
        }
        //************* KONIEC PROBY TENTATYWNEJ **********************

        mRozdzielacz.ustaw(lBts, wszystkieRozne, roznicujObrazki,false);

        wygenerujButtony();
        mRozdzielacz.dajZestaw();
        setCurrentImage();

        //Toast.makeText(MainActivity.this, Integer.toString(mRozdzielacz.getAktWybrKl()), Toast.LENGTH_SHORT).show();

        //Ukrywam (ewentualnie) widoczne buttony pod obrazkiem (czasami zostają...)- kosmetyka:
        bDalej.setVisibility(View.INVISIBLE);
        bAgain.setVisibility(View.INVISIBLE);




        //Jezeli bez obrazkow, to trzeba jakos 'uczulić' puste miejsce po obrazku, zeby nadal mozna bylo wchodzic do Ustawien:
        if (ZmienneGlobalne.getInstance().BEZ_OBRAZKOW) {
            l_obrazek_i_reszta.setOnLongClickListener(this);
        } else {
            l_obrazek_i_reszta.setOnLongClickListener(null);
        }
    } //koniec Metody()


        public static ArrayList<File> findObrazki(File katalog) {
        /* ******************************************************************************************************************* */
        /* Zwraca liste obrazkow (plikow graficznych .jpg .bmp .png) z katalogu katalog - uzywana tylko dla przypadku SD karty */
        /* ******************************************************************************************************************* */
            ArrayList<File> al = new ArrayList<File>(); //al znaczy "Array List"
            File[] files = katalog.listFiles(); //w files WSZYSTKIE pliki z katalogu (rowniez nieporządane)
            if (files != null) { //lepiej sprawdzic, bo wali sie w petli for na niektorych emulatorach...
                for (File singleFile : files) {
                    if ((singleFile.getName().toUpperCase().endsWith(".JPG")) || (singleFile.getName().toUpperCase().endsWith(".PNG")) || (singleFile.getName().toUpperCase().endsWith(".BMP"))) {
                        al.add(singleFile);
                    }
                }
            }
            return al;
        }  //koniec metody findObrazki


    private void wypiszOstrzezenie(String tekscik) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);   //Theme.Dialog);    //R.style.MyDialogTheme);
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


}
