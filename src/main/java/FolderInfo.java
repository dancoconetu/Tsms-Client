import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dic on 08-10-2015.
 */
public class FolderInfo {

    File folderPath;
    SystemInfo systemInfo;
    public FolderInfo(SystemInfo systemInfo)
    {
        this.systemInfo = new SystemInfo();
         folderPath = systemInfo.getPathForHome();
        String pythonExtension = "py";
    }
    //SystemInfo systemInfo = new SystemInfo();

    public File[] getAllFolderFiles()
    {
        File folder = folderPath;
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
            if (getFileExtension(listOfFiles[i]).equals("IIQ"))
            {
                System.out.println("This is an image");
            }
        }
        return listOfFiles;
    }

    public ArrayList<File> getAllFilesWithExtension(String extension)
    {   //folderPath = systemInfo.getPathForHome();
        File folder = folderPath;
        File[] listOfFiles = folder.listFiles();
        ArrayList<File> allFilesWithExtension = new ArrayList<File>();

        for (int i = 0; i < listOfFiles.length; i++)
        if (getFileExtension(listOfFiles[i]).equals(extension))
        {
            allFilesWithExtension.add( listOfFiles[i]);

        }
        return allFilesWithExtension;
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    public void createFolder(String name)
    {
        //System.out.println("Folder created: " +( folderPath.getCanonicalPath() ) );
        try {
            File f = new File(folderPath.getCanonicalPath() + "\\" + name);
            System.out.println(f.mkdir() + " : " + f.getCanonicalFile());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
