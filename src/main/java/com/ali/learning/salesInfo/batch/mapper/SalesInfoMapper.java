package com.ali.learning.salesInfo.batch.mapper;

import com.ali.learning.salesInfo.batch.dto.SalesInfoDTO;
import com.ali.learning.salesInfo.domain.SalesInfo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SalesInfoMapper {

    SalesInfo mapToEntity(SalesInfoDTO salesInfoDTO);
}
