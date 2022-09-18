package com.kyyee.service;

import java.io.File;

public interface ZipService {
    void zip(File file, String dst);

    void zip(File file, String dst, String password);

    String unzip(File file, String dst);

    String unzip(File file, String dst, String password);
}
