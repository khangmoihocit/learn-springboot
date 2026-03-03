package com.khangmoihocit.learn.modules.users.services.impl;

import com.khangmoihocit.learn.modules.users.requests.UserCatalogue.StoreRequest;
import com.khangmoihocit.learn.modules.users.resources.UserCatalogueResource;
import com.khangmoihocit.learn.modules.users.services.interfaces.UserCatalogueService;
import com.khangmoihocit.learn.services.BaseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "UserCatalogueService")
@RequiredArgsConstructor
public class UserCatalogueServiceImpl extends BaseService implements UserCatalogueService {
    @Override
    public UserCatalogueResource save(StoreRequest request) {
        return null;
    }
}
