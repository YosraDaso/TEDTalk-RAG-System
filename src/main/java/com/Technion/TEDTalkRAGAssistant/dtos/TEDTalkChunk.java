package com.Technion.TEDTalkRAGAssistant.dtos;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope("prototype")
@Data
public class TEDTalkChunk {
    private String chunkContent;
    private Map<String, Object> keywords;
}