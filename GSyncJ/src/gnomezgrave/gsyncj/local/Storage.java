/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gnomezgrave.gsyncj.local;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author praneeth
 */
public class Storage {

    private HashMap<String, String> mimeTypes;

    public Storage() {
        mimeTypes = new HashMap<>();
        loadMIMEs(mimeTypes);
    }

    private void loadMIMEs(HashMap<String, String> mimeTypes) {
        try {
            Properties p = new Properties();
            p.load(new FileInputStream("driveMIMEs.properties"));
            for (String key : p.stringPropertyNames()) {
                mimeTypes.put(key.trim(), p.getProperty(key).trim());
            }
        } catch (IOException ex) {
            Logger.getLogger(Storage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void downloadAllFiles(Drive drive, String rootFolderId, String rootFolder) throws IOException {
        Drive.Children.List request = drive.children().list(rootFolderId);

        do {
            try {
                ChildList children = request.execute();

                for (ChildReference child : children.getItems()) {
                    try {
                        String id = child.getId();

                        com.google.api.services.drive.model.File file = drive.files().get(id).execute();
                        System.out.println("File Id: " + file.getMimeType() + "\t" + file.getFileExtension() + "\t" + file.getTitle());
                        String fileName = file.getTitle();
                        if (file.getMimeType().equals("application/vnd.google-apps.folder")) {
                            File f = new File(rootFolder + "/" + fileName);
                            f.mkdir();
                            System.out.println("Folder Created " + f.getName());
                            downloadAllFiles(drive, id, rootFolder + "/" + fileName);
                        } else {
                            String downloadUrl = file.getDownloadUrl();
                            String ext = "";
                            
                            GenericUrl genericUrl;
                            if (downloadUrl == null) {
                                String[] val = mimeTypes.get(file.getMimeType()).split(",");
                                String get = file.getExportLinks().get(val[1]);
                                ext = "." + val[0];
                                System.out.println("GET " + get);
                                genericUrl = new GenericUrl(get);
                            } else {
                                genericUrl = new GenericUrl(downloadUrl);
                            }
                            HttpResponse resp = drive.getRequestFactory().buildGetRequest(genericUrl).execute();
                            InputStream content = resp.getContent();
                            FileOutputStream fo = new FileOutputStream(rootFolder + "/" + fileName + ext);
                            int b = -1;
                            while ((b = content.read()) != -1) {
                                fo.write(b);
                            }
                            fo.flush();
                            fo.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                request.setPageToken(children.getNextPageToken());
            } catch (IOException e) {
                System.out.println("An error occurred: " + e);
                request.setPageToken(null);
            }
        } while (request.getPageToken() != null && request.getPageToken().length() > 0);
    }
}
