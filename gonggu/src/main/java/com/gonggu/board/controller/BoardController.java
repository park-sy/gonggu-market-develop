package com.gonggu.board.controller;

import com.gonggu.board.domain.User;
import com.gonggu.board.request.*;
import com.gonggu.board.response.BoardDetailResponse;
import com.gonggu.board.response.BoardMemberResponse;
import com.gonggu.board.response.BoardResponse;
import com.gonggu.board.service.BoardService;
import com.gonggu.board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final UserService userService;
    //게시글 불러오기
    @GetMapping("/board")
    public List<BoardResponse> getBoard(@ModelAttribute BoardSearch boardSearch){
        return boardService.getList(boardSearch);
    }
    //게시글 상세 보기
    @GetMapping("/board/{boardId}")
    public BoardDetailResponse requestBoard(@PathVariable Long boardId){
        boardService.updateView(boardId);
        return boardService.get(boardId);
    }
    //게시글 작성
    @PostMapping("/board/post")
    public void postBoard(@AuthenticationPrincipal User user,@RequestBody BoardCreate boardCreate){
      //  User user = boardService.findUserTemp(userTemp);
        boardService.createBoard(boardCreate, user);
    }
    @PostMapping("/board/temp")
    public void tempBoard(@AuthenticationPrincipal User user,@RequestBody BoardCreate boardCreate){
        boardService.createBoard(boardCreate, user);
    }
    //게시글 이미지 업로드
    @PostMapping("/board/{boardId}/image")
    public void uploadImage(@PathVariable Long boardId, @RequestParam  MultipartFile[] files){
        boardService.uploadImage(boardId, files);
    }
    //게시글 수정
    @PatchMapping("/board/{boardId}")
    public void editBoard(@PathVariable Long boardId, @ModelAttribute BoardEdit boardEdit){
        boardService.editBoard(boardId,boardEdit);
    }
    //게시글 삭제
    @DeleteMapping("/board/{boardId}/deletion")
    public void deleteBoard(@PathVariable Long boardId){
        boardService.deleteBoard(boardId);
    }


    //구매 참가 요청
    @PostMapping("/board/{boardId}/join")
    public void requestJoin(@PathVariable Long boardId, @AuthenticationPrincipal User user,
                            @RequestBody BoardJoin join){
        //User user = boardService.findUserTemp(userTemp);
        boardService.createJoin(boardId, join, user);
    }
    //구매 정보 수정
    @PatchMapping("/board/{boardId}/join")
    public void editJoin(@PathVariable Long boardId,@AuthenticationPrincipal User user,
                         @RequestBody BoardJoin join){
       // User user = boardService.findUserTemp(userTemp);
        boardService.editJoin(boardId,join,user);
    }

    //구매 철회
    @DeleteMapping("/board/{boardId}/join")
    public void deleteJoin(@PathVariable Long boardId, @ModelAttribute UserTemp userTemp){
        User user = boardService.findUserTemp(userTemp);
        boardService.deleteJoin(boardId,user);
    }
    //구매자 명단 가져오기
    @GetMapping("/board/{boardId}/join")
    public List<BoardMemberResponse> getJoin(@PathVariable Long boardId){
        return boardService.getJoin(boardId);
    }

    //내 판매 내역
    @GetMapping("/board/sell-list")
    public List<BoardResponse> getMySellBoard(@ModelAttribute UserTemp userTemp){
        User user = boardService.findUserTemp(userTemp);
        return boardService.getMySellBoard(user);
    }
    //내 구매 내역
    @GetMapping("/board/join-list")
    public List<BoardResponse> getMyJoinBoard(@ModelAttribute UserTemp userTemp){
        User user = boardService.findUserTemp(userTemp);
        return boardService.getMyJoinBoard(user);
    }

    @GetMapping("/board/join-list/temp")
    public List<BoardResponse> getMyJoinBoard(@AuthenticationPrincipal User user){
        return boardService.getMyJoinBoard(user);
    }
    @PostMapping("/board/user")
    public void createUser(@RequestBody UserCreate userCreate){
        userService.createUser(userCreate);
    }


    //참여 시 채팅방 서버에 request
}
