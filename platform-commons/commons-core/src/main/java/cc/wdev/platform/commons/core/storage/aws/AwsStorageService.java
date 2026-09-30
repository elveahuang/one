package cc.wdev.platform.commons.core.storage.aws;

import cc.wdev.platform.commons.core.storage.StorageService;
import cc.wdev.platform.commons.core.storage.StorageUtils;
import cc.wdev.platform.commons.core.storage.model.FileObject;
import cc.wdev.platform.commons.core.storage.model.FileOptions;
import cc.wdev.platform.commons.core.storage.model.FileUploadResult;
import cc.wdev.platform.commons.core.storage.model.GenerateUrlRequest;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.JacksonUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static cc.wdev.platform.commons.utils.FileUtils.newTempFile;

/**
 * @author elvea
 * @see AwsStorageService
 * @see StorageService
 */
@Slf4j
public record AwsStorageService(AwsStorageConfig config) implements StorageService<S3Client> {

    /**
     * @see StorageService#getClient()
     */
    @Override
    public S3Client getClient() {
        return S3Client.builder()
            .credentialsProvider(this.getCredentialsProvider())
            .endpointOverride(URI.create(this.config.getEndpoint()))
            .region(Region.of(this.config.getRegion()))
            .forcePathStyle(this.config.isPathStyleEnabled())
            .build();
    }

    /**
     * @see StorageService#closeClient(Object)
     */
    @Override
    public void closeClient(S3Client client) {
        if (client != null) {
            client.close();
        }
    }

    /**
     * @see StorageService#getBucket()
     */
    @Override
    public String getBucket() {
        return this.config.getBucket();
    }

    /**
     * @see StorageService#getEndpoint()
     */
    @Override
    public String getEndpoint() {
        return this.config.getEndpoint();
    }

    /**
     * @see StorageService#getDomain()
     */
    @Override
    public String getDomain() {
        return this.config.getDomain();
    }

    /**
     * @see StorageService#getUrl(GenerateUrlRequest)
     */
    @Override
    public FileObject<?> getUrl(GenerateUrlRequest request) {
        try (S3Presigner s3Presigner = getS3Presigner()) {
            GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(this.getBucket())
                .key(request.getKey())
                .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(objectRequest)
                .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            log.info("Presigned URL: [{}]", presignedRequest.url().toString());
            log.info("HTTP method: [{}]", presignedRequest.httpRequest().method());
            return AwsFileObject.builder().key(request.getKey()).url(presignedRequest.url().toExternalForm()).build();
        }
    }

    /**
     * @see StorageService#getFile(String)
     */
    @Override
    public FileObject<?> getFile(String path) {
        try (S3Client client = getClient()) {
            GetObjectRequest request = GetObjectRequest.builder()
                .bucket(this.config.getBucket())
                .key(path)
                .build();

            try (ResponseInputStream<GetObjectResponse> is = client.getObject(request)) {
                File localTempFile = newTempFile(StorageUtils.generateFilename(path));
                FileUtils.writeByteArrayToFile(localTempFile, IOUtils.toByteArray(is));
                // 构建文件信息
                return AwsFileObject.builder().key(path).object(localTempFile).build();
            }
        } catch (Exception e) {
            throw new ServiceException("Fail to get AWS file.", e);
        }
    }

    /**
     * @see StorageService#uploadFile(InputStream, FileOptions)
     */
    @Override
    public FileObject<?> uploadFile(InputStream is, FileOptions options) throws Exception {
        try (S3Client client = this.getClient()) {
            // 处理请求参数 - 生成文件对象标识
            String key = StorageUtils.generateFileKey(options);

            PutObjectRequest request = PutObjectRequest.builder()
                .key(key)
                .bucket(this.config.getBucket())
                .build();

            // 上传文件
            RequestBody body = RequestBody.fromInputStream(is, options.getSize());
            PutObjectResponse response = client.putObject(request, body);
            log.info("AWS putObject response - [{}].", JacksonUtils.toJson(response));

            // 处理响应结果
            FileUploadResult result = FileUploadResult.builder().key(key).build();
            return AwsFileObject.builder().key(key).response(response).etag(response.eTag()).result(result).url(this.getUrl(key).getUrl()).build();
        }
    }

    /**
     * @see StorageService#download(String, OutputStream)
     */
    @Override
    public void download(String path, OutputStream os) {
        try (S3Client client = this.getClient()) {
            GetObjectRequest request = GetObjectRequest.builder()
                .bucket(this.config.getBucket())
                .key(path)
                .build();

            try (ResponseInputStream<GetObjectResponse> is = client.getObject(request)) {
                log.info("AWS getObject download response - [{}].", JacksonUtils.toJson(is.response()));
                is.transferTo(os);
            }
        } catch (Exception e) {
            throw new ServiceException("Fail to download AWS file.", e);
        }
    }

    private S3Presigner getS3Presigner() {
        return S3Presigner.builder()
            .s3Client(getClient())
            .endpointOverride(URI.create(this.config.getEndpoint()))
            .region(Region.of(this.config.getRegion()))
            .credentialsProvider(this.getCredentialsProvider())
            .serviceConfiguration(this.getS3Configuration())
            .build();
    }

    private S3Configuration getS3Configuration() {
        return S3Configuration.builder()
            .pathStyleAccessEnabled(this.config.isPathStyleEnabled())
            .chunkedEncodingEnabled(this.config.isChunkedEncodingEnabled())
            .build();
    }

    private AwsCredentialsProvider getCredentialsProvider() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(this.config.getAccessKey(), this.config.getSecretKey()));
    }

    @Override
    public Map<String, String> presignedObjectUrlMap(Collection<String> fileKeys) {
        if (CollectionUtils.isEmpty(fileKeys)) {
            return Collections.emptyMap();
        }

        Map<String, String> urlMap = Maps.newHashMapWithExpectedSize(fileKeys.size());
        try (S3Presigner presigner = getS3Presigner()) {
            for (String fileKey : fileKeys) {
                try {
                    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                        .bucket(getBucket())
                        .key(fileKey)
                        .build();

                    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(30))
                        .getObjectRequest(getObjectRequest)
                        .build();

                    String url = presigner.presignGetObject(presignRequest).url().toExternalForm();
                    urlMap.put(fileKey, url);
                } catch (Exception e) {
                    log.error("AWS S3 generatePresignedUrl failed. key={}", fileKey, e);
                }
            }
        } catch (Exception e) {
            log.error("AWS S3 presigner initialization failed.", e);
        }

        return urlMap;
    }
}
