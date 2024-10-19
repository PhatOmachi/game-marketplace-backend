package poly.gamemarketplacebackend.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import poly.gamemarketplacebackend.core.dto.AccountDTO;
import poly.gamemarketplacebackend.core.entity.Account;

@Mapper
public interface AccountMapper {
    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    AccountDTO toDTO(Account account);
    Account toEntity(AccountDTO accountDTO);
}