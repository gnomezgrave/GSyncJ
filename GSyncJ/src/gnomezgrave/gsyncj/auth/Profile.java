/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gnomezgrave.gsyncj.auth;

import com.google.api.services.drive.Drive;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
public class Profile implements Serializable {

    private volatile Drive drive;
    private String profileName;
    private String userName;
    private String filePath;
    private String syncPath;
    private BidiMap<String, String> files; // fileID, path

    public Profile(String userName, String profileName, String filePath, String syncPath) {
        this.userName = userName;
        this.profileName = profileName;
        this.filePath = filePath + "/" + profileName;
        this.syncPath = syncPath;
        files = new DualHashBidiMap<>();
    }

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

    public static Profile loadProfileSettings(String fileName) throws IOException, ClassNotFoundException {
        ObjectInputStream oi = new ObjectInputStream(new FileInputStream(fileName));
        Profile profile = (Profile) oi.readObject();
        oi.close();
        return profile;
    }

    public static void saveSettings(Profile profile, String fileName) throws IOException, ClassNotFoundException {
        ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(fileName));
        oo.writeObject(profile);
        oo.close();
    }

    public void saveSettings() throws IOException, ClassNotFoundException {
        saveSettings(this, filePath);
    }

    /**
     * @return the driveName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @return the drive
     */
    public Drive getDrive() {
        return drive;
    }

    /**
     * @param drive the drive to set
     */
    public void setDrive(Drive drive) {
        this.drive = drive;
    }

    /**
     * @return the syncPath
     */
    public String getSyncPath() {
        return syncPath;
    }

    /**
     * @param syncPath the syncPath to set
     */
    public void setSyncPath(String syncPath) {
        this.syncPath = syncPath;
    }

}
