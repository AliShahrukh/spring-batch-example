package com.ali.learning.salesInfo.batch.processor;

import com.ali.learning.salesInfo.batch.dto.SalesInfoDTO;
import com.ali.learning.salesInfo.batch.mapper.SalesInfoMapper;
import com.ali.learning.salesInfo.domain.SalesInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SalesInfoItemProcessor implements ItemProcessor<SalesInfoDTO, SalesInfo> {

    private final SalesInfoMapper salesInfoMapper;

    @Override
    public SalesInfo process(SalesInfoDTO salesInfoDTO) throws Exception {
        Thread.sleep(2000);
        return salesInfoMapper.mapToEntity(salesInfoDTO);
    }
}
