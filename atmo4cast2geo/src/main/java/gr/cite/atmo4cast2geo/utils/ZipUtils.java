package gr.cite.atmo4cast2geo.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    public static File createZipFile(File target, List<File> sources) {
        try {
            OutputStream fos = new FileOutputStream(target);
            ZipOutputStream zipOutputStream = new ZipOutputStream(fos);
            for (File resultFile : sources) {
                ZipEntry entry = new ZipEntry(resultFile.getName());
                zipOutputStream.putNextEntry(entry);
                InputStream fis = new FileInputStream(resultFile);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOutputStream.write(bytes, 0, length);
                }
                fis.close();
                Files.deleteIfExists(resultFile.toPath());
            }
            zipOutputStream.flush();
            zipOutputStream.close();
            fos.close();
            return target;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<File> extractZipFile(File file, String parentPath) {
        List<File> files = new ArrayList<>();
        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
            ZipEntry zipEntry = zis.getNextEntry();
            while(zipEntry != null) {
                if (!zipEntry.isDirectory()) {
                    int lastIndex = Math.max(zipEntry.getName().lastIndexOf("/"), 0);
                    File extractedFile = new File(parentPath + "/" + zipEntry.getName().substring(lastIndex));
                    OutputStream fos = new FileOutputStream(extractedFile);
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = zis.read(bytes)) >= 0) {
                        fos.write(bytes, 0, length);
                    }
                    fos.flush();
                    fos.close();
                    files.add(extractedFile);
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return files;
    }
}
