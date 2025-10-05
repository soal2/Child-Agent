package com.example.child_agent_back.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String message;
    private String type; // text, voice, image, combined
    private String timestamp;
}