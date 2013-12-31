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

    private HashMap<String, Profile> profiles;

    public GSyncJ() {
        profiles = new HashMap<>();

        try {
            homePath = getHomePath();
            settings = loadSettings();
        } catch (IOException ex) {
            Logger.getLogger(GSyncJ.class.getName()).log(Level.INFO, "New Settings Directory Created.", "");
            try {
                String path = getSettingsPath();
                Settings settings = new Settings(path);

                Properties p = new Properties();
                p.load(new FileInputStream("settings.properties"));
                String folder = p.getProperty("settingsFolder").trim();

                File settingsFolder = new File(homePath + "/" + folder);

                if (!settingsFolder.exists()) {
                    settingsFolder.mkdir();
                }
                saveSettings(settings, path);
            } catch (IOException ex1) {
                Logger.getLogger(GSyncJ.class.getName()).log(Level.SEVERE, null, ex1);
            } catch (InterruptedException ex1) {
                Logger.getLogger(GSyncJ.class.getName()).log(Level.SEVERE, null, ex1);
            } catch (ClassNotFoundException ex1) {
                Logger.getLogger(GSyncJ.class.getName()).log(Level.SEVERE, null, ex1);
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(GSyncJ.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GSyncJ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Settings loadSettings() throws IOException, FileNotFoundException, InterruptedException, ClassNotFoundException {
        settings = Settings.loadSettings(getSettingsPath());
        ProfileManagement.setSettings(settings);
        return settings;
    }

    public void saveSettings(Settings settings, String fileName) throws IOException, ClassNotFoundException {
        this.settings = settings;
        ProfileManagement.setSettings(settings);
        Settings.saveSettings(settings, fileName);
    }

    public void addProfile(String userName, String profileName, String key, String path) throws IOException, ClassNotFoundException {
        ProfileSettings proSet = ProfileManagement.addProfile(profileName, key, path);
        Drive drive = Authorization.getDrive(key);
        Profile profile = new Profile(userName, profileName, drive);
        profile.setProfileSettings(proSet);
        profiles.put(profileName, profile);
    }

    private String getSettingsPath() throws FileNotFoundException, IOException, InterruptedException {
        Properties p = new Properties();
        p.load(new FileInputStream("settings.properties"));
        String folder = p.getProperty("settingsFolder").trim();
        String file = p.getProperty("settingsFile").trim();

        settingsPath = homePath + "/" + folder + "/" + file;
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
            g.addProfile("Praneeth", "gnomez.grave@gmail.com", "4/9jljgEeBTJdUtZhz-B-pF34wyFmf.gnHc8sVOGC0bPvB8fYmgkJzl9zOMhgI", "/home/praneeth/Gmail");
        } catch (IOException ex) {
            Logger.getLogger(GSyncJ.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GSyncJ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
