package com.gonggu.deal.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gonggu.deal.repository.DealRepository;
import com.gonggu.deal.request.DealSearch;
import com.gonggu.deal.response.DealResponse;
import com.gonggu.deal.service.DealService;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class DealTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DealRepository dealRepository;
    @Autowired
    private DealService dealService;

    @BeforeEach
    void clean(){
        //dealRepository.deleteAll();
    }
    @Test
    void pointTest() throws Exception{
        Double latitude = 37.51435;
        Double longitude = 127.12215;
        String pointWKT = String.format("POINT(%s %s)", longitude, latitude);
        Point point = (Point) new WKTReader().read(pointWKT);
        Deal deal = Deal.builder()
                        .title("제목")
                        .content("내용")
                        .price(1000L)
                        .unitPrice(200L)
                        .totalCount(5)
                        .url("url/")
                        .expireTime(LocalDateTime.now())
                        .quantity(10)
                        .unitQuantity(2)
                        .nowCount(2)
                        .point(point)
                        .build();
        dealRepository.save(deal);

        Deal find = dealRepository.findById(deal.getId()).orElseThrow();
        assertTrue(deal.getPoint().equals(find.getPoint()));
    }
    @Test
    void getDealTest() throws Exception{
        //given
        DealSearch[] searches = new DealSearch[5];
        // 1. 최신 페이지 조회
        searches[0] = DealSearch.builder()
                .page(1)
                .size(10)
                .build();
        // 2. 마지막 최신 페이지 조회
        searches[2]  = DealSearch.builder()
                .page(1000)
                .size(10)
                .build();
        // 3.남은 수량 적은 순 조회
        searches[1] = DealSearch.builder()
                .order(2)
                .page(1)
                .size(10)
                .build();
        // 4. 필터링 조회
        searches[3]  = DealSearch.builder()
                .category("카테고리1")
                .minPrice(1000)
                .maxPrice(10000)
                .page(1)
                .size(10)
                .build();
        // 5. 필터링 & 적게 남은순 & 뒷 페이지 조회
        searches[4]  = DealSearch.builder()
                .category("카테고리1")
                .minPrice(1000)
                .maxPrice(10000)
                .order(2)
                .page(6)
                .size(10)
                .build();
        User user = User.builder()
                .nickname("유저")
                .email("user@test.com")
                .password("password").build();

        StopWatch[] stopWatches = new StopWatch[5];
        //when
        for (int i = 0; i < 5; i++) {
            stopWatches[i] = new StopWatch((i+1)+"번");
            stopWatches[i].start();
            dealService.getList(searches[i],user);
            stopWatches[i].stop();
        }
        //then
        for (int i = 0; i < 5; i++) {
            System.out.println(stopWatches[i].prettyPrint());
        }
    }
    @Test
    void getDealTestWithProjection() throws Exception{
        //given
        DealSearch[] searches = new DealSearch[5];
        // 1. 최신 페이지 조회
        searches[0] = DealSearch.builder()
                .page(1)
                .size(10)
                .build();
        // 2. 마지막 최신 페이지 조회
        searches[2]  = DealSearch.builder()
                .page(1000)
                .size(10)
                .build();
        // 3.남은 수량 적은 순 조회
        searches[1] = DealSearch.builder()
                .order(2)
                .page(1)
                .size(10)
                .build();
        // 4. 필터링 조회
        searches[3] = DealSearch.builder()
                .category("카테고리1")
                .minPrice(1000)
                .maxPrice(10000)
                .page(1)
                .size(10)
                .build();
        // 5. 필터링 & 적게 남은순 & 뒷 페이지 조회
        searches[4]  = DealSearch.builder()
                .category("카테고리1")
                .minPrice(1000)
                .maxPrice(10000)
                .order(2)
                .page(6)
                .size(10)
                .build();
        User user = User.builder()
                .nickname("유저")
                .email("user@test.com")
                .password("password").build();

        StopWatch[] stopWatches = new StopWatch[5];
        //when
        for (int i = 0; i < 5; i++) {
            stopWatches[i] = new StopWatch((i+1)+"번");
            stopWatches[i].start();
            dealService.getList2(searches[i],user);
            stopWatches[i].stop();
        }
        //then
        for (int i = 0; i < 5; i++) {
            System.out.println(stopWatches[i].prettyPrint());
        }
    }
    @Test
    void getDealTestWithCovering() throws Exception{
        //given
        DealSearch[] searches = new DealSearch[5];
        // 1. 최신 페이지 조회
        searches[0] = DealSearch.builder()
                .page(1)
                .size(10)
                .build();
        // 2. 마지막 최신 페이지 조회
        searches[2]  = DealSearch.builder()
                .page(1000)
                .size(10)
                .build();

        // 3.남은 수량 적은 순 조회
        searches[1] = DealSearch.builder()
                .order(2)
                .page(1)
                .size(10)
                .build();
        // 4. 필터링 조회
        searches[3]  = DealSearch.builder()
                .category("카테고리1")
                .minPrice(1000)
                .maxPrice(10000)
                .page(1)
                .size(10)
                .build();
        // 5. 필터링 & 적게 남은순 & 뒷 페이지 조회
        searches[4]  = DealSearch.builder()
                .category("카테고리1")
                .minPrice(1000)
                .maxPrice(10000)
                .order(2)
                .page(6)
                .size(10)
                .build();
        User user = User.builder()
                .nickname("유저")
                .email("user@test.com")
                .password("password").build();

        StopWatch[] stopWatches = new StopWatch[5];
        //when
        for (int i = 0; i < 5; i++) {
            stopWatches[i] = new StopWatch((i+1)+"번");
            stopWatches[i].start();
            dealService.getList3(searches[i],user);
            stopWatches[i].stop();
        }
        //then
        for (int i = 0; i < 5; i++) {
            System.out.println(stopWatches[i].prettyPrint());
        }
    }
}