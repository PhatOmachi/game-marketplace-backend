package poly.gamemarketplacebackend.core.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import poly.gamemarketplacebackend.core.dto.UsersDTO;
import poly.gamemarketplacebackend.core.entity.Users;

@Mapper(componentModel = "spring")
public interface UsersMapper {
    UsersMapper INSTANCE = Mappers.getMapper(UsersMapper.class);

    @Mapping(target = "totalSpent", ignore = true)
    UsersDTO toDTO(Users users);

    Users toEntity(UsersDTO usersDTO);

    @AfterMapping
    default void calculateTotalSpent(Users users, @MappingTarget UsersDTO usersDTO) {
        float totalSpent = (float) users.getOrders().stream()
                .mapToDouble(order -> order.getTotalPayment())
                .sum();
        usersDTO.setTotalSpent(totalSpent);
    }
}
