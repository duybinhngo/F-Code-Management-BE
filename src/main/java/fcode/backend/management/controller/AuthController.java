package fcode.backend.management.controller;

import fcode.backend.management.model.response.Response;
import fcode.backend.management.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static fcode.backend.management.service.constant.ServiceMessage.SUCCESS_MESSAGE;

@RestController
@RequestMapping
public class AuthController {
    @Autowired
    AuthService authService;
    @Value("${auth.login.member}")
    private String loginByMemberUrl;
    @Value("${auth.login.member.dev}")
    private String loginByMemberUrlDev;
    @Value("${auth.login.student}")
    private String loginByStudentUrl;
    @Value("${auth.register}")
    private String registerUrl;
    @Value("${auth.redirect.url}")
    private String authRedirectUrl;
    private static final Logger logger = LogManager.getLogger(AuthController.class);

    @GetMapping("/login/student")
    public RedirectView loginByStudentRedirect() throws IOException {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(loginByStudentUrl);
        return redirectView;
    }
    @GetMapping("/login/member/dev")
    public RedirectView loginByMemberRedirectDev(HttpServletResponse response) throws IOException {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(loginByMemberUrlDev);
        return redirectView;
    }
    @GetMapping("/register")
    public RedirectView registerRedirect(HttpServletResponse response) throws IOException {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(registerUrl);
        return redirectView;
    }
    @GetMapping("/auth/student/dev")
    public Response<String> loginByStudent(@RequestParam String code, HttpServletRequest request) {
        return authService.loginByStudent(code,request.getRequestURL().toString());
    }
    @GetMapping("/auth/member/dev")
    public Response<String> loginByMemberDev(@RequestParam String code, HttpServletRequest request) {
        return authService.loginByMember(code, request.getRemoteAddr(), request.getRequestURL().toString());
    }
    @GetMapping("/auth/register")
    public Response<Void> register(@RequestParam String code,HttpServletRequest request){
        return authService.register(code,request.getRequestURL().toString());
    }
    @RequestMapping(value = "/", method = RequestMethod.OPTIONS)
    public Response<Void> preflightRequest(HttpServletRequest request){
        return new Response<>(HttpStatus.OK.value(), SUCCESS_MESSAGE.getMessage());
    }
    @GetMapping("/login/member")
    public RedirectView loginByMemberRedirect(HttpServletResponse response) throws IOException {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(loginByMemberUrl);
        return redirectView;
    }
    @GetMapping("/auth/member")
    public RedirectView loginByMember(@RequestParam String code, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Response<String> result = authService.loginByMember(code, request.getRemoteAddr(), request.getRequestURL().toString());
        RedirectView redirectView = new RedirectView();
        if(result.getCode() == HttpStatus.OK.value())
            redirectView.setUrl(authRedirectUrl + "?success=true&token=" + result.getData());
        else redirectView.setUrl(authRedirectUrl + "?success=false&message=" + result.getData());
        return redirectView;
    }
}
