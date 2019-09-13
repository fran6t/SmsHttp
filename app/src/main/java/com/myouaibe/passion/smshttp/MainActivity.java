package com.myouaibe.passion.smshttp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.owlike.genson.Genson;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    /* Pour la gestion des preferences */
    private TextView textView;
    private EditText editText;
    private Button applyTextButton;
    private Button saveButton;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    private String text;



    /* On declare notre champs text et notre bouton */
    private EditText txtIdentifier;
    private EditText txtDescription;
    private EditText txtBrand;
    private EditText txtPrice;
    private Button   btnUpdate;
    private Button   btnTestServer;

    private EditText txtIdSms;
    private EditText txtQuelnumero;
    private EditText txtQuelmessage;
    private TextView txtQuelstatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Pour la gestion des preferences */
        textView = (TextView) findViewById(R.id.textview);
        editText = (EditText) findViewById(R.id.edittext);
        applyTextButton = (Button) findViewById(R.id.apply_text_button);
        saveButton = (Button) findViewById(R.id.save_button);

        applyTextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick( View view){
                textView.setText(editText.getText().toString());
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick( View view){
               saveData();
            }
        });

        /* On charge les preferences */
        loadData();
        updateViews();

        /* On relie notre objet graphique à nos varaible */
        txtIdentifier = (EditText) findViewById(R.id.txtIdentifier);
        txtDescription = (EditText) findViewById(R.id.txtDescription);
        txtBrand = (EditText) findViewById(R.id.txtBrand);
        txtPrice = (EditText) findViewById(R.id.txtPrice);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);

        txtIdSms = (EditText) findViewById(R.id.txtidSms);
        txtQuelnumero = (EditText) findViewById(R.id.txtQuelnumero);
        txtQuelmessage = (EditText) findViewById(R.id.txtQuelmessage);

        txtQuelstatus = (TextView) findViewById(R.id.txtQuelstatus);

        /*On ajoute un gestionnaire d'evenement sur notre bouton */
        btnUpdate.setOnClickListener( btnUpdateListener );

        IsServeurOnLine();

    }
    @Override
    protected void onResume(){
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL("http://192.168.0.1/SmsHttp/index.php");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    InputStream in = new BufferedInputStream( urlConnection.getInputStream());
                    Scanner scanner = new Scanner( in );
                    final Article article = new Genson().deserialize( scanner.nextLine(), Article.class);
                    Log.i("SMS-HTTP","Result == " + article);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtIdentifier.setText("" + article.getIdArticle());
                            txtDescription.setText( article.getDescription());
                            txtBrand.setText( article.getBrand());
                            txtPrice.setText( "" + article.getPrice());
                        }
                    });

                    in.close();
                } catch( Exception e) {
                    Log.e("SMS-HTTP","Cannot fund http server", e);
                } finally {
                    if ( urlConnection != null) urlConnection.disconnect();
                }
            }
        }).start();
    }

    private View.OnClickListener btnUpdateListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection urlConnection = null;
                    try {
                        /* L'URL appelée renvoi un json de la forme {"idSms":0,"quelnumero":"0622243401","quelmessage":"Le texte du SMS à envoyer"} */
                        URL url = new URL("http://192.168.0.1/SmsHttp/envoi_sms.php");
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");

                        InputStream in = new BufferedInputStream( urlConnection.getInputStream());
                        Scanner scanner = new Scanner( in );
                        final SMS_envoi sms_envoi = new Genson().deserialize( scanner.nextLine(), SMS_envoi.class);
                        Log.i("SMS-HTTP","Result sms_envoi == " + sms_envoi);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txtIdSms.setText("" + sms_envoi.getIdSms());
                                txtQuelnumero.setText("" + sms_envoi.getQuelnumero());
                                txtQuelmessage.setText( sms_envoi.getQuelmessage());
                                /* On tente l'envoi du sms */
                                boolean status_Envoi = false;
                                try{
                                    SmsManager sms = SmsManager.getDefault();
                                    sms.sendTextMessage(sms_envoi.getQuelnumero(), null, sms_envoi.getQuelmessage(), null, null);
                                    status_Envoi = true;
                                }
                                catch(Exception e){
                                    status_Envoi = false;
                                }
                                /* On appel le php pour indiquer comment ça s'est passé */
                                crSMS(sms_envoi.getIdSms(), status_Envoi);

                                //SmsManager.getDefault().sendTextMessage(sms_envoi.getQuelnumero(), null, sms_envoi.getQuelmessage(), null, null);
                            }
                        });

                        in.close();
                    } catch( Exception e) {
                        Log.e("SMS-HTTP","Cannot fund http server", e);
                    } finally {
                        if ( urlConnection != null) urlConnection.disconnect();
                    }
                }
            }).start();
        }
    };


    /* Function test si serveur distant ok
    La function appel le serveur qui est dans les preferences et met à jour le text de status
     */
    public void IsServeurOnLine(){
        Log.i("SMS-HTTP","Appui sur bouton test");
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    /* L'URL appelée renvoi un json de la forme {"Status":"OnLine"} */
                    //URL url = new URL("http://192.168.0.1/SmsHttp/status.php");
                    URL url = new URL(text +"/status.php");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    InputStream in = new BufferedInputStream( urlConnection.getInputStream());
                    Scanner scanner = new Scanner( in );
                    final SERVEUR_status serveur_status = new Genson().deserialize( scanner.nextLine(), SERVEUR_status.class);
                    Log.i("SMS-HTTP","Result SERVEUR_status == " + serveur_status.getQuelstatus());

                    maj_txtQuelstatus(serveur_status.getQuelstatus());

                    in.close();
                } catch( Exception e) {
                    Log.e("SMS-HTTP","Cannot fund http server", e);
                    maj_txtQuelstatus("Serveur Introuvable");
                } finally {
                    if ( urlConnection != null) urlConnection.disconnect();
                }
            }
        }).start();
    }

    public void maj_txtQuelstatus( final String affichequoi){
        /* On met à jour le champs txtQuelstatus */
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtQuelstatus.setText("" + affichequoi);
            }
        });
    }
    /* Fonction permettant d'indiquer au PHP si le SMS est bien parti */
    public void crSMS(final int IdSms, final Boolean status_Envoi){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    /* L'URL appelée renvoi un json de la forme {"idSms":0,"quelnumero":"0622243401","quelmessage":"Le texte du SMS à envoyer"} */
                    URL url = new URL("http://192.168.0.1/SmsHttp/envoi_sms.php?IdSms=" + IdSms + "&CrSms=" + status_Envoi);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    //InputStream in = new BufferedInputStream( urlConnection.getInputStream());
                    //Scanner scanner = new Scanner( in );
                    //final SMS_envoi sms_envoi = new Genson().deserialize( scanner.nextLine(), SMS_envoi.class);
                    Log.i("SMS-HTTP","Requete" + "http://192.168.0.1/SmsHttp/envoi_sms.php?IdSms=" + IdSms + "&CrSms=" + status_Envoi);

                    //in.close();
                } catch( Exception e) {
                    Log.e("SMS-HTTP","Cannot fund http server", e);
                } finally {
                    if ( urlConnection != null) urlConnection.disconnect();
                }
            }
        }).start();
    }

    /* Les trois methodes pour la gestion des preferences */
    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT,textView.getText().toString());

        editor.apply();

        Toast.makeText(this,"Data saved", Toast.LENGTH_SHORT).show();
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        text = sharedPreferences.getString(TEXT, "http://192.168.0.1/SmsHttp"); // C'est ici defValue qu'on peut mettre la valeur par defaut
    }

    public void updateViews(){
        textView.setText(text);
    }
}


