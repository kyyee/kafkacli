package com.kyyee.service.impl;

import com.kyyee.common.utils.MyFileUtils;
import com.kyyee.service.ResolveService;
import com.kyyee.service.ZipService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResolveServiceImpl implements ResolveService {

    private final ZipService zipService = new ZipServiceImpl();
    private final static String flag = "DELETE";

    @Override
    public void resolve(File srcFile) {
        //Collection<File> files = FileUtils.listFiles(srcFile, new String[]{}, true);
        final List<File> fileDir = MyFileUtils.getFileChild(srcFile);
        final List<File> files = MyFileUtils.getFileChild(fileDir.get(0));

        for (File file : files) {
            if (file.isFile() && "zip".equals(FilenameUtils.getExtension(file.getName()))) {

                String tmpFileStr = srcFile.getAbsolutePath() + File.separator + FilenameUtils.getBaseName(file.getName());
                //String tmpFileStr = fileDir.get(0).getAbsolutePath();
                final String unzipDir = zipService.unzip(file, tmpFileStr);
                File tmpFile = new File(unzipDir);
                if (tmpFile.isDirectory()) {
                    //Collection<File> imgFiles = FileUtils.listFiles(tmpFile, new String[]{}, true);
                    List<File> imgFiles = MyFileUtils.getFileChild(tmpFile);
                    if(tmpFile.getName().contains("档搜")){
                        imgFiles = new ArrayList<>();
                        final List<File> archDir = MyFileUtils.getFileChild(tmpFile);
                        for(File archFile : archDir){
                            final List<File> pics = MyFileUtils.getFileChild(archFile);
                            imgFiles.addAll(pics);
                        }
                    }
                    // todo 处理图片的目录层级，使用递归处理
                    for (File imgFile : imgFiles) {
                        if (imgFile.getName().contains(flag) && imgFile.isFile()) {
                            try {
                                FileUtils.forceDelete(imgFile);
                            } catch (IOException e) {
                                System.out.println("删除不合规的图片失败\n");
                            }
                        }
                    }
                }
                /*try {
                    //删除原压缩包
                    FileUtils.forceDelete(file);
                    //将新文件夹生成压缩包
                    //zipService.zip(tmpFile,fileDir.get(0).getAbsolutePath());
                    //删除文件夹
                    //FileUtils.forceDelete(new File(tmpFileStr));


                } catch (IOException e) {
                    System.out.println("删除失败");
                }*/
            }
        }
    }
}
