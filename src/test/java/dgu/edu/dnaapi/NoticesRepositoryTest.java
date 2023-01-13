package dgu.edu.dnaapi;

import dgu.edu.dnaapi.domain.Notices;

import dgu.edu.dnaapi.domain.dto.NoticesDeleteRequestDto;
import dgu.edu.dnaapi.domain.dto.NoticesSaveRequestDto;
import dgu.edu.dnaapi.domain.dto.NoticesUpdateRequestDto;
import dgu.edu.dnaapi.repository.NoticesRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NoticesRepositoryTest {
    @LocalServerPort
    private int port;

    @Autowired
    NoticesRepository noticesRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @After
    public void cleanup() {
        noticesRepository.deleteAll();
    }

    @Test
    public void 공지사항저장_불러오기() {
        String title = "공지사항 제목1";
        String content = "공지사항 내용1";

        noticesRepository.save(Notices.builder()
                .title(title)
                .content(content)
//                .author("test@test.com")
                .build());

        List<Notices> noticeList = noticesRepository.findAll();

        Notices notices = noticeList.get(0);
        assertThat(notices.getTitle()).isEqualTo(title);
        assertThat(notices.getContent()).isEqualTo(content);
    }

    @Test
    public void 공지사항_등록하기() throws Exception {
        String title = "my title";
        String content = "my content";

        NoticesSaveRequestDto requestDto = NoticesSaveRequestDto.builder()
                .title(title)
                .content(content)
                .build();

        String url = "http://localhost:" + port + "/boards/notices";

        ResponseEntity<Long> responseEntity = restTemplate.
                postForEntity(url, requestDto, Long.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Notices> all = noticesRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent()).isEqualTo(content);
    }

    @Test
    public void 공지사항_수정하기() throws Exception {
        Notices savedNotices = noticesRepository.save(Notices.builder()
                .title("ex-title")
                .content("ex-content")
//                .author("km")
                .build());

        Long updateId = savedNotices.getNoticeId();
        String expectedTitle = "title2";
        String expectedContent = "content2";

        NoticesUpdateRequestDto requestDto = NoticesUpdateRequestDto.builder()
                .noticeId(updateId)
                .title(expectedTitle)
                .content(expectedContent)
                .build();
        String url = "http://localhost:" + port + "/boards/notices";

        HttpEntity<NoticesUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

        ResponseEntity<Long> responseEntity = restTemplate
                .exchange(url, HttpMethod.PUT, requestEntity, Long.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Notices> all = noticesRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(expectedTitle);
        assertThat(all.get(0).getContent()).isEqualTo(expectedContent);
    }

    @Test
    public void 공지사항_삭제하기() {
        Notices savedNotices = noticesRepository.save(Notices.builder()
                .title("ex-title")
                .content("ex-content")
//                .author("km")
                .build());

        Long deleteId = savedNotices.getNoticeId();

        NoticesDeleteRequestDto requestDto = NoticesDeleteRequestDto.builder()
                .noticeId(deleteId)
                .build();
        String url = "http://localhost:" + port + "/boards/notices";

        HttpEntity<NoticesDeleteRequestDto> requestEntity = new HttpEntity<>(requestDto);

        ResponseEntity<Long> responseEntity = restTemplate
                .exchange(url, HttpMethod.DELETE, requestEntity, Long.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Notices> all = noticesRepository.findAll();

        assertThat(all.size()).isEqualTo(0);
    }
}
