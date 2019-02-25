package com.shop.controller;


import com.PageUtil;
import com.ResultMsg;
import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.shop.pojo.Brand;
import com.shop.service.BrandService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author syl
 * @since 2019-02-19
 */
@Controller
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("/test")
    public String findAllBrands(@RequestParam(value = "index",defaultValue = "1") Integer pageIndex,
                            @RequestParam(value = "size",defaultValue = "8") Integer pageSize,
                            Brand brand, Model model){
        EntityWrapper<Brand> wrapper = new EntityWrapper<>();
        Page<Brand> page = new Page<>(pageIndex, pageSize);

        if (brand.getName()!=null&&brand.getName()!=""){
            wrapper.like("name",brand.getName());
        }
        wrapper.eq("del",0);
        wrapper.orderBy("id").last("desc");

        Page<Brand> pageBrands = brandService.selectPage(page, wrapper);

        List<Brand> brands = pageBrands.getRecords();

       /* for (Brand record : brands) {
            System.out.println("name="+record.getName());
        }
*/
        int pages = ((Long)pageBrands.getPages()).intValue();
        int totalCount = ((Long)pageBrands.getTotal()).intValue();
        boolean hasPrevious = pageBrands.hasPrevious();
        boolean hasNext = pageBrands.hasNext();

        PageUtil<Brand> pageUtil = new PageUtil<Brand>(pageSize, pageIndex, totalCount, brand, brands);

       /* List<Integer> numbers = pageUtil.getNumbers();

        for (Integer number : numbers) {
            System.out.println("i="+number);
        }*/

        int totalCount1 = pageUtil.getTotalCount();
        System.out.println("总条数=="+totalCount1);

        model.addAttribute("pageUtil",pageUtil);
        model.addAttribute("hasPrevious",hasPrevious);
        model.addAttribute("hasNext",hasNext);

        return "brand";
    }

    /*新增品牌*/
    @RequestMapping("/add")
    @ResponseBody
    public ResultMsg addBrand(Brand brand){
        brand.setDel(0);
        boolean b = brandService.insert(brand);

        ResultMsg result = null;
        if (b){
            result = new ResultMsg(true,"新增成功");
        }else{
            result = new ResultMsg(true,"新增失败");
        }
        return result;
    }

    /*根据id查询品牌信息*/
    @RequestMapping("/findBrandById")
    @ResponseBody
    public Brand findBrandById(Integer id){
        Brand brand = brandService.selectById(id);
        return brand;
    }

    /*更新品牌信息*/
    @RequestMapping("/updateBrand")
    @ResponseBody
    public ResultMsg updateBrand(Brand brand){
        System.out.println(brand.getName()+"=="+brand.getId()+"=="+brand.getPic()+"=="+brand.getFirstChar());
        boolean b = brandService.updateById(brand);
        ResultMsg result = null;
        if (b){
            result = new ResultMsg(true,"更新成功");
        }else{
            result = new ResultMsg(true,"更新失败");
        }
        return result;
    }

    /*删除品牌信息*/
    @RequestMapping("/deleteBrand")
    @ResponseBody
    public ResultMsg deleteBrand(Brand brand){
        brand.setDel(1);
        boolean b = brandService.updateById(brand);
        ResultMsg result = null;
        if (b){
            result = new ResultMsg(true,"删除成功");
        }else{
            result = new ResultMsg(true,"删除失败");
        }
        return result;
    }

    /*批量刪除*/
    @RequestMapping("/deleteManyBrands")
    @ResponseBody
    public ResultMsg deleteManyBrands(Integer[] brandIds){

        for (Integer brandId : brandIds) {
            System.out.println("id==="+brandId);
        }

        List<Brand> brands = brandService.selectBatchIds(Arrays.asList(brandIds));

        for (Brand brand : brands) {
            brand.setDel(1);
        }

        boolean b = brandService.updateBatchById(brands);
        ResultMsg result = null;
        if (b){
            result = new ResultMsg(true,"批量删除成功");
        }else{
            result = new ResultMsg(true,"批量删除失败");
        }
        return result;
    }
}

