package de.hska.iwi.vslab.webshopcoreproductservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "/")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping(path = "/hello")
    public @ResponseBody String helloworld() {
        return "Hello World!";
    }

}
