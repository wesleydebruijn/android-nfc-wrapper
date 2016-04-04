<p align="center">
	<img src="https://raw.githubusercontent.com/wesleydebruijn/android-nfc-wrapper/master/nfcwrapper.png" />
</p>
# Android NFC wrapper
A simple to use Android NFC wrapper to easily read and write NDEF data with your Android app.

The structure of an NDEF message is as follows:

<img src="https://raw.githubusercontent.com/wesleydebruijn/android-nfc-wrapper/master/ndefmessage.png" />


## Setup
- Create a new package in your Android project to store the library _(source code located at: src/lib)_
- Place the value xml file (nfc.xml) in the res/values folder of your Android project
- Make sure the references in the NFC library are correct to be able to use the resource (R) object
- Change the MIME TYPE in src/lib/NFCManager.java and in res/values/nfc.xml to your own preference

## Usage
**AndroidManifest.xml**
```
<uses-permission android:name="android.permission.NFC" />
<uses-feature android:name="android.hardware.nfc" android:required="true" />
```
Add the following code to the activity where you want to use the NFC read and/or write tasks. For this example, the MainActivity is used.

**Method: onCreate**
```
private NFCManager nfcManager;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Initialize NFC Manager
    nfcManager = new NFCManager(this);
    handleIntent(getIntent());
}
```
### Reading
**Method: onNewIntent**
```
@Override
protected void onNewIntent(Intent intent) {
    handleIntent(intent);
}
```
**Method: handleIntent**
```
private void handleIntent(Intent intent) {
  if(nfcManager.isTagReadable(intent))
  {
    nfcManager.read(intent, new NFCReadTask() {
        @Override
        protected void onPostExecute(NFCTag tag) {
            // do stuff with NFCTag : tag
        }
    });
  }
}
```
**Method: onPause**
```
@Override
public void onPause() {
    super.onPause();
    nfcManager.stopReadDispatch(this);
}
```
**Method: onResume**
```
public void onResume() {
    super.onResume();
    nfcManager.startReadDispatch(this);
}
```
### Writing
The following code can be used in the method where you want to open the write dialog.
```
NFCTag tag = new NFCTag("123", "article");
nfcManager.openWriteDialog(this, tag);
```
**Method: handleIntent**
```
private void handleIntent(Intent intent) {
    if(nfcManager.isTagWritable(intent))
    {
        nfcManager.write(intent, new NFCWriteTask() {
            @Override
            protected void onPostExecute(Boolean result) {
                nfcManager.closeWriteDialog(MainActivity.this);
                // do stuff with boolean : result
            }
        });
    }
}
```
