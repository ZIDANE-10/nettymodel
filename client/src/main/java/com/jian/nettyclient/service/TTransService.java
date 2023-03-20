package com.jian.nettyclient.service;

import com.jian.nettyclient.domain.TTransRecord;
import com.jian.nettyclient.mapper.TTransRecordMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class TTransService {

    private AtomicLong id = new AtomicLong(1L);
    private static final long RANGE = 1000L;

    @Resource
    private TTransRecordMapper tTransRecordMapper;

    public List<TTransRecord> getData(){
        if (id.get() < 12000000L){
            long start = id.getAndAdd(RANGE);
            long end = start + RANGE;
            return tTransRecordMapper.selectByIdRange(start,end);
        }
        return null;
    }

    public void reset(){
        this.id.set(1L);
    }

}
