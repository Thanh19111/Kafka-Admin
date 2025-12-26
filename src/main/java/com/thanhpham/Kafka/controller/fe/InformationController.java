package com.thanhpham.Kafka.controller.fe;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/infor")
public class InformationController {
    @GetMapping
    public String t() {
        return "Test";
    }
}
