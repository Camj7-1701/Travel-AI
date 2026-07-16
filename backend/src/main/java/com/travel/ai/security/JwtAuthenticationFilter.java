package com.travel.ai.security;

import com.travel.ai.common.Result;
import com.travel.ai.util.JwtUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            
            try {
                if (!jwtUtil.isTokenExpired(token)) {
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    String username = jwtUtil.getUsernameFromToken(token);
                    
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userId, username, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JSON.toJSONString(Result.error(401, "Token无效")));
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
}