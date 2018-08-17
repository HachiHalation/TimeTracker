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
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

public class GoogleDrive {

    private Drive handler;

    public GoogleDrive(HttpTransport transport, Credential credential) throws IOException{

        handler = makeServiceHandler(transport, credential);

    }


    private Drive makeServiceHandler(HttpTransport transport, Credential credential) throws IOException {

        Drive.Builder serviceBuild = new Drive.Builder(transport, Options.getFACTORY(), credential);
        serviceBuild.setApplicationName(Options.getAppName());
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

    //sends specific file to parent folder
    public void toFolder(String source_id, String parent_id, Drive service_handler) throws IOException{

            File source = handler.files().get(source_id).execute();

            List<String> sourceParents = source.getParents();
            StringBuilder formerParents = new StringBuilder();
            if(sourceParents != null) {
                for (String former_parent : sourceParents) {
                    formerParents.append(former_parent);
                    formerParents.append(",");
                }
            }

            File temp = new File();

            service_handler.files().update(source_id, temp)
                    .setAddParents(parent_id)
                    .setRemoveParents(formerParents.toString())
                    .execute();

    }

    public Drive getHandler() {
        return handler;
    }
}
