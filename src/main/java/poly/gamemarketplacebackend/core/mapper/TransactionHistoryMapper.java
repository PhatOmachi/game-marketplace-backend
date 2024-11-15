package poly.gamemarketplacebackend.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import poly.gamemarketplacebackend.core.dto.TransactionHistoryDTO;
import poly.gamemarketplacebackend.core.entity.TransactionHistory;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionHistoryMapper {
    TransactionHistoryMapper INSTANCE = Mappers.getMapper(TransactionHistoryMapper.class);

    TransactionHistoryDTO toDTO(TransactionHistory transactionHistory);

    TransactionHistory toEntity(TransactionHistoryDTO transactionHistoryDTO);

    List<TransactionHistoryDTO> toDTOs(List<TransactionHistory> transactionHistories);

    List<TransactionHistory> toEntities(List<TransactionHistoryDTO> transactionHistoryDTOS);
}