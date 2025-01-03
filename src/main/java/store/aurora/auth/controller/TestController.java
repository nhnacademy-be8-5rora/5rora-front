package store.aurora.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import store.aurora.common.annotation.Auth;

@Controller
public class TestController {

    private static final Logger USER_LOG = LoggerFactory.getLogger("user-logger");

    //todo 제거 예정
    @GetMapping("/login-test")
    public String userIdTest(@Auth String username, Model model){
        model.addAttribute("username", username);
        return "login-test";
    }

    @GetMapping("/")
    public String indexTest(){
        return "home";
    }
}
