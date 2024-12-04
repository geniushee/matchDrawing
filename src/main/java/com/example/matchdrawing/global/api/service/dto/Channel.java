package com.example.matchdrawing.global.api.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 한국국어표준사전의 OpenAPI요청에 의한 답을 받기 위한 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Channel {
    private String title;
    private String link;
    private String description;
    private String lastbuilddate;
    private Integer total;
    private Integer start;
    private Integer num;
    private List<KoreanDictItem> item;
}
