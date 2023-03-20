package com.jian.nettyclient.mapper;

import com.jian.nettyclient.domain.TransRecordMockResult;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransRecordMockResultMapper {

    int insertSelect(String ser23);

    int updateTouchFlag(String ser23);

    TransRecordMockResult selectBySer23(String ser23);
}