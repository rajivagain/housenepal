package com.gyawalibros.controllers;

import com.cloudinary.utils.ObjectUtils;
import com.gyawalibros.Service.CloudinaryService;
import com.gyawalibros.config.CloudinaryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;

@Controller
public class HomeController {

    @Autowired
    CloudinaryConfig cloudinaryConfig;

    @RequestMapping("/")
    public String home() throws IOException {

        return "index";

    }
}