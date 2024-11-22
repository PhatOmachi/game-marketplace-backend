package poly.gamemarketplacebackend.core.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import poly.gamemarketplacebackend.core.constant.ResponseObject;
import poly.gamemarketplacebackend.core.dto.ChatRoomDTO;
import poly.gamemarketplacebackend.core.entity.ChatRoom;
import poly.gamemarketplacebackend.core.entity.Message;
import poly.gamemarketplacebackend.core.mapper.RoomChatMapper;
import poly.gamemarketplacebackend.core.repository.ChatRoomRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatAPI {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private RoomChatMapper roomChatMapper;

    @PostMapping("/send/{staff}")
    public ResponseEntity<String> sendMessage(@RequestParam("userName") String userName, @RequestBody String content, @PathVariable Boolean staff) {
        ChatRoom chatRoom = chatRoomRepository.findByUserName(userName).orElse(new ChatRoom());
        chatRoom.setUserName(userName);

        Message message = new Message();
        message.setSender(userName); // người gửi
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        message.setChatRoom(chatRoom);
        message.setStaff(staff);

        chatRoom.getMessages().add(message);
        chatRoomRepository.save(chatRoom);

        return ResponseEntity.ok("Message sent");
    }

    @GetMapping("/room/{userName}")
    public ResponseEntity<List<Message>> getMessages(@PathVariable String userName) {
        ChatRoom chatRoom = chatRoomRepository.findByUserName(userName).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        System.out.println(chatRoom);
        return ResponseEntity.ok(chatRoom.getMessages());
    }

    @GetMapping("/room-chat")
    public ResponseObject<?> getRoom() {
        List<Optional<ChatRoom>> chatRoom = chatRoomRepository.timPhongChat();

        System.out.println(chatRoom);
        return ResponseObject.builder()
                .message("Lấy room thành công")
                .data(roomChatMapper.toDTOs(chatRoom.stream().map(Optional::get).collect(Collectors.toList())))
                .status(HttpStatus.OK).build();
    }

    @DeleteMapping("/room/{userName}")
    public ResponseEntity<String> deleteRoom(@PathVariable String userName) {
        chatRoomRepository.deleteById(chatRoomRepository.findByUserName(userName).map(ChatRoom::getId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        return ResponseEntity.ok("Room deleted");
    }
}
