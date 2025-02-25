package com.example.matchdrawing.domain.game.game.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CustomPageDto<T> {
    private List<T> content;
    private int number;
    private long totalElements;
    private int totalPages;
    private int pageSize;
    private boolean empty;

    public CustomPageDto(){
        this.content = new ArrayList<>();this.empty = true;
    }

    public CustomPageDto(List<T> contents, int number, long totalElements, int totalPages, int pageSize){
        this.content = new ArrayList<>(contents);
        this.number = number;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.pageSize = pageSize;
        this.empty = false;
    }

    public CustomPageDto(Page<T> page){
        this.content = new ArrayList<>(page.getContent());
        this.number = page.getNumber();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.pageSize = page.getSize();
    }

}
