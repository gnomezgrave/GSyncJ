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
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

/**
 *
 * @author praneeth
 */
public class ProfileSettings implements Serializable {

    private String filePath;
    private String userName;
    private String profileName;
    private String syncPath;
    private BidiMap<String, String> files; // fileID, path

    public ProfileSettings(String userName, String profileName, String filePath, String syncPath) {
        this.userName = userName;
        this.profileName = profileName;
        this.filePath = filePath;
        this.syncPath = syncPath;
        files = new DualHashBidiMap<>();
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
     * @return the filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @return the syncPath
     */
    public String getSyncPath() {
        return syncPath;
    }

    /**
     * @return the files
     */
    public BidiMap<String, String> getFiles() {
        return files;
    }

    public String getPathByID(String id) {
        return files.get(id);
    }

    public String getIDByPath(String path) {
        return files.getKey(path);
    }

    public void addFile(String id, String path) {
        files.put(id, path);
    }
}
