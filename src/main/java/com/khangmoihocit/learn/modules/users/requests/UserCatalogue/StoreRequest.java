package com.khangmoihocit.learn.modules.users.requests.UserCatalogue;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreRequest {

    @NotBlank(message = "Tên nhóm thành viên không được để trống")
    String name;

    @Min(value = 0, message = "Giá trị trạng thái phải lớn hơn hoặc bằng 0")
    @Max(value = 2, message = "Giá trị trạng thái phải nhỏ hơn hoặc bằng 2")
    Integer publish;
}
