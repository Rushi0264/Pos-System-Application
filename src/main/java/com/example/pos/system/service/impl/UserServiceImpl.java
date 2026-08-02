package com.example.pos.system.service.impl;


import com.example.pos.system.configuration.JwtProvider;
import com.example.pos.system.domain.UserRole;
import com.example.pos.system.exception.UserException;
import com.example.pos.system.mapper.UserMapper;
import com.example.pos.system.modal.Branch;
import com.example.pos.system.modal.Store;
import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.UserDto;
import com.example.pos.system.repository.BranchRepository;
import com.example.pos.system.repository.StoreRepository;
import com.example.pos.system.repository.UserRepository;
import com.example.pos.system.service.UserService;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;



@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {



    private final UserRepository userRepository;

    private final JwtProvider jwtProvider;

    private final PasswordEncoder passwordEncoder;

    private final StoreRepository storeRepository;

    private final BranchRepository branchRepository;








    @Override
    public User getUserFromJwtToken(String token)
            throws UserException {



        String email =
                jwtProvider.getEmailFromToken(token);



        User user =
                userRepository.findByEmail(email);



        if(user==null){

            throw new UserException(
                    "Invalid token"
            );

        }


        return user;

    }








    @Override
    public User getCurrentUser()
            throws UserException {



        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();




        User user =
                userRepository.findByEmail(email);



        if(user==null){

            throw new UserException(
                    "User not found"
            );

        }


        return user;

    }









    @Override
    public User getUserByEmail(String email)
            throws UserException {



        User user =
                userRepository.findByEmail(email);



        if(user==null){

            throw new UserException(
                    "User not found"
            );

        }



        return user;

    }









    @Override
    public User getUserById(Long id, User currentUser) throws Exception {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new Exception("User not found"));

        if (currentUser.getRole() == UserRole.ROLE_SUPER_ADMIN) {
            return user;
        }

        if (currentUser.getStore() == null ||
                user.getStore() == null ||
                !currentUser.getStore().getId().equals(user.getStore().getId())) {

            throw new UserException("Access Denied");
        }

        return user;
    }










    @Override
    public List<User> getAllUsers(User currentUser) throws Exception {

        if (currentUser.getRole() == UserRole.ROLE_SUPER_ADMIN) {
            return userRepository.findAll();
        }

        if (currentUser.getStore() == null) {
            throw new UserException("Store not assigned");
        }

        return userRepository.findByStoreId(
                currentUser.getStore().getId()
        );
    }









    @Override
    @Transactional
    public User createUser(UserDto dto)
            throws UserException {



        User exist =
                userRepository.findByEmail(
                        dto.getEmail()
                );


        if(exist!=null){

            throw new UserException(
                    "Email already exists"
            );

        }




        User user = new User();


        user.setFullName(
                dto.getFullName()
        );


        user.setEmail(
                dto.getEmail()
        );


        user.setPassword(
                passwordEncoder.encode(
                        dto.getPassword()
                )
        );


        user.setPhone(
                dto.getPhone()
        );



        user.setRole(
                dto.getRole()
        );



        user.setCreatedAt(
                LocalDateTime.now()
        );


        user.setUpdatedAt(
                LocalDateTime.now()
        );





    /*
      Store optional
      User can be created first
    */


        if(dto.getStoreId()!=null){


            Store store =
                    storeRepository.findById(
                                    dto.getStoreId()
                            )
                            .orElseThrow(
                                    ()->new UserException(
                                            "Store not found"
                                    )
                            );


            user.setStore(store);

        }





        if(dto.getBranchId()!=null){


            Branch branch =
                    branchRepository.findById(
                                    dto.getBranchId()
                            )
                            .orElseThrow(
                                    ()->new UserException(
                                            "Branch not found"
                                    )
                            );


            user.setBranch(branch);

        }




        return userRepository.save(user);

    }











    @Override
    public User updateUser(
            Long id,
            UserDto dto
    ) throws Exception {



        User user =
                userRepository.findById(id)
                        .orElseThrow(
                                ()->new Exception(
                                        "User not found"
                                )
                        );




        if(dto.getFullName()!=null &&
                !dto.getFullName().isBlank()){

            user.setFullName(
                    dto.getFullName()
            );

        }



        if(dto.getEmail()!=null &&
                !dto.getEmail().isBlank()){

            user.setEmail(
                    dto.getEmail()
            );

        }



        if(dto.getPhone()!=null){

            user.setPhone(
                    dto.getPhone()
            );

        }



        if(dto.getRole()!=null){

            user.setRole(
                    dto.getRole()
            );

        }






        if(dto.getPassword()!=null &&
                !dto.getPassword().isBlank()){


            user.setPassword(
                    passwordEncoder.encode(
                            dto.getPassword()
                    )
            );

        }






        if(dto.getStoreId()!=null){


            Store store =
                    storeRepository
                            .findById(
                                    dto.getStoreId()
                            )
                            .orElseThrow(
                                    ()->new UserException(
                                            "Store not found"
                                    )
                            );


            user.setStore(store);


        }








        if(dto.getBranchId()!=null){


            Branch branch =
                    branchRepository
                            .findById(
                                    dto.getBranchId()
                            )
                            .orElseThrow(
                                    ()->new UserException(
                                            "Branch not found"
                                    )
                            );


            user.setBranch(branch);


        }







        user.setUpdatedAt(
                LocalDateTime.now()
        );



        return userRepository.save(user);

    }












    @Override
    public void deleteUser(Long id)
            throws Exception {



        User user =
                userRepository.findById(id)
                        .orElseThrow(
                                ()->new Exception(
                                        "User not found"
                                )
                        );





        if(user.getStore()!=null){


            throw new UserException(
                    "Cannot delete user assigned to store"
            );


        }





        userRepository.delete(user);


    }









    @Override
    public User findUserById(Long id){


        return userRepository
                .findById(id)
                .orElse(null);


    }









    @Override
    public List<User> getUsersByRole(UserRole role){


        return userRepository
                .findByRole(role);


    }









    @Override
    public void save(User user){


        userRepository.save(user);


    }









    @Override
    public List<User> getUsersByStore(Long storeId){



        return userRepository
                .findByStoreId(storeId);


    }









    @Override
    public List<UserDto> getEmployeesByStore(Long storeId){



        return userRepository
                .findByStoreId(storeId)
                .stream()
                .map(UserMapper::toDTO)
                .toList();


    }









    @Override
    public UserDto createEmployee(UserDto dto)
            throws UserException {



        User user =
                createUser(dto);



        return UserMapper.toDTO(user);


    }









    @Override
    public UserDto updateEmployee(
            Long id,
            UserDto dto
    ) throws Exception {



        User user =
                updateUser(id,dto);



        return UserMapper.toDTO(user);


    }









    @Override
    public void deleteEmployee(Long id)
            throws Exception {



        deleteUser(id);


    }


}