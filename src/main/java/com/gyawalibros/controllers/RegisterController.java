package com.gyawalibros.controllers;

import com.gyawalibros.Service.CloudinaryService;
import com.gyawalibros.Service.ConfirmationService;
import com.gyawalibros.Service.EmailService;
import com.gyawalibros.Service.UserService;
import com.gyawalibros.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class RegisterController {

    @Autowired
    UserService userService;

    @Autowired
    CloudinaryService cloudinaryService;

    @Autowired
    EmailService emailService;

    @Autowired
    ConfirmationService confirmationService;

//    @RequestMapping(value = "/login", method = RequestMethod.GET)
//    public String login() {
//        System.out.println("login");
//        return "login";
//    }

    @GetMapping("/register")
    public String getRegisterUser(Model model){
        User user = new User();
        model.addAttribute("user", user);
        return "user/register";
    }

    @PostMapping("/register")
    public String postRegisterUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model, HttpServletRequest httpServletRequest) {

        //Email pattern validation
        String regexEmail = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern patternEmail = Pattern.compile(regexEmail);
        Matcher matcherEmail = patternEmail.matcher(user.getEmail());

        Boolean foundUser = userService.checkEmail(user);
        if(bindingResult.hasErrors() || !matcherEmail.matches() || foundUser){
            model.addAttribute("user", user);
            if(!matcherEmail.matches()){
                String emailValidationMessage = "Please enter a valid email.";
                model.addAttribute("emailMessage", emailValidationMessage);
            }
            if(foundUser){
                String emailValidationMessage = "This email is already in use.";
                model.addAttribute("emailMessage", emailValidationMessage);
            }
            return "user/register";
        }

        //Validation Email to be Sent
        user.setConfirmationToken(UUID.randomUUID().toString());

        String url = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName() + ":8080";

        System.out.println(user.getConfirmationToken());

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(user.getEmail());
        simpleMailMessage.setSubject("Registerd Confirmation");
        simpleMailMessage.setText("To Confirm your email please click the link below:\n"
                +url+"/confirm?token="+user.getConfirmationToken());
        emailService.sendEmail(simpleMailMessage);


        userService.addUser(user);
        return "redirect:/user";
    }

    @GetMapping("/confirm")
    public String showConfirmationPage(Model model, @RequestParam("token") String token){

        User user = userService.findByConfirmationToken(token);
        confirmationService.activeUser(user, true);

        return "user/confirm";
    }

    @PostMapping("/confirm")
    public String processConfirmationForm(Model model, @RequestParam Map requestParams){
        User user = userService.findByConfirmationToken((String) requestParams.get("token"));

        user.setActive(true);
        userService.addUser(user);

        return "redirect:/user/login";
    }
}