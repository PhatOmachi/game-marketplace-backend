package poly.gamemarketplacebackend.core.service;

import poly.gamemarketplacebackend.core.dto.UsersDTO;

import java.io.IOException;
import java.util.List;

public interface UsersService {
    UsersDTO findByUsername(String username);

    int updateUserProfile(UsersDTO usersDTO);

    UsersDTO getCurrentUser();

    void updateUserAvatar(String username, String avatarUrl);

    List<UsersDTO> getAllUsers();

    void updateAvatarAndHoVaTenByUsername(UsersDTO usersDTO) throws IOException;
}
