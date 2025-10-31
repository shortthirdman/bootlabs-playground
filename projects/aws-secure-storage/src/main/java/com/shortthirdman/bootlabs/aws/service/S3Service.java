package com.shortthirdman.bootlabs.aws.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${bootlabs.amazon.s3.bucket-name}")
    private String bucketName;

    private final S3Client s3Client;
    private final S3Presigner s3presigner;

    public String uploadFile(MultipartFile file) {
        try {
            File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile);

            String key = file.getOriginalFilename();

            PutObjectRequest putObjectRequest =
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(file.getContentType())
                            .build();

            s3Client.putObject(putObjectRequest, tempFile.toPath());

            Files.deleteIfExists(tempFile.toPath());

            return "File uploaded successfully to S3 with key: " + key;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    public String uploadFileStream(MultipartFile file) {
        String filename = file.getOriginalFilename();

        if (StringUtils.isEmpty(filename)) {
            return "No file name";
        }

        try (InputStream inputStream = file.getInputStream()) {

            PutObjectRequest request =
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(filename)
                            .contentType(file.getContentType())
                            .build();

            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, file.getSize()));
            log.info("File uploaded to S3: " + filename);
            return filename;
        } catch (IOException e) {
            log.error("Failed to upload file: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    public String getPreSignedURL(String fileName) {
        try {
            GetObjectPresignRequest request =
                    GetObjectPresignRequest.builder()
                            .signatureDuration(Duration.ofMinutes(60))
                            .getObjectRequest(GetObjectRequest.builder().bucket(bucketName).key(fileName).build())
                            .build();

            URL url = s3presigner.presignGetObject(request).url();

            return url.toString();
        } catch (Exception e) {
            log.error("Failed to get presigned URL: {}", e.getMessage());
            return null;
        }
    }

    public void downloadFile(String fileName, HttpServletResponse response) {
        GetObjectRequest getRequest =
                GetObjectRequest.builder().bucket(bucketName).key(fileName).build();

        try (ResponseInputStream<GetObjectResponse> s3Stream = s3Client.getObject(getRequest);
             OutputStream out = response.getOutputStream()) {

            response.setContentType(
                    s3Stream.response().contentType() != null
                            ? s3Stream.response().contentType()
                            : "application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = s3Stream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();

        } catch (IOException e) {
            log.error("Failed to download file from S3: {}", e.getMessage());
            throw new RuntimeException("Failed to download file from S3", e);
        } catch (S3Exception e) {
            log.error("Failed to download file from secure bucket: {}", e.getMessage());
            if (e.statusCode() == 404) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else {
                throw e;
            }
        }
    }

    public boolean deleteFile(String keyName) {
        log.info("Deleting file from S3: " + keyName);
        DeleteObjectRequest deleteRequest =
                DeleteObjectRequest.builder().bucket(bucketName).key(keyName).build();

        DeleteObjectResponse response = s3Client.deleteObject(deleteRequest);

        return response.sdkHttpResponse().isSuccessful();
    }

}
