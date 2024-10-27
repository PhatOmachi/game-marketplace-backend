package poly.gamemarketplacebackend.core.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import poly.gamemarketplacebackend.core.constant.ResponseObject;
import poly.gamemarketplacebackend.core.dto.UsersDTO;
import poly.gamemarketplacebackend.core.service.UsersService;

@RestController
@Slf4j
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersAPI {
    private final UsersService usersService;

    @GetMapping("/account-profile")
    public ResponseObject<?> accountProfile(@RequestParam("username") String username){
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
}
