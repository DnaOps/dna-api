package dgu.edu.dnaapi;

import dgu.edu.dnaapi.domain.Notice;
import dgu.edu.dnaapi.repository.NoticeRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NoticeRepositoryTest {
    @Autowired
    NoticeRepository noticeRepository;

    @After
    public void cleanup() {
        noticeRepository.deleteAll();
    }

    @Test
    public void 공지사항저장_불러오기() {
        String title = "공지사항 제목1";
        String content = "공지사항 내용1";

        noticeRepository.save(Notice.builder()
                .title(title)
                .content(content)
                .author("test@test.com")
                .build());

        List<Notice> noticeList = noticeRepository.findAll();

        Notice notices = noticeList.get(0);
        assertThat(notices.getTitle()).isEqualTo(title);
        assertThat(notices.getContent()).isEqualTo(content);
    }


}
