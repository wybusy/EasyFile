package com.wybusy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EasyFile {
    public static Path mkDirs(String path) {
        Path filePath = Paths.get(path);
        if (!Files.exists(filePath)) {
            try {
                Files.createDirectories(filePath);
            } catch (IOException e) {
            }
        }
        return filePath;
    }

    public static Long lastModify(String path, String fileName) {
        Long result = -1l;
        Path filePath = Paths.get(path + "/" + fileName);
        try {
            result = Files.getLastModifiedTime(filePath).toMillis();
        } catch (IOException e) {
        }
        return result;
    }
}
