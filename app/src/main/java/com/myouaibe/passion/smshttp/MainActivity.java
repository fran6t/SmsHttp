package com.myouaibe.passion.smshttp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.owlike.genson.Genson;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    /* On declare notre champs text et notre bouton */
    private EditText txtIdentifier;
    private EditText txtDescription;
    private EditText txtBrand;
    private EditText txtPrice;
    private Button btnUpdate;

    private EditText txtIdSms;
    private EditText txtQuelnumero;
    private EditText txtQuelmessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* On relie notre objet graphique à nos varaible */
        txtIdentifier = (EditText) findViewById(R.id.txtIdentifier);
        txtDescription = (EditText) findViewById(R.id.txtDescription);
        txtBrand = (EditText) findViewById(R.id.txtBrand);
        txtPrice = (EditText) findViewById(R.id.txtPrice);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);

        txtIdSms = (EditText) findViewById(R.id.txtidSms);
        txtQuelnumero = (EditText) findViewById(R.id.txtQuelnumero);
        txtQuelmessage = (EditText) findViewById(R.id.txtQuelmessage);

        /*On ajoute un gestionnaire d'evenement sur notre bouton */
        btnUpdate.setOnClickListener( btnUpdateListener );

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


}
