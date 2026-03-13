package com.khangmoihocit.learn.modules.users.services.interfaces;

import com.khangmoihocit.learn.modules.users.entities.UserCatalogue;
import com.khangmoihocit.learn.modules.users.requests.UserCatalogue.StoreRequest;
import com.khangmoihocit.learn.modules.users.resources.UserCatalogueResource;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface UserCatalogueService {
    UserCatalogueResource save(StoreRequest request);
    UserCatalogueResource update(StoreRequest request, Long id);
    Page<UserCatalogue> panigate(Map<String, String[]> parameters);
}
