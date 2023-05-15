package com.gpt.server.service;

import com.gpt.server.config.ChatGptConfig;
import com.gpt.server.dto.quiz.QuizDto;
import com.gpt.server.dto.quiz.QuizGameRequestDto;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class QuizService {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuizService.class);

    private final RestTemplate restTemplate;

    @Autowired
    public QuizService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


     // 퀴즈, 답 생성하여 반환
    public ResponseEntity<Map<String, List<String>>> askQuestion(QuizGameRequestDto requestDto) {

        try {
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(createRequestBody(requestDto), headers);
            ResponseEntity<String> response = executeRequest(ChatGptConfig.URL, HttpMethod.POST, entity);
            LOGGER.info("Response received: {}", response.getBody());

            List<QuizDto> quizDtos = parseResponse(response.getBody());
            Map<String, List<String>> responseMap = extractQuizData(quizDtos);

            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            LOGGER.error("HTTP error occurred: ", e);
            return new ResponseEntity<>(null, e.getStatusCode());
        } catch (Exception e) {
            LOGGER.error("An error occurred: ", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    protected ResponseEntity<String> executeRequest(String url, HttpMethod method, HttpEntity<String> entity) {
        return restTemplate.exchange(url, method, entity, String.class);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(ChatGptConfig.MEDIA_TYPE));
        headers.add(ChatGptConfig.AUTHORIZATION, ChatGptConfig.BEARER + ChatGptConfig.API_KEY);
        return headers;
    }

    // quizDtos을 답,질문 분리하여 반환
    private Map<String, List<String>> extractQuizData(List<QuizDto> quizDtos) {
        List<String> questions = new ArrayList<>();
        List<String> answers = new ArrayList<>();

        for (QuizDto quiz : quizDtos) {
            questions.add(quiz.getQuestion());
            answers.add(quiz.getAnswer());
        }

        Map<String, List<String>> responseMap = new HashMap<>();
        responseMap.put("questions", questions);
        responseMap.put("answers", answers);

        return responseMap;
    }

     //GPT에게 받은 응답 (질문,답) 분리
    public List<QuizDto> parseResponse(String responseBody) {

        JSONObject responseJson = new JSONObject(responseBody);
        JSONArray messages = responseJson.getJSONArray("choices");
        JSONObject message = messages.getJSONObject(0).getJSONObject("message");
        String content = message.getString("content");

        List<QuizDto> quizDtos = new ArrayList<>();

        String[] parts = content.split("\n");
        for (String part : parts) {
            String[] subParts = part.split(" \\(정답: ");
            if (subParts.length == 2) {
                String question = subParts[0].trim();
                String answer = subParts[1].substring(0, subParts[1].indexOf(")")).trim();

                QuizDto quiz = new QuizDto(question, answer);
                quizDtos.add(quiz);
            }
        }
        return quizDtos;
    }

    private String createRequestBody(QuizGameRequestDto requestDto) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("role", "user");
        jsonObject.put("content", requestDto.getRound() + "개의" + requestDto.getTopic() + "에 대한 퀴즈와 정답을 내줘, " +
                "답은 무조건 한단어야, 대답 형식은 다음과 같아 1. '질문' : (정답: '정답') ");

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(jsonObject);

        JSONObject finalObject = new JSONObject();

        finalObject.put("model", ChatGptConfig.MODEL);
        finalObject.put("messages", jsonArray);
        finalObject.put("max_tokens", ChatGptConfig.BASE_TOKEN + (requestDto.getRound() * 60));
        finalObject.put("temperature", ChatGptConfig.TEMPERATURE);
        finalObject.put("top_p", ChatGptConfig.TOP_P);

        return finalObject.toString();
    }
}
