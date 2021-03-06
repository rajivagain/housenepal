package com.gyawalibros.controllers;

import com.gyawalibros.Service.UserService;
import com.gyawalibros.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminRestController {

    @Autowired
    UserService userService;


    @GetMapping(path = "/user/all")
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }
}
