package dgu.edu.dnaapi.service.albumPost;

import dgu.edu.dnaapi.service.albumPost.dto.AmazonS3ObjectInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AlbumPostImageService {
    AmazonS3ObjectInfo upload(MultipartFile multipartFile, String dirName) throws IOException;
    void delete(String imageKey);
}
