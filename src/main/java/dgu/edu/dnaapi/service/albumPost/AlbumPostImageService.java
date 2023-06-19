package dgu.edu.dnaapi.service.albumPost;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AlbumPostImageService {
    AmazonS3ObjectInfo upload(MultipartFile multipartFile, String dirName) throws IOException;
    void delete(String imageKey);
}
