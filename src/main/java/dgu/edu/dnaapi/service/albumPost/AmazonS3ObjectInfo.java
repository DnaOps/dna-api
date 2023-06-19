package dgu.edu.dnaapi.service.albumPost;

import lombok.Getter;

@Getter
public class AmazonS3ObjectInfo {
    private final String key;
    private final String imageURL;

    public AmazonS3ObjectInfo(String key, String imageURL) {
        this.key = key;
        this.imageURL = imageURL;
    }
}
