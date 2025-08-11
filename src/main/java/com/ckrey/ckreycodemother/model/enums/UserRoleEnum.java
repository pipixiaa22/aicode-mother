package com.ckrey.ckreycodemother.model.enums;

import lombok.Getter;

@Getter
public enum UserRoleEnum {

    USER("普通用户","user"),
    ADMIN("管理员","admin");


    private final String text;

    private final String role;

     UserRoleEnum(String text, String role) {
        this.text = text;
        this.role = role;
    }

    public static UserRoleEnum getEnumByValue(String value){
        UserRoleEnum[] values = UserRoleEnum.values();
        for (UserRoleEnum userRoleEnum : values) {
            if (userRoleEnum.getRole().equals(value)){
                return userRoleEnum;
            }
        }
        return null;
    }






}
