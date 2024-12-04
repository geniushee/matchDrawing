package com.example.matchdrawing.global.api.service;

import com.example.matchdrawing.global.api.service.dto.KoreanDictItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RestAPIServiceTest {

    @Autowired
    private RestAPIService restAPIService;


    @Test
    @DisplayName("한국어 사전 api 테스트")
    public void t1(){
        String response = restAPIService.searchWordAsString("나무");
        System.out.println(response);

//        assertThat(list.get(0).getWord()).as("검색이 제대로 됐는지 검색어 확인").isEqualTo("나무");
//        assertThat(list.get(0).getSense().getDefinition()).as("definition 확인").contains("식물");
    }

    @Test
    @DisplayName("한국어 사전 api 테스트")
    public void t2() throws JsonProcessingException {
        List<KoreanDictItem> list = restAPIService.searchWord("나무");
        System.out.println(list);

        assertThat(list.get(0).getWord()).as("검색이 제대로 됐는지 검색어 확인").isEqualTo("나무");
        assertThat(list.get(0).getSense().getDefinition()).as("definition 확인").contains("식물");
    }
}
