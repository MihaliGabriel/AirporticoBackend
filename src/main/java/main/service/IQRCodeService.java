package main.service;


import com.google.zxing.WriterException;

import java.io.IOException;
import java.nio.file.Path;

public interface IQRCodeService {
    byte[] generateQRCode(String text, int width, int height) throws WriterException, IOException;
    Path generateQRCodeFile(String text, int width, int height, String filePath) throws WriterException, IOException;
}
