package poly.gamemarketplacebackend.core.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import poly.gamemarketplacebackend.core.constant.ResponseObject;
import poly.gamemarketplacebackend.core.dto.UsersDTO;
import poly.gamemarketplacebackend.core.entity.Users;
import poly.gamemarketplacebackend.core.repository.UsersRepository;
import poly.gamemarketplacebackend.core.service.UsersService;
import poly.gamemarketplacebackend.core.service.impl.CloudinaryService;
import poly.gamemarketplacebackend.core.service.impl.UsersServiceImpl;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersAPI {
    private final UsersService usersService;
    private final CloudinaryService cloudinaryService;
    private final UsersRepository repository;

    @GetMapping("/all")
    public ResponseObject<?> getAllUsers() {
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Danh sách tất cả người dùng")
                .data(usersService.getAllUsers())
                .build();
    }

    @GetMapping("/account-profile")
    public ResponseObject<?> accountProfile(@RequestParam("username") String username) {
        UsersDTO usersDTO = usersService.findByUsername(username);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Thông tin account")
                .data(usersDTO)
                .build();
    }

    @PutMapping("/update")
    public ResponseObject<?> updateUser(@RequestBody UsersDTO usersDTO) {
        int result = usersService.updateUserProfile(usersDTO);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Call api thành công")
                .data(result)
                .build();
    }

    @PostMapping("/upload-avatar")
    public ResponseObject<?> uploadAvatar(@RequestParam("file") MultipartFile file
            /*, @RequestParam("username") String username*/) {
        try {
            String avatarUrl = cloudinaryService.uploadImage(file);
//            usersService.updateUserAvatar(username, avatarUrl);
            return ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("Avatar uploaded successfully")
                    .data(avatarUrl)
                    .build();
        } catch (IOException e) {
            return ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Failed to upload avatar")
                    .build();
        }
    }

}
