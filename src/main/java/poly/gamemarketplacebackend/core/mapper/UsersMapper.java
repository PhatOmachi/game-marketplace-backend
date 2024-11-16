package poly.gamemarketplacebackend.core.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import poly.gamemarketplacebackend.core.dto.UsersDTO;
import poly.gamemarketplacebackend.core.entity.Orders;
import poly.gamemarketplacebackend.core.entity.Users;

@Mapper(componentModel = "spring", uses = {OwnedGameMapper.class})
public interface UsersMapper {
    UsersMapper INSTANCE = Mappers.getMapper(UsersMapper.class);

    @Mapping(target = "totalSpent", ignore = true)
    @Mapping(target = "ownedGames", source = "ownedGames")
    UsersDTO toDTO(Users users);

    @Mapping(target = "ownedGames", source = "ownedGames")
    Users toEntity(UsersDTO usersDTO);

    @AfterMapping
    default void calculateTotalSpent(Users users, @MappingTarget UsersDTO usersDTO) {
        if (users.getOrders() == null) {
            usersDTO.setTotalSpent(0f);
            return;
        }
        float totalSpent = (float) users.getOrders().stream()
                .mapToDouble(Orders::getTotalPayment)
                .sum();
        usersDTO.setTotalSpent(totalSpent);
    }
}
