package com.shop.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.shop.mapper.BrandMapper;
import com.shop.pojo.Brand;
import com.shop.service.BrandService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author syl
 * @since 2019-02-19
 */
@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {

}
