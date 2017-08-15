package autyzmsoft.pl.profmarcin;

/**
 * Created by developer on 2017-08-04.
 */

import android.app.Application;

/**
 singleton na przechowywanie zmiennych globalnych
 */
public class ZmienneGlobalne extends Application {

    public boolean zGlosem;
    public boolean PELNA_WERSJA;
    public boolean ZRODLEM_JEST_KATALOG; //Co jest aktualnie źródlem obrazków - Asstes czy Katalog (np. katalog na karcie SD)
    public String  WYBRANY_KATALOG;      //katalog (if any) wybrany przez usera jako zrodlo obrazkow (z external SD lub Urządzenia)
    public boolean ZMIENIONO_ZRODLO;     //jakakolwiek zmiana zrodla obrazkow - Assets/Katalog JAK ROWNIEZ zmiana katalogu
    public boolean ALFABET;              //Czy obrazki mają być wyświetlano alfabetycznie czy losowo
    public boolean DLA_KRZYSKA;          //Czy dla Krzyska do testowania - jesli tak -> wylaczam logo i strone www
    public int     POZIOM;               //Poziom trudnosci (liczba pokazywanych klawiszy)
    public boolean WSZYSTKIE_ROZNE;      //Wszystkie klawisze(napisy) maja byc rożne
    public boolean ROZNICUJ_OBRAZKI;     //Za każdym razem pokazywany inny obrazek
    public boolean BEZ_OBRAZKOW;         //nie pokazywac obrazkow
    public boolean BEZ_DZWIEKU;          //nie odgrywać słów
    public boolean TRYB_TRENING;         //czy pracujemy w trybie treningowym (pokazujac cwiczenie od razu wyswietlamy nazwe pod obrazkiem)

    private static ZmienneGlobalne ourInstance = new ZmienneGlobalne();

    public static ZmienneGlobalne getInstance() {
        return ourInstance;
    }


    //konstruktor tego singletona + ustawienia poczatkowe aplikacji:
    private ZmienneGlobalne() {
        zGlosem      = true;  //false - na potrzeby developmentu w 1.05 i na 232 Wawrz.40
        ALFABET      = true;
        PELNA_WERSJA = true;
        POZIOM       = 4;
        WSZYSTKIE_ROZNE  = true;
        ROZNICUJ_OBRAZKI = true;
        BEZ_OBRAZKOW = false;
        BEZ_DZWIEKU  = false;
        TRYB_TRENING = false;

        ZRODLEM_JEST_KATALOG = false;  //startujemy ze zrodlem w Assets
        ZMIENIONO_ZRODLO = true;       //inicjacyjnie na true, zeby po uruchomieniu apki wykonala sie onResume() w calosci
        WYBRANY_KATALOG = "*^5%dummy"; //"nic jeszcze nie wybrano" - lepiej to niz null...
        DLA_KRZYSKA = false;
    } //konstruktor
}


