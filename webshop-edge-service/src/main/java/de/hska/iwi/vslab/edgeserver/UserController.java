package de.hska.iwi.vslab.edgeserver;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("user-api")
public class UserController {

    public static final String API_USERS = "http://user-service:8080/users";

}
