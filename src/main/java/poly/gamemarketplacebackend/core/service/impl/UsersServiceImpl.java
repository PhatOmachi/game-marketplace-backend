package poly.gamemarketplacebackend.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import poly.gamemarketplacebackend.core.dto.UsersDTO;
import poly.gamemarketplacebackend.core.entity.Users;
import poly.gamemarketplacebackend.core.mapper.UsersMapper;
import poly.gamemarketplacebackend.core.repository.UsersRepository;
import poly.gamemarketplacebackend.core.security.service.AuthService;
import poly.gamemarketplacebackend.core.service.UsersService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

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
                usersDTO.getGender(),
                usersDTO.getDOB(),
                usersDTO.getPhoneNumber(),
                usersDTO.getUsername()
        );
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

    @Override
    public List<UsersDTO> getAllUsers() {
        return usersMapper.toDTOs(usersRepository.findAll());
    }

    @Override
    public void updateAvatarAndHoVaTenByUsername(UsersDTO usersDTO) throws IOException {
        // Fetch the current user
        Users user = usersRepository.findByUsername(usersDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        String avatar = user.getAvatar();
        if (usersDTO.getFiles() != null && !usersDTO.getFiles().isEmpty()) {
            // Delete old image
            if (avatar != null) {
                deleteImage(avatar);
            }
            // Save new image
            avatar = saveImage(usersDTO.getFiles().get(0), usersDTO.getUsername(), usersDTO.getFileName());
        }

        usersRepository.updateAvatarAndHoVaTenByUsername(
                avatar,
                usersDTO.getHoVaTen(),
                usersDTO.getUsername()
        );
    }

    private void deleteImage(String imageUrl) {
        String filePath = "src/main/resources/static" + imageUrl.substring(imageUrl.indexOf("/CustomerImages"));
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    private String saveImage(String base64Image, String username, String fileName) throws IOException {
        // Create directory for voucher images
        File voucherDir = new File("src/main/resources/static/CustomerImages/" + username);
        if (!voucherDir.exists()) {
            voucherDir.mkdirs();
        }

        // Decode base64 image
        byte[] decodedBytes = Base64.getDecoder().decode(base64Image.split(",")[1]);
        String filePath = "src/main/resources/static/CustomerImages/" + username + "/" + fileName; // Adjust the path and file name as needed

        // Save image to file
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(decodedBytes);
        }

        // Build full URL for the image
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return baseUrl + "/CustomerImages/" + username + "/" + fileName;
    }


}
