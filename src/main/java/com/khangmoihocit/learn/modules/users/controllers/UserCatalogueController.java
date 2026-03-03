package com.khangmoihocit.learn.modules.users.controllers;

import com.khangmoihocit.learn.modules.users.entities.User;
import com.khangmoihocit.learn.modules.users.requests.UserCatalogue.StoreRequest;
import com.khangmoihocit.learn.modules.users.resources.UserCatalogueResource;
import com.khangmoihocit.learn.modules.users.services.interfaces.UserCatalogueService;
import com.khangmoihocit.learn.modules.users.services.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Validated
@RequestMapping("${api.prefix}/user-catalogue")
public class UserCatalogueController {
    UserCatalogueService userCatalogueService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody StoreRequest request){

        return ResponseEntity.ok("");
    }
}
