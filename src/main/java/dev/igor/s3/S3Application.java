package dev.igor.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.FileOutputStream;
import java.io.IOException;

@SpringBootApplication
public class S3Application implements ApplicationRunner {
	private static final Logger logger = LoggerFactory.getLogger(S3Application.class);
	private final S3Client client;

    public S3Application(S3Client client) {
        this.client = client;
    }

    public static void main(String[] args) {
		SpringApplication.run(S3Application.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		// List Buckets
		ListBucketsResponse buckets = client.listBuckets();
		logger.info("Size {}", buckets.buckets().size());
		buckets.buckets().forEach(i -> logger.info("Bucket {}", i));

		// Configuração da solicitação para listar objetos
		ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
				.bucket("file-bucket-igor")
				.build();
		// Executa a solicitação de listagem de objetos
		ListObjectsV2Response listObjectsResponse = client.listObjectsV2(listObjectsRequest);
		listObjectsResponse.contents().forEach(i -> logger.info("Object {}", i.key()));

		GetObjectRequest request = GetObjectRequest.builder()
				.bucket("file-bucket-igor")
				.key("File.txt")
				.build();
		ResponseInputStream<GetObjectResponse> object = client.getObject(request);
		byte[] bytes = object.readAllBytes();
		try (FileOutputStream fos = new FileOutputStream("files/File.txt")) {
			fos.write(bytes);
		} catch (IOException e) {
			logger.error("Failed to save object");
		}
	}
}
