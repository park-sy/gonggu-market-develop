package com.gonggu.deal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gonggu.deal.domain.*;
import com.gonggu.deal.repository.*;
import com.gonggu.deal.request.DealCreate;
import com.gonggu.deal.request.DealEdit;
import com.gonggu.deal.request.DealJoin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
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
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;


import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.gonngu.com",uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
public class DealControllerDocTest {
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
    void clean(){
        dealMemberRepository.deleteAll();
        dealRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        keywordRepository.deleteAll();
        testUser = User.builder()
                .nickname("테스트유저")
                .roles(Collections.singletonList("ROLE_USER")).build();
        userRepository.save(testUser);
    }
    @Test
    @DisplayName("구매 정보 수정")
    @WithUserDetails(value = "테스트유저", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void editJoin() throws Exception{
        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

        User user = User.builder()
                .nickname("호스트").build();
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

        this.mockMvc.perform(post("/deal/{dealId}/enrollment", deal.getId())
                        .content(objectMapper.writeValueAsString(dealJoin))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        this.mockMvc.perform(patch("/deal/{dealId}/enrollment", deal.getId())
                        .content(objectMapper.writeValueAsString(dealJoin2))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("deal/join-edit"
                        ,pathParameters(
                                parameterWithName("dealId").description("게시글 ID")
                        )
                        , requestFields(
                                fieldWithPath("quantity").description("구매 변경 수량")
                        )
                ));

    }

    @Test
    @DisplayName("구매 취소")
    @WithUserDetails(value = "테스트유저", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteJoin() throws Exception{
        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

        User user = User.builder()
                .nickname("호스트").build();
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

        this.mockMvc.perform(post("/deal/{dealId}/enrollment", deal.getId())
                        .content(objectMapper.writeValueAsString(dealJoin))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        this.mockMvc.perform(delete("/deal/{dealId}/enrollment", deal.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("deal/join-delete"
                        ,pathParameters(
                                parameterWithName("dealId").description("게시글 ID")
                        )
                ));

    }
    @Test
    @DisplayName("게시글 가져오기")
    void getDeal() throws Exception{
        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);
        User user = User.builder()
                .nickname("유저").build();
        LocalDateTime date = LocalDateTime.now();
        List<Deal> deals = IntStream.range(0,20)
                .mapToObj(i -> Deal.builder()
                        .title("제목" +i)
                        .category(category)
                        .content("내용")
                        .price(1000L)
                        .unitPrice(200L)
                        .totalCount(i)
                        .url("url/")
                        .expireTime(date.plusDays(i%4))
                        .quantity(10)
                        .unitQuantity(2)
                        .nowCount(i/2)
                        .build()).collect(Collectors.toList());
        dealRepository.saveAll(deals);

        List<DealImage> images =  IntStream.range(0, 20)
                .mapToObj(i -> DealImage.builder()
                        .deal(deals.get(i))
                        .originFileName("origin_name"+i)
                        .newFileName("new_name"+i)
                        .filePath("경로/"+i+"/img.png")
                        .build()).collect(Collectors.toList());
        dealImageRepository.saveAll(images);

        Keyword keyword = Keyword.builder().word("키워드").build();
        keywordRepository.save(keyword);
        List<DealKeyword> keywords =  IntStream.range(0, 60)
                .mapToObj(i -> DealKeyword.builder()
                        .deal(deals.get(i%20))
                        .keyword(keyword)
                        .build()).collect(Collectors.toList());
        dealKeywordRepository.saveAll(keywords);


        this.mockMvc.perform(get("/deal?title=제목&category=카테고리&minPrice=1000&maxPrice=2000&order=1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("deal/search"
                        , pathParameters(
                                parameterWithName("title").description("게시글 제목").optional()
                                        .attributes(key("constraint").value("입력된 단어가 포함된 모든 게시글. '제목'입력했습니다.")),
                                parameterWithName("category").description("게시글의 카테고리").optional()
                                        .attributes(key("constraint").value("'카테고리'입력했습니다.")),
                                parameterWithName("minPrice").description("최소 가격").optional(),
                                parameterWithName("maxPrice").description("최대 가격").optional(),
                                parameterWithName("order").description("게시글 정렬").optional()
                                        .attributes(key("constraint").value("미입력시 최신순, 1 = 인기순, 2 = 적게 남은 수량 순"))
                        )
                        , responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("게시글 ID"),
                                fieldWithPath("[].category").description("제품 카테고리"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("[].remainDate").description("남은 날짜"),
                                fieldWithPath("[].unitPrice").description("단위 가격"),
                                fieldWithPath("[].quantity").description("제품 수량"),
                                fieldWithPath("[].nowCount").description("현재 모집 수량"),
                                fieldWithPath("[].totalCount").description("총 모집 수량"),
                                fieldWithPath("[].image.local").description("이미지 경로"),
                                fieldWithPath("[].image.path").description("이미지 경로"),
                                fieldWithPath("[].deletion").description("삭제여부")
                        )
                ));
    }

    @Test
    @DisplayName("게시글 상세보기")
    void getDealDetail() throws Exception{
        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

        User user = User.builder()
                .nickname("유저").build();

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

        DealImage image = DealImage.builder()
                        .deal(deal)
                        .originFileName("origin_name")
                        .newFileName("new_name")
                        .filePath("경로/"+deal.getId()+"/img.png")
                        .build();
        dealImageRepository.save(image);

        Keyword keyword = Keyword.builder().word("키워드").build();
        keywordRepository.save(keyword);
        List<DealKeyword> keywords =  IntStream.range(0,3)
                .mapToObj(i -> DealKeyword.builder()
                        .deal(deal)
                        .keyword(keyword)
                        .build()).collect(Collectors.toList());
        dealKeywordRepository.saveAll(keywords);

        this.mockMvc.perform(get("/deal/{dealId}", deal.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("deal/detail"
                        , pathParameters(
                                parameterWithName("dealId").description("게시글 ID")
                        )
                        , responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("게시글 ID"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("remainDate").description("남은 날짜"),
                                fieldWithPath("price").description("가격"),
                                fieldWithPath("unitPrice").description("단위 가격"),
                                fieldWithPath("quantity").description("제품 수량"),
                                fieldWithPath("unitQuantity").description("단위 수량"),
                                fieldWithPath("unit").description("단위"),
                                fieldWithPath("nowCount").description("현재 모집 수량"),
                                fieldWithPath("totalCount").description("총 모집 수량"),
                                fieldWithPath("url").description("상품 URL"),
                                fieldWithPath("view").description("조회수"),
                                fieldWithPath("images[].local").description("이미지 경로"),
                                fieldWithPath("images[].path").description("이미지 경로"),
                                fieldWithPath("deletion").description("삭제여부"),
                                fieldWithPath("user").description("게시글 작성 유저 닉네임"),
                                //fieldWithPath("user.name").description("게시글 작성 유저 이름"),
                                fieldWithPath("category.id").description("게시글 카테고리 ID"),
                                fieldWithPath("category.name").description("게시글 카테고리 이름")
                        )
                ));
    }

    @Test
    @DisplayName("게시글 작성")
    @WithMockUser
    void postDeal() throws Exception{
//        User user = User.builder()
//                .name("유저").build();
//        userRepository.save(user);

        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);
        LocalDateTime now = LocalDateTime.now();
        List<String> keywords = IntStream.range(0,3).mapToObj(i -> "키워드"+i).collect(Collectors.toList());
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
                .keywords(keywords)
                .expireTime(now.plusDays(2))
                .build();

        this.mockMvc.perform(post("/deal")
                        .content(objectMapper.writeValueAsString(dealCreate))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("deal/create"
                        , requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("센터 주소"),
                                fieldWithPath("price").description("제품 가격"),
                                fieldWithPath("unitQuantity").description("제품 단위 수량"),
                                fieldWithPath("unit").description("제품 단위"),
                                fieldWithPath("nowCount").description("현재 모집 단위(내가 살 단위)"),
                                fieldWithPath("totalCount").description("총 모집 단위(내가 살 단위)"),
                                fieldWithPath("url").description("상품 URL"),
                                fieldWithPath("categoryId").description("카테고리 ID"),
                                fieldWithPath("keywords").description("키워드"),
                                fieldWithPath("expireTime").description("게시글 만료 시간")
                        )
                ));
    }


    @Test
    @DisplayName("게시글 수정")
        //@WithUserDetails(value = "1",  setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void editDeal() throws Exception{
        User user = User.builder()
                .nickname("유저").build();
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

        this.mockMvc.perform(patch("/deal/{dealId}",deal.getId())
                        .content(objectMapper.writeValueAsString(dealEdit))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("deal/edit"
                        , pathParameters(
                                parameterWithName("dealId").description("게시글 ID")
                        )
                        , requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("변경 내용")
                        )
                ));
    }

    @Test
    @DisplayName("게시글 삭제")
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


        this.mockMvc.perform(delete("/deal/{dealId}",deal.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("deal/delete"
                        , pathParameters(
                                parameterWithName("dealId").description("게시글 ID")
                        )
                ));
    }

    @Test
    @DisplayName("구매 참가")
    @WithMockUser
    void requestJoin() throws Exception{
        List<User> users = IntStream.range(0,2)
                .mapToObj(i -> User.builder()
                        .nickname("이름" +i)
                        .build()).collect(Collectors.toList());
        userRepository.saveAll(users);

        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

        User user = User.builder()
                .nickname("유저").build();
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

        this.mockMvc.perform(post("/deal/{dealId}/enrollment", deal.getId())
                        .content(objectMapper.writeValueAsString(dealJoin))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("deal/join"
                        ,pathParameters(
                                parameterWithName("dealId").description("게시글 ID")
                        )
                        , requestFields(
                                fieldWithPath("quantity").description("구매 수량")
                        )
                ));
    }


    @Test
    @DisplayName("판매 내역 조회")
    void getMySellList() throws Exception{
        User user = User.builder()
                .nickname("유저").build();
        userRepository.save(user);

        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);
        LocalDateTime date = LocalDateTime.now();
        List<Deal> deals = IntStream.range(0,5)
                .mapToObj(i -> Deal.builder()
                        .title("제목" +i)
                        .category(category)
                        .content("내용")
                        .price(1000L)
                        .unitPrice(200L)
                        .totalCount(i)
                        .url("url/")
                        .expireTime(date.plusDays(i%4))
                        .quantity(10)
                        .unitQuantity(2)
                        .nowCount(i)
                        .user(user)
                        .build()).collect(Collectors.toList());
        dealRepository.saveAll(deals);
        List<DealImage> images =  IntStream.range(0, 5)
                .mapToObj(i -> DealImage.builder()
                        .deal(deals.get(i))
                        .originFileName("origin_name"+i)
                        .newFileName("new_name"+i)
                        .filePath("경로/"+i+"/img.png")
                        .build()).collect(Collectors.toList());
        dealImageRepository.saveAll(images);

        Keyword keyword = Keyword.builder().word("키워드").build();
        keywordRepository.save(keyword);
        List<DealKeyword> keywords =  IntStream.range(0, 20)
                .mapToObj(i -> DealKeyword.builder()
                        .deal(deals.get(i%5))
                        .keyword(keyword)
                        .build()).collect(Collectors.toList());
        dealKeywordRepository.saveAll(keywords);

        this.mockMvc.perform(get("/deal/sale/{userId}",user.getNickname())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("deal/sell-list"
                        , pathParameters(
                                parameterWithName("userId").description("유저 ID")
                        )
                        , responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("게시글 ID"),
                                fieldWithPath("[].category").description("제품 카테고리"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("[].remainDate").description("남은 날짜"),
                                fieldWithPath("[].unitPrice").description("단위 가격"),
                                fieldWithPath("[].quantity").description("제품 수량"),
                                fieldWithPath("[].nowCount").description("현재 모집 수량"),
                                fieldWithPath("[].totalCount").description("총 모집 수량"),
                                fieldWithPath("[].image.local").description("이미지 경로"),
                                fieldWithPath("[].image.path").description("이미지 경로"),
                                fieldWithPath("[].deletion").description("삭제여부")
                        )
                ));

    }

    @Test
    @DisplayName("구매 내역 조회")
    void getMyJoinListTemp() throws Exception{

        List<User> users = IntStream.range(0,5)
                .mapToObj(i -> User.builder()
                        .nickname("이름" +i)
                        .build()).collect(Collectors.toList());
        userRepository.saveAll(users);

        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);
        LocalDateTime date = LocalDateTime.now();
        List<Deal> deals = IntStream.range(0,5)
                .mapToObj(i -> Deal.builder()
                        .title("제목" +i)
                        .category(category)
                        .content("내용")
                        .price(1000L)
                        .unitPrice(200L)
                        .totalCount(i)
                        .url("url/")
                        .expireTime(date.plusDays(i%4))
                        .quantity(10)
                        .unitQuantity(2)
                        .nowCount(i)
                        .user(users.get(i))
                        .build()).collect(Collectors.toList());
        dealRepository.saveAll(deals);
        List<DealImage> images =  IntStream.range(0, 5)
                .mapToObj(i -> DealImage.builder()
                        .deal(deals.get(i))
                        .originFileName("origin_name"+i)
                        .newFileName("new_name"+i)
                        .filePath("경로/"+i+"/img.png")
                        .build()).collect(Collectors.toList());
        dealImageRepository.saveAll(images);

        Keyword keyword = Keyword.builder().word("키워드").build();
        keywordRepository.save(keyword);
        List<DealKeyword> keywords =  IntStream.range(0, 20)
                .mapToObj(i -> DealKeyword.builder()
                        .deal(deals.get(i%5))
                        .keyword(keyword)
                        .build()).collect(Collectors.toList());
        dealKeywordRepository.saveAll(keywords);

        List<DealMember> dealMembers = IntStream.range(1,5)
                .mapToObj(i -> DealMember.builder()
                        .host(false)
                        .deal(deals.get(i%5))
                        .user(users.get(0))
                        .quantity(i%5)
                        .build()).collect(Collectors.toList());
        dealMemberRepository.saveAll(dealMembers);

        this.mockMvc.perform(get("/deal/enrollment/{userId}", users.get(0).getNickname())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("deal/join-list"
                        , pathParameters(
                                parameterWithName("userId").description("유저 ID")
                        )
                        , responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("게시글 ID"),
                                fieldWithPath("[].category").description("제품 카테고리"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("[].remainDate").description("남은 날짜"),
                                fieldWithPath("[].unitPrice").description("단위 가격"),
                                fieldWithPath("[].quantity").description("제품 수량"),
                                fieldWithPath("[].nowCount").description("현재 모집 수량"),
                                fieldWithPath("[].totalCount").description("총 모집 수량"),
                                fieldWithPath("[].image.local").description("이미지 경로"),
                                fieldWithPath("[].image.path").description("이미지 경로"),
                                fieldWithPath("[].deletion").description("삭제여부")
                        )
                ));
    }
}
