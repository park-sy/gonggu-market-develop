package com.gonggu.deal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gonggu.deal.domain.*;
import com.gonggu.deal.repository.*;
import com.gonggu.deal.request.DealCreate;
import com.gonggu.deal.request.DealEdit;
import com.gonggu.deal.request.DealJoin;
import org.junit.jupiter.api.*;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DealControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DealRepository dealRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DealMemberRepository dealMemberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private DealImageRepository dealImageRepository;
    @Autowired
    private DealKeywordRepository dealKeywordRepository;
    @Autowired
    private KeywordRepository keywordRepository;
    private User testUser;


    @BeforeEach
    void clean() throws Exception{
        dealMemberRepository.deleteAll();
        dealRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        keywordRepository.deleteAll();
        Double latitude = 37.51435;
        Double longitude = 127.12215;
        String pointWKT = String.format("POINT(%s %s)", longitude, latitude);
        Point point = (Point) new WKTReader().read(pointWKT);
        testUser = User.builder()
                .nickname("테스트유저")
                .email("test@test.com")
                .password("password")
                .point(point)
                .distance(50000)
                .roles(Collections.singletonList("ROLE_USER")).build();
        userRepository.save(testUser);
    }

    @Test
    @DisplayName("게시글 가져오기")
    void getDeal() throws Exception{
        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

        LocalDateTime date = LocalDateTime.now();
//        List<Deal> deals = IntStream.range(0,20)
//                .mapToObj(i -> Deal.builder()
//                        .title("제목" +i)
//                        .category(category)
//                        .content("내용")
//                        .price(1000L)
//                        .unitPrice(200L)
//                        .totalCount(i)
//                        .url("url/")
//                        .expireTime(date)
//                        .quantity(10)
//                        .unitQuantity(2)
//                        .nowCount(i/2)
//                        .build()).collect(Collectors.toList());
//        dealRepository.saveAll(deals);
//
//        List<DealImage> images =  IntStream.range(0, 20)
//                .mapToObj(i -> DealImage.builder()
//                        .deal(deals.get(i))
//                        .build()).collect(Collectors.toList());
//        dealImageRepository.saveAll(images);

        mockMvc.perform(get("/deal")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    @DisplayName("게시글 상세보기")
    void getDealDetail() throws Exception{
        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

        User user = User.builder()
                .nickname("유저")
                .email("user@test.com")
                .password("password").build();
        userRepository.save(user);

        LocalDateTime now = LocalDateTime.now();
        Deal deal = Deal.builder()
                .category(category)
                .title("제목")
                .content("내용")
                .price(1000L)
                .quantity(10)
                .unitQuantity(2)
                .unitPrice(200L)
                .totalCount(10)
                .url("url/")
                .expireTime(now.plusDays(3))
                .nowCount(2)
                .user(user)
                .build();
        dealRepository.save(deal);


        mockMvc.perform(get("/deal/{dealId}", deal.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 작성")
    @WithMockUser
    void postDeal() throws Exception{
        User user = User.builder()
                .nickname("유저")
                .email("user@test.com")
                .password("password").build();
        userRepository.save(user);

        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

        LocalDateTime date = LocalDateTime.now();
        DealCreate dealCreate = DealCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .price(10000L)
                .unitQuantity(5)
                .unit("단위")
                .nowCount(1)
                .totalCount(5)
                .categoryId(category.getId())
                .url("url 주소")
                .expireTime(date.plusDays(2))
                .build();

        mockMvc.perform(post("/deal")
                        .content(objectMapper.writeValueAsString(dealCreate))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("게시글 수정")
    @WithMockUser
    void editDeal() throws Exception{
        User user = User.builder()
                .nickname("유저")
                .email("user@test.com")
                .password("password").build();
        userRepository.save(user);

        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

        LocalDateTime now = LocalDateTime.now();
        Deal deal = Deal.builder()
                .category(category)
                .title("제목")
                .content("내용")
                .price(1000L)
                .quantity(10)
                .unitQuantity(2)
                .unitPrice(200L)
                .totalCount(10)
                .url("url/")
                .expireTime(now.plusDays(3))
                .nowCount(2)
                .user(user)
                .build();
        dealRepository.save(deal);

        DealEdit dealEdit = DealEdit.builder()
                .content("내용변경")
                .build();
        mockMvc.perform(patch("/deal/{dealId}",deal.getId())
                        .content(objectMapper.writeValueAsString(dealEdit))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 삭제")
    @WithMockUser
    void deleteDeal() throws Exception{
        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

        LocalDateTime now = LocalDateTime.now();
        Deal deal = Deal.builder()
                .category(category)
                .title("제목")
                .content("내용")
                .price(1000L)
                .quantity(10)
                .unitQuantity(2)
                .unitPrice(200L)
                .totalCount(10)
                .url("url/")
                .expireTime(now.plusDays(3))
                .nowCount(2)
                .user(testUser)
                .build();
        dealRepository.save(deal);


        mockMvc.perform(delete("/deal/{dealId}",deal.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("구매 참가")
    @WithUserDetails(value = "테스트유저", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void requestJoin() throws Exception{
        List<User> users = IntStream.range(0,2)
                .mapToObj(i -> User.builder()
                        .nickname("이름" +i)
                        .email("email@test.com")
                        .password("password")
                        .build()).collect(Collectors.toList());
        userRepository.saveAll(users);

        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

        User user = User.builder()
                .nickname("유저")
                .email("test@test.com")
                .password("password").build();
        userRepository.save(user);

        LocalDateTime now = LocalDateTime.now();
        Deal deal = Deal.builder()
                .category(category)
                .title("제목")
                .content("내용")
                .price(1000L)
                .quantity(10)
                .unitQuantity(2)
                .unitPrice(200L)
                .totalCount(10)
                .url("url/")
                .expireTime(now.plusDays(3))
                .nowCount(2)
                .user(users.get(0))
                .build();
        dealRepository.save(deal);

        DealJoin dealJoin = DealJoin.builder()
                .quantity(5)
                .build();

        mockMvc.perform(post("/deal/{dealId}/enrollment", deal.getId())
                        .content(objectMapper.writeValueAsString(dealJoin))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/deal/{dealId}", deal.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("구매 정보 수정")
    @WithUserDetails(value = "테스트유저", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void editJoin() throws Exception{
        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

        User user = User.builder()
                .nickname("유저")
                .email("user@test.com")
                .password("password").build();
        userRepository.save(user);

        LocalDateTime now = LocalDateTime.now();
        Deal deal = Deal.builder()
                .category(category)
                .title("제목")
                .content("내용")
                .price(1000L)
                .quantity(10)
                .unitQuantity(2)
                .unitPrice(200L)
                .totalCount(10)
                .url("url/")
                .expireTime(now.plusDays(3))
                .nowCount(2)
                .user(user)
                .build();
        dealRepository.save(deal);

        DealJoin dealJoin = DealJoin.builder()
                .quantity(5)
                .build();

        DealJoin dealJoin2 = DealJoin.builder()
                .quantity(2)
                .build();

        mockMvc.perform(post("/deal/{dealId}/enrollment", deal.getId())
                        .content(objectMapper.writeValueAsString(dealJoin))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/deal/{dealId}/enrollment", deal.getId())
                        .content(objectMapper.writeValueAsString(dealJoin2))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/deal/{dealId}", deal.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("구매 취소")
    @WithUserDetails(value = "테스트유저", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteJoin() throws Exception{
        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

        User user = User.builder()
                .nickname("유저")
                .email("user@test.com")
                .password("password").build();
        userRepository.save(user);

        LocalDateTime now = LocalDateTime.now();
        Deal deal = Deal.builder()
                .category(category)
                .title("제목")
                .content("내용")
                .price(1000L)
                .quantity(10)
                .unitQuantity(2)
                .unitPrice(200L)
                .totalCount(10)
                .url("url/")
                .expireTime(now.plusDays(3))
                .nowCount(2)
                .user(user)
                .build();
        dealRepository.save(deal);

        DealJoin dealJoin = DealJoin.builder()
                .quantity(5)
                .build();

        mockMvc.perform(post("/deal/{dealId}/enrollment", deal.getId())
                        .content(objectMapper.writeValueAsString(dealJoin))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/deal/{dealId}/enrollment", deal.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/deal/{dealId}", deal.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("배치 글 생성")
    void createForBatch(){
        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

        List<Deal> deals = IntStream.range(0,20)
                .mapToObj(i -> Deal.builder()
                        .title("제목" +i)
                        .category(category)
                        .content("내용")
                        .price(1000L)
                        .unitPrice(200L)
                        .totalCount(i)
                        .url("url/")
                        .expireTime(LocalDateTime.now().plusDays((i > 9 ? 1:0)))
                        .quantity(10)
                        .unitQuantity(2)
                        .nowCount(i/2)
                        .build()).collect(Collectors.toList());
        dealRepository.saveAll(deals);
    }

    @Test
    @DisplayName("게시글 가져오기(거리 포함)")
    @WithUserDetails(value = "테스트유저", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void getDealWithPoint() throws Exception {
        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

        Double longitude = 127.12215;
        Double latitude = 37.51435;

        Point point1 = (Point) new WKTReader().read(String.format("POINT(%s %s)", longitude, latitude));
        Point point2 = (Point) new WKTReader().read(String.format("POINT(%s %s)", longitude+10, latitude));
        Point[] points = {point1, point2};
        List<Deal> deals = IntStream.range(0, 20)
                .mapToObj(i -> Deal.builder()
                        .title("제목" + i)
                        .category(category)
                        .content("내용")
                        .price(1000L)
                        .unitPrice(200L)
                        .totalCount(i)
                        .url("url/")
                        .expireTime(LocalDateTime.now())
                        .quantity(10)
                        .unitQuantity(2)
                        .nowCount(i / 2)
                        .point(points[i%2])
                        .build()).collect(Collectors.toList());
        dealRepository.saveAll(deals);

        List<DealImage> images = IntStream.range(0, 20)
                .mapToObj(i -> DealImage.builder()
                        .deal(deals.get(i))
                        .build()).collect(Collectors.toList());
        dealImageRepository.saveAll(images);

        Keyword keyword = Keyword.builder().word("키워드").build();
        keywordRepository.save(keyword);
        List<DealKeyword> keywords = IntStream.range(0, 60)
                .mapToObj(i -> DealKeyword.builder()
                        .deal(deals.get(i % 20))
                        .keyword(keyword)
                        .build()).collect(Collectors.toList());
        dealKeywordRepository.saveAll(keywords);

        mockMvc.perform(get("/deal")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()",is(10)))
                .andDo(print());
    }
}