package poly.gamemarketplacebackend.core.Chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import poly.gamemarketplacebackend.core.constant.ResponseObject;
import poly.gamemarketplacebackend.core.service.ChatService;
import poly.gamemarketplacebackend.core.service.impl.ChatServiceImpl;

@RestController
@Slf4j
@RequestMapping("/api/chat")
public class ChatAPI {

    @Autowired
    private ChatService chatService;


    @GetMapping("/room-chat")
    public ResponseObject<?> getAllRoom() {
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(chatService.findAll())
                .build();
    }

    @GetMapping("/channel_name_mess")
    public ResponseObject<?> getChannelNameMess(@RequestParam("username") String username) {
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(chatService.findByChannel(username))
                .build();
    }

}
