package NFC;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;
import android.nfc.NdefMessage;
import android.nfc.tech.NdefFormatable;
import android.nfc.Tag;
import android.os.AsyncTask;

/*!
 * Android NFC wrapper
 *
 * Copyright 2016 Wesley de Bruijn
 * Released under the MIT license
 * https://github.com/wesleydebruijn/android-nfc-wrapper/LICENSE.md
 */

public class NFCWriteTask extends AsyncTask<Intent, Void, Boolean> {

    protected NFCTag tag;

    public void setTag(NFCTag tag) {
        this.tag = tag;
    }

    @Override
    protected Boolean doInBackground(Intent... params) {
        Intent intent = params[0];

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NdefMessage message = this.tag.encode();

        if (tag != null) {
            try {
                Ndef ndefTag = Ndef.get(tag);
                if (ndefTag == null) {
                    NdefFormatable ndefForm = NdefFormatable.get(tag);
                    if (ndefForm != null) {
                        ndefForm.connect();
                        ndefForm.format(message);
                        ndefForm.close();
                    }
                }
                else {
                    ndefTag.connect();
                    ndefTag.writeNdefMessage(message);
                    ndefTag.close();
                }
                return true;
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
