package dgu.edu.dnaapi.controller.albumPost;

import dgu.edu.dnaapi.config.jwt.JwtProperties;
import dgu.edu.dnaapi.controller.dto.PostSearchCondition;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.dto.albumPost.AlbumPostResponseDto;
import dgu.edu.dnaapi.domain.dto.albumPost.AlbumPostSaveRequestDto;
import dgu.edu.dnaapi.domain.dto.albumPost.AlbumPostUpdateRequestDto;

import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.domain.response.ListResponse;
import dgu.edu.dnaapi.domain.response.Message;
import dgu.edu.dnaapi.domain.response.ResponseEntity;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.service.TokenService;
import dgu.edu.dnaapi.service.UserService;
import dgu.edu.dnaapi.service.albumPost.AlbumPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@RestController
@RequiredArgsConstructor
public class AlbumPostController {

    private final TokenService tokenService;
    private final AlbumPostService albumPostService;
    private final UserService userService;

    @PostMapping("/albumPosts")
    private ResponseEntity<Message> save (
            @RequestPart("albumPost") AlbumPostSaveRequestDto requestDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> albumPostImages,
            HttpServletRequest request
    ) throws IOException {
        User findUser = getUserFromToken(request);

        if(albumPostImages == null)
            requestDto.setThumbnailImageIndex(null);
        if(albumPostImages != null && requestDto.getThumbnailImageIndex() == null)
            requestDto.setThumbnailImageIndex(0);

        Long saveId = albumPostService.save(requestDto.toEntity(findUser), albumPostImages, requestDto.getThumbnailImageIndex());
        return ResponseEntity.createSuccessResponseMessage(Message.createSuccessMessage(saveId));
    }

    private User getUserFromToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(JwtProperties.HEADER_STRING);
        if(!hasText(authorizationHeader))
            throw new DNACustomException(DnaStatusCode.TOKEN_INVALID);
        User findUser = userService.getUserByUserId(tokenService.getUserId(authorizationHeader));
        return findUser;
    }

    @GetMapping("/albumPosts/{albumPostId}")
    public ResponseEntity<Message> findById(
            @PathVariable("albumPostId") Long albumPostId,
            @RequestHeader(JwtProperties.HEADER_STRING) String headerTokenValue
    ) {
        Long userId = hasText(headerTokenValue) ? tokenService.getUserId(headerTokenValue) : null;
        AlbumPostResponseDto responseDto = albumPostService.findAlbumPostWithLikedInfoByAlbumPostIdAndUserId(albumPostId, userId);
        Message message = Message.createSuccessMessage(responseDto);
        return ResponseEntity.createSuccessResponseMessage(message);
    }

    @GetMapping("/albumPosts")
    public ResponseEntity<Message> findAll(
            @PageableDefault(size = 13, sort = "albumPostId", direction = Sort.Direction.DESC) Pageable pageable,
            PostSearchCondition condition
    ) {
        ListResponse listResponse = albumPostService.findAllAlbumPostMetaDataWithCondition(condition, pageable);
        Message message = Message.createSuccessMessage(listResponse);
        return ResponseEntity.createSuccessResponseMessage(message);
    }

    @PutMapping("/albumPosts/{albumPostId}")
    public ResponseEntity<Message> update(
            @RequestPart("albumPost") AlbumPostUpdateRequestDto requestDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> albumPostImages,
            @PathVariable("albumPostId") Long albumPostId,
            HttpServletRequest request
    ) throws IOException {
        User findUser = getUserFromToken(request);
        Long updateId = albumPostService.update(requestDto, albumPostId, findUser, albumPostImages);
        Message message = Message.createSuccessMessage(updateId);
        return ResponseEntity.createSuccessResponseMessage(message);
    }

    @DeleteMapping("/albumPosts/{albumPostId}")
    public ResponseEntity<Message> delete(
            @PathVariable("albumPostId") Long albumPostId,
            HttpServletRequest request
    ) {
        User findUser = getUserFromToken(request);
        Long deleteId = albumPostService.delete(albumPostId, findUser.getId());
        Message message = Message.createSuccessMessage(deleteId);
        return ResponseEntity.createSuccessResponseMessage(message);
    }

}
