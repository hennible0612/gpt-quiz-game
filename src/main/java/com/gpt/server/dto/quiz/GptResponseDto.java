package com.gpt.server.dto.quiz;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class GptResponseDto implements Serializable {
    private String id;
    private String object;
    private int created;
    private String model;
    private Usage usage;
    private List<Choice> choices;

    @Getter
    @ToString
    public static class Usage implements Serializable {
        @JsonProperty("prompt_tokens")
        private int promptTokens;
        @JsonProperty("completion_tokens")
        private int completionTokens;
        @JsonProperty("total_tokens")
        private int totalTokens;
    }

    @Getter
    @ToString
    public static class Choice implements Serializable {
        private Message message;
        @JsonProperty("finish_reason")
        private String finishReason;
        private int index;

        @Getter
        @ToString
        public static class Message implements Serializable {
            private String role;
            private String content;
        }
    }
}