public class CurrentSpreadsheet {

    private String currentID;
    private String folderID;
    private String name;

    public CurrentSpreadsheet(String currentID, String folderID, String name) {
        this.currentID = currentID;
        this.folderID = folderID;
        this.name = name;

    }

    public String getCurrentID() {
        return currentID;
    }

    public String getFolderID() {
        return folderID;
    }

    public void setCurrentID(String currentID) {
        this.currentID = currentID;
    }

    public void setFolderID(String folderID) {
        this.folderID = folderID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {

        return name;
    }
}
