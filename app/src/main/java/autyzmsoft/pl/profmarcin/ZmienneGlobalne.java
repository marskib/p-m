package autyzmsoft.pl.profmarcin;

/**
 * Created by developer on 2017-08-04.
 */

import android.app.Application;

/**
 singleton na przechowywanie zmiennych globalnych
 */
public class ZmienneGlobalne extends Application {

    public boolean PELNA_WERSJA;
    public boolean ZRODLEM_JEST_KATALOG; //Co jest aktualnie źródlem obrazków - Asstes czy Katalog (np. katalog na karcie SD)
    public String  WYBRANY_KATALOG;      //katalog (if any) wybrany przez usera jako zrodlo obrazkow (z external SD lub Urządzenia)
    public boolean ZMIENIONO_ZRODLO;     //jakakolwiek zmiana zrodla obrazkow - Assets/Katalog JAK ROWNIEZ zmiana katalogu
    public boolean DLA_KRZYSKA;          //Czy dla Krzyska do testowania - jesli tak -> wylaczam logo i strone www
    public int     POZIOM;               //Poziom trudnosci (liczba pokazywanych klawiszy)
    public boolean WSZYSTKIE_ROZNE;      //Wszystkie klawisze(napisy) maja byc rożne
    public boolean ROZNICUJ_OBRAZKI;     //Za każdym razem pokazywany inny obrazek

    public boolean BEZ_OBRAZKOW;         //nie pokazywac obrazkow
    public boolean BEZ_DZWIEKU;          //nie odgrywać słów

    public boolean BEZ_KOMENT;          //Bez Komentarza-Nagrody po wybraniu klawisza
    public boolean TYLKO_OKLASKI;       //patrz wyżej
    public boolean TYLKO_GLOS;          //patrz wyżej
    public boolean CISZA;               //kompletna Cisza, bez nagrod i bez 'ding,'brrr' po kliknieciu klawisza

    public boolean TRYB_TRENING;        //czy pracujemy w trybie treningowym (pokazujac cwiczenie od razu wyswietlamy nazwe pod obrazkiem)
    public boolean TRYB_PODP;           //to samo co TRYB_TTRENING, tylko nazwa pod obrazkiem duzo mniejszymi literami i nie czerwona
    public boolean DELAYED;             //czy pokazywać klawisze z wyrazami z opóźnieniem (efekciarstwo ;))
    public boolean ODMOWA_DOST;         //na etapie instalacji/1-go uruchomienia user odmowil dostepu do kart(y); dotyczy androida 6 i więcej

    public boolean nieGrajJestemW105;  //robocza na czas developmentu


    private static final ZmienneGlobalne ourInstance = new ZmienneGlobalne();

    public static ZmienneGlobalne getInstance() {
        return ourInstance;
    }


    //konstruktor tego singletona + ustawienia poczatkowe aplikacji:
    private ZmienneGlobalne() {

        nieGrajJestemW105 = false; //wyrzucić po skonczonym developmencie


        PELNA_WERSJA = true;
        POZIOM       = 4;
        WSZYSTKIE_ROZNE  = true;
        ROZNICUJ_OBRAZKI = true;

        BEZ_OBRAZKOW = false;
        BEZ_DZWIEKU  = false;

        BEZ_KOMENT    = false;
        TYLKO_OKLASKI = false;
        TYLKO_GLOS    = false;
        CISZA         = false;

        TRYB_TRENING = false;
        TRYB_PODP    = false;
        DELAYED      = true;
        ODMOWA_DOST  = false;                //w wersji Androida <=5 dostep jest automatyczny, wiec muszę to ustawic bo logika aplikacji by przeszkadzala...

        ZRODLEM_JEST_KATALOG = false;        //startujemy ze zrodlem w Assets
        ZMIENIONO_ZRODLO = true;             //inicjacyjnie na true, zeby po uruchomieniu apki wykonala sie onResume() w calosci
        WYBRANY_KATALOG = "*^5%dummy";       //"nic jeszcze nie wybrano" - lepiej to niz null, bo z null'em problemy...

        DLA_KRZYSKA = true;
    } //konstruktor
}


