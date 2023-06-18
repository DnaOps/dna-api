package dgu.edu.dnaapi.service.albumPost;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AlbumPostImageService {
    String upload(MultipartFile multipartFile, String dirName) throws IOException;
    void delete(String imageUrl);
}
