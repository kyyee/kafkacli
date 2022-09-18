package com.kyyee.common.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;

public final class ZipUtils {
    private static final byte[] ZIP_HEADER_1 = new byte[]{80, 75, 3, 4};
    private static final byte[] ZIP_HEADER_2 = new byte[]{80, 75, 5, 6};

    public static boolean isArchiveFile(File file) {
        if (file == null) {
            return false;
        }

        if (file.isDirectory()) {
            return false;
        }

        boolean isArchive = false;
        try (InputStream input = Files.newInputStream(file.toPath())) {
            byte[] buffer = new byte[4];
            int length = input.read(buffer, 0, 4);
            if (length == 4) {
                isArchive = (Arrays.equals(ZIP_HEADER_1, buffer)) || (Arrays.equals(ZIP_HEADER_2, buffer));
            }
        } catch (IOException e) {
            System.out.println("读取文件失败");
        }

        return isArchive;
    }
}
