/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gnomezgrave.gsyncj.auth;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author praneeth
 */
public class Settings implements Serializable {

    private HashMap<String, String> profilePaths;   // name,path
    private HashMap<String, Profile> profiles;   // name,profile
    private String filePath;

    public Settings() {
        profilePaths = new HashMap<String, String>();
        profiles = new HashMap<String, Profile>();
    }

    public Settings(String path) {
        this();
        this.filePath = path;
    }

    public String getProfilePath(String userName) {
        return profilePaths.get(userName);
    }

    public void addProfile(String userName, String path, Profile profile) throws IOException, ClassNotFoundException {
        profilePaths.put(userName, path);
        profiles.put(userName, profile);
        saveSettings();
    }

    public Profile removeProfile(String userName) throws IOException, ClassNotFoundException {
        profilePaths.remove(userName);
        Profile remove = profiles.remove(userName);
        saveSettings();
        return remove;
    }

    public Profile getProfileSettings(String userName) {
        return profiles.get(userName);
    }

    public int getProfileCount() {
        return profilePaths.size();
    }

    public boolean ifExists(String userName) {
        return profilePaths.containsKey(userName);
    }

    public void saveSettings() throws IOException, ClassNotFoundException {
        ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(filePath));
        oo.writeObject(this);
        oo.close();
    }

    public static Settings loadSettings(String fileName) throws IOException, ClassNotFoundException {
        ObjectInputStream oi = new ObjectInputStream(new FileInputStream(fileName));
        Settings settings = (Settings) oi.readObject();
        oi.close();
        return settings;
    }

    public static void saveSettings(Settings settings, String fileName) throws IOException, ClassNotFoundException {
        ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(fileName));
        oo.writeObject(settings);
        oo.close();
    }

    /**
     * @return the profiles
     */
    public HashMap<String, Profile> getProfiles() {
        return profiles;
    }

}
