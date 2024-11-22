package poly.gamemarketplacebackend.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import poly.gamemarketplacebackend.core.dto.OrdersDTO;
import poly.gamemarketplacebackend.core.entity.Orders;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrdersMapper {
    OrdersMapper INSTANCE = Mappers.getMapper(OrdersMapper.class);

    @Mapping(target = "slug", source = "game.slug")
    @Mapping(target = "sysIdUser", source = "users.sysIdUser")
    @Mapping(target = "gameName", source = "game.gameName")
    @Mapping(target = "sysIdProduct", source = "game.sysIdGame")
    OrdersDTO toDTO(Orders orders);

    @Mapping(source = "slug", target = "game.slug")
    @Mapping(source = "sysIdUser", target = "users.sysIdUser")
    @Mapping(source = "sysIdProduct", target = "game.sysIdGame")
    Orders toEntity(OrdersDTO ordersDTO);


    List<OrdersDTO> toDTOs(List<Orders> orders);

    List<Orders> toEntities(List<OrdersDTO> ordersDTOs);
}