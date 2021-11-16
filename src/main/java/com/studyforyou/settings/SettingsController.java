package com.studyforyou.settings;

import com.studyforyou.account.AccountService;
import com.studyforyou.account.CurrentUser;
import com.studyforyou.domain.Account;
import com.studyforyou.dto.PasswordForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    public static final String SETTINGS_PROFILE = "settings/profile";
    public static final String SETTINGS_PASSWORD = "settings/password";
    private final AccountService accountService;

    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordFormValidator());
    }

    @GetMapping("/settings/profile")
    public String profileUpdateForm(@CurrentUser Account account, Model model) {

        model.addAttribute(account);
        Profile profile = Profile.createProfile(account);
        model.addAttribute(profile);
        return SETTINGS_PROFILE;
    }

    @PostMapping("/settings/profile")
    public String profileUpdate(@CurrentUser Account account, @Valid Profile profile, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_PROFILE;
        }

        accountService.completeProfileUpdate(account, profile);
        model.addAttribute(account);
        return "redirect:/profile/" + account.getNickname();
    }

    @GetMapping("/settings/password")
    public String passwordUpdate(@CurrentUser Account account, Model model) {

        model.addAttribute(account);
        model.addAttribute("passwordForm", new PasswordForm());
        return SETTINGS_PASSWORD;
    }

    @PostMapping("/settings/password")
    public String passwordUpdate(@Valid PasswordForm passwordForm, BindingResult bindingResult,
                                 Model model ,@CurrentUser Account account, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_PASSWORD;
        }
        accountService.updatePassword(passwordForm, account);

        model.addAttribute(account);
        redirectAttributes.addFlashAttribute("message", "패스워드 변경이 완료 되었습니다.");
        return "redirect:/"+SETTINGS_PASSWORD;
    }
}
