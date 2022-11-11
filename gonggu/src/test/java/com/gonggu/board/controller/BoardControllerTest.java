package com.gonggu.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gonggu.board.config.JwtTokenProvider;
import com.gonggu.board.domain.*;
import com.gonggu.board.repository.BoardMemberRepository;
import com.gonggu.board.repository.BoardRepository;
import com.gonggu.board.repository.CategoryRepository;
import com.gonggu.board.repository.UserRepository;
import com.gonggu.board.request.BoardCreate;
import com.gonggu.board.request.BoardEdit;
import com.gonggu.board.request.BoardJoin;
import com.gonggu.board.service.CustomUserDetailsService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
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
class BoardControllerTest {
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

    private User testUser;

    @BeforeEach
    void clean(){
        boardMemberRepository.deleteAll();
        boardRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        testUser = User.builder()
                .name("테스트유저")
                .roles(Collections.singletonList("ROLE_USER")).build();
        userRepository.save(testUser);
    }


    @Test
    @DisplayName("게시글 가져오기")
    void getBoard() throws Exception{
        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);
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
                        .nowCount(i)
                        .build()).collect(Collectors.toList());
        boardRepository.saveAll(boards);
        mockMvc.perform(get("/board")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()",is(10)))
                .andExpect(jsonPath("$[0].title").value("제목19"))
                .andDo(print());
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


        mockMvc.perform(get("/board/{boardId}", board.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 작성")
    @WithMockUser
    void postBoard() throws Exception{
        User user = User.builder()
                .name("유저").build();
        userRepository.save(user);
        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

        LocalDateTime date = LocalDateTime.now();
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
                .expireTime(date.plusDays(2))
                .build();

        mockMvc.perform(post("/board")
                        .content(objectMapper.writeValueAsString(boardCreate))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("게시글 수정")
    //@WithUserDetails(value = "1",  setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void editBoard() throws Exception{
        User user = User.builder()
                .name("유저").build();
        userRepository.save(user);

        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

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

        BoardEdit boardEdit = BoardEdit.builder()
                .content("내용변경")
                .build();
        mockMvc.perform(patch("/board/{boardId}",board.getId())
                        .content(objectMapper.writeValueAsString(boardEdit))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 삭제")
    //@WithUserDetails(value = "1",  setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteBoard() throws Exception{
        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

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
                .user(testUser)
                .build();
        boardRepository.save(board);


        mockMvc.perform(delete("/board/{boardId}",board.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
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

        mockMvc.perform(post("/board/{boardId}/enrollment", board.getId())
                        .content(objectMapper.writeValueAsString(boardJoin))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/board/{boardId}", board.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

//    @Test
//    @DisplayName("구매 정보 수정")
//    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    void editJoin() throws Exception{
//        Category category = Category.builder()
//                .name("카테고리").build();
//        categoryRepository.save(category);
//
//        User user = User.builder()
//                .name("유저").build();
//        userRepository.save(user);
//
//        LocalDateTime now = LocalDateTime.now();
//        Board board = Board.builder()
//                .category(category)
//                .title("제목")
//                .content("내용")
//                .price(1000L)
//                .quantity(10)
//                .unitQuantity(2)
//                .unitPrice(200L)
//                .totalCount(10)
//                .url("url/")
//                .expireTime(now.plusDays(3))
//                .nowCount(2)
//                .user(user)
//                .build();
//        boardRepository.save(board);
//
//        BoardJoin boardJoin = BoardJoin.builder()
//                .quantity(5)
//                .build();
//
//        BoardJoin boardJoin2 = BoardJoin.builder()
//                .quantity(2)
//                .build();
//
//        mockMvc.perform(post("/board/{boardId}/enrollment", board.getId())
//                        .content(objectMapper.writeValueAsString(boardJoin))
//                        .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk());
//
//        mockMvc.perform(patch("/board/{boardId}/enrollment", board.getId())
//                        .content(objectMapper.writeValueAsString(boardJoin2))
//                        .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print());
//
//        mockMvc.perform(get("/board/{boardId}", board.getId())
//                        .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print());
//    }
//
//    @Test
//    @DisplayName("구매 취소")
//    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
//    void deleteJoin() throws Exception{
//        Category category = Category.builder()
//                .name("카테고리").build();
//        categoryRepository.save(category);
//
//        User user = User.builder()
//                .name("유저").build();
//        userRepository.save(user);
//
//        LocalDateTime now = LocalDateTime.now();
//        Board board = Board.builder()
//                .category(category)
//                .title("제목")
//                .content("내용")
//                .price(1000L)
//                .quantity(10)
//                .unitQuantity(2)
//                .unitPrice(200L)
//                .totalCount(10)
//                .url("url/")
//                .expireTime(now.plusDays(3))
//                .nowCount(2)
//                .user(user)
//                .build();
//        boardRepository.save(board);
//
//        BoardJoin boardJoin = BoardJoin.builder()
//                .quantity(5)
//                .build();
//
//        mockMvc.perform(post("/board/{boardId}/enrollment", board.getId())
//                        .content(objectMapper.writeValueAsString(boardJoin))
//                        .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk());
//
//        mockMvc.perform(delete("/board/{boardId}/enrollment", board.getId())
//                        .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print());
//
//        mockMvc.perform(get("/board/{boardId}", board.getId())
//                        .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print());
//    }

    @Test
    @DisplayName("구매자 명단")
    void getJoin() throws Exception{

    }

    @Test
    @DisplayName("판매 내역 조회")
    void getMySellList() throws Exception{
        User user = User.builder()
                .name("유저").build();
        userRepository.save(user);

        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);
        LocalDateTime date = LocalDateTime.now();
        List<Board> boards = IntStream.range(0,5)
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
                        .nowCount(i)
                        .user(user)
                        .build()).collect(Collectors.toList());
        boardRepository.saveAll(boards);

        mockMvc.perform(get("/board/sale/{userId}",user.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    @DisplayName("구매 내역 조회")
    void getMyJoinListTemp() throws Exception{

        List<User> users = IntStream.range(0,5)
                .mapToObj(i -> User.builder()
                        .name("이름" +i)
                        .build()).collect(Collectors.toList());
        userRepository.saveAll(users);

        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);
        LocalDateTime date = LocalDateTime.now();
        List<Board> boards = IntStream.range(0,5)
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
                        .nowCount(i)
                        .user(users.get(i))
                        .build()).collect(Collectors.toList());
        boardRepository.saveAll(boards);

        List<BoardMember> boardMembers = IntStream.range(1,5)
                .mapToObj(i -> BoardMember.builder()
                        .host(false)
                        .board(boards.get(i%5))
                        .user(users.get(0))
                        .quantity(i%5)
                        .build()).collect(Collectors.toList());
        boardMemberRepository.saveAll(boardMembers);

        mockMvc.perform(get("/board/sale/{userId}", users.get(0).getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}