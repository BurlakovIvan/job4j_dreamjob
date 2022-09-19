package ru.job4j.dreamjob.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;
import ru.job4j.dreamjob.utilits.UserSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@ThreadSafe
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public String users(Model model, HttpSession session) {
        model.addAttribute("user", UserSession.user(session));
        model.addAttribute("users", userService.findAll());
        return "users";
    }

    @GetMapping("/formAddUser")
    public String addUser(Model model, HttpSession session) {
        model.addAttribute("user", UserSession.user(session));
        return "addUser";
    }

    @PostMapping("/registration")
    public String registration(Model model, @ModelAttribute User user) {
        Optional<User> regUser = userService.add(user);
        if (regUser.isEmpty()) {
            return "redirect:/fail";
        }
        return "redirect:/index";
    }

    @GetMapping("/fail")
    public String failRegistration(Model model) {
        User userSession = new User();
        userSession.setName("Гость");
        model.addAttribute("user", userSession);
        model.addAttribute("message", "Пользователь с такой почтой уже существует");
        return "fail";
    }

    @PostMapping("/failRedirect")
    public String failRedirect(Model model) {
        return "redirect:/formAddUser";
    }

    @GetMapping("/loginPage")
    public String loginPage(Model model, @RequestParam(name = "fail", required = false) Boolean fail) {
        model.addAttribute("fail", fail != null);
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute User user, HttpServletRequest req) {
        Optional<User> userDb = userService.findUserByEmailAndPwd(
                user.getEmail(), user.getPassword()
        );
        if (userDb.isEmpty()) {
            return "redirect:/loginPage?fail=true";
        }
        HttpSession session = req.getSession();
        session.setAttribute("user", userDb.get());
        return "redirect:/index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/loginPage";
    }
}
