package com.thanhpham.Kafka.controller.fe;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class HomeUIController {
    @GetMapping
    public String home() {
        return "index";
    }
}
