package com.myouaibe.passion.smshttp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class PokeReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            // récupérer SMS
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;

            if (bundle != null)
            {
                // récupérer le SMS
                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];
                String body ="";
                for (int i=0; i<msgs.length; i++){
                    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                    body =  msgs[i].getMessageBody().toString();
                    if(body.startsWith("##androPoke##")){
                        // action a effectuer à la réception du SMS: ici affichage du mot POKE
                        // lorsque le SMS commence par ##androPoke##
                        Toast.makeText(context, "POKE", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(context, body, Toast.LENGTH_SHORT).show();
                }

            }

        }
    }

}
