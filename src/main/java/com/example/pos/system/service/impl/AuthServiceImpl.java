package com.example.pos.system.service.impl;

import com.example.pos.system.configuration.JwtProvider;
import com.example.pos.system.domain.UserRole;
import com.example.pos.system.exception.UserException;
import com.example.pos.system.mapper.UserMapper;
import com.example.pos.system.modal.Branch;
import com.example.pos.system.modal.Store;
import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.UserDto;
import com.example.pos.system.payload.response.AuthResponse;
import com.example.pos.system.repository.BranchRepository;
import com.example.pos.system.repository.StoreRepository;
import com.example.pos.system.repository.UserRepository;
import com.example.pos.system.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final CustomUserImplementation customUserImplementation;
    private final StoreRepository storeRepository;
    private final BranchRepository branchRepository;



    @Override
    public AuthResponse signUp(UserDto userDto) throws UserException {


        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            throw new UserException("Email is already registered.");
        }


        boolean superAdminExists =
                userRepository.existsByRole(UserRole.ROLE_SUPER_ADMIN);


        if (superAdminExists) {
            throw new UserException(
                    "Public registration closed. Please contact your Super Admin to create an account."
            );
        }


        User newUser = new User();

        newUser.setFullName(userDto.getFullName());
        newUser.setEmail(userDto.getEmail());
        newUser.setPhone(userDto.getPhone());
        newUser.setPassword(
                passwordEncoder.encode(userDto.getPassword())
        );

        newUser.setRole(UserRole.ROLE_SUPER_ADMIN);

        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        newUser.setLastLogin(LocalDateTime.now());


        User savedUser = userRepository.save(newUser);



        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDto.getEmail(),
                        userDto.getPassword()
                );


        SecurityContextHolder.getContext()
                .setAuthentication(authentication);



        String jwt =
                jwtProvider.generateToken(authentication);



        AuthResponse response = new AuthResponse();

        response.setJwt(jwt);
        response.setMessage("Registered Successfully");
        response.setUser(
                UserMapper.toDTO(savedUser)
        );


        return response;
    }





    @Override
    public AuthResponse login(UserDto userDto)
            throws UserException {


        String email = userDto.getEmail();
        String password = userDto.getPassword();


        Authentication authentication =
                authenticate(email,password);



        SecurityContextHolder.getContext()
                .setAuthentication(authentication);



        String jwt =
                jwtProvider.generateToken(authentication);



        User user =
                userRepository.findByEmail(email);



        user.setLastLogin(LocalDateTime.now());

        userRepository.save(user);



        AuthResponse response =
                new AuthResponse();


        response.setJwt(jwt);
        response.setMessage("Login Successfully");
        response.setUser(
                UserMapper.toDTO(user)
        );


        return response;
    }





    private Authentication authenticate(
            String email,
            String password
    ) throws UserException {



        UserDetails userDetails =
                customUserImplementation
                        .loadUserByUsername(email);



        if(userDetails == null){
            throw new UserException(
                    "email id doesn't exist " + email
            );
        }



        if(!passwordEncoder.matches(
                password,
                userDetails.getPassword()
        )){
            throw new UserException(
                    "password doesn't match"
            );
        }



        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }







    private void validateUserCreationPermission(
            UserRole creatorRole,
            UserRole newRole
    ) throws UserException {



        switch (creatorRole){


            case ROLE_SUPER_ADMIN:


                if(newRole == UserRole.ROLE_SUPER_ADMIN){

                    throw new UserException(
                            "Super Admin already exists."
                    );
                }


                break;



            case ROLE_STORE_ADMIN:


                if(
                        newRole != UserRole.ROLE_BRANCH_MANAGER &&
                                newRole != UserRole.ROLE_BRANCH_CASHIER &&
                                newRole != UserRole.ROLE_INVENTORY_MANAGER &&
                                newRole != UserRole.ROLE_ACCOUNTANT
                ){

                    throw new UserException(
                            "Store Admin can create only Branch Manager, Cashier, Inventory Manager and Accountant."
                    );
                }


                break;



            default:

                throw new UserException(
                        "You don't have permission to create users."
                );
        }

    }








    @Override
    public AuthResponse createUserByAdmin(UserDto userDto)
            throws UserException {



        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();


        String email = authentication.getName();


        User creator =
                userRepository.findByEmail(email);


        if (creator == null) {
            throw new UserException(
                    "Logged in user not found."
            );
        }


        validateUserCreationPermission(
                creator.getRole(),
                userDto.getRole()
        );



        User currentUser = creator;



        if(userRepository.findByEmail(
                userDto.getEmail()
        ) != null){

            throw new UserException(
                    "Email is already registered."
            );
        }



        if(userDto.getRole() == null){

            throw new UserException(
                    "Role is required."
            );
        }



        User newUser = new User();


        newUser.setFullName(userDto.getFullName());
        newUser.setEmail(userDto.getEmail());
        newUser.setPhone(userDto.getPhone());

        newUser.setPassword(
                passwordEncoder.encode(
                        userDto.getPassword()
                )
        );


        newUser.setRole(
                userDto.getRole()
        );


        newUser.setCreatedAt(
                LocalDateTime.now()
        );

        newUser.setUpdatedAt(
                LocalDateTime.now()
        );

        newUser.setLastLogin(
                LocalDateTime.now()
        );





        // STORE ASSIGNMENT

        if(currentUser.getRole()
                == UserRole.ROLE_STORE_ADMIN){


            if(currentUser.getStore()==null){

                throw new UserException(
                        "Store Admin is not assigned to any store."
                );
            }


            newUser.setStore(
                    currentUser.getStore()
            );

        }
        else {


            if(userDto.getStoreId()!=null){


                Store store =
                        storeRepository.findById(
                                        userDto.getStoreId()
                                )
                                .orElseThrow(() ->
                                        new UserException(
                                                "Store not found"
                                        ));


                newUser.setStore(store);

            }

        }





        // BRANCH ASSIGNMENT


        if(userDto.getBranchId()!=null){


            Branch branch =
                    branchRepository.findById(
                                    userDto.getBranchId()
                            )
                            .orElseThrow(() ->
                                    new UserException(
                                            "Branch not found"
                                    ));



            if(currentUser.getRole()
                    == UserRole.ROLE_STORE_ADMIN){


                if(!branch.getStore()
                        .getId()
                        .equals(
                                currentUser.getStore().getId()
                        )){


                    throw new UserException(
                            "You cannot assign user to another store branch."
                    );

                }

            }


            newUser.setBranch(branch);

        }






        // ROLE VALIDATION


        switch(userDto.getRole()){



            case ROLE_STORE_ADMIN:


                if(newUser.getStore()==null){

                    throw new UserException(
                            "Store is required for Store Admin."
                    );

                }

                break;



            case ROLE_BRANCH_MANAGER:
            case ROLE_BRANCH_CASHIER:


                if(newUser.getStore()==null){

                    throw new UserException(
                            "Store is required."
                    );

                }


                if(newUser.getBranch()==null){

                    throw new UserException(
                            "Branch is required."
                    );

                }

                break;




            case ROLE_INVENTORY_MANAGER:


                if(newUser.getStore()==null){

                    throw new UserException(
                            "Store is required for Inventory Manager."
                    );
                }

                break;




            case ROLE_ACCOUNTANT:


                if(newUser.getStore()==null){

                    throw new UserException(
                            "Store is required for Accountant."
                    );
                }

                break;




            default:

                throw new UserException(
                        "Invalid role."
                );

        }





        User savedUser =
                userRepository.save(newUser);



        AuthResponse response =
                new AuthResponse();


        response.setMessage(
                "User created successfully by Admin"
        );


        response.setUser(
                UserMapper.toDTO(savedUser)
        );


        return response;

    }

}