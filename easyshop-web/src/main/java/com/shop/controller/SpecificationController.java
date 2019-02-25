package com.shop.controller;


import com.PageUtil;
import com.ResultMsg;
import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.shop.pojo.Brand;
import com.shop.pojo.Specification;
import com.shop.pojo.SpecificationOption;
import com.shop.service.SpecificationOptionService;
import com.shop.service.SpecificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author syl
 * @since 2019-02-21
 */
@Controller
@RequestMapping("/specification")
public class SpecificationController {

    @Reference
    private SpecificationService service;
    @Reference
    private SpecificationOptionService optionService;

    @RequestMapping("/test")
    public String findAllBrands(@RequestParam(value = "index",defaultValue = "1") Integer pageIndex,
                                @RequestParam(value = "size",defaultValue = "8") Integer pageSize,
                                Specification specification, Model model){
        EntityWrapper<Specification> wrapper = new EntityWrapper<>();
        Page<Specification> page = new Page<>(pageIndex, pageSize);

        if (specification.getSpecName()!=null&&specification.getSpecName()!=""){
            wrapper.like("specName",specification.getSpecName());
        }
        wrapper.eq("del",0);
        wrapper.orderBy("id").last("desc");

        Page<Specification> pageSpecification = service.selectPage(page, wrapper);

        List<Specification> specifications = pageSpecification.getRecords();

       /* for (Brand record : brands) {
            System.out.println("name="+record.getName());
        }
*/
        int pages = ((Long)pageSpecification.getPages()).intValue();
        int totalCount = ((Long)pageSpecification.getTotal()).intValue();
        boolean hasPrevious = pageSpecification.hasPrevious();
        boolean hasNext = pageSpecification.hasNext();

        PageUtil<Specification> pageUtil = new PageUtil<Specification>(pageSize, pageIndex, totalCount, specification, specifications);

       /* List<Integer> numbers = pageUtil.getNumbers();

        for (Integer number : numbers) {
            System.out.println("i="+number);
        }*/

        int totalCount1 = pageUtil.getTotalCount();
        System.out.println("总条数=="+totalCount1);

        model.addAttribute("pageUtil",pageUtil);
        model.addAttribute("hasPrevious",hasPrevious);
        model.addAttribute("hasNext",hasNext);

        return "specification";
    }

    /*新增*/
    @RequestMapping("/addSpecification")
    @ResponseBody
    public ResultMsg addSpecification(Specification specification,Integer[] orders,String[] optionNames){

        int i ;

        ResultMsg result = null;
        try {
            i = service.addSpecification(specification, orders, optionNames);
            if (i > 0) {
                result = new ResultMsg(true, "新增成功");
            } else {
                result = new ResultMsg(false, "新增失敗");
            }
        }catch (Exception e){
            result = new ResultMsg(false, "新增失敗");
        }
        return result;
    }

    /*修改前查询*/
    @RequestMapping("/findSpecification")
    @ResponseBody
    public Map<String, Object> findSpecification(Integer id){

        Specification specification = service.selectById(id);
        List<SpecificationOption> sopList = optionService.selectList(new EntityWrapper<SpecificationOption>().eq("spec_id", id).eq("del",0));
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("spec",specification);
        resultMap.put("sopList",sopList);

        return resultMap;
    }

    /*修改规格信息*/
    @RequestMapping("/editSpecification")
    @ResponseBody
    public ResultMsg editSpecification(Specification specification,Integer[] orders,String[] optionNames){

        int j ;

        ResultMsg result = null;
        try {
            j = service.editSpecification(specification, orders, optionNames);
            if (j > 0) {
                result = new ResultMsg(true, "更新成功");
            } else {
                result = new ResultMsg(false, "更新失敗");
            }
        }catch (Exception e){
            result = new ResultMsg(false, "更新失敗");
        }
        return result;
    }


    /*删除规格信息*/

    /*删除品牌信息*/
    @RequestMapping("/deleteSpecification")
    @ResponseBody
    public ResultMsg deleteSpecification(Specification specification){

        int i = service.deleteSpecification(specification);

        ResultMsg result = null;
        if (i>0){
            result = new ResultMsg(true,"删除成功");
        }else{
            result = new ResultMsg(true,"删除失败");
        }
        return result;
    }

    /*批量删除规格信息*/
    @RequestMapping("/deleteManySpecifications")
    @ResponseBody
    public ResultMsg deleteManySpecifications(Integer[] specificationIds){

        System.out.println("数量:"+specificationIds.length);

        int i = service.deleteManySpecifications(specificationIds);

        ResultMsg result = null;
        if (i>0){
            result = new ResultMsg(true,"删除成功");
        }else{
            result = new ResultMsg(true,"删除失败");
        }
        return result;

    }
}

