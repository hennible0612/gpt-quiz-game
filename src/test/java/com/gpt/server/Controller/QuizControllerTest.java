package com.gpt.server.Controller;

import com.gpt.server.dto.quiz.QuizGameRequestDto;
import com.gpt.server.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

@WebMvcTest(QuizController.class)
public class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizService quizService;

    @BeforeEach
    public void setup() {
        Map<String, List<String>> responseMap = new HashMap<>();
        responseMap.put("questions", Arrays.asList("1. 선진국들이 무엇?", "2. 경제적 무엇 하나요?"));
        responseMap.put("answers", Arrays.asList("산업화", "자유시장"));

        ResponseEntity<Map<String, List<String>>> responseEntity = new ResponseEntity<>(responseMap, HttpStatus.OK);

        when(quizService.askQuestion(
                any(QuizGameRequestDto.class)))
                .thenReturn(responseEntity);
    }

    @Test
    public void testSendQuestion() throws Exception {
        mockMvc.perform(post("/api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"round\": 2, \"topic\": \"경제\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.questions", hasSize(2)))
                .andExpect(jsonPath("$.questions[0]").value(org.hamcrest.Matchers.is("1. 선진국들이 무엇?")))
                .andExpect(jsonPath("$.questions[1]").value(org.hamcrest.Matchers.is("2. 경제적 무엇 하나요?")))
                .andExpect(jsonPath("$.answers", hasSize(2)))
                .andExpect(jsonPath("$.answers[0]").value(org.hamcrest.Matchers.is("산업화")))
                .andExpect(jsonPath("$.answers[1]").value(org.hamcrest.Matchers.is("자유시장")));


        verify(quizService, times(1)).askQuestion(any(QuizGameRequestDto.class));
    }
}