package com.mySpringProject.main;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mySpringProject.services.TestClass;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
public class TestController {
	@Autowired
	private TestClass kakaoService;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
 
		
		
		return "home";
	}
	
    @RequestMapping(value="/main/kakao_login.ajax")
    public String kakaoLogin() {
        StringBuffer loginUrl = new StringBuffer();
        loginUrl.append("https://kauth.kakao.com/oauth/authorize?client_id=");
        loginUrl.append("2f2662c79b2457f82f5e188d75b827ac"); 
        loginUrl.append("&redirect_uri=");
        loginUrl.append("http://192.168.0.165/kakao_callback"); 
        loginUrl.append("&response_type=code");
        
        return "redirect:"+loginUrl.toString();
    }
	
    @RequestMapping(value = "/kakao_callback", method = RequestMethod.GET)
    public String redirectkakao(@RequestParam String code, HttpSession session) throws IOException {
            System.out.println(code);
            
            //접속토큰 get
            String access_Token = kakaoService.getReturnAccessToken(code);
            
            //접속자 정보 get
            Map<String,Object> userInfo = kakaoService.getUserInfo(access_Token);
            System.out.println("컨트롤러 출력"+userInfo.get("nickname")+userInfo.get("email"));
            if(userInfo.get("email") !=null) {
            	session.setAttribute("userId", userInfo.get("email"));
            	session.setAttribute("access_Token", access_Token);
            }
            
        return "testLogOut";
    }
    
    @RequestMapping(value="/login/logout_proc")
    public String logout(ModelMap modelMap, HttpSession session)throws IOException {
    	String kakaoToken = (String)session.getAttribute("kakaoToken");
    	
        if(kakaoToken != null && !"".equals(kakaoToken)){
        	kakaoService.getLogout(kakaoToken);
        	session.removeAttribute("kakaoToken");
        	session.removeAttribute("userId");
        }else {
            System.out.println("이미 로그아웃");
        }
        
        System.out.println("로그아웃");
        return "testHome";
    }


}
