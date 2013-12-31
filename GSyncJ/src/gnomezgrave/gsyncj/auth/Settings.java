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

    private HashMap<String, String> profiles;
    private String filePath;

    public Settings() {
        profiles = new HashMap<String, String>();
    }

    public Settings(String path) {
        this();
        this.filePath = path;
    }

    public String getProfilePath(String name) {
        return profiles.get(name);
    }

    public void addProfilePath(String name, String path) {
        profiles.put(name, path);
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
}
