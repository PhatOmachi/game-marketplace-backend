package poly.gamemarketplacebackend.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import poly.gamemarketplacebackend.core.dto.VoucherDTO;
import poly.gamemarketplacebackend.core.entity.Voucher;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VoucherMapper {
    VoucherMapper INSTANCE = Mappers.getMapper(VoucherMapper.class);

    @Mapping(target = "voucherDetails", ignore = true)
    @Mapping(target = "games", ignore = true)
    VoucherDTO toDTO(Voucher voucher);

    Voucher toEntity(VoucherDTO voucherDTO);

    @Mapping(target = "voucherDetails", ignore = true)
    @Mapping(target = "games", ignore = true)
    List<VoucherDTO> toDTOList(List<Voucher> vouchers);

    List<Voucher> toEntityList(List<VoucherDTO> voucherDTOs);
}
