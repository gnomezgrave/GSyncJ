/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gnomezgrave.gsyncj.auth;

import com.google.api.services.drive.Drive;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 * @author praneeth
 */
public class Profile implements Serializable {

    private Drive drive;
    private String profileName;
    private String userName;
    private ProfileSettings profileSettings;

    public Profile(String userName, String profileName, Drive drive) {
        this.userName = userName;
        this.profileName = profileName;
        this.drive = drive;
    }

    public void sync(){
        
    }
    
    public void loadProfileSettings(String fileName) throws IOException, ClassNotFoundException {
        profileSettings = ProfileSettings.loadProfileSettings(fileName);
    }

    public void saveProfileSettings(ProfileSettings profileSettings, String fileName) throws FileNotFoundException, IOException {
        ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(fileName));
        oo.writeObject(profileSettings);
        oo.close();
    }

    /**
     * @return the drive
     */
    public Drive getProfile() {
        return drive;
    }

    /**
     * @param profile the drive to set
     */
    public void setProfile(Drive profile) {
        this.drive = profile;
    }

    /**
     * @return the driveName
     */
    public String getName() {
        return userName;
    }

    /**
     * @param name the driveName to set
     */
    public void setName(String name) {
        this.userName = name;
    }

    /**
     * @return the driveSettings
     */
    public ProfileSettings getProfileSettings() {
        return profileSettings;
    }

    /**
     * @param profileSettings the driveSettings to set
     */
    public void setProfileSettings(ProfileSettings profileSettings) {
        this.profileSettings = profileSettings;
    }

}
