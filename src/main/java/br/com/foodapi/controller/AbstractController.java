package br.com.foodapi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static br.com.foodapi.controller.UserController.CONSTANT_PATH;


@RequestMapping(CONSTANT_PATH)
@RestController
public abstract class AbstractController {

    public static final String CONSTANT_PATH = "/api/v1";
}
