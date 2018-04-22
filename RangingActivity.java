package com.hackru.beaconapp;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class RangingActivity extends Activity implements BeaconConsumer, RangeNotifier {

    private static String TAG = "RangingActivity";
    private BeaconManager mBeaconManager;
    private Beacon closest;
    private List<Identifier> beaconIDs;
    private List<String> songUrls;
    private MediaPlayer player = new MediaPlayer();

    public RangingActivity() {
    }

    @Override
    public void onResume(){
        super.onResume();
        mBeaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        // Detect the URL frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        mBeaconManager.bind(this);
    }

    public void onBeaconServiceConnect() {
        Region region = new Region("all-beacons-region", null, null, null);
        try {
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mBeaconManager.addRangeNotifier(this);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        double closestdistance = 10.0;
        Beacon closestbeacon = null;
        for (Beacon beacon: beacons) {
            if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x10) {
                // This is a Eddystone-URL frame
                String url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
                Log.e(TAG, "I see a beacon transmitting a url: " + url +
                        " approximately " + beacon.getDistance() + " meters away.");
                if(!beaconIDs.contains(beacon.getId1())){
                    newBeaconFound(beacon);
                }
                if(beacon.getDistance() < closestdistance){
                    closestdistance = beacon.getDistance();
                    closestbeacon = beacon;
                }
            }
        }
        if(closest != closestbeacon){
            closest = closestbeacon;
            playSong(closest);
        }

    }

    public void newBeaconFound(Beacon beacon){
        beaconIDs.add(beacon.getId1());
        String input = "";
        // open dialog box/send notification
        // wait for user input

        // read
        songUrls.add(input);
    }

    @Override
    public void onPause() {
        super.onPause();
        mBeaconManager.unbind(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        closest = null;
        setContentView(R.layout.activity_ranging);
    }

    public void playSong(Beacon beacon){
        try {
            player.setDataSource(songUrls.get(beaconIDs.indexOf(beacon.getId1())));
        } catch (IOException e) {
            Log.d(TAG,"Oh, oh. URL does not seem to exist");
            /* inform user */
            return;
        }
        try {
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.start();
    }

    public void temp(View view)
    {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(i, "Select music"), 1);
    }
}
