package NFC;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import com.frutch.artogoadmin.R;

public class NFCManager
{
    private NfcAdapter adapter;

    private AlertDialog writeDialog;
    private NFCWriteTask writeTask;

    private boolean writeMode;
    private boolean readMode;

    public static final String MIME_TYPE = "artogo";

    public NFCManager(Context context) {
        this.adapter = NfcAdapter.getDefaultAdapter(context);
    }

    public boolean isTagWritable(Intent intent) {
        if(writeMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) return true;
        return false;
    }

    public boolean isTagReadable(Intent intent) {
        if(readMode && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()))
        {
            String type = intent.getType();
            if (type.startsWith(MIME_TYPE)) {
                return true;
            }
        }
        return false;
    }

    public void write(Intent intent) {
        this.writeTask.execute(intent);
    }

    public void read(Intent intent, NFCReadTask task) {
        task.execute(intent);
    }

    public void openWriteDialog(Activity activity, NFCTag tag) {
        openWriteDialog(activity, tag, new NFCWriteTask());
    }

    public void openWriteDialog(Activity activity, NFCTag tag, NFCWriteTask task) {
        if(this.adapter != null) {
            this.startWriteDispatch(activity);

            // set write task
            this.writeTask = task;
            this.writeTask.setTag(tag);

            // Build alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(activity.getString(R.string.dialog_title));
            builder.setMessage(activity.getString(R.string.dialog_message));
            builder.setNegativeButton(activity.getString(R.string.dialog_cancel), null);

            // Show alert dialog with options
            this.writeDialog = builder.create();
            this.writeDialog.setCanceledOnTouchOutside(false);
            this.writeDialog.show();
        }
    }

    public void closeWriteDialog(Activity activity)
    {
        this.writeDialog.dismiss();
        this.writeDialog = null;
        this.writeTask = null;

        stopWriteDispatch(activity);
    }

    public void startWriteDispatch(Activity activity) {
        if(this.adapter != null) {
            // set up foregound dispatch for writing purposes
            IntentFilter[] filters = getWriteIntentFilter();
            startForegroundDispatch(activity, filters);
        }
        this.writeMode = true;
    }

    public void startReadDispatch(Activity activity) {
        if(this.adapter != null) {
            // set up foregound dispatch for reading purposes
            IntentFilter[] filters = getReadIntentFilter();
            startForegroundDispatch(activity, filters);
        }
        this.readMode = true;
    }

    public void startForegroundDispatch(Activity activity, IntentFilter[] filters) {
        if(this.adapter != null) {
            if (this.readMode || this.writeMode) this.adapter.disableForegroundDispatch(activity);

            final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

            this.adapter.enableForegroundDispatch(activity, pendingIntent, filters, null);
        }
    }

    public void stopWriteDispatch(Activity activity) {
        if(this.adapter != null) {
            this.adapter.disableForegroundDispatch(activity);

            // start read dispatcher if it was active before
            if(readMode) startReadDispatch(activity);
        }
    }

    public void stopReadDispatch(Activity activity) {
        if(this.adapter != null) {
            this.adapter.disableForegroundDispatch(activity);

            // start write dispatcher if it was active before
            if(writeMode) startWriteDispatch(activity);
        }
    }

    public IntentFilter[] getIntentFilter(String action, String category, String mimeType) {
        IntentFilter filter = new IntentFilter();
        if(action != null) filter.addAction(action);
        if(category != null) filter.addCategory(category);

        if(mimeType != null) {
            try {
                filter.addDataType(mimeType);
            } catch (MalformedMimeTypeException e) {
                throw new RuntimeException("Error handling MIME type.");
            }
        }

        return new IntentFilter[] { filter };
    }

    private IntentFilter[] getWriteIntentFilter() {
        return getIntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED, null, null);
    }

    private IntentFilter[] getReadIntentFilter() {
        return getIntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED, Intent.CATEGORY_DEFAULT, MIME_TYPE + "/*");
    }
}
