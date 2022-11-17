package com.gonggu.deal.service;

import com.gonggu.deal.domain.*;
import com.gonggu.deal.exception.DealJoinFailed;
import com.gonggu.deal.exception.DealNotFound;
import com.gonggu.deal.exception.UserNotFound;
import com.gonggu.deal.repository.*;
import com.gonggu.deal.request.*;
import com.gonggu.deal.response.DealDetailResponse;
import com.gonggu.deal.response.DealMemberResponse;
import com.gonggu.deal.response.DealResponse;
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
public class DealService {

    private final DealRepository dealRepository;
    private final DealKeywordRepository dealKeywordRepository;
    private final DealImageRepository dealImageRepository;
    private final DealMemberRepository dealMemberRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public User findUserTemp(UserTemp userTemp){
        return userRepository.findById(userTemp.getId()).orElseThrow();
    }

    public List<DealResponse> getList(DealSearch dealSearch) {
        return dealRepository.getList(dealSearch).stream()
                .map(DealResponse::new).collect(Collectors.toList());
    }
    public DealDetailResponse get(Long id){
        Deal deal = dealRepository.findById(id).orElseThrow(DealNotFound::new);
        DealDetailResponse dealDetailResponse = new DealDetailResponse(deal);
        return dealDetailResponse;
    }

    public void createDeal(DealCreate dealCreate, User user){
        Category category = categoryRepository.findById(dealCreate.getCategoryId()).orElseThrow();
        Deal deal = Deal.builder()
                .title(dealCreate.getTitle())
                .content(dealCreate.getContent())
                .price(dealCreate.getPrice())
                .unitPrice(dealCreate.getPrice()/dealCreate.getUnitQuantity())
                .quantity(dealCreate.getUnitQuantity()*dealCreate.getTotalCount())
                .unitQuantity(dealCreate.getUnitQuantity())
                .url(dealCreate.getUrl())
                .nowCount(dealCreate.getNowCount())
                .user(user)
                .totalCount(dealCreate.getTotalCount())
                .category(category)
                .build();
        dealRepository.save(deal);

        DealMember dealMember = DealMember.builder()
                .deal(deal)
                .user(user)
                .host(true)
                .quantity(dealCreate.getNowCount())
                .build();
        dealMemberRepository.save(dealMember);

        if(dealCreate.getKeywords() != null){
            DealKeyword bk;
            for(String keyword : dealCreate.getKeywords()){
                bk = DealKeyword.builder().keyword(keyword).deal(deal).build();
                dealKeywordRepository.save(bk);
            }
        }
    }
    public void deleteDeal(Long id){
        Deal deal = dealRepository.findById(id).orElseThrow(DealNotFound::new);
        DealEditor.DealEditorBuilder editorBuilder = deal.toEditor();
        DealEditor dealEditor = editorBuilder.deletion(false).build();
        deal.edit(dealEditor);
    }
    public DealDetailResponse editDeal(Long id, DealEdit dealEdit){
        Deal deal = dealRepository.findById(id).orElseThrow(DealNotFound::new);
        DealEditor.DealEditorBuilder editorBuilder = deal.toEditor();
        DealEditor dealEditor = editorBuilder.content(dealEdit.getContent()).build();
        deal.edit(dealEditor);

        DealDetailResponse dealDetailResponse = new DealDetailResponse(deal);
        return dealDetailResponse;
    }

    public void uploadImage(Long id, MultipartFile[] files) {
        Deal deal = dealRepository.findById(id).orElseThrow(DealNotFound::new);
        LocalDateTime localDateTime = LocalDateTime.now();
        String now = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String basicPath = System.getProperty("deal.dri")+"/files";
        String savePath = basicPath + "\\deal";
        if (!new File(basicPath).exists()) new File(basicPath).mkdir();
        if (!new File(savePath).exists()) new File(savePath).mkdir();
        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();
            String newFilename = now +"_"+ filename;
            String filePath = savePath + "\\" + newFilename;

            try {
                file.transferTo(new File(filePath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            DealImage dealImage = DealImage.builder()
                    .originFileName(filename)
                    .newFileName(newFilename)
                    .filePath("deal/" + newFilename)
                    .deal(deal).build();
            dealImageRepository.save(dealImage);
        }
    }

    public void updateView(Long dealId) {
        dealRepository.updateView(dealId);
    }

    public void createJoin(Long dealId, DealJoin join, User user) {
        Deal deal = dealRepository.findById(dealId).orElseThrow(DealNotFound::new);
        if(deal.getNowCount() + join.getQuantity() > deal.getTotalCount()){
            throw new DealJoinFailed("구매 참여에 실패하였습니다.");
        }
        deal.editCount(join.getQuantity() + deal.getNowCount());

        DealMember dealMember = DealMember.builder()
                .deal(deal)
                .user(user)
                .quantity(join.getQuantity())
                .build();
        dealMemberRepository.save(dealMember);
    }

    public void editJoin(Long dealId, DealJoin join, User user){
        Deal deal = dealRepository.findById(dealId).orElseThrow(DealNotFound::new);
        DealMember dealMember = dealMemberRepository.findByDealAndUser(deal, user);
        Integer afterCount = deal.getNowCount() + join.getQuantity() - dealMember.getQuantity();
        if(afterCount < 0 || afterCount > deal.getTotalCount()){
            throw new DealJoinFailed("수량 변경에 실패하였습니다.");
        }

        deal.editCount(afterCount);
        dealMember.editQuantity(join.getQuantity());
    }

    public void deleteJoin(Long dealId, User user){
        Deal deal = dealRepository.findById(dealId).orElseThrow(DealNotFound::new);
        DealMember dealMember = dealMemberRepository.findByDealAndUser(deal, user);
        deal.editCount(deal.getNowCount()-dealMember.getQuantity());
        dealMemberRepository.delete(dealMember);
    }

    public List<DealMemberResponse> getJoin(Long dealId){
        Deal deal = dealRepository.findById(dealId).orElseThrow(DealNotFound::new);
       return dealMemberRepository.findByDeal(deal).stream()
               .map(DealMemberResponse::new).collect(Collectors.toList());
    }

    public List<DealResponse> getSellDeal(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFound::new);
        return dealRepository.findByUser(user).stream()
                .map(DealResponse::new).collect(Collectors.toList());
    }

    public List<DealResponse> getJoinDeal(Long userId){
        User user = userRepository.findById(userId).orElseThrow(UserNotFound::new);
        return dealRepository.getJoinList(user).stream()
                .map(DealResponse::new).collect(Collectors.toList());
    }
}
