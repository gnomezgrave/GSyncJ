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

/**
 *
 * @author praneeth
 */
public class ProfileSettings implements Serializable {

    private String filePath;
    private String key;

    public ProfileSettings(String path) {
        filePath = path;
    }

    public ProfileSettings(String path, String key) {
        this(path);
        this.key = key;
    }

    public static ProfileSettings loadProfileSettings(String fileName) throws IOException, ClassNotFoundException {
        ObjectInputStream oi = new ObjectInputStream(new FileInputStream(fileName));
        ProfileSettings profileSettings = (ProfileSettings) oi.readObject();
        oi.close();
        return profileSettings;
    }

    public static void saveSettings(ProfileSettings settings, String fileName) throws IOException, ClassNotFoundException {
        ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(fileName));
        oo.writeObject(settings);
        oo.close();
    }

    public void saveSettings() throws IOException, ClassNotFoundException {
        ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(filePath));
        oo.writeObject(this);
        oo.close();
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }
}
