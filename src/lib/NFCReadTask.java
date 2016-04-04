package NFC;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.util.Log;
import android.nfc.Tag;
import android.os.AsyncTask;
import java.io.UnsupportedEncodingException;

/*!
 * Android NFC wrapper
 *
 * Copyright 2016 Wesley de Bruijn
 * Released under the MIT license
 * https://github.com/wesleydebruijn/android-nfc-wrapper/LICENSE.md
 */

 public class NFCReadTask extends AsyncTask<Intent, Void, NFCTag> {

     @Override
     protected NFCTag doInBackground(Intent... params) {
         Intent intent = params[0];

         Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
         Ndef ndef = Ndef.get(tag);

         if (ndef == null) {
             return null;
         }

         NdefMessage ndefMessage = ndef.getCachedNdefMessage();

         if(ndefMessage != null) {
             NdefRecord[] records = ndefMessage.getRecords();
             for (NdefRecord ndefRecord : records) {
                 try {
                     return new NFCTag(ndefRecord).decode();
                 } catch (UnsupportedEncodingException e) {
                     Log.e("NFC", "Unsupported Encoding", e);
                 }
             }
         }

         return null;
     }
 }
