package com.gonggu.board.service;

import com.gonggu.board.domain.*;
import com.gonggu.board.repository.*;
import com.gonggu.board.request.*;
import com.gonggu.board.response.BoardDetailResponse;
import com.gonggu.board.response.BoardMemberResponse;
import com.gonggu.board.response.BoardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardKeywordRepository boardKeywordRepository;
    private final BoardImageRepository boardImageRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final UserRepository userRepository;

    public User findUserTemp(UserTemp userTemp){
        return userRepository.findById(userTemp.getId()).orElseThrow();
    }

    public List<BoardResponse> getList(BoardSearch boardSearch) {
        return boardRepository.getList(boardSearch).stream()
                .map(BoardResponse::new).collect(Collectors.toList());
    }
    public BoardDetailResponse get(Long id){
        Board board = boardRepository.findById(id).orElseThrow();
        BoardDetailResponse boardDetailResponse = new BoardDetailResponse(board);
        return boardDetailResponse;
    }

    public void createBoard(BoardCreate boardCreate, User user){
        Board board = Board.builder()
                .title(boardCreate.getTitle())
                .content(boardCreate.getContent())
                .price(boardCreate.getPrice())
                .quantity(boardCreate.getQuantity())
                .url(boardCreate.getUrl())
                .nowCount(boardCreate.getNowCount())
                .user(user)
                .recruitmentNumber(boardCreate.getRecruitmentNumber())
                .build();
        boardRepository.save(board);

        BoardMember boardMember = BoardMember.builder()
                .board(board)
                .user(user)
                .host(true)
                .quantity(boardCreate.getQuantity())
                .build();
        boardMemberRepository.save(boardMember);

        if(boardCreate.getKeywords() != null){
            BoardKeyword bk;
            for(String keyword : boardCreate.getKeywords()){
                bk = BoardKeyword.builder().keyword(keyword).board(board).build();
                boardKeywordRepository.save(bk);
            }
        }

    }
    public void deleteBoard(Long id){
        Board board = boardRepository.findById(id).orElseThrow();
        BoardEditor.BoardEditorBuilder editorBuilder = board.toEditor();
        BoardEditor boardEditor = editorBuilder.deletion(false).build();
        board.edit(boardEditor);
    }
    public BoardDetailResponse editBoard(Long id, BoardEdit boardEdit){
        Board board = boardRepository.findById(id).orElseThrow();
        BoardEditor.BoardEditorBuilder editorBuilder = board.toEditor();
        BoardEditor boardEditor = editorBuilder.content(boardEdit.getContent()).build();
        board.edit(boardEditor);

        BoardDetailResponse boardDetailResponse = new BoardDetailResponse(board);
        return boardDetailResponse;
    }

    public void uploadImage(Long id, MultipartFile[] files) {
        Board board = boardRepository.findById(id).orElseThrow();
        LocalDateTime localDateTime = LocalDateTime.now();
        String now = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String basicPath = System.getProperty("board.dri")+"/files";
        String savePath = basicPath + "\\board";
        if (!new File(basicPath).exists()) new File(basicPath).mkdir();
        if (!new File(savePath).exists()) new File(savePath).mkdir();
        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();
            String newFilename = now + filename;
            String filePath = savePath + "\\" + newFilename;
            try {
                file.transferTo(new File(filePath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            BoardImage boardImage = BoardImage.builder()
                    .originFileName(filename)
                    .newFileName(newFilename)
                    .filePath("board/" + newFilename)
                    .board(board).build();
            boardImageRepository.save(boardImage);
        }
    }

    public void updateView(Long boardId) {
        boardRepository.updateView(boardId);
    }

    public void createJoin(Long boardId, BoardJoin join, User user) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        board.editCount(join.getQuantity() + board.getNowCount());
        BoardMember boardMember = BoardMember.builder()
                .board(board)
                .user(user)
                .quantity(join.getQuantity())
                .build();
        boardMemberRepository.save(boardMember);
    }

    public void editJoin(Long boardId, BoardJoin join, User user){
        Board board = boardRepository.findById(boardId).orElseThrow();
        BoardMember boardMember = boardMemberRepository.findByBoardAndUser(board, user);
        Integer afterCount = board.getNowCount() + join.getQuantity() - boardMember.getQuantity();
        if(afterCount < 0 || afterCount > board.getRecruitmentNumber()) return;

        board.editCount(afterCount);
        boardMember.editQuantity(join.getQuantity());
    }

    public void deleteJoin(Long boardId, User user){
        Board board = boardRepository.findById(boardId).orElseThrow();
        BoardMember boardMember = boardMemberRepository.findByBoardAndUser(board, user);
        board.editCount(board.getNowCount()-boardMember.getQuantity());
        boardMemberRepository.delete(boardMember);
    }

    public List<BoardMemberResponse> getJoin(Long boardId){
        Board board = boardRepository.findById(boardId).orElseThrow();
       return boardMemberRepository.findByBoard(board).stream()
               .map(BoardMemberResponse::new).collect(Collectors.toList());
    }

    public List<BoardResponse> getMySellBoard(User user) {
        return boardRepository.findByUser(user).stream()
                .map(BoardResponse::new).collect(Collectors.toList());
    }

    public List<BoardResponse> getMyJoinBoard(User user){
        return boardRepository.getJoinList(user).stream()
                .map(BoardResponse::new).collect(Collectors.toList());
    }
}
