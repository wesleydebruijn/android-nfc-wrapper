package NFC;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/*!
 * Android NFC wrapper
 *
 * Copyright 2016 Wesley de Bruijn
 * Released under the MIT license
 * https://github.com/wesleydebruijn/android-nfc-wrapper/LICENSE.md
 */

public class NFCTag {
    private NdefRecord record;
    private String data;
    private String type;

    public NFCTag(NdefRecord record) {
        this.record = record;
    }

    public String getData() {
        return this.data;
    }

    public String getType() {
        return this.type;
    }

    public NdefRecord getRecord() {
        return this.record;
    }

    public NFCTag(String data, String type) {
        this.data = data;
        this.type = NFCManager.MIME_TYPE + "/" + type;
    }

    public NFCTag decode() throws UnsupportedEncodingException {
        if(record != null) {
            this.data = new String(record.getPayload(), StandardCharsets.UTF_8);
            this.type = new String(record.getType(), StandardCharsets.UTF_8);
        }

        return this;
    }

    public NdefMessage encode() {
        this.record = NdefRecord.createMime(this.type.toLowerCase(), this.data.getBytes());
        return new NdefMessage(new NdefRecord[] { this.record });
    }
}
