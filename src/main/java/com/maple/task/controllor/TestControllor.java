package com.maple.task.controllor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestControllor {
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/test")
    public void test() {

    }
}
