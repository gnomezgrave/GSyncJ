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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
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
    private HashMap<String, String> ignoredMimeTypes;

    public Storage() {
        mimeTypes = new HashMap<>();
        ignoredMimeTypes = new HashMap<>();
        loadMIMEs(mimeTypes, "driveMIMEs.properties");
    }

    private void loadMIMEs(HashMap<String, String> mimeTypes, String fileName) {
        try {
            Properties p = new Properties();
            p.load(new FileInputStream(fileName));

            for (String key : p.stringPropertyNames()) {
                mimeTypes.put(key.trim(), p.getProperty(key).trim());
            }
        } catch (IOException ex) {
            Logger.getLogger(Storage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void syncFiles(Drive drive, String rootFolder) throws IOException {
        Logger.getLogger(Storage.class.getName()).log(Level.INFO, "Syncing started.", "");
        downloadFiles(drive, "root", rootFolder);
        Logger.getLogger(Storage.class.getName()).log(Level.INFO, "Syncing Finished.", "");
    }

    private void downloadFiles(Drive drive, String rootFolderId, String rootFolder) throws IOException {

        Drive.Children.List request = drive.children().list(rootFolderId);

        do {
            try {
                ChildList children = request.execute();

                for (ChildReference child : children.getItems()) {
                    try {
                        String id = child.getId();

                        com.google.api.services.drive.model.File file = drive.files().get(id).execute();
                        //System.out.println("File Id: " + file.getMimeType() + "\t" + file.getFileExtension() + "\t" + file.getTitle());
                        String fileName = file.getTitle();

                        if (file.getMimeType().equals("application/vnd.google-apps.folder")) {
                            File f = new File(rootFolder + "/" + fileName);
                            if (!f.exists()) {
                                f.mkdir();
                                System.out.println("Folder Created " + f.getName());
                            }
                            downloadFiles(drive, id, rootFolder + "/" + fileName);
                        } else {
                            String downloadUrl = file.getDownloadUrl();
                            String ext = "";

                            GenericUrl genericUrl = null;
                            if (downloadUrl == null) {
                                //System.out.println("###Type : " + file.getMimeType());
                                try {
                                    String[] val = mimeTypes.get(file.getMimeType()).split(",");
                                    String get = file.getExportLinks().get(val[1]);
                                    ext = "." + val[0];
                                    genericUrl = new GenericUrl(get);
                                } catch (Exception e) {

                                    int ind = file.getMimeType().lastIndexOf(".");

                                    if (ind == -1) {
                                        System.out.println("Unknown format : " + file.getMimeType());
                                    } else {
                                        System.out.println("Export Forms : " + file.getExportLinks());
                                    }
                                    return;
                                }

                            } else {
                                genericUrl = new GenericUrl(downloadUrl);
                            }
                            String path = rootFolder + "/" + fileName + ext;

                            File f = new File(path);
                            if (!f.exists() || file.getModifiedDate().getValue() > f.lastModified()) {
                                downloadFile(drive, genericUrl, f);
                                f.setLastModified(file.getModifiedDate().getValue());
                            } else {
                                System.out.println("Upload... : " + fileName);
                            }
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

    public static String getMD5Checksum(String filename) throws Exception {
        byte[] b = createChecksum(filename);
        String result = "";

        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public static byte[] createChecksum(String filename) throws Exception {
        InputStream fis = new FileInputStream(filename);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        return complete.digest();
    }

    private void downloadFile(Drive drive, GenericUrl genericUrl, File outputFile) throws FileNotFoundException, IOException {
        HttpResponse resp = drive.getRequestFactory().buildGetRequest(genericUrl).execute();
        InputStream content = resp.getContent();
        FileOutputStream fo = new FileOutputStream(outputFile);
        int b = -1;
        while ((b = content.read()) != -1) {
            fo.write(b);
        }
        fo.flush();
        fo.close();
    }
}
