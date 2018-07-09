import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

public class GoogleDrive {
    private String APP_NAME;

    private String CREDENTIAL_FOLDER;
    private String CLIENT_SCERET;
    private JsonFactory FACTORY;
    private List<String> scopes;

    public GoogleDrive(){
        APP_NAME = "Time Tracker";
        CREDENTIAL_FOLDER = "credentialsDrive";
        CLIENT_SCERET = "credentials_drive.json";
        FACTORY = JacksonFactory.getDefaultInstance();
        scopes = Collections.singletonList(DriveScopes.DRIVE);
    }


    public Drive makeServiceHandler(HttpTransport transport) throws IOException {
        InputStream in = GoogleSheets.class.getResourceAsStream(CLIENT_SCERET);
        GoogleClientSecrets secret = GoogleClientSecrets.load(FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow.Builder flowbuild = new GoogleAuthorizationCodeFlow.Builder(transport, FACTORY, secret, scopes);
        flowbuild.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(CREDENTIAL_FOLDER)));
        flowbuild.setAccessType("offline");
        GoogleAuthorizationCodeFlow flow = flowbuild.build();

        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        Drive.Builder serviceBuild = new Drive.Builder(transport, FACTORY, credential);
        serviceBuild.setApplicationName(APP_NAME);
        return serviceBuild.build();
    }

    //makes a time tracker folder if one does not exist already. return false if folder not found, true otherwise.
    public String findFolder(Drive service_handler) throws IOException{
        List<File> list = service_handler.files().list().setQ("(name = 'Time Tracker') and (mimeType = 'application/vnd.google-apps.folder')").execute().getFiles();
        if(list.size() == 0){
            File newFolder = new File();
            newFolder.setName("Time Tracker");
            newFolder.setMimeType("application/vnd.google-apps.folder");
            File folder = service_handler.files().create(newFolder).execute();
            return folder.getId();
        }
        return list.get(0).getId();
    }

    //sends all files with prefix "TimeTracker" to target folder
    public void toFolder(String parent_id, Drive service_handler) throws IOException{
        List<File> list = service_handler.files().list().setQ("(name contains 'TimeTracker') and (mimeType = 'application/vnd.google-apps.spreadsheet') and (not '" + parent_id + "' in parents)").execute().getFiles();

        for(int i = 0; i < list.size(); i++){
            File source = list.get(i);
            String sourceId = source.getId();

            List<String> sourceParents = source.getParents();
            StringBuilder formerParents = new StringBuilder();
            if(sourceParents != null) {
                for (String former_parent : sourceParents) {
                    formerParents.append(former_parent);
                    formerParents.append(",");
                }
            }

            File temp = new File();

            service_handler.files().update(sourceId, temp)
                    .setAddParents(parent_id)
                    .setRemoveParents(formerParents.toString())
                    .execute();

        }
    }
}
