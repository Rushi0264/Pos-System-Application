package com.example.pos.system.service.impl;

import com.example.pos.system.configuration.JwtProvider;
import com.example.pos.system.domain.StoreStatus;
import com.example.pos.system.domain.UserRole;
import com.example.pos.system.exception.UserException;
import com.example.pos.system.mapper.UserMapper;
import com.example.pos.system.modal.Branch;
import com.example.pos.system.modal.Store;
import com.example.pos.system.modal.StoreContact;
import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.StoreRegistrationDTO;
import com.example.pos.system.payload.dto.UserDto;
import com.example.pos.system.payload.response.AuthResponse;
import com.example.pos.system.repository.BranchRepository;
import com.example.pos.system.repository.StoreRepository;
import com.example.pos.system.repository.UserRepository;
import com.example.pos.system.service.AuthService;
import com.example.pos.system.service.EmailService;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final CustomUserImplementation customUserImplementation;
    private final StoreRepository storeRepository;
    private final BranchRepository branchRepository;
    private final EmailService emailService;



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
    public AuthResponse registerStore(StoreRegistrationDTO dto) throws UserException {

        if (userRepository.findByEmail(dto.getOwnerEmail()) != null) {
            throw new UserException("Email is already registered.");
        }

        // ---- Create Store (PENDING by default) ----
        Store store = new Store();
        store.setBrand(dto.getBrand());
        store.setStoreType(dto.getStoreType());
        store.setDescription(dto.getDescription());
        store.setStatus(StoreStatus.PENDING);

        StoreContact contact = new StoreContact();
        contact.setAddress(dto.getAddress());
        contact.setPhone(dto.getPhone());
        contact.setEmail(dto.getEmail());
        store.setContact(contact);

        Store savedStore = storeRepository.save(store);

        // ---- Create Store Admin (Owner) ----
        User owner = new User();
        owner.setFullName(dto.getOwnerFullName());
        owner.setEmail(dto.getOwnerEmail());
        owner.setPhone(dto.getOwnerPhone());
        owner.setPassword(passwordEncoder.encode(dto.getOwnerPassword()));
        owner.setRole(UserRole.ROLE_STORE_ADMIN);
        owner.setStore(savedStore);
        owner.setCreatedAt(LocalDateTime.now());
        owner.setUpdatedAt(LocalDateTime.now());

        User savedOwner = userRepository.save(owner);

        // ---- Notify all Super Admins ----
        notifySuperAdminsOfNewStoreRegistration(savedStore, savedOwner);

        AuthResponse response = new AuthResponse();
        response.setMessage(
                "Your store has been registered successfully and is pending approval. "
                        + "You will be notified by email once it is approved."
        );
        response.setUser(UserMapper.toDTO(savedOwner));

        return response;
    }

    private void notifySuperAdminsOfNewStoreRegistration(Store store, User owner) {

        List<User> superAdmins = userRepository.findByRole(UserRole.ROLE_SUPER_ADMIN);

        if (superAdmins == null || superAdmins.isEmpty()) {
            return;
        }

        String subject = "New Store Registration - Approval Needed";

        for (User admin : superAdmins) {

            String body = "Hello " + admin.getFullName() + ",\n\n"
                    + "A new store \"" + store.getBrand() + "\" has been registered by "
                    + owner.getFullName() + " (" + owner.getEmail() + ").\n"
                    + "Please review and approve or reject it from the Super Admin dashboard.\n\n"
                    + "Thank you.";

            emailService.sendEmail(admin.getEmail(), subject, body);
        }
    }



    @Override
    public AuthResponse login(UserDto userDto)
            throws UserException {

        String email = userDto.getEmail();
        String password = userDto.getPassword();

        Authentication authentication =
                authenticate(email, password);

        User user = userRepository.findByEmail(email);

        if (user.getRole() != UserRole.ROLE_SUPER_ADMIN) {

            if (user.getStore() == null) {
                throw new UserException(
                        "No store found for your account. Please register a store to continue."
                );
            }

            StoreStatus storeStatus = user.getStore().getStatus();

            if (storeStatus == StoreStatus.BLOCKED) {
                notifySuperAdminOfLoginAttempt(user, storeStatus);
                throw new UserException(
                        "Your store has been blocked by the Super Admin. Please contact support for assistance."
                );
            }

            if (storeStatus == StoreStatus.PENDING) {
                notifySuperAdminOfLoginAttempt(user, storeStatus);
                throw new UserException(
                        "Your store is still under review. You will be able to log in once it is approved."
                );
            }
        }

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        String jwt =
                jwtProvider.generateToken(authentication);

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




    private void notifySuperAdminOfLoginAttempt(User user, StoreStatus storeStatus) {

        List<User> superAdmins = userRepository.findByRole(UserRole.ROLE_SUPER_ADMIN);

        if (superAdmins == null || superAdmins.isEmpty()) {
            return;
        }

        String storeName = user.getStore() != null ? user.getStore().getBrand() : "Unknown Store";

        String subject = "Login Attempt From " +
                (storeStatus == StoreStatus.BLOCKED ? "Blocked" : "Pending") + " Store";

        for (User admin : superAdmins) {

            String body = "Hello " + admin.getFullName() + ",\n\n"
                    + user.getFullName() + " (" + user.getEmail() + ") from store \"" + storeName + "\" "
                    + "tried to log in, but their store is currently " + storeStatus.name() + ".\n\n"
                    + "Thank you.";

            emailService.sendEmail(admin.getEmail(), subject, body);
        }
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