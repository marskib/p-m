package autyzmsoft.pl.profmarcin;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.TextView;


/**
 * Created by developer on 2017-08-03.
 * Button z textem zmniejszajacym sie, jesli ten tekst nie miescilby sie na tymze buttonie
 * Skłaowe i metody odnośnie zmniejszania tekstu skopiowałem z klasy ResizeTextView.java
 *
 *
 */

public class MojButton extends Button {


    //Grupa 3-ch skladowych na potrzeby zmniejszania tekstu:
    private final int mOriginalTextSize;
    private final int mMinTextSize;
    private final static int sMinSize = 20; //ponizej tej liczby nie zmniejszaj

    //Skladowa na przechowywanie nazwy pliku odpowiadajacej tekstowi na buttonie;
    //Do wykorzystania w np. Rozdzielaczu.
    private String nazwaPliku;

    public MojButton(Context context, int btnWys, float textRozmiar, CharSequence wyraz) {
        //1. Kreowanie buttona:
        super(context);
        this.setHeight(btnWys);
        this.setText(wyraz);
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX, textRozmiar);
        this.setTypeface(null, Typeface.BOLD);
        this.setMaxLines(1); //istotne - zeby nie zawijal tekstu, jesli przydlugi...
        this.nazwaPliku="";
        //this.setPadding(0,0,0,0); //zakometowuję, bo bez ustawiania paddingow klawisze z napisami wygladaja lepiej...
        /**/
        //2. Na potrzeby zmniejszania textu:
        mOriginalTextSize = (int) getTextSize();
        mMinTextSize = (int) sMinSize;
    } //koniec Konstruktora

    public void setNazwaPliku(String nazwaPliku) {

        this.nazwaPliku = nazwaPliku;
    }

    public String getNazwaPliku() {

        return nazwaPliku;
    }



    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        ViewUtil.resizeText(this, mOriginalTextSize, mMinTextSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        ViewUtil.resizeText(this, mOriginalTextSize, mMinTextSize);
    }
}

class ViewUtil {

    private ViewUtil() {}

    public static void resizeText(TextView textView, int originalTextSize, int minTextSize) {
        final Paint paint = textView.getPaint();
        final int width = textView.getWidth();
        if (width == 0) return;
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, originalTextSize);
        CharSequence robStr = textView.getText(); //na sledzenie - wyrzucic, nie potrzebna w kodzie
        float mT = paint.measureText(textView.getText().toString());
        float ratio = (width-60) / mT;  // -60, bo to Button i sa duze 'zakladki' po obu stronach tekstu...; doswiadczalnie
        if (ratio <= 1.0f) {
           textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, Math.max(minTextSize, originalTextSize * ratio));
        }
    } //koniec metody()

}
