package com.gyawalibros.controllers;

import com.gyawalibros.Service.CloudinaryService;
import com.gyawalibros.Service.UserDetailsImpl;
import com.gyawalibros.Service.UserService;
import com.gyawalibros.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    CloudinaryService cloudinaryService;

    @GetMapping("/user")
    public String userProfile(Model model){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();
        User matchedUser = userService.getUserByEmail(user.getEmail());
        model.addAttribute("user", matchedUser);

        return "user/profile";
    }

    @GetMapping("/user/update")
    public String getUserUpdate(Model model){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();

        User currentUser = new User();
        currentUser.setFullName(user.getFullName());
        currentUser.setPrimaryPhoneNumber(user.getPrimaryPhoneNumber());
        currentUser.setSecondaryPhoneNumber(user.getSecondaryPhoneNumber());
        currentUser.setStreetName(user.getStreetName());
        currentUser.setAreaLocation(user.getAreaLocation());
        currentUser.setCityName(user.getCityName());

        model.addAttribute("user", currentUser);
        return "user/update";
    }

    @PostMapping("/user/update")
    public String postUserUpdate(@Valid @ModelAttribute User user, BindingResult bindingResult, Model model){

        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        for (FieldError fieldError :
                fieldErrors) {
            if (!fieldError.getField().equals("email") && !fieldError.getField().equals("password")) {
                model.addAttribute(user);
                return "/user/update";
            }
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userDetails.getUser();
        user.setId(currentUser.getId());

        userService.updateUser(user);
        return "redirect:/user";
    }

    @PostMapping("/user/profileImageUpload")
    public String profileImageUpload(Model model, @RequestParam("image") MultipartFile images) throws IOException {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();

        String fileURL = cloudinaryService.upload(images);
        userService.updateProfilePic(userDetails, fileURL);

        User matchedUser = userService.getUserByEmail(user.getEmail());
        String uploadMessage = "Uploaded Sucessfully!";
        model.addAttribute("uploadMessage", uploadMessage);
        model.addAttribute("user", matchedUser);
        System.out.println("UPLOADED");
        return "/user/profile";
    }
}