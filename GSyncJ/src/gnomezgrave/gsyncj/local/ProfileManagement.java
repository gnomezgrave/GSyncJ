/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gnomezgrave.gsyncj.local;

import com.google.api.services.drive.Drive;
import gnomezgrave.gsyncj.auth.Profile;
import gnomezgrave.gsyncj.auth.ProfileSettings;
import gnomezgrave.gsyncj.auth.Settings;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author praneeth
 */
public class ProfileManagement {

    private static Settings settings;

    public static ProfileSettings addProfile(String profileName, String key, String path) throws IOException, ClassNotFoundException {
        String profileSettingsPath = path + "/." + profileName;

        ProfileSettings proSet = new ProfileSettings(profileSettingsPath, key);
        proSet.saveSettings();

        settings.addProfilePath(profileName, path);
        settings.saveSettings();
        return proSet;
    }

    public Profile loadProfile(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectInputStream oi = new ObjectInputStream(new FileInputStream(fileName));
        Profile profile = (Profile) oi.readObject();
        oi.close();
        return profile;
    }

    public void saveProfile(Profile profile, String fileName) throws FileNotFoundException, IOException {
        ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(fileName));
        oo.writeObject(profile);
        oo.close();
    }

    /**
     * @return the settings
     */
    public static Settings getSettings() {
        return settings;
    }

    /**
     * @param aSettings the settings to set
     */
    public static void setSettings(Settings aSettings) {
        settings = aSettings;
    }
}
