package com.shortthirdman.bootlabs.qrcodegen.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class QrCodeService {

    /**
     * Generates a QR code image.
     *
     * @param content  The text content to encode in the QR code.
     * @param width    The width of the QR code image.
     * @param height   The height of the QR code image.
     * @param filePath The path where the QR code image will be saved.
     */
    public void generateQRCode(String content, int width, int height, String filePath) throws WriterException, IOException {
        // 1. Set QR code parameters
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");      // Character encoding
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // High error correction
        hints.put(EncodeHintType.MARGIN, 2);                   // Margin

        // 2. Generate the QR code matrix
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

        // 3. Create a BufferedImage and draw the QR code
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.WHITE); // Background color
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(Color.BLACK); // QR code color
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (bitMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }

        // 4. Save the image to the specified file path
        File outputFile = new File(filePath);
        ImageIO.write(image, "png", outputFile);
    }
}
