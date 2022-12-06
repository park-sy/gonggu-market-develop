package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.jwt.JwtTokenProvider;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final CustomUserDetailService customUserDetailService;
    //private final EmailService emailService;
    //private final ResponseEntity response;

    //테스트 용
    @RequestMapping("/test")
    public String test(){
        return "test";
    }

    // 회원가입
    @PostMapping("/join")
    public JSONObject join(@RequestBody Map<String, String> user) throws ParseException {
    //public JSONObject join(@RequestBody String strget) throws ParseException {
        JSONParser parser = new JSONParser();
        //JSONObject jsonObject = (JSONObject) parser.parse(strget);
        //Map<String, String> user = jsonObject;

        if(!user.containsKey("id")){
            String str = String.format("{\"ok\":\"false\", \"error\":\"enter id\"}");
            JSONObject jsonObject = (JSONObject) parser.parse(str);
            return jsonObject;
        }

        if(!user.containsKey("email")){
            String str = String.format("{\"ok\":\"false\", \"error\":\"enter email\"}");
            JSONObject jsonObject = (JSONObject) parser.parse(str);
            return jsonObject;
        }

        if(!user.containsKey("password")){
            String str = String.format("{\"ok\":\"false\", \"error\":\"enter password\"}");
            JSONObject jsonObject = (JSONObject) parser.parse(str);
            return jsonObject;
        }

        Optional<User> member = userRepository.findByNickname(user.get("id"));
        if(member.isPresent()) {
            String str = String.format("{\"ok\":\"false\", \"error\":\"id already used\"}");
            //JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(str);
            return jsonObject;
        }
        userRepository.save(User.builder()
                .email(user.get("email"))
                .password(passwordEncoder.encode(user.get("password")))
                .nickname(user.get("id"))
                .roles(Collections.singletonList("ROLE_USER")) // 최초 가입시 USER 로 설정
                .build());
        String str = String.format("{\"ok\":\"true\"}");
        //JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(str);
        return jsonObject;
    }

    @PostMapping("/isduplicate")
    public JSONObject isduplicate(@RequestBody Map<String, String> user) throws ParseException {
        JSONParser parser = new JSONParser();
        //JSONObject jsonObject = (JSONObject) parser.parse(strget);
        //Map<String, String> user = jsonObject;
        //뭐를 받는지?
        Optional<User> member = userRepository.findByNickname(user.get("id"));
        if(member.isPresent()){
            String str = String.format("{\"ok\":\"false\", \"error\":\"id already used\"}");
            JSONObject jsonObject = (JSONObject) parser.parse(str);
            return jsonObject;
        }
        String str = String.format("{\"ok\":\"true\"}");
        JSONObject jsonObject = (JSONObject) parser.parse(str);
        return jsonObject;
    }

    // 로그인
    @PostMapping("/login")
    public JSONObject login(@RequestBody Map<String, String> user) throws ParseException {
    //public JSONObject login(@RequestBody String strget) throws ParseException {
        JSONParser parser = new JSONParser();
        //JSONObject jsonObject = (JSONObject) parser.parse(strget);
        //Map<String, String> user = jsonObject;

        if(!user.containsKey("id")){
            String str = String.format("{\"ok\":\"false\", \"error\":\"enter id\"}");
            //JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(str);
            return jsonObject;
        }
        if(!user.containsKey("password")){
            String str = String.format("{\"ok\":\"false\", \"error\":\"enter password\"}");
            //JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(str);
            return jsonObject;
        }
        Optional<User> member = userRepository.findByNickname(user.get("id"));

        if(member.isEmpty()){
            String str = String.format("{\"ok\" : \"false\", \"error\":\"wrong input\"}");
            JSONObject jsonObject = (JSONObject) parser.parse(str);
            return jsonObject;
        }

        if (!passwordEncoder.matches(user.get("password"), member.get().getPassword())) {
            String str = String.format("{\"ok\" : \"false\", \"error\":\"wrong input\"}");
            JSONObject jsonObject = (JSONObject) parser.parse(str);
            return jsonObject;
        }

        String token = jwtTokenProvider.createToken(member.get().getUsername(), member.get().getRoles());
        String str = String.format("{\"ok\" : \"true\", \"accesstoken\":\"%s\"}", token);
        JSONObject jsonObject = (JSONObject) parser.parse(str);
        return jsonObject;
    }
    //로그아웃
    /*
    @PostMapping("/user/logout")
    public JSONObject logout(HttpServletRequest request) throws ParseException {
        String token = jwtTokenProvider.resolveToken(request);
        Long expiration = jwtTokenProvider.getExpiration(token);

        redisTemplate.opsForValue()
            .set(token, "logout", expiration, TimeUnit.MILLISECONDS);
        String str = String.format("{\"ok\" : \"true\"}");
        //JSONObject jsonObject = new JSONObject(str);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(str);
        return jsonObject;
    }*/

    //유저 마이페이지
    @PostMapping("/user/mypage")
    public JSONObject mypage(Authentication authentication) throws ParseException {
        Optional<User> user= userRepository.findByNickname(authentication.getName());

        String obj = String.format("{\"email\" : \"%s\", \"id\" : \"%s\", \"keyword\" : %s}",
                user.get().getEmail(), user.get().getNickname(), customUserDetailService.getKeyword(user.get()));
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(obj);
        return jsonObject;
    }
    /*
    @PostMapping("temp")
    public void temp(@RequestBody String strget) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(strget);
        JSONArray jsonArray = (JSONArray) jsonObject.get("keyword");
        System.out.println(jsonArray.get(1));
    }*/

    //키워드 변경하기
    @PostMapping("user/updatekeyword")
    public JSONObject updatekeyword(Authentication authentication, @RequestBody String strget) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(strget);

        if(!jsonObject.containsKey("keyword")){
            String str = String.format("{\"ok\":\"false\", \"error\":\"enter keyword\"}");
            jsonObject = (JSONObject) parser.parse(str);
            return jsonObject;
        }

        List<String> memberArray = (List<String>) jsonObject.get("keyword");

        //System.out.println(memberArray.get(1));
        customUserDetailService.updateKeywords(authentication.getName(), memberArray);

        String str = String.format("{\"ok\" : \"true\"}");
        jsonObject = (JSONObject) parser.parse(str);
        return jsonObject;

    }

    //이메일 변경
    @PostMapping("user/changeemail")
    //public JSONObject changeemail(Authentication authentication, @RequestBody String strget) throws ParseException {
    public JSONObject changeemail(Authentication authentication, @RequestBody Map<String, String> user) throws ParseException {

        JSONParser parser = new JSONParser();
        //JSONObject jsonObject = (JSONObject) parser.parse(strget);
        //Map<String, String> user = jsonObject;

        if(!user.containsKey("email")){
            String str = String.format("{\"ok\":\"false\", \"error\":\"enter new email\"}");
            JSONObject jsonObject = (JSONObject) parser.parse(str);
            return jsonObject;
        }

        Optional<User> member = userRepository.findByNickname(authentication.getName());
        if(member.isEmpty()){
            String str = String.format("{\"ok\" : \"false\", \"error\" : \"wrong input\"}");
            JSONObject jsonObject = (JSONObject) parser.parse(str);
            return jsonObject;
        }
        customUserDetailService.updateEmail(member.get(), user.get("email"));
        String str = String.format("{\"ok\" : \"true\"}");
        JSONObject jsonObject = (JSONObject) parser.parse(str);
        return jsonObject;
    }

    //비밀번호 변경
   @PostMapping("user/changepassword")
    public JSONObject changepassword(Authentication authentication, @RequestBody Map<String, String> user) throws ParseException {
        JSONParser parser = new JSONParser();
        //JSONObject jsonObject = (JSONObject) parser.parse(strget);
        //Map<String, String> user = jsonObject;

       if(!user.containsKey("password")){
           String str = String.format("{\"ok\":\"false\", \"error\":\"enter new password\"}");
           JSONObject jsonObject = (JSONObject) parser.parse(str);
           return jsonObject;
       }

        Optional<User> member = userRepository.findByNickname(authentication.getName());
        if(member.isEmpty()){
            String str = String.format("{\"ok\" : \"false\", \"error\" : \"wrong input\"}");
            JSONObject jsonObject = (JSONObject) parser.parse(str);
            return jsonObject;
        }
        customUserDetailService.updatePassword(member.get(), passwordEncoder.encode(user.get("password")));
        String str = String.format("{\"ok\" : \"true\"}");
        JSONObject jsonObject = (JSONObject) parser.parse(str);
        return jsonObject;
    }

        /*
    @PostMapping("/user/verify")
    public Response verify(@RequestBody RequestVerifyEmail requestVerifyEmail, HttpServletRequest req, HttpServletResponse res) {
        Response response;
        try {
            User user = userRepository.findByEmail(requestVerifyEmail.getUsername()).get();
            emailService.sendVerificationMail(user);
            response = new Response("success", "성공적으로 인증메일을 보냈습니다.", null);
        } catch (Exception exception) {
            response = new Response("error", "인증메일을 보내는데 문제가 발생했습니다.", exception);
        }
        return response;
    }

    @GetMapping("/verify/{key}")
    public Response getVerify(@PathVariable String key) {
        Response response;
        try {
            emailService.verifyEmail(key);
            response = new Response("success", "성공적으로 인증메일을 확인했습니다.", null);

        } catch (Exception e) {
            response = new Response("error", "인증메일을 확인하는데 실패했습니다.", null);
        }
        return response;
    }
     */
}