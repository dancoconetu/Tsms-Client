import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dic on 08-10-2015.
 */
public class FolderInfo {
    SystemInfo systemInfo = new SystemInfo();
    String folderPath = systemInfo.getPathForHome();
    String pythonExtension = "py";
    public File[] getAllFolderFiles()
    {
        File folder = new File(folderPath);
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
    {
        File folder = new File(folderPath);
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


}
