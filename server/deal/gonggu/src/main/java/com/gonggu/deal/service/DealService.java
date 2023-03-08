package com.gonggu.deal.service;

import com.gonggu.deal.domain.*;
import com.gonggu.deal.exception.CategoryNotFound;
import com.gonggu.deal.exception.DealJoinFailed;
import com.gonggu.deal.exception.DealNotFound;
import com.gonggu.deal.exception.UserNotFound;
import com.gonggu.deal.repository.*;
import com.gonggu.deal.request.*;
import com.gonggu.deal.response.DealDetailResponse;
import com.gonggu.deal.response.DealMemberResponse;
import com.gonggu.deal.response.DealResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
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
    private final KeywordRepository keywordRepository;
    @Value("${geo.url}")
    private String apiUrl;
    @Value(("${geo.key}"))
    private String apiKey;


    public List<DealResponse> getList(DealSearch dealSearch, User user) {
        return dealRepository.getList(dealSearch, user).stream()
                .map(DealResponse::new).collect(Collectors.toList());
    }
    public DealDetailResponse getDealDetail(Long id){
        Deal deal = dealRepository.findById(id).orElseThrow(DealNotFound::new);
        DealDetailResponse dealDetailResponse = new DealDetailResponse(deal);
        return dealDetailResponse;
    }

    public void createDeal(DealCreate dealCreate, User user){
        Category category = categoryRepository.findById(dealCreate.getCategoryId()).orElseThrow(CategoryNotFound::new);
        Deal deal = Deal.builder()
                .title(dealCreate.getTitle())
                .content(dealCreate.getContent())
                .price(dealCreate.getPrice())
                .unitPrice(dealCreate.getPrice()/dealCreate.getUnitQuantity())
                .quantity(dealCreate.getUnitQuantity()*dealCreate.getTotalCount())
                .unitQuantity(dealCreate.getUnitQuantity())
                .unit(dealCreate.getUnit())
                .url(dealCreate.getUrl())
                .nowCount(dealCreate.getNowCount())
                .user(user)
                .createTime(LocalDateTime.now())
                .expireTime(dealCreate.getExpireTime().plusHours(9))
                .totalCount(dealCreate.getTotalCount())
                .category(category)
                .point(changeToJSON(findPointByKakao(dealCreate.getAddress())))
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
            for(String keyword : dealCreate.getKeywords()){
                Keyword findKey = keywordRepository.findByWord(keyword);
                if (findKey == null) {
                    findKey = Keyword.builder().word(keyword).build();
                    keywordRepository.save(findKey);
                }
                DealKeyword bk = DealKeyword.builder().keyword(findKey).deal(deal).build();
                dealKeywordRepository.save(bk);
            }
        }
        if(dealCreate.getImages() != null){
            List<DealImage> dealImages = dealCreate.getImages().stream()
                    .map(path-> new DealImage(deal,path)).collect(Collectors.toList());
            dealImages.get(0).setThumbnail(true);
            dealImageRepository.saveAll(dealImages);
        }
    }
    public void deleteDeal(Long id){
        Deal deal = dealRepository.findById(id).orElseThrow(DealNotFound::new);
        DealEditor.DealEditorBuilder editorBuilder = deal.toEditor();
        DealEditor dealEditor = editorBuilder.deletion(true).build();
        deal.edit(dealEditor);
    }
    public DealDetailResponse editDeal(Long id, DealEdit dealEdit){
        Deal deal = dealRepository.findById(id).orElseThrow(DealNotFound::new);
        DealEditor.DealEditorBuilder editorBuilder = deal.toEditor();
        DealEditor dealEditor = editorBuilder.content(dealEdit.getContent()).build();
        deal.edit(dealEditor);

        if (dealEdit.getKeywords()!=null){
            List<DealKeyword> deleteKeyword = dealKeywordRepository.findByDeal(deal);
            dealKeywordRepository.deleteAll(deleteKeyword);
            for(String keyword : dealEdit.getKeywords()){
                Keyword findKey = keywordRepository.findByWord(keyword);
                if (findKey == null) {
                    findKey = Keyword.builder().word(keyword).build();
                    keywordRepository.save(findKey);
                }
                DealKeyword bk = DealKeyword.builder().keyword(findKey).deal(deal).build();
                dealKeywordRepository.save(bk);
            }
        }
        if(dealEdit.getImages()!=null){
            List<DealImage> deleteImage = dealImageRepository.findByDeal(deal);
            for(DealImage dealImage : deleteImage){
                if(!dealEdit.getImages().contains(dealImage.getFileName())) dealImageRepository.delete(dealImage);
            }
            dealImageRepository.deleteAll(deleteImage);
            List<DealImage> dealImages = dealEdit.getImages().stream()
                    .map(path-> new DealImage(deal,path)).collect(Collectors.toList());
            dealImages.get(0).setThumbnail(true);
            dealImageRepository.saveAll(dealImages);
        }
        DealDetailResponse dealDetailResponse = new DealDetailResponse(deal);
        return dealDetailResponse;
    }

    public void updateView(Long dealId) {
        dealRepository.updateView(dealId);
    }

    public boolean createJoin(Long dealId, DealJoin join, User user) {
        Deal deal = dealRepository.findById(dealId).orElseThrow(DealNotFound::new);
        if(dealMemberRepository.findByDealAndUser(deal,user).isPresent()) throw new DealJoinFailed("이미 참여한 공구입니다.");
        if(deal.getNowCount() + join.getQuantity() > deal.getTotalCount()) throw new DealJoinFailed("구매 참여에 실패하였습니다.");

        deal.editCount(join.getQuantity() + deal.getNowCount());
        DealMember dealMember = DealMember.builder()
                .deal(deal)
                .user(user)
                .quantity(join.getQuantity())
                .build();
        dealMemberRepository.save(dealMember);
        if(deal.getNowCount() == deal.getTotalCount()) return true;
        else return false;
    }

    public boolean editJoin(Long dealId, DealJoin join, User user){
        Deal deal = dealRepository.findById(dealId).orElseThrow(DealNotFound::new);
        DealMember dealMember = dealMemberRepository.findByDealAndUser(deal, user).orElseThrow(UserNotFound::new);
        Integer afterCount = deal.getNowCount() + join.getQuantity() - dealMember.getQuantity();
        if(afterCount < 0 || afterCount > deal.getTotalCount()) throw new DealJoinFailed("수량 변경에 실패하였습니다.");

        deal.editCount(afterCount);
        dealMember.editQuantity(join.getQuantity());
        if(afterCount == deal.getTotalCount()) return true;
        else return false;
    }

    public void deleteJoin(Long dealId, User user){
        Deal deal = dealRepository.findById(dealId).orElseThrow(DealNotFound::new);
        DealMember dealMember = dealMemberRepository.findByDealAndUser(deal, user).orElseThrow(UserNotFound::new);
        deal.editCount(deal.getNowCount()-dealMember.getQuantity());
        dealMemberRepository.delete(dealMember);
    }

    public List<DealMemberResponse> getJoin(Long dealId){
        Deal deal = dealRepository.findById(dealId).orElseThrow(DealNotFound::new);
       return dealMemberRepository.findByDeal(deal).stream()
               .map(DealMemberResponse::new).collect(Collectors.toList());
    }

    public List<DealResponse> getSellDeal(String userId) {
        User user = userRepository.findByNickname(userId).orElseThrow(UserNotFound::new);
        return dealMemberRepository.getByUser(user,true).stream()
                .map(o-> new DealResponse(o.getDeal(), o.getQuantity())).collect(Collectors.toList());
    }

    public List<DealResponse> getJoinDeal(String userId){
        User user = userRepository.findByNickname(userId).orElseThrow(UserNotFound::new);
        return dealMemberRepository.getByUser(user,false).stream()
                .map(o-> new DealResponse(o.getDeal(), o.getQuantity())).collect(Collectors.toList());
    }

    public String findPointByKakao(String address){
        String jsonString = null;
        try {
            address = URLEncoder.encode(address,"UTF-8");
            String addr = apiUrl + address;
            String authKey = "KakaoAK " + apiKey;

            URL url = new URL(addr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", authKey);
            BufferedReader json = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuffer docJson = new StringBuffer();
            String line;
            while ((line = json.readLine()) != null) {
                docJson.append(line);
            }
            jsonString = docJson.toString();
            json.close();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return jsonString;
    }
    public Point changeToJSON(String jsonString) {

        JsonObject parseTree = null;
        try {
            parseTree = (JsonObject) JsonParser.parseString(jsonString);
            JsonArray documents = parseTree.getAsJsonArray("documents");
            JsonObject basicInfo = (JsonObject) documents.get(0);
            Double lon = Double.parseDouble(String.valueOf(basicInfo.get("x")).replaceAll("\\\"",""));
            Double lat = Double.parseDouble(String.valueOf(basicInfo.get("y")).replaceAll("\\\"",""));
            System.out.println("lon: "+lon+", lat: "+lat);
            String pointWKT = String.format("POINT(%s %s)", lon, lat);
            Point point = (Point) new WKTReader().read(pointWKT);
            return point;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
