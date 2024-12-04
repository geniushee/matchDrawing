package com.example.matchdrawing.global.api.service;

import com.example.matchdrawing.global.api.service.dto.KoreanDictItem;
import com.example.matchdrawing.global.api.service.dto.StDictResponse;
import com.example.matchdrawing.global.config.AppConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class RestAPIService {

    public List<KoreanDictItem> searchWord(String word) throws JsonProcessingException {
        RestTemplate rt = new RestTemplate();

        // 파라미터에 포함되어 있음.
        //        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<Object> entity = new HttpEntity<>(headers);
        URI uri = UriComponentsBuilder
                .fromUriString("https://stdict.korean.go.kr")
                .path("/api/search.do")
                .queryParam("key", AppConfig.getKoreanDictKey())
                .queryParam("q", word)
                .queryParam("req_type", "json")
                .queryParam("advanced", "n")
                .build()
                .toUri();

        ObjectMapper mapper = new ObjectMapper();

        String responseAsString = rt.getForObject(uri, String.class);
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
    public String searchWordAsString(String word) {
        RestTemplate rt = new RestTemplate();

        // 파라미터에 포함되어 있음.
        //        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<Object> entity = new HttpEntity<>(headers);
        URI uri = UriComponentsBuilder
                .fromUriString("https://stdict.korean.go.kr")
                .path("/api/search.do")
                .queryParam("key", AppConfig.getKoreanDictKey())
                .queryParam("q", word)
                .queryParam("req_type", "json")
                .queryParam("advanced", "n")
                .build()
                .toUri();

        String response = rt.getForObject(uri, String.class);

        return response;
    }
}
