package com.kindsonthegenius.fleetmsv2.security.controllers;

import com.kindsonthegenius.fleetmsv2.exception.InvalidTokenException;
import com.kindsonthegenius.fleetmsv2.security.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationController {

    private static final String REDIRECT_LOGIN = "redirect:/login";
    private static final String TOKEN_ERROR_KEY = "tokenError";
    private static final String TOKEN_ERROR_MESSAGE = "Token is invalid";
    private static final String VERIFICATION_SUCCESS_KEY = "verification.email.msg";

    private final UserService userService;
    private final MessageSource messageSource;

    @GetMapping("/verify")
    public String verifyUser(@RequestParam(required = false) String token, RedirectAttributes redirectAttributes) {
        if (!StringUtils.hasText(token)) {
            addTokenError(redirectAttributes);
            return REDIRECT_LOGIN;
        }

        try {
            userService.verifyUser(token);
            addSuccessMessage(redirectAttributes);
        } catch (InvalidTokenException e) {
            log.error("Error verifying user token: {}", token, e);
            addTokenError(redirectAttributes);
        }

        return REDIRECT_LOGIN;
    }

    private void addTokenError(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(TOKEN_ERROR_KEY, TOKEN_ERROR_MESSAGE);
    }

    private void addSuccessMessage(RedirectAttributes redirectAttributes) {
        String message = messageSource.getMessage(
                VERIFICATION_SUCCESS_KEY,
                null,
                LocaleContextHolder.getLocale()
        );
        redirectAttributes.addFlashAttribute("message", message);
    }
}