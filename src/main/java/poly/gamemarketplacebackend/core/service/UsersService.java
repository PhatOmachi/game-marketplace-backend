package poly.gamemarketplacebackend.core.service;

import poly.gamemarketplacebackend.core.dto.UsersDTO;

public interface UsersService {
    UsersDTO findByUsername(String username);

    int updateUserProfile(UsersDTO usersDTO);

    UsersDTO getCurrentUser();
}
