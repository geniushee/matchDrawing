package com.example.matchdrawing.global.api.service;

import com.example.matchdrawing.global.api.service.dto.KoreanDictItem;
import com.example.matchdrawing.global.api.service.dto.StDictResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class RestAPIService {

    public final String koreanDictKey;

    public RestAPIService(@Qualifier("koreanDictKey") String koreanDictKey){
        this.koreanDictKey = koreanDictKey;
    }

    /**
     * 표준대국어사전에서 단어(명사)를 검색할 수 있다.
     * @param word
     * @return
     * @throws JsonProcessingException
     */
    public List<KoreanDictItem> searchWord(String word) throws JsonProcessingException {
        RestTemplate rt = new RestTemplate();
        URI uri = UriComponentsBuilder
                .fromUriString("https://stdict.korean.go.kr")
                .path("/api/search.do")
                .queryParam("key", koreanDictKey)
                .queryParam("q", word)
                .queryParam("req_type", "json")
                .queryParam("advanced", "n")
                .build()
                .toUri();
        String responseAsString = rt.getForObject(uri, String.class);

        // http응답 'content-type : text/json'으로 오기 때문에 RestTemplate의 Converter가 자동으로 json을 변환하지 못함.
        ObjectMapper mapper = new ObjectMapper();

        StDictResponse response = mapper.readValue(responseAsString, StDictResponse.class);
        List<KoreanDictItem> items = response.getChannel().getItem();

        if(!items.isEmpty()){
            for(KoreanDictItem item : items){
                if(item.getPos().equals("명사")){
                    break;
                }
            }
        }
        return items;
    }

    /**
     * 초기에 RestTemplate을 시험하기 위한 메소드
     * @param word
     * @return
     */
    public String searchWordAsString(String word) {
        RestTemplate rt = new RestTemplate();
        URI uri = UriComponentsBuilder
                .fromUriString("https://stdict.korean.go.kr")
                .path("/api/search.do")
                .queryParam("key", koreanDictKey)
                .queryParam("q", word)
                .queryParam("req_type", "json")
                .queryParam("advanced", "n")
                .build()
                .toUri();

        String response = rt.getForObject(uri, String.class);

        return response;
    }
}
