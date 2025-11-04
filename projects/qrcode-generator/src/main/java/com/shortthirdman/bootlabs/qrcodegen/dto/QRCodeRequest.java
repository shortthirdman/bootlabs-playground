package com.shortthirdman.bootlabs.qrcodegen.dto;

import java.util.Set;

public record QRCodeRequest(String content, int width, int height, String fileName) {

    /**
     * Constructor for QRCodeRequest.
     *
     * @param content  The content to encode in the QR code.
     * @param width    The width of the QR code image.
     * @param height   The height of the QR code image.
     * @param fileName The file path where the QR code image will be saved.
     */
    public QRCodeRequest {
        // Validation can be added here if necessary
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be a positive integer");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be a positive integer");
        }
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }
        if (!endsWithAnyExtension(fileName, Set.of("png", "jpg", "jpeg", "gif"))) {
            throw new IllegalArgumentException("File name must end with a valid image extension (png, jpg, jpeg, gif)");
        }
    }

    private boolean endsWithAnyExtension(String fileName, Set<String> extensions) {
        if (fileName == null || extensions == null || extensions.isEmpty()) {
            return false;
        }

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return false; // No extension or empty extension
        }

        String fileExtension = fileName.substring(dotIndex + 1).toLowerCase();
        return extensions.contains(fileExtension);
    }
}
