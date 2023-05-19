package com.gpt.server.dto.quiz;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class GptRequestDto implements Serializable {

    private String model;
    private List<MessageDto> messages;
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    private double temperature;
    @JsonProperty("top_p")
    private double topP;
    public GptRequestDto(QuizGameRequestDto requestDto, String model, Integer maxTokens, double temperature, double topP) {
        MessageDto messageDto = new MessageDto();

        String content = requestDto.getRound() + "개의" + requestDto.getTopic() + "에 대한 퀴즈와 정답을 내줘, " +
                "답은 무조건 한단어야, 대답 형식은 다음과 같아 1. '질문' : (정답: '정답') ";
        messageDto.setRole("user");
        messageDto.setContent(content);
        this.messages = new ArrayList<>();
        this.messages.add(messageDto);

        this.model = model;
        this.maxTokens = maxTokens + (requestDto.getRound() * 80);
        this.temperature = temperature;
        this.topP = topP;
    }

}
