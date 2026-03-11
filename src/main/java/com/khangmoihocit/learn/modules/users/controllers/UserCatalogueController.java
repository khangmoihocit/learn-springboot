package com.khangmoihocit.learn.modules.users.controllers;

import com.khangmoihocit.learn.Resources.ApiResource;
import com.khangmoihocit.learn.modules.users.entities.User;
import com.khangmoihocit.learn.modules.users.entities.UserCatalogue;
import com.khangmoihocit.learn.modules.users.requests.UserCatalogue.StoreRequest;
import com.khangmoihocit.learn.modules.users.resources.UserCatalogueResource;
import com.khangmoihocit.learn.modules.users.resources.UserResource;
import com.khangmoihocit.learn.modules.users.services.interfaces.UserCatalogueService;
import com.khangmoihocit.learn.modules.users.services.interfaces.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.apache.catalina.Store;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Validated
@RequestMapping("${api.prefix}/user-catalogue")
public class UserCatalogueController {
    UserCatalogueService userCatalogueService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody StoreRequest request){
        UserCatalogueResource userCatalogue = userCatalogueService.save(request);
        ApiResource<UserCatalogueResource> respond = ApiResource.ok(userCatalogue, "Tạo nhóm thành viên thành công");
        return ResponseEntity.ok(respond);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResource> update(@Valid @RequestBody StoreRequest request,
                                              @PathVariable(name = "id") Long id){

        return ResponseEntity.ok(ApiResource.ok(userCatalogueService.update(request, id), "cập nhật thành công"));
    }

    @GetMapping("/find-all")
    public ResponseEntity<?> findAll(HttpServletRequest request){
        Map<String, String[]> parameters = request.getParameterMap();
        Page<UserCatalogue> userCatalogues = userCatalogueService.panigate(parameters);
        Page<UserCatalogueResource> userCatalogueResources = userCatalogues.map(userCatalogue -> {
           return UserCatalogueResource.builder()
                   .id(userCatalogue.getId())
                   .name(userCatalogue.getName())
                   .publish(userCatalogue.getPublish().toString())
                   .build();
        });

        ApiResource<Page<UserCatalogueResource>> response = ApiResource.ok(userCatalogueResources, "SUCCESS");

        return ResponseEntity.ok(response);
    }
}
