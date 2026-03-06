package com.khangmoihocit.learn.modules.users.services.impl;

import com.khangmoihocit.learn.helpers.AppException;
import com.khangmoihocit.learn.modules.users.entities.UserCatalogue;
import com.khangmoihocit.learn.modules.users.repositories.UserCatalogueRepository;
import com.khangmoihocit.learn.modules.users.requests.UserCatalogue.StoreRequest;
import com.khangmoihocit.learn.modules.users.resources.UserCatalogueResource;
import com.khangmoihocit.learn.modules.users.services.interfaces.UserCatalogueService;
import com.khangmoihocit.learn.services.BaseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "UserCatalogueService")
@RequiredArgsConstructor
public class UserCatalogueServiceImpl extends BaseService implements UserCatalogueService {
    UserCatalogueRepository userCatalogueRepository;

    @Override
    public UserCatalogueResource save(StoreRequest request) {
        UserCatalogue userCatalogue = UserCatalogue.builder()
                .name(request.getName())
                .publish(request.getPublish())
                .build();
        userCatalogue = userCatalogueRepository.save(userCatalogue);
        UserCatalogueResource respond = UserCatalogueResource.builder()
                .id(userCatalogue.getId())
                .name(userCatalogue.getName())
                .publish(userCatalogue.getPublish().toString())
                .build();

        return respond;
    }

    @Override
    public UserCatalogueResource update(StoreRequest request, Long id) {
        UserCatalogue userCatalogue = userCatalogueRepository.findById(id)
                .orElseThrow(()-> new AppException("Không tìm thấy user catalogue"));

        userCatalogue.setName(request.getName());
        userCatalogue.setPublish(request.getPublish());
        userCatalogue = userCatalogueRepository.save(userCatalogue);

        UserCatalogueResource respond = UserCatalogueResource.builder()
                .id(userCatalogue.getId())
                .name(userCatalogue.getName())
                .publish(userCatalogue.getPublish().toString())
                .build();
        return respond;
    }

    @Override
    public Page<UserCatalogueResource> panigate(Map<String, String[]> parameters) {
        return null;
    }


}
