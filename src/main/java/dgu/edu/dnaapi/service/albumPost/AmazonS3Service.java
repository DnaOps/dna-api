package dgu.edu.dnaapi.service.albumPost;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.exception.DNACustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.springframework.util.StringUtils.hasText;

@Service
@RequiredArgsConstructor
public class AmazonS3Service implements AlbumPostImageService {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucketName;

    @Transactional
    public String upload(MultipartFile file, String dirName) throws IOException {

        if(!hasText(file.getOriginalFilename()) || !isValidateFileExtension(getFileExtension(file.getOriginalFilename())))
            throw new DNACustomException(DnaStatusCode.INVALID_File_Format);

        String fileName = dirName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        ObjectMetadata objectMetadata = getObjectMetadata(file);

        try(InputStream inputStream = file.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            return amazonS3Client.getUrl(bucketName, fileName).toString();
        } catch(IOException e) {
            throw new DNACustomException(DnaStatusCode.AMAZONS3_IO_EXCEPTION);
        }
    }

    private ObjectMetadata getObjectMetadata(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        return objectMetadata;
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private boolean isValidateFileExtension(String fileExtension) {
        if (!hasText(fileExtension)) {
            throw new DNACustomException(DnaStatusCode.INVALID_File_Format);
        }
        Set<String> validFileExtensionSet = new HashSet<>(Arrays.asList(
                ".jpg", ".jpeg", ".png", ".JPG", ".JPEG", ".PNG"
        ));
        if (!validFileExtensionSet.contains(fileExtension)) {
            return false;
        }
        return true;
    }

    public void delete(String imageUrl) {
        amazonS3Client.deleteObject(bucketName, imageUrl);
    }
}
