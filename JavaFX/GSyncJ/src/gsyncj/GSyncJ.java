/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gsyncj;

import com.google.api.services.drive.Drive;
import gnomezgrave.gsyncj.auth.Authorization;
import gnomezgrave.gsyncj.auth.Profile;
import gnomezgrave.gsyncj.auth.ProfileSettings;
import gnomezgrave.gsyncj.auth.Settings;
import gnomezgrave.gsyncj.local.ProfileManagement;
import gnomezgrave.gsyncj.local.Storage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author praneeth
 */
public class GSyncJ {

    private Settings settings;
    private String settingsPath;
    private String homePath;
    private HashMap<String, Drive> profiles;

    private HashMap<String, ProfileSettings> profileSettings; // name,Profile

    public GSyncJ() {
        profileSettings = new HashMap<>();
        profiles = new HashMap<>();
        try {
            homePath = getHomePath();
            settings = loadSettings();
        } catch (IOException ex) {
            Logger.getLogger(GSyncJ.class.getName()).log(Level.INFO, "New Settings Directory Created.", "");
            try {
                String path = getSettingsPath(true);
                Settings settings = new Settings(path);

                Properties p = new Properties();
                p.load(new FileInputStream("settings.properties"));
                String folder = p.getProperty("settingsFolder").trim();

                File settingsFolder = new File(homePath + "/" + folder);

                if (!settingsFolder.exists()) {
                    settingsFolder.mkdir();
                }
                saveSettings(settings, path);
            } catch (IOException | ClassNotFoundException | InterruptedException ex1) {
                Logger.getLogger(GSyncJ.class.getName()).log(Level.SEVERE, null, ex1);
            }

        } catch (InterruptedException | ClassNotFoundException ex) {
            Logger.getLogger(GSyncJ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Settings loadSettings() throws IOException, FileNotFoundException, InterruptedException, ClassNotFoundException {
        settings = Settings.loadSettings(getSettingsPath(true));
        profileSettings = settings.getProfileSettings();
        ProfileManagement.setSettings(settings);
        return settings;
    }

    public void saveSettings(Settings settings, String fileName) throws IOException, ClassNotFoundException {
        this.settings = settings;
        ProfileManagement.setSettings(settings);
        Settings.saveSettings(settings, fileName);
    }

    public void saveSettings() throws IOException, ClassNotFoundException {
        settings.saveSettings();
    }

    public Profile addProfile(String userName, String profileName, String syncPath, String key) throws IOException, ClassNotFoundException, FileNotFoundException, InterruptedException {
        String path = getSettingsPath(false);
        Drive drive = Authorization.getDrive(key);
        profiles.put(userName, drive);
        ProfileSettings proSet = ProfileManagement.addProfile(userName, profileName, path, syncPath);
        Profile profile = new Profile(userName, profileName, drive);
        profile.setProfileSettings(proSet);
        profileSettings.put(profileName, proSet);
        return profile;
    }

    public Drive getDrive(String userName) {
        return profiles.get(userName);
    }

    public void setDrive(String userName, Drive drive) {
        profiles.put(userName, drive);
    }

    public void synchronize(Drive drive, String userName) throws IOException {
        String profilePath = settings.getProfilePath(userName);
        new Storage().syncFiles(drive, profilePath);
    }

    public boolean checkProfileExsistence(String userName) {
        return settings.ifExists(userName);
    }

    private String getSettingsPath(boolean isFullPath) throws FileNotFoundException, IOException, InterruptedException {
        Properties p = new Properties();
        p.load(new FileInputStream("settings.properties"));
        String folder = p.getProperty("settingsFolder").trim();
        String file = p.getProperty("settingsFile").trim();

        settingsPath = homePath + "/" + folder + (isFullPath ? "/" + file : "");
        return settingsPath;
    }

    private String getHomePath() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(new String[]{"sh", "-c", "echo ~"}).getInputStream()));

        String path = br.readLine().trim();
        br.close();
        return path;
    }

    public static void main(String[] args) {
        try {
            GSyncJ g = new GSyncJ();

            g.addProfile("Praneeth", "gnomez.grave@gmail.com", "/home/praneeth/G", "4/3KCTydkoLKptaJdBNQ052KWuZPsL.QspqsS9mtLAcPvB8fYmgkJyP5jS_hgI");
        } catch (IOException | ClassNotFoundException | InterruptedException ex) {
            Logger.getLogger(GSyncJ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the settings
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * @return the profileSettings
     */
    public HashMap<String, ProfileSettings> getProfiles() {
        return profileSettings;
    }

    /**
     * @param profiles the profileSettings to set
     */
    public void setProfiles(HashMap<String, ProfileSettings> profiles) {
        this.profileSettings = profiles;
    }
}
