# android-nfc-wrapper
A simple to use Android NFC wrapper to easily read and write NDEF data with your Android device.

## Setup
- Create a new package in your Android project to store the library _(source code can be found in: src/lib)_
- Put the value xml file (nfc.xml) in the res/values folder of your Android project
- Make sure the references in the NFC library are correct _(so you are able use the R.string.value)_
- Change the MIME TYPE in src/lib/NFCManager.java and res/values/nfc.xml to your own preference

## Usage
**AndroidManifest.xml**
```
<uses-permission android:name="android.permission.NFC" />
<uses-feature android:name="android.hardware.nfc" android:required="true" />
```
### General
Add the following code to the activity where you want to use the NFC read and/or write tasks.

**Method: onCreate**
```
private NFCManager nfcManager;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_layout);

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
**Method: onNewIntent**
```
private void handleIntent(Intent intent) {
  if(nfcManager.isTagReadable(intent)) {
    nfcManager.read(intent, new NewReadTask());
  }
}
```
**Inner class: NewReadTask**

Within this class you define the actions you are doing when you receive the data from the read task, for example: updating the UI.
```
private class NewReadTask extends NFCReadTask {
    @Override
    protected void onPostExecute(NFCTag tag) {
        if (tag != null) {
            setTitle(tag.data);
        }
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
