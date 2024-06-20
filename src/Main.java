import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 'Main' PSVM class.
 * @author Conor O
 * @version 0.1
 */
public class Main
{
    /**
     * PSVM function body.
     * @param args String array of argument code
     */
    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);
        System.out.print("Please enter the part number and serial number(s) of the units corresponding " +
                "with your pictures: \n ~ ");
        String partsAndSerials = userInput.nextLine();
        System.out.print("\nNow type 'in' for incoming pics or 'out' for outgoing pics: \n ~ ");
        partsAndSerials += "_" + userInput.nextLine() + "_";
        System.out.println("Great - renaming .jpg files now!");
        renameFiles(System.getProperty("user.dir"), partsAndSerials);
    }

    /**
     * Rename picture files using
     * @param folder the file-containing folder
     * @param fileName  the filename (without the file number)
     */
    public static void renameFiles(String folder, String fileName) {
        AtomicInteger fileNumber = new AtomicInteger(1);

        File file = new File(folder);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            assert files != null;
            List<File> filelist = Arrays.asList(files);
            filelist.forEach(f -> {
                String fileExt = "";
                String fName = f.getName();
                int lastI = fName.lastIndexOf(".");
                if(lastI >= 0) {
                    fileExt = fName.substring(lastI+1);
                    if(!fileExt.equals("jpg")) {
                        fileExt = "";
                    }
                }
                if(!f.isDirectory() && !fileExt.isEmpty()) {
                    String absPath = f.getAbsolutePath();
                    int lastBackslash = absPath.lastIndexOf("\\");
                    String newName = absPath.substring(0, lastBackslash+1) + fileName + fileNumber.get() + ".jpg";
                    boolean isRenamed = f.renameTo(new File(newName));
                    if(isRenamed) {
                        fileNumber.getAndIncrement();
                        System.out.printf("Renamed this file %s to  %s%n", f.getName(), newName);
                    } else {
                        System.out.printf("%s file is not renamed to %s%n", f.getName(), newName);
                    }
                }
            });
        }
    }
}
