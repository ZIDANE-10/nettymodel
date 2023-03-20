package com.jian.nettyclient.mapper;

import com.jian.nettyclient.domain.TTransRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface TTransRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TTransRecord record);

    TTransRecord selectBySer23(String ser23);

    List<TTransRecord> selectByIdRange(@Param("sta")long sta,@Param("end")long end);

    int updateByPrimaryKey(TTransRecord record);
}