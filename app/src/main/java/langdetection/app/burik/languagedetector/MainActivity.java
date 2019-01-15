package langdetection.app.burik.languagedetector;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.commons.lang3.StringUtils;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static java.lang.String.valueOf;


public class MainActivity extends AppCompatActivity {

    private static final int FILE_CODE = 1;
    final String LOG_TAG = "myLogs";

    Uri uri = null;
    StringBuffer textL = null;

    TextView txt;
    TextView showText;
    TextView counter;
    String answerLang = "";
    List<String> textRead = new ArrayList<String>();
    String langsArray [] = null;
    int counterLangsArray [] = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt = (TextView) findViewById(R.id.language);
        txt.setMovementMethod(new ScrollingMovementMethod());

        showText = (TextView) findViewById(R.id.text);
        showText.setMovementMethod(new ScrollingMovementMethod());

        counter = (TextView) findViewById(R.id.counter);
        counter.setMovementMethod(new ScrollingMovementMethod());
    }

    public void Open(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        startActivityForResult(intent, FILE_CODE);
    }
   @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                openPath(uri);
            }
        }
    }
    public void openPath(Uri uri) {
        InputStream is = null;
        String str = "";
        StringBuffer buf = new StringBuffer();
        try {
            is = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            if (is != null) {
                while ((str = reader.readLine()) != null) {
                    buf.append(str + "\n");
                }

                //Convert your stream to data here
                is.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        showText.setText(buf);
        textL = buf;

    }

    // Read text from file
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void ReadBtn(View v) {

        if (uri == null) {
            txt.setText("OPEN TEXT FILE");
        } else {
            InputStream is = null;
            try {

                // открываем поток для чтения
                is = getContentResolver().openInputStream(uri);
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                // List<String> readText = new ArrayList<>();

                String str = "";
                // читаем содержимое
                while ((str = br.readLine()) != null) {
                    Log.d(LOG_TAG, str);
                    //  Toast.makeText(getBaseContext(), str,Toast.LENGTH_SHORT).show();
                    //txt.setText(str);
                    textRead = Arrays.asList(str.trim().split("\\s+"));

                }

                //pobranie i przetwarzanie słowników w tablicy
                List<String> deutschVocabulary = Arrays.asList(LoadData("deutsch.txt").trim().split("\\s+"));
                List<String> englishVocabulary = Arrays.asList(LoadData("english.txt").trim().split("\\s+"));
                List<String> espanolVocabulary = Arrays.asList(LoadData("espanol.txt").trim().split("\\s+"));
                List<String> francaisVocabulary = Arrays.asList(LoadData("francais.txt").trim().split("\\s+"));
                List<String> italianoVocabulary = Arrays.asList(LoadData("italiano.txt").trim().split("\\s+"));
                List<String> danishVocabulary = Arrays.asList(LoadData("dansk.txt").trim().split("\\s+"));
                List<String> nederlandsVocabulary = Arrays.asList(LoadData("nederlands.txt").trim().split("\\s+"));
                List<String> norwegianVocabulary = Arrays.asList(LoadData("norsk.txt").trim().split("\\s+"));
                //liczba słów porywnywanych ze słówników
                int counterDeutsch = WordCounter(textRead, deutschVocabulary);
                int counterEnglish = WordCounter(textRead, englishVocabulary);
                int counterEspanol = WordCounter(textRead, espanolVocabulary);
                int counterFrancais = WordCounter(textRead, francaisVocabulary);
                int counterItaliano = WordCounter(textRead, italianoVocabulary);
                int counterDansk = WordCounter(textRead, danishVocabulary);
                int counterNederl = WordCounter(textRead, nederlandsVocabulary);
                int counterNorsk = WordCounter(textRead, norwegianVocabulary);

                counterLangsArray = new int[]{counterDeutsch, counterEnglish, counterEspanol, counterFrancais,
                        counterItaliano, counterDansk, counterNederl, counterNorsk};
                langsArray = new String[]{"GERMAN LANGUAGE", "ENGLISH LANGUAGE", "SPANISH LANGUAGE", "FRENCH LANGUAGE",
                        "ITALIAN LANGUAGE", "DANISH LANGUAGE", "DUTCH LANGUAGE", "NORWEGIAN LANGUAGE"};

                String strArray[] = new String[counterLangsArray.length];
                String lngArray[] = new String[langsArray.length];
                for (int i = 0; i < counterLangsArray.length; i++) {
                    strArray[i] = String.valueOf(counterLangsArray[i]);
                    lngArray[i] = String.valueOf(langsArray[i]);
                }

                int maxAt = 0;
                for (int i = 0; i < counterLangsArray.length; i++) {
                    maxAt = counterLangsArray[i] > counterLangsArray[maxAt] ? i : maxAt;
                }
                txt.setText(langsArray[maxAt]);

//            float procent = ProcentFrequencyWord(counterDeutsch);
//            counter.setText(String.format("%.2f", procent));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //funkcja przeczytywania baz słów(wyczytuje słowniki) z pliku assets
    public String LoadData(String inFile) {
        String tContents = "";
        try {
            InputStream stream = getAssets().open(inFile);
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            tContents = new String(buffer);
        } catch (IOException e) {
            // Handle exceptions here
        }
        return tContents;
    }

    //funkcja która liczy słowa ktorę spotykają się w słownikach
    public int WordCounter(List<String> text, List<String> Vocabulary){
        int count=0;
        for (int i = 0; i < text.size(); i++) {
            for (int j = 0; j < Vocabulary.size(); j++) {
                if (text.get(i).equals(Vocabulary.get(j))) {
                    count++;
                }
            }
        }
        return count;
    }
    //przetwarzanie w procenty
    public float ProcentFrequencyWord(int countsLang){
        int countWordsInText = textRead.size();
        float divideProc=((float)countsLang / countWordsInText)*100;
        return divideProc;
    }

    public void SimilarLangs(View view) {
        String langsWithNumbers= "";
        for(int i=0;i<langsArray.length;i++) {
            langsWithNumbers =langsWithNumbers + String.valueOf(langsArray[i]+" - " + counterLangsArray[i]+"\n");
        }
        counter.setText(langsWithNumbers);
    }
}
