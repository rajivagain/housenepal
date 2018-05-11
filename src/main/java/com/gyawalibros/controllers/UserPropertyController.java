package com.gyawalibros.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.Singleton;
import com.gyawalibros.Repository.UserRepository;
import com.gyawalibros.Service.PropertyService;
import com.gyawalibros.Service.UserDetailsImpl;
import com.gyawalibros.Service.UserService;
import com.gyawalibros.domain.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserPropertyController {

    @Autowired
    PropertyService propertyService;

    private String UPLOADED_FOLDER = "G://temp//";

    @RequestMapping("/property/list")
    public String listUserProperties(Model model){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Property> properties = propertyService.findUserProperties(userDetails);

        model.addAttribute("properties", properties);

        return "user/property/list";
    }

    @GetMapping("/property/add")
    public String addProperty(Model model){
        Property property = new Property();
        model.addAttribute("property", property);
        return "user/property/add";
    }

    @PostMapping("/property/add")
    public String appPostProperty(@ModelAttribute Property property, @RequestParam("image[]") MultipartFile[] image) throws IOException {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        for(MultipartFile multipartFile: image) {
            System.out.println(multipartFile.getOriginalFilename());
            File file = new File(UPLOADED_FOLDER + multipartFile.getOriginalFilename());
            multipartFile.transferTo(file);
        }
        propertyService.addProperty(property, userDetails);
        return "redirect:/user/property/list";
    }



    @RequestMapping("/property/view/{propertyId}")
    public String viewUserProperty(@PathVariable String propertyId, Model model){
        Long propertyIdLong = Long.valueOf(propertyId);

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Property property = propertyService.getUserProperty(propertyIdLong, userDetails);

        model.addAttribute("property", property);
        return "user/property/view";
    }

    @RequestMapping("/property/update/{propertyId}")
    public String updateUserProperty(@PathVariable String propertyId, Model model){
        Long propertyIdLong = Long.valueOf(propertyId);
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Property property = propertyService.getUserProperty(propertyIdLong, userDetails);
        model.addAttribute("property", property);
        return "user/property/update";
    }

    @PostMapping("/property/update")
    public String updateByPutUserProperty(@ModelAttribute Property property){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        propertyService.updatePropertyByUser(property, userDetails);
        return "redirect:/user/property/list";
    }

    @GetMapping("/property/delete/{propertyId}")
    public String deleteUserProperty(@PathVariable String propertyId){
        Long propertyIdLong = Long.valueOf(propertyId);
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        propertyService.removeUserProperty(propertyIdLong, userDetails);
        return "redirect:/user/property/list";
    }
}