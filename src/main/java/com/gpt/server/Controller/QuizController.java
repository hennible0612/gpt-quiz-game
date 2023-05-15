package com.gpt.server.Controller;

import com.gpt.server.dto.quiz.QuizGameRequestDto;
import com.gpt.server.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/quiz")
    public ResponseEntity<Map<String, List<String>>> sendQuestion(@RequestBody QuizGameRequestDto requestDto) {
        return quizService.askQuestion(requestDto);
    }
}