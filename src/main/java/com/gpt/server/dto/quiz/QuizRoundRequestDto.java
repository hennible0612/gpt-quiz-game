package com.gpt.server.dto.quiz;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizRoundRequestDto {
    private String topic;
    private Integer round;
}
