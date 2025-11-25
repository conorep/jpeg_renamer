import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * 'Main' PSVM class.
 * @author Conor O
 * @version 0.1
 */
public class Main
{
    public static final double ONE_TENTH = 0.2;

    /**
     * PSVM function body.
     * @param args String array of argument code
     */
    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);
        String currentDir = System.getProperty("user.dir");
        File files = new File(currentDir);

        System.out.print("Please enter the part number and serial number(s) of the units corresponding " +
                "with your pictures: \n ~ ");
        String partsAndSerials = userInput.nextLine();

        System.out.print("\nNow type 'in' for incoming pics or 'out' for outgoing pics: \n ~ ");
        partsAndSerials += "_" + userInput.nextLine() + "_";

        System.out.println("Great - renaming .jpg files now.");

        renameFiles(files, partsAndSerials);

        System.out.print("\nWould you like to resize large images? Y/N: \n ~ ");
        String resizePermission = userInput.nextLine();
        resizePermission = resizePermission.toLowerCase();

        if(resizePermission.equals("y")) {
            System.out.println("Great - resizing your images now.");
            resizeFiles(files);
        } else {
            System.out.println("Noted - exiting program.");
        }
    }

    /**
     * Check if a file is a .jpg or not
     * @param analyzedFile file we need to know extension of
     * @return file extension
     */
    public static String checkForJPG(File analyzedFile) {
        String ext = "";
        String fName = analyzedFile.getName();
        int lastI = fName.lastIndexOf(".");
        if(lastI >= 0) {
            ext = fName.substring(lastI+1);
            if(!ext.equals("jpg")) {
                ext = "";
            }
        }
        return ext;
    }

    /**
     * Rename picture files based on user input
     * @param fileFolder directory that has image files
     * @param fileName the filename (without the file number)
     */
    public static void renameFiles(File fileFolder, String fileName) {
        AtomicInteger fileNumber = new AtomicInteger(1);

        if(fileFolder.isDirectory()) {
            File[] files = fileFolder.listFiles();
            assert files != null;
            List<File> filelist = Arrays.asList(files);
            filelist.forEach(f -> {
                String fileExt = checkForJPG(f);

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

    /**
     * Resize large image files
     * @param fileFolder directory that has image files
     */
    public static void resizeFiles(File fileFolder) {
        if(fileFolder.isDirectory()) {
            File[] files = fileFolder.listFiles();
            assert files != null;
            List<File> filelist = Arrays.asList(files);

            filelist.forEach(f -> {
                String fileExt = checkForJPG(f);

                if(!f.isDirectory() && !fileExt.isEmpty()) {
                    String filePath = f.getAbsolutePath();
                    try {
                        BufferedImage currImage = ImageIO.read(new File(filePath));

                        if(currImage != null) {
                            int width = currImage.getWidth();
                            int height = currImage.getHeight();
                            if(width > 1000 || height > 1000) {
                                System.out.println("Image big enough to resize. Width: " + width + ", Height: " + height);

                                int newWidth = (int)(width * ONE_TENTH);
                                int newHeight = (int)(height * ONE_TENTH);
                                Image resultingImage = currImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

                                BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
                                newImage.getGraphics().drawImage(resultingImage, 0, 0, null);

                                ImageIO.write(newImage, "jpg", new File(filePath));
                            } else {
                                System.out.println("Image small enough. Skip it. Width:" + width + "/Height:" + height);
                            }
                        } else {
                            System.out.println("Image file is not found! Path: " + filePath);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }
}
