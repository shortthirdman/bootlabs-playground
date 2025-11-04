package com.shortthirdman.bootlabs.qrcodegen.controller;

import com.shortthirdman.bootlabs.qrcodegen.dto.QRCodeRequest;
import com.shortthirdman.bootlabs.qrcodegen.service.QrCodeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1")
public class QrCodeController {

    private final QrCodeService qrCodeService;

    /**
     * Defines an API endpoint to generate a QR code.
     */
    @GetMapping("/generateQRCode")
    public ResponseEntity<String> generateQRCode(
            @RequestParam String content, @RequestParam int width, @RequestParam int height, @RequestParam String filePath) {
        try {
            qrCodeService.generateQRCode(content, width, height, filePath);
            return ResponseEntity.ok("QR code generated successfully! Path: " + filePath);
        } catch (Exception e) {
            log.error("Error generating QR code:\n{}", ExceptionUtils.getFullStackTrace(e));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate QR code: " + e.getMessage());
        }
    }

    @PostMapping("/generateQRCode")
    public ResponseEntity<String> generateQRCode(@RequestBody QRCodeRequest request) {
        try {
            qrCodeService.generateQRCode(request.content(), request.width(), request.height(), request.fileName());
            return ResponseEntity.ok("QR code generated successfully! Path: " + request.fileName());
        } catch (Exception e) {
            log.error("Error generating QR code for text:\n{}", ExceptionUtils.getFullStackTrace(e));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate QR code: " + e.getMessage());
        }
    }

    @GetMapping("/download/generated-file")
    public ResponseEntity<Resource> downloadGeneratedFile() {
        // Simulate generating file content (e.g., a simple text file)
        String fileContent = "This is the content of the generated file.";
        byte[] fileBytes = fileContent.getBytes();

        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(fileBytes));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"generated_file.txt\"");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE); // Set appropriate MIME type
        headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileBytes.length));

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
}
