package com.example.demo.jwt;

import com.example.demo.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Collections;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean{
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    @Override
    //public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 헤더에서 JWT 를 받아옵니다.
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        //String path = httpServletRequest.getServletPath();
        String path = httpServletRequest.getRequestURI();
        if(path.contains("/user")){
            String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
            //String token = tokenUtil.requestBodyJsonV1(request, response);
            // 유효한 토큰인지 확인합니다.
            if (token != null && jwtTokenProvider.validateToken(token)) {
                //String isLogout = (String)redisTemplate.opsForValue().get(token);
                //System.out.println("access token validate");
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            /*
            if (ObjectUtils.isEmpty(isLogout)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }*/
                // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
                //Authentication authentication = jwtTokenProvider.getAuthentication(token);
                // SecurityContext 에 Authentication 객체를 저장합니다.
                //SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            else{
                //System.out.println("access token not validate");
                String refreshtoken = null;
                Cookie[] cookies = httpServletRequest.getCookies();
                if(cookies == null){
                    //System.out.println("cookies is null");
                }
                else{
                    for (Cookie cookie : cookies) {
                        String str = URLDecoder.decode(cookie.getValue(), "UTF-8");
                        if (cookie.getName().equals("refreshtoken")) {
                            refreshtoken = str;
                            break;
                        }
                    }
                    if(refreshtoken == null){
                        //System.out.println("refreshtoken null");
                    }
                    else {
                        String redisdata = redisService.getData(refreshtoken);


                        if (redisdata == null) {
                            //System.out.println("redis data null");
                        } else if (!jwtTokenProvider.validateToken(refreshtoken)) {
                            //System.out.println("not validate");
                        } else if (!redisdata.equals(jwtTokenProvider.getUserPk(refreshtoken))) {
                            //System.out.println("not equal");
                        } else if (redisdata != null && redisdata.equals(jwtTokenProvider.getUserPk(refreshtoken))) {
                            //System.out.println("refresh token validate");
                            String newtoken = jwtTokenProvider.createToken(jwtTokenProvider.getUserPk(refreshtoken), Collections.singletonList("USER"));
                            Authentication authentication = jwtTokenProvider.getAuthentication(newtoken);
                            SecurityContextHolder.getContext().setAuthentication(authentication);

                /*
                Cookie newcookie = new Cookie("newcookie", newtoken);
                newcookie.setHttpOnly(false);
                newcookie.setMaxAge((int)jwtTokenProvider.accesstokenValidTime);
                newcookie.setPath("/");
                //httpServletResponse = (HttpServletResponse) response;
                httpServletResponse.addCookie(newcookie);*/
                            httpServletRequest.setAttribute("accesstoken", newtoken);

                        } else {
                            //System.out.println("else");
                        }
                    }
                }
            }
        }

        if(httpServletRequest != null) chain.doFilter(httpServletRequest, response);
        else    System.out.println("request is null");
    }
}
