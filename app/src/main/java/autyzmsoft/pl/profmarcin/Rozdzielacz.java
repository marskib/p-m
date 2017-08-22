package autyzmsoft.pl.profmarcin;

import static autyzmsoft.pl.profmarcin.MainActivity.listaObrazkowAssets;
import static autyzmsoft.pl.profmarcin.MainActivity.myObrazkiSD;
import static autyzmsoft.pl.profmarcin.MainActivity.tButtons;

/**
 * Created by developer on 2017-08-05.
 * Na wzor obiektu TRozdzielacz z pascalowej wersji ProfMarcina.
 * Obiekt sterujacy przydzielaniem zasobow na klawisze.
 */

public class Rozdzielacz {

    Pamietacz mPamietacz;  //do pamietania przydzielonych obrazkow, zeby w miare mozliwosci nie powtarzaly sie

    private int
        ileObrazkow,
        aktLevel,
        aktWybrKl;

    public int getAktWybrKl() {
        /**
         * robie getter'a , bo wybrany klawisz moze sie przydac (i przyda) sie na zewnatrz
         */
        return aktWybrKl;
    } //koniec metody


    private String
        aktWybrZasob,
        popWybrZasob,
        aktWybrWyraz;

    public String getAktWybrWyraz() {
        //wykorzystywane na zewnatrz
        return aktWybrWyraz;
    }

    public String getAktWybrZasob() {
        /** robie getter'a , zeby setImage() wyswietlil ten wlasnie ten obrazek. */
        return aktWybrZasob;
    }

    //na potrzeby diagnostyki:
    public String getIleObrazkow() {
        return Integer.toString(ileObrazkow);
    }



    private int
        popWybrKl,
        ileKLDoZwoln;       //Ile Klawiszy Do Zwolnienia - b. istotne jesli zmieniamy AktLevel - trzeba zwalniac pamiec (i klawisze z ekranu)
    private boolean
        wszystkieRozne,     //czy wszystkie generowane klawisze maja miec rozne etykiety/napisy/zasoby
        podpowiedz,         //czy ma byc wysw. podpowiedz na belce okna
        roznicujObrazki;    //czy za każdym kliknieciem pokazywać inny obrazek


    public Rozdzielacz(int ileObrazkow, int aktLevel) {
        this.ileObrazkow  = ileObrazkow;
        this.aktLevel     = aktLevel;
        this.ileKLDoZwoln = this.aktLevel;
        //Jezeli mamy mniej obrazkow niz klawiszy na ekranie, to niemozliwe jest spelnienie warunku, zeby wszystkie klawisze byly rozna... :
        if (ileObrazkow<aktLevel) {
            this.wszystkieRozne = false;
        }
        else {
            this.wszystkieRozne = true;
        }
        //
        this.roznicujObrazki = true;
        this.aktWybrKl    = -1;
        this.aktWybrZasob = "";
        this.popWybrKl    = -1;
        this.popWybrZasob = "";
        //
        mPamietacz = new Pamietacz();  //stworzenie Pamietacza do pamietania przydzielonych obrazkow
    } //koniec Konstruktora()

    public void ustaw(int aktLevel, boolean wszystkieRozne, boolean roznicujObrazki, boolean podpowiedz) {
        /**
         * Ustawienie Rozdzielacza wg tego co na FParametry
         */
        this.aktLevel = aktLevel;

        //parametr 'eszystkieRozne' musi byc kontrolowany:
        if (wszystkieRozne==true) {
            if (ileObrazkow<aktLevel) {
                this.wszystkieRozne = false;
            }
            else {
                this.wszystkieRozne = true;
            }
        }
        else
            this.wszystkieRozne = false;
        //koniec kontroli parametru
        this.roznicujObrazki = roznicujObrazki;
        this.podpowiedz      = podpowiedz;
    } //koniec metody


    public void dajZestaw() {
        /**
         * Na pdst. aktualnego ustawienia Obiektu Rozdzielacz wydaje zestaw zasobow.
         * Zasoby umieszcza na klawiszach; Aktualnie wybrany do zgadywania - w zmiennej Self.AktWybrZasob
         * Jeszcze pare innych mniej istotnych f-cji np.                    , pamietanie 'poprzednich' itd
         */

        //Wybor 'trafionego' klawisza; jezeli jest to ten sam (geograficznie) co poprzedni- ponowna generacja, w ten sposob p-stwo,ze znow ten sam maleje o polowe... :
        aktWybrKl = (int) (Math.random() * aktLevel); //aktLevel = liczba kl. na ekranie
        if (aktWybrKl==popWybrKl) {
            aktWybrKl = (int) (Math.random() * aktLevel); //zmniejszm p-stwo (patrz wyżej)
        }

        if (roznicujObrazki) {
            //wybranie 'niepowtarzalnego' zasobu (=obrazka) :
            aktWybrZasob = mPamietacz.dajSwiezyZasob(); //obiekt Pamietacz ma zapewnic niepowtarzalnosc wyboru (jak juz sie 'wyczerpie', to 'napelni' sie na nowo
            tButtons[aktWybrKl].setNazwaPliku(aktWybrZasob);    //zapamietujemy pelna (bez sciezki) nazwe pliku, bo moze byc potrzebna
            String wyraz = getRemovedExtensionName(aktWybrZasob); //na klawiszu widac goly wyraz bez .jpg
            tButtons[aktWybrKl].setText(wyraz);
        }

        //Teraz 'obslugujemy' pozostale klawisze - na klawiszach pojawia sie napisy; pod klawisze podpiete zostana zasoby :
        for (int i=0; i<aktLevel; i++) {
            if ((roznicujObrazki) && (i==aktWybrKl))  { //wtedy nie robimy nic - ten jeden szczegolny, juz wybrany i 'oklejony' klawisz...
                    /*nie robimy nic*/
            }
            else {
                String zasob = dajDozwolonyZasob();  //ta proc. zadziala poprawnie rowniez wtedy kiedy jest RoznicowanieObrazkow - proc. bierze pod uwage juz 'oklejony' klawisz
                tButtons[i].setNazwaPliku(zasob);    //zapamietujemy pelna (bez sciezki) nazwe pliku, bo moze byc potrzebna
                String wyraz = getRemovedExtensionName(zasob); //na klawiszu widac goly wyraz bez .jpg
                tButtons[i].setText(wyraz);
            }
            //Nazwę wybranego pliku i wyraz na wybranym klawiszu przekazuję na zawnatrz:
            if (i==aktWybrKl) {
                aktWybrZasob = tButtons[aktWybrKl].getNazwaPliku();
                aktWybrWyraz = getRemovedExtensionName(aktWybrZasob);
            }
        } //for
    }//koniec metody dajZestaw()




    private String dajDozwolonyZasob() {
        boolean zKatalogu = ZmienneGlobalne.getInstance().ZRODLEM_JEST_KATALOG;
        String zasob="";
        if (wszystkieRozne) {
            //generujemy tak dlugo, az trafimy na taki, ktorego jeszcze nie ma na juz pokazywanych klawiszach:
            do {
                int rob = (int) (Math.random()*this.ileObrazkow);
                if (!zKatalogu) {
                    zasob = listaObrazkowAssets[rob];      //pascal: FOperacje.Memo1.Lines[Random(IleObrazkow)]; UWAGA 'zasob' jest tutaj nazwa pliku z rozszerzeniem,
                } else {
                    zasob = myObrazkiSD.get(rob).getName();
                }
            } while (jestJuzTakiNaKlawiszach(zasob));
        }
        else {
            int rob = (int) (Math.random() * this.ileObrazkow);
            if (!zKatalogu) {
                zasob = listaObrazkowAssets[rob];
            } else {
                zasob = myObrazkiSD.get(rob).getName();
            }
        }
        return zasob;
    }  //koniec Metody()




/* oryginal skib skib 2017.08.20

    private String dajDozwolonyZasob() {
        String zasob="";
        if (wszystkieRozne) {
            //generujemy tak dlugo, az trafimy na taki, ktorego jeszcze nie ma na juz pokazywanych klawiszach:
            do {
                int rob = (int) (Math.random()*this.ileObrazkow);
                zasob = listaObrazkowAssets[rob];      //pascal: FOperacje.Memo1.Lines[Random(IleObrazkow)]; UWAGA 'zasob' jest tutaj nazwa pliku z rozszerzeniem,
            } while (jestJuzTakiNaKlawiszach(zasob));
        }
        else {
            int rob = (int) (Math.random() * this.ileObrazkow);
            zasob = listaObrazkowAssets[rob];
        }
        return zasob;
    }  //koniec metody

*/

    private boolean jestJuzTakiNaKlawiszach(String zasob) {
        /**
         * Sprawdza,czy zasob podany w parametrze jest juz podpiety pod jakis klawisz
         * UWAGA - porownywanie nastepuje po nazwach plikow (bezpieczniej, jest kwesrtia setImage(), gdzie tylko nazwa pliku *0
         * Wykorzystywana, gdy wszystkie generowane klawisze maja byc rozne-> wszystkieRozna==true
         */
        String nazwaPlikuPodKlawiszem;
        int i = 0;
        boolean znalazl = false;
        while ((i<aktLevel)&&(!znalazl)) {
            nazwaPlikuPodKlawiszem = tButtons[i].getNazwaPliku();
            if (nazwaPlikuPodKlawiszem.equals(zasob))  //uwaga na stringi - equals
              znalazl = true;
            else
              i++;
        } //while
        return znalazl;
    } //koniec matody()



    public void wymieszajNapisy() {
        /**
         * Uruchomiana na klik na bAgain; 'symulacja' wymieszania klawiszy
         * Metoda - zwykłe, jednorazowe przejście 'bąbelkowe' (efekt zadowalający)
         */

        boolean bylSort = false;

        for (int i = 0; i < aktLevel-1; i++) {
            int result = tButtons[i+1].getNazwaPliku().compareTo(tButtons[i].getNazwaPliku());
            if (result<0) { //klawisze "zamieniamy"  mmiejscami

                String robStr1 = tButtons[i+1].getNazwaPliku();
                String robStr2 = tButtons[i+1].getText().toString();

                tButtons[i+1].setNazwaPliku(tButtons[i].getNazwaPliku());
                tButtons[i+1].setText(tButtons[i].getText());

                tButtons[i].setNazwaPliku(robStr1);
                tButtons[i].setText(robStr2);

                bylSort = true;
            }
        }
        //Gdyby nie posortowal w przebiegu powyzej, bo juz bylo posortowane rosnaco, to teraz 1-jeden przebieg na malejaco:
        if (!bylSort) {
            for (int i = 0; i < aktLevel-1; i++) {
                int result = tButtons[i+1].getNazwaPliku().compareTo(tButtons[i].getNazwaPliku());
                if (result>0) { //klawisze "zamieniamy"  mmiejscami

                    String robStr1 = tButtons[i].getNazwaPliku();
                    String robStr2 = tButtons[i].getText().toString();

                    tButtons[i].setNazwaPliku(tButtons[i+1].getNazwaPliku());
                    tButtons[i].setText(tButtons[i+1].getText());

                    tButtons[i+1].setNazwaPliku(robStr1);
                    tButtons[i+1].setText(robStr2);
                }
            }  //for
        }
        //Po sortowaniu zmienila dsie pozycja 'gorocego klawisza. Ustalamy nową pozycję (na wszelki wypadek, zeby zachowac spoknosc Rozdzielacza):
        for (int i = 0; i < aktLevel; i++) {
            if (tButtons[i].getNazwaPliku().equals(this.getAktWybrZasob())) {
                this.aktWybrKl = i;
                break;
            }
        }
    }  //koniec Metody()


    public static String getRemovedExtensionName(String name){
        /**
         * Pomocnicza, widoczna wszedzie metodka na pozbycie sie rozszerzenia z nazwy pliku - dostajemy "goly" wyraz
         */
        String baseName;
        if(name.lastIndexOf(".")==-1){
            baseName=name;
        }else{
            int index=name.lastIndexOf(".");
            baseName=name.substring(0,index);
        }
        return baseName;
    }  //koniec metody()



}
