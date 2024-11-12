package poly.gamemarketplacebackend.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.gamemarketplacebackend.core.dto.UsersDTO;
import poly.gamemarketplacebackend.core.entity.Users;
import poly.gamemarketplacebackend.core.mapper.UsersMapper;
import poly.gamemarketplacebackend.core.repository.UsersRepository;
import poly.gamemarketplacebackend.core.security.service.AuthService;
import poly.gamemarketplacebackend.core.service.UsersService;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;

    @Override
    public UsersDTO findByUsername(String username) {
        Users users = usersRepository.findByUsername(username);
        return users == null ? null : usersMapper.toDTO(users);
    }

    @Override
    public int updateUserProfile(UsersDTO usersDTO) {
        return usersRepository.updateUsersByUsername(
                usersDTO.getEmail(),
                usersDTO.getHoVaTen(),
                usersDTO.getAvatar(),
                usersDTO.getUsername());
    }

    @Override
    public UsersDTO getCurrentUser() {
        return usersMapper.toDTO(usersRepository.findByUsername(AuthService.getCurrentAccount().getUsername()));
    }

    @Override
    public void updateUserAvatar(String username, String avatarUrl) {
        Users user = usersRepository.findByUsername(username);
        if (user != null) {
            user.setAvatar(avatarUrl);
            usersRepository.save(user);
        }
    }


}
