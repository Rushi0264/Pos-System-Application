package com.example.pos.system.mapper;


import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.UserDto;




public class UserMapper {

    //private com.example.pos.system.domain.StoreStatus storeStatus;

    public static UserDto toDTO(User user){

        UserDto dto=new UserDto();

        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());

        if(user.getStore()!=null){

            dto.setStoreId(
                    user.getStore().getId()
            );

            dto.setStoreBrand(
                    user.getStore().getBrand()
            );

            dto.setStoreStatus(
                    user.getStore().getStatus()
            );

        }

        if(user.getBranch()!=null){

            dto.setBranchId(
                    user.getBranch().getId()
            );

            dto.setBranchName(
                    user.getBranch().getName()
            );

            if(dto.getStoreStatus() == null
                    && user.getBranch().getStore() != null){

                dto.setStoreId(
                        user.getBranch().getStore().getId()
                );

                dto.setStoreBrand(
                        user.getBranch().getStore().getBrand()
                );

                dto.setStoreStatus(
                        user.getBranch().getStore().getStatus()
                );

            }

        }

        return dto;

    }





    public static User toEntity(UserDto dto){



        User user = new User();



        user.setFullName(
                dto.getFullName()
        );



        user.setEmail(
                dto.getEmail()
        );



        user.setPhone(
                dto.getPhone()
        );



        user.setRole(
                dto.getRole()
        );



        user.setPassword(
                dto.getPassword()
        );



        user.setCreatedAt(
                dto.getCreatedAt()
        );



        user.setUpdatedAt(
                dto.getUpdatedAt()
        );



        user.setLastLogin(
                dto.getLastLogin()
        );



        return user;

    }

}