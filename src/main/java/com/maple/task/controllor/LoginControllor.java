package com.maple.task.controllor;

import com.maple.task.common.PasswordException;
import com.maple.task.common.UserException;
import com.maple.task.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class LoginControllor {
    @Autowired
    private LoginService loginService;

    //被拦截的请求，重定向于此处，返回提示信息
    @GetMapping("/loginIntercept")
    public String intercept() {
        return "loginIntercept";
    }

    //登陆界面
    @GetMapping("/login")
    public ModelAndView login(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    //注册界面
    @GetMapping("/register")
    public ModelAndView register() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        return modelAndView;
    }

    //退出登录
    @GetMapping("/quit")
    public String quit(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return "login";
    }

    //检测表单信息是否符合登录要求，若符合则登录进入主页
    //若不符合则返回错误信息，回到登陆页面
    @PostMapping("/login")
    public ModelAndView loginAssert(HttpServletRequest request,
                                    @ModelAttribute("username") String username,
                                    @ModelAttribute("password") String password) {
        ModelAndView modelAndView = new ModelAndView();
        Map<String, Object> model = modelAndView.getModel();
        boolean sign = false;
        try {
            sign = loginService.login(username, password);
        } catch (UserException e) {
            model.put("userError", e.getMessage());
        } catch (PasswordException e) {
            model.put("passwordError", e.getMessage());
        }
        if (sign) {
            HttpSession session = request.getSession();
            session.setAttribute("user", username);

            modelAndView.setViewName("redirect:/");
        } else {
            modelAndView.setViewName("login");
        }
        return modelAndView;
    }

    //判定注册信息是否符合条件，不符合条件则保存错误信息，回到原页面
    //符合则完成注册，转到登陆页面
    @PostMapping("/register")
    public ModelAndView registerAssert(@ModelAttribute("username") String username,
                                       @ModelAttribute("password") String password) {
        ModelAndView modelAndView = new ModelAndView();
        Map<String, Object> model = modelAndView.getModel();
        int nameLength = username.length();

        //标记所有判定过程是否出错
        boolean totalSign = false;

        //标记用户名判定过程是否出错
        boolean sign = false;
        for (int i = 0; i < nameLength; i++) {
            char c = username.charAt(i);
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || ((c >= '0' && c <= '9')))) {
                sign = true;
                break;
            }
        }
        if (nameLength > 20 || nameLength < 10 || sign) {
            model.put("userError", "用户名由10~20个字母或数字组成");
            totalSign = true;
        }
        if (loginService.userExists(username)) {
            model.put("userError", "用户名已存在");
            totalSign = true;
        }
        int passwordLength = password.length();
        if (passwordLength > 20 || passwordLength < 6) {
            model.put("passwordError", "密码由6~20个字符组成");
            totalSign = true;
        }

        if (totalSign) {
            modelAndView.setViewName("register");
        } else {
            //加入普通用户
            loginService.addUser(username, password);
            modelAndView.setViewName("login");
            model.put("tipMsg", "注册成功，请登录");
        }

        return modelAndView;
    }
}
