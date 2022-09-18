package com.kyyee.service.impl;

import com.kyyee.common.utils.MyFileUtils;
import com.kyyee.service.ZipService;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.progress.ProgressMonitor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

public class ZipServiceImpl implements ZipService {

    @Override
    public void zip(File file, String dst) {
        zip(file, dst, null);

    }

    @Override
    public void zip(File file, String dst, String password) {

        try (ZipFile zipFile = new ZipFile(dst)) {
            if (StringUtils.isNotEmpty(password)) {
                zipFile.setPassword(password.toCharArray());
            }
            ProgressMonitor monitor = zipFile.getProgressMonitor();
            zipFile.setRunInThread(false);
            zipFile.addFolder(file);

            if (monitor.getResult() != null) {
                if (Objects.equals(monitor.getResult(), ProgressMonitor.Result.SUCCESS)) {
                    System.out.printf("filepath:%s > [filename:%s] Successfully added folder to zip\n", file.getPath(), dst);
                } else if (Objects.equals(monitor.getResult(), ProgressMonitor.Result.ERROR)) {
                    System.out.printf("filepath:%s > [filename:%s] Error occurred. Error message: %s\n", file.getPath(), dst, monitor.getException().getMessage());
                } else if (Objects.equals(monitor.getResult(), ProgressMonitor.Result.CANCELLED)) {
                    System.out.printf("filepath:%s > [filename:%s] Task cancelled\n", file.getPath(), dst);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("输出压缩文件失败！");
        }

    }

    @Override
    public String unzip(File file, String dst) {
        return unzip(file, dst, null);
    }

    @Override
    public String unzip(File file, String dst, String password) {
        try (ZipFile zipFile = new ZipFile(file)) {
            // Windows系统下使用默认使用gbk编码
            zipFile.setCharset(Charset.forName("GBK"));

            if (!zipFile.isValidZipFile()) {
                throw new RuntimeException("文件不合法，可能已经被损坏！请重新打包尝试！");
            }
            File dstDir = new File(dst);
            if (Files.isDirectory(dstDir.toPath()) && !Files.exists(dstDir.toPath())) {
                try {
                    FileUtils.forceMkdirParent(dstDir);
                } catch (IOException e) {
                    throw new RuntimeException("创建临时目录失败");
                }
            }

            if (zipFile.isEncrypted()) {
                if (StringUtils.isEmpty(password)) {
                    throw new RuntimeException("密码为空，请输入解压缩密码！");
                }
                zipFile.setPassword(password.toCharArray());
            }
            zipFile.extractAll(dst);
            final List<File> fileChild = MyFileUtils.getFileChild(dstDir);
            return fileChild.get(0).getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("解析压缩文件过程中出现错误！");
        }
    }

}
