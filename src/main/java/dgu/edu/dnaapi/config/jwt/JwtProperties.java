package dgu.edu.dnaapi.config.jwt;

public interface JwtProperties {
    String SECRET = "DNAWEBPROJECTBACKENDMADEBYHOJUNLEEGpqYWJhZWdpLWp3dC10ZXN0LWFsamphYmFlZ2ktand0LXRlc3QtYWxqamFiYWVnaS1qd3QtdGVzdC1hbGpqYWJhZWd13123nandkjfan1ijaiodfnlksnfhleoo2akworldaasdfasfasdfas123123asdfp";
    int EXPIRATION_TIME = 86400000; // 10일 (1/1000초)
    int REFRESH_EXPIRATION_TIME = 864000000; // 10일 (1/1000초)
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
}
