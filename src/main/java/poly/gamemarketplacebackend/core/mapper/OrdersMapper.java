package poly.gamemarketplacebackend.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import poly.gamemarketplacebackend.core.dto.OrdersDTO;
import poly.gamemarketplacebackend.core.entity.Orders;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrdersMapper {
    OrdersMapper INSTANCE = Mappers.getMapper(OrdersMapper.class);

    OrdersDTO toDTO(Orders orders);

    Orders toEntity(OrdersDTO ordersDTO);

    List<OrdersDTO> toDTOs(List<Orders> orders);

    List<Orders> toEntities(List<OrdersDTO> ordersDTOs);
}