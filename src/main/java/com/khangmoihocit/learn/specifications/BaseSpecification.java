package com.khangmoihocit.learn.specifications;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;


public class BaseSpecification<T> {

    //fields: các trường cần tìm kiếm
    public static <T> Specification<T> keywordSpec(String keyword, String... fields) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isEmpty())
                return criteriaBuilder.conjunction();
            Predicate[] predicates = new Predicate[fields.length];
            //tạo ra các câu like
            for (int i = 0; i < fields.length; i++) {
                predicates[i] = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(fields[i])), "%" + keyword.toLowerCase() + "%");
            }
            return criteriaBuilder.or(predicates); //gộp các câu like: like %%a% or like %b% or ...
        };
    }


}
