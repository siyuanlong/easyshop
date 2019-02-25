package com.shop.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.shop.mapper.SpecificationMapper;
import com.shop.mapper.SpecificationOptionMapper;
import com.shop.pojo.Specification;
import com.shop.pojo.SpecificationOption;
import com.shop.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author syl
 * @since 2019-02-21
 */
@Service
public class SpecificationServiceImpl extends ServiceImpl<SpecificationMapper, Specification> implements SpecificationService {

    @Autowired
    private SpecificationMapper sm;

    @Autowired
    private SpecificationOptionMapper som;

    @Override
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
    public int addSpecification(Specification specification, Integer[] orders, String[] optionNames) {

        specification.setDel(0);
        Integer count = sm.insert(specification);

        int sum = 0;
        for (int i = 0; i <orders.length ; i++) {
            SpecificationOption sop = new SpecificationOption();
            sop.setOrders(orders[i]);
            sop.setOptionName(optionNames[i]);
            sop.setSpecId(specification.getId());
            sop.setDel(0);
            Integer opCount = som.insert(sop);
            if (opCount>0){
                sum++;
            }
        }

        if (count>0&&orders.length==sum){
            return 1;
        }else{
            return 0;
        }
    }

    @Override
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
    public int editSpecification(Specification specification, Integer[] orders, String[] optionNames) {

        Integer updateCount = sm.updateById(specification);

        Integer deleteCount =
                som.delete(new EntityWrapper<SpecificationOption>().eq("spec_id", specification.getId()));

        int sum01 = 0;
        for (int i = 0; i <orders.length ; i++) {
            SpecificationOption sop01 = new SpecificationOption();
            sop01.setOptionName(optionNames[i]);
            sop01.setOrders(orders[i]);
            sop01.setSpecId(specification.getId());
            sop01.setDel(0);
            Integer insertOpCount = som.insert(sop01);
            if (insertOpCount>0){
                sum01++;
            }
        }

        if (deleteCount>0&&updateCount>0&&orders.length==sum01){
            return 1;
        }else{
            return 0;
        }
    }

    @Override
    public int deleteSpecification(Specification specification) {
        specification.setDel(1);
        Integer smUpdateCount = sm.updateById(specification);

        List<SpecificationOption> optionList =
                som.selectList(new EntityWrapper<SpecificationOption>().eq("spec_id", specification.getId()));

        Integer sum = 0;
        for (SpecificationOption option : optionList) {
            option.setDel(1);
            Integer somUpdateCount = som.updateById(option);
            if (somUpdateCount>0){
                sum++;
            }
        }

        if (smUpdateCount>0&&sum==optionList.size()){
            return 1;
        }else {
            return 0;
        }
    }

    @Override
    public int deleteManySpecifications(Integer[] specificationIds) {

        Integer sum01 = 0;
        Integer sum02 = 0;
        Integer sum03 = 0;

        System.out.println("已到此处");

        for (int i = 0; i <specificationIds.length ; i++) {

            Integer specid = specificationIds[i];

            Specification specification = sm.selectById(specid);
            specification.setDel(1);
            Integer smUpdateCount = sm.updateById(specification);

            if (smUpdateCount>0){
                sum01++;
            }

            List<SpecificationOption> optionList =
                    som.selectList(new EntityWrapper<SpecificationOption>().eq("spec_id", specid));

            sum03 += optionList.size();

            for (SpecificationOption option : optionList) {
                option.setDel(1);
                Integer somUpdateCount = som.updateById(option);
                if (somUpdateCount>0){
                    sum02=sum02+1;
                }
            }
        }
        if (sum01==specificationIds.length&&sum02==sum03){
            return 1;
        }else {
            return 0;
        }
    }
}
