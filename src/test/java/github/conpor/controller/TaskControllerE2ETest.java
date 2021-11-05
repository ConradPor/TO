package github.conpor.controller;

import github.conpor.model.Task;
import github.conpor.model.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

class TaskControllerE2ETest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    TaskRepository repo;

    @Test
    void httpGet_returnsAllTasks() {
        //given
        int initial = repo.findAll().size();
        repo.save(new Task("foo", LocalDateTime.now()));
        repo.save(new Task("bar", LocalDateTime.now()));
        repo.findAll().size();

        //when
        Task[] result = restTemplate.getForObject("http://localhost:" + port + "/tasks", Task[].class);

        //then

        assertThat(result).hasSize(initial + 2);

    }

    @Test
    void httpGet_returnsGivenTask() {
        // given
        String desc = "job no 1";
        Task givenTask = new Task(desc, LocalDateTime.now());
        repo.save(givenTask);
        int givenTaskId = givenTask.getId();


        // when
        Task result = restTemplate.getForObject("http://localhost:" + port + "/tasks/" + givenTaskId, Task.class);

        // then
        assertThat(result.getId()).isEqualTo(givenTaskId);
        assertThat(result.getDescription()).isEqualTo("job no 1");
    }
}