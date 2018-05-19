package com.madcodez.flashlight;

import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;

import java.io.DataOutputStream;
import java.io.IOException;

@RequiresApi(api = Build.VERSION_CODES.N)
public class FlashTile extends TileService {

    boolean on = false;

    @Override
    public void onClick() {
        super.onClick();
        turnOn();
    }

    Tile tile;
    Icon icon;
    @Override
    public void onStartListening() {
        tile = getQsTile();
        tile.setIcon(Icon.createWithResource(this,
                R.mipmap.ic_off));
        tile.setLabel(getString(R.string.tile_label));
        tile.setContentDescription(getString(R.string.tile_content_description));
        tile.setState(Tile.STATE_INACTIVE);
        tile.updateTile();
    }

    public void turnOn() {
        Process p;
        try {
            p = Runtime.getRuntime().exec("su");

            // Attempt to write a file to a root-only
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            if (!on) {
//                os.writeBytes("echo \"255\" >/sys/class/leds/torch-light/brightness\n");
                os.writeBytes("echo \"255\" >/sys/class/leds/led:flash_torch/brightness\n"); // cm12
                on = true;
                tile.setState(Tile.STATE_ACTIVE);
                icon = Icon.createWithResource(getApplicationContext(), R.mipmap.ic_on);
getQsTile().updateTile();
                getQsTile().setIcon(icon);
                getQsTile().updateTile();
            } else {
                os.writeBytes("echo \"0\" >/sys/class/leds/led:flash_torch/brightness\n"); // cm12

//                os.writeBytes("echo \"0\" >/sys/class/leds/torch-light/brightness\n");
                on = false;
//                tile.setIcon(Icon.createWithResource(this,R.mipmap.ic_off));
                tile.setState(Tile.STATE_INACTIVE);

                icon = Icon.createWithResource(getApplicationContext(), R.mipmap.ic_off);
                getQsTile().setIcon(icon);
                getQsTile().updateTile();

            }
            // Close the terminal
            os.writeBytes("exit\n");
            os.flush();
            try {
                p.waitFor();
                if (p.exitValue() != 255) {
                    // TODO Code to run on success
                    //       toastMessage("root");
                } else {
                    // TODO Code to run on unsuccessful
//                    toastMessage("not root");
                }
            } catch (InterruptedException e) {
                // TODO Code to run in interrupted exception
                //              toastMessage("not root");
            }
        } catch (IOException e) {
            // TODO Code to run in input/output exception
            //        toastMessage("not root");
        }

    }
}
