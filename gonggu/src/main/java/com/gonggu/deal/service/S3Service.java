package com.gonggu.deal.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.gonggu.deal.domain.Deal;
import com.gonggu.deal.domain.DealImage;
import com.gonggu.deal.repository.DealImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class S3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private final DealImageRepository dealImageRepository;

    public void upload(MultipartFile[] files, Deal deal) throws IOException {
        for (MultipartFile file : files) {
            String s3FileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

            ObjectMetadata objMeta = new ObjectMetadata();
            objMeta.setContentLength(file.getInputStream().available());

            amazonS3.putObject(bucket, s3FileName, file.getInputStream(), objMeta);
            DealImage dealImage = DealImage.builder()
                    .originFileName(file.getOriginalFilename())
                    .newFileName(s3FileName)
                    .filePath(amazonS3.getUrl(bucket, s3FileName).toString())
                    .deal(deal).build();
            dealImageRepository.save(dealImage);
        }
    }

}
