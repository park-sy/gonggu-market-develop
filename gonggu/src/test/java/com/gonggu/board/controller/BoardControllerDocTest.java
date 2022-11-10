package com.gonggu.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gonggu.board.domain.*;
import com.gonggu.board.repository.*;
import com.gonggu.board.request.BoardCreate;
import com.gonggu.board.request.BoardJoin;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
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
public class BoardControllerDocTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BoardMemberRepository boardMemberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BoardImageRepository boardImageRepository;
    @Autowired
    private BoardKeywordRepository boardKeywordRepository;

    @BeforeEach
    void clean(){
        boardKeywordRepository.deleteAll();
        boardImageRepository.deleteAll();
        boardMemberRepository.deleteAll();
        boardRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @BeforeEach
    void getAccessToken() throws Exception{

    }

    @Test
    @DisplayName("게시글 가져오기")
    void getBoard() throws Exception{
        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);
        User user = User.builder()
                .name("유저").build();
        LocalDateTime date = LocalDateTime.now();
        List<Board> boards = IntStream.range(0,20)
                .mapToObj(i -> Board.builder()
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
        boardRepository.saveAll(boards);

        List<BoardImage> images =  IntStream.range(0, 20)
                .mapToObj(i -> BoardImage.builder()
                        .board(boards.get(i))
                        .originFileName("origin_name"+i)
                        .newFileName("new_name"+i)
                        .filePath("경로/"+i+"/img.png")
                        .build()).collect(Collectors.toList());
        boardImageRepository.saveAll(images);
        List<BoardKeyword> keywords =  IntStream.range(0, 60)
                .mapToObj(i -> BoardKeyword.builder()
                        .board(boards.get(i%20))
                        .keyword("키워드"+i)
                        .build()).collect(Collectors.toList());
        boardKeywordRepository.saveAll(keywords);


        this.mockMvc.perform(get("/board?title=제목&category=카테고리&minPrice=1000&maxPrice=2000&order=1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("board/search"
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
                                fieldWithPath("[].images[].local").description("이미지 경로"),
                                fieldWithPath("[].images[].path").description("이미지 경로"),
                                fieldWithPath("[].deletion").description("삭제여부")
                        )
                ));

    }

    @Test
    @DisplayName("게시글 상세보기")
    void getBoardDetail() throws Exception{
        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

        User user = User.builder()
                .name("유저").build();

        userRepository.save(user);
        LocalDateTime now = LocalDateTime.now();
        Board board = Board.builder()
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
        boardRepository.save(board);

        BoardImage image = BoardImage.builder()
                        .board(board)
                        .originFileName("origin_name")
                        .newFileName("new_name")
                        .filePath("경로/"+board.getId()+"/img.png")
                        .build();
        boardImageRepository.save(image);

        List<BoardKeyword> keywords =  IntStream.range(0,3)
                .mapToObj(i -> BoardKeyword.builder()
                        .board(board)
                        .keyword("키워드"+i)
                        .build()).collect(Collectors.toList());
        boardKeywordRepository.saveAll(keywords);

        this.mockMvc.perform(get("/board/{boardId}", board.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("board/detail"
                        , pathParameters(
                                parameterWithName("boardId").description("게시글 ID")
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
//                                fieldWithPath("user").description("게시글 작성 유저 정보"),
                                fieldWithPath("user.id").description("게시글 작성 유저 ID"),
                                fieldWithPath("user.name").description("게시글 작성 유저 이름"),
//                                fieldWithPath("category").description("게시글 카테고리")
                                fieldWithPath("category.id").description("게시글 카테고리 ID"),
                                fieldWithPath("category.name").description("게시글 카테고리 이름")
                        )
                ));
    }

    @Test
    @DisplayName("게시글 작성")
    @WithMockUser
    void postBoard() throws Exception{
//        User user = User.builder()
//                .name("유저").build();
//        userRepository.save(user);

        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);
        LocalDateTime now = LocalDateTime.now();
        List<String> keywords = IntStream.range(0,3).mapToObj(i -> "키워드"+i).collect(Collectors.toList());
        BoardCreate boardCreate = BoardCreate.builder()
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

        this.mockMvc.perform(post("/board/post")
                        .content(objectMapper.writeValueAsString(boardCreate))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("board/create"
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
    @DisplayName("구매 참가")
    @WithMockUser
    void requestJoin() throws Exception{
        List<User> users = IntStream.range(0,2)
                .mapToObj(i -> User.builder()
                        .name("이름" +i)
                        .build()).collect(Collectors.toList());
        userRepository.saveAll(users);

        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

        User user = User.builder()
                .name("유저").build();
        userRepository.save(user);

        LocalDateTime now = LocalDateTime.now();
        Board board = Board.builder()
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
        boardRepository.save(board);

        BoardJoin boardJoin = BoardJoin.builder()
                .quantity(5)
                .build();

        this.mockMvc.perform(post("/board/{boardId}/join", board.getId())
                        .content(objectMapper.writeValueAsString(boardJoin))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("board/join"
                        ,pathParameters(
                                parameterWithName("boardId").description("게시글 ID")
                        )
                        , requestFields(
                                fieldWithPath("quantity").description("구매 수량")
                        )
                ));

    }
}
