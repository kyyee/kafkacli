package com.kyyee.kafkacli.common.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyFileUtils {
    /**
     * 获取文件夹子目录
     * @param file
     * @return
     */
    public static List<File> getFileChild(File file){
        final File[] files = file.listFiles();
        if(files.length == 0){
            return new ArrayList<>();
        }
        return Arrays.asList(files);
    }
}
