package com.example.demo.controller;

import com.example.demo.entity.Device;
import com.example.demo.entity.User;
import com.example.demo.jwt.JwtTokenProvider;
import com.example.demo.repository.DeviceRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CustomUserDetailService;
import com.example.demo.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final CustomUserDetailService customUserDetailService;
    private final RedisService redisService;
    private final DeviceRepository deviceRepository;

    //테스트 용
    @RequestMapping("/test")
    public String test(){
        return "test";
    }

    //test
    @RequestMapping("/testa")
    public String testa(HttpServletRequest request) throws ServletException, ParseException {
        try{
            Cookie[] cookies = request.getCookies();
            //if(cookies.)
            for(Cookie cookie : cookies){
                String str = URLDecoder.decode(cookie.getValue(), "UTF-8");
                if(cookie.getName().equals("refreshtoken")){
                    //System.out.println(str);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "abc";
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
    public JSONObject login(@RequestBody Map<String, String> user, HttpServletResponse httpServletResponse) throws ParseException {
    //public JSONObject login(@RequestBody String strget) throws ParseException {
        JSONParser parser = new JSONParser();
        //JSONObject jsonObject = (JSONObject) parser.parse(strget);
        //Map<String, String> user = jsonObject;

        if(!user.containsKey("id")){
            String str = String.format("{\"ok\":\"false\", \"error\":\"enter id\"}");
            JSONObject jsonObject = (JSONObject) parser.parse(str);
            return jsonObject;
        }
        if(!user.containsKey("password")){
            String str = String.format("{\"ok\":\"false\", \"error\":\"enter password\"}");
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
        String refreshtoken = jwtTokenProvider.createRefreshToken(member.get().getUsername(), member.get().getRoles());
        redisService.setDataExpire(refreshtoken, member.get().getUsername(), jwtTokenProvider.refreshtokenValidTime);

        /*
        Cookie newcookie = new Cookie("refreshtoken", refreshtoken);
        newcookie.setHttpOnly(false);
        newcookie.setMaxAge((int)jwtTokenProvider.accesstokenValidTime);
        newcookie.setPath("/");
        newcookie.setDomain("localhost");
        httpServletResponse.addCookie(newcookie);
        */
        ResponseCookie newcookie = ResponseCookie.from("refreshtoken", refreshtoken) // key & value
                .httpOnly(true)
                //.secure(true)
                .domain(".09market.site")
                .path("/")
                .maxAge((int)jwtTokenProvider.accesstokenValidTime)
                //.sameSite("None")
                .build();
        httpServletResponse.setHeader("Set-Cookie", newcookie.toString());

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

    //device 등록
    @PostMapping("/user/register")
    public JSONObject register(Authentication authentication, @RequestBody String str) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(str);
        if(!jsonObject.containsKey("endpoint")){
            String ret = String.format("{\"ok\":\"false\", \"error\":\"enter endpoint\"}");
            JSONObject jsonObject1 = (JSONObject) parser.parse(ret);
            return jsonObject1;
        }
        if(!jsonObject.containsKey("keys")){
            String ret = String.format("{\"ok\":\"false\", \"error\":\"enter keys\"}");
            JSONObject jsonObject1 = (JSONObject) parser.parse(ret);
            return jsonObject1;
        }
        else{
            String endpoint = (String) jsonObject.get("endpoint");
            JSONObject keys = (JSONObject) jsonObject.get("keys");
            if(!keys.containsKey("auth")){
                String ret = String.format("{\"ok\":\"false\", \"error\":\"enter auth\"}");
                JSONObject jsonObject1 = (JSONObject) parser.parse(ret);
                return jsonObject1;
            }
            if(!keys.containsKey("p256dh")){
                String ret = String.format("{\"ok\":\"false\", \"error\":\"enter p256dh\"}");
                JSONObject jsonObject1 = (JSONObject) parser.parse(ret);
                return jsonObject1;
            }
            String auth = (String) keys.get("auth");
            String p256dh = (String) keys.get("p256dh");

            Optional<Device> device = deviceRepository.findByEndpoint(endpoint);
            if(device.isPresent()){
                String ret = String.format("{\"ok\":\"false\", \"error\":\"device already registered\"}");
                JSONObject jsonObject1 = (JSONObject) parser.parse(ret);
                return jsonObject1;
            }
            Optional<User> user= userRepository.findByNickname(authentication.getName());

            deviceRepository.save(Device.builder()
                    .user(user.get())
                    .auth(auth)
                    .p256dh(p256dh)
                    .endpoint(endpoint)
                    .build());

            String ret = String.format("{\"ok\" : \"true\"}");
            JSONObject jsonObject1 = (JSONObject) parser.parse(ret);
            return jsonObject1;
        }

    }

    //device 해제
    @PostMapping("/user/unregister")
    public JSONObject unregister(Authentication authentication, @RequestBody String str) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(str);

        if(!jsonObject.containsKey("endpoint")){
            String ret = String.format("{\"ok\":\"false\", \"error\":\"enter endpoint\"}");
            JSONObject jsonObject1 = (JSONObject) parser.parse(ret);
            return jsonObject1;
        }
        String endpoint = (String) jsonObject.get("endpoint");

        //Optional<User> user= userRepository.findByNickname(authentication.getName());

        Optional<Device> device = deviceRepository.findByEndpoint(endpoint);
        if(device.isEmpty()){
            String ret = String.format("{\"ok\":\"false\", \"error\":\"device is not registered\"}");
            JSONObject jsonObject1 = (JSONObject) parser.parse(ret);
            return jsonObject1;
        }
        deviceRepository.delete(device.get());

        String ret = String.format("{\"ok\" : \"true\"}");
        JSONObject jsonObject1 = (JSONObject) parser.parse(ret);
        return jsonObject1;
    }

    //유저 마이페이지
    @PostMapping("/user/mypage")
    public JSONObject mypage(Authentication authentication, HttpServletRequest httpServletRequest) throws ParseException, UnsupportedEncodingException {
        Optional<User> user= userRepository.findByNickname(authentication.getName());
        /*
        Cookie[] cookies = httpServletRequest.getCookies();
        for (Cookie cookie : cookies) {
            String str = URLDecoder.decode(cookie.getValue(), "UTF-8");
            if(cookie.getName().equals("accesstoken"))    System.out.println(str);
        }*/

        String newtoken = (String) httpServletRequest.getAttribute("accesstoken");
        //System.out.println(newtoken);
        String obj;
        if(newtoken == null){
            obj = String.format("{\"email\" : \"%s\", \"id\" : \"%s\", \"keyword\" : %s}",
                    user.get().getEmail(), user.get().getNickname(), customUserDetailService.getKeyword(user.get()));
        }
        else{
            obj = String.format("{\"email\" : \"%s\", \"id\" : \"%s\", \"keyword\" : %s, \"accesstoken\" : \"%s\"}",
                    user.get().getEmail(), user.get().getNickname(), customUserDetailService.getKeyword(user.get()), newtoken);
        }

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(obj);
        return jsonObject;
    }

    //키워드 변경하기
    @PostMapping("user/updatekeyword")
    public JSONObject updatekeyword(Authentication authentication, @RequestBody String str, HttpServletRequest httpServletRequest) throws ParseException {
        str = str.replace("\'", "\"");
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(str);

        if(!jsonObject.containsKey("keyword")){
            String str2 = String.format("{\"ok\":\"false\", \"error\":\"enter keyword\"}");
            jsonObject = (JSONObject) parser.parse(str2);
            return jsonObject;
        }

        List<String> memberArray = (List<String>) jsonObject.get("keyword");
        customUserDetailService.updateKeywords(authentication.getName(), memberArray);

        String newtoken = (String) httpServletRequest.getAttribute("accesstoken");
        String str2;
        if(newtoken == null){
            str2 = String.format("{\"ok\" : \"true\"}");
        }
        else{
            str2 = String.format("{\"ok\" : \"true\", \"accesstoken\" : \"%s\"}", newtoken);
        }
        jsonObject = (JSONObject) parser.parse(str2);
        return jsonObject;

    }

    //test
    @PostMapping("updatekeyword1")
    public void updatekeyword1(@RequestBody String str) throws ParseException {
        str = str.replace("\'", "\"");
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(str);
        List<String> memberArray = (List<String>)jsonObject.get("keyword");
        for(String k : memberArray){
            //System.out.println(k);
        }


    }
    //이메일 변경
    @PostMapping("user/changeemail")
    //public JSONObject changeemail(Authentication authentication, @RequestBody String strget) throws ParseException {
    public JSONObject changeemail(Authentication authentication, @RequestBody Map<String, String> user, HttpServletRequest httpServletRequest) throws ParseException {

        JSONParser parser = new JSONParser();

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

        String newtoken = (String) httpServletRequest.getAttribute("accesstoken");
        String str;
        if(newtoken == null){
            str = String.format("{\"ok\" : \"true\"}");
        }
        else{
            str = String.format("{\"ok\" : \"true\", \"accesstoken\" : \"%s\"}", newtoken);
        }
        JSONObject jsonObject = (JSONObject) parser.parse(str);
        return jsonObject;
    }

    //비밀번호 변경
   @PostMapping("user/changepassword")
    public JSONObject changepassword(Authentication authentication, @RequestBody Map<String, String> user, HttpServletRequest httpServletRequest) throws ParseException {
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
       String newtoken = (String) httpServletRequest.getAttribute("accesstoken");
       String str;
       if(newtoken == null){
           str = String.format("{\"ok\" : \"true\"}");
       }
       else{
           str = String.format("{\"ok\" : \"true\", \"accesstoken\" : \"%s\"}", newtoken);
       }
        JSONObject jsonObject = (JSONObject) parser.parse(str);
        return jsonObject;
    }
}