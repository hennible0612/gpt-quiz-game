package com.gpt.server.service;

import static org.junit.jupiter.api.Assertions.*;
import com.gpt.server.dto.quiz.QuizGameRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class QuizServiceTest {
    @InjectMocks
    private QuizService quizService;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAskQuestion() {
        // given
        ResponseEntity<String> mockResponse = new ResponseEntity<>("{\"id\":\"chat..v\",\"object\":\"chat.completion\",\"created\":16,\"model\":\"gpt-3.5-turbo-0301\",\"usage\":{\"prompt_tokens\":76,\"completion_tokens\":138,\"total_tokens\":214},\"choices\":[{\"message\":{\"role\":\"assistant\",\"content\":\"1. 선진국들이 무엇? (정답: 산업화)\\n2. 경제적 무엇 하나요? (정답: 자유시장)\\n3. 경제적 무엇이라고 하나요? (정답: 경제정책)\"},\"finish_reason\":\"stop\",\"index\":0}]}", HttpStatus.OK);
        when(restTemplate.exchange(
                any(String.class),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(Class.class)))
                .thenReturn(mockResponse);

        QuizGameRequestDto requestDto = new QuizGameRequestDto();
        requestDto.setRound(3);
        requestDto.setTopic("경제");

        // when
        ResponseEntity<Map<String, List<String>>> response = quizService.askQuestion(requestDto);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, List<String>> body = response.getBody();
        assertNotNull(body);
        assertEquals(3, body.get("questions").size());
        assertEquals("1. 선진국들이 무엇?", body.get("questions").get(0));
        assertEquals("2. 경제적 무엇 하나요?", body.get("questions").get(1));
        assertEquals("3. 경제적 무엇이라고 하나요?", body.get("questions").get(2));
        assertEquals(3, body.get("answers").size());
        assertEquals("산업화", body.get("answers").get(0));
        assertEquals("자유시장", body.get("answers").get(1));
        assertEquals("경제정책", body.get("answers").get(2));
    }

    @Test
    public void testAskQuestion_HTTPError() {
        // given
        when(restTemplate.exchange(
                any(String.class),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(Class.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        QuizGameRequestDto requestDto = new QuizGameRequestDto();
        requestDto.setRound(2);
        requestDto.setTopic("경제");

        // when
        ResponseEntity<Map<String, List<String>>> response = quizService.askQuestion(requestDto);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testAskQuestion_OtherError() {
        // given
        when(restTemplate.exchange(
                any(String.class),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(Class.class)))
                .thenThrow(new RuntimeException());

        QuizGameRequestDto requestDto = new QuizGameRequestDto();
        requestDto.setRound(2);
        requestDto.setTopic("경제");

        // when
        ResponseEntity<Map<String, List<String>>> response = quizService.askQuestion(requestDto);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}