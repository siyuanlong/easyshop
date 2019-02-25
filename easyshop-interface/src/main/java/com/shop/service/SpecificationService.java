package com.shop.service;

import com.baomidou.mybatisplus.service.IService;
import com.shop.pojo.Specification;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author syl
 * @since 2019-02-21
 */
public interface SpecificationService extends IService<Specification> {

    //新增规格信息
    public int addSpecification(Specification specification,Integer[] orders,String[] optionNames);

    //修改规格信息
    public int editSpecification(Specification specification,Integer[] orders,String[] optionNames);

    //删除规格信息
    public int deleteSpecification(Specification specification);

    //批量删除规格信息
    public int deleteManySpecifications(Integer[] specificationIds);
}
