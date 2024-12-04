package com.example.matchdrawing.global.api.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KoreanDictItem {
    private Long sup_no;
    private String word;
    private String target_code;
    private Sense sense;
    private String pos;
}
