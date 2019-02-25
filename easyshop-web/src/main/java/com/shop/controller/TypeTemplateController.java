package com.shop.controller;


import com.Json2ObjectUtils;
import com.PageUtil;
import com.ResultMsg;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.shop.pojo.*;
import com.shop.service.BrandService;
import com.shop.service.SpecificationService;
import com.shop.service.TypeTemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author syl
 * @since 2019-02-24
 */
@Controller
@RequestMapping("/typeTemplate")
public class TypeTemplateController {

    @Reference
    private TypeTemplateService service;

    @Reference
    private BrandService brandService;

    @Reference
    private SpecificationService specService;

    /*分页显示查找内容*/
    @RequestMapping("/test")
    public String findAllTypeTemplates(@RequestParam(value = "index", defaultValue = "1") Integer pageIndex,
                                       @RequestParam(value = "size", defaultValue = "8") Integer pageSize,
                                       TypeTemplate typeTemplate, Model model) {
        EntityWrapper<TypeTemplate> wrapper = new EntityWrapper<>();
        Page<TypeTemplate> page = new Page<>(pageIndex, pageSize);

        if (typeTemplate.getName() != null && typeTemplate.getName() != "") {
            wrapper.like("name", typeTemplate.getName());
        }
        wrapper.eq("del", 0);
        wrapper.orderBy("id").last("desc");

        Page<TypeTemplate> pageTypeTemplate = service.selectPage(page, wrapper);

        List<TypeTemplate> typeTemplates = pageTypeTemplate.getRecords();
        List<TypeTemplate> typeTemplateList = new ArrayList<>();

        for (TypeTemplate template : typeTemplates) {

            //关联品牌
            String brandJsonIds = template.getBrandIds();
            //关联规格
            String specJsonIds = template.getSpecIds();
            //拓展属性
            String attrJsonItems = template.getCustomAttributeItems();

            //调用工具类将json对象转换为string类型

            template.setBrandIds(Json2ObjectUtils.json2Object(brandJsonIds));
            template.setSpecIds(Json2ObjectUtils.json2Object(specJsonIds));
            template.setCustomAttributeItems(Json2ObjectUtils.json2Object(attrJsonItems));

            typeTemplateList.add(template);

        }

        int pages = ((Long) pageTypeTemplate.getPages()).intValue();
        int totalCount = ((Long) pageTypeTemplate.getTotal()).intValue();
        boolean hasPrevious = pageTypeTemplate.hasPrevious();
        boolean hasNext = pageTypeTemplate.hasNext();

        PageUtil<TypeTemplate> pageUtil = new PageUtil<TypeTemplate>(pageSize, pageIndex, totalCount, typeTemplate, typeTemplateList);

        int totalCount1 = pageUtil.getTotalCount();

        model.addAttribute("pageUtil", pageUtil);
        model.addAttribute("hasPrevious", hasPrevious);
        model.addAttribute("hasNext", hasNext);

        return "type_template";
    }

    /*新增前查询关联品牌和规格*/
    @RequestMapping("/selectBeforeAdd")
    @ResponseBody
    public Map<String, Object> selectBeforeAdd() {
        List<Brand> brands = brandService.selectList(new EntityWrapper<Brand>().eq("del", 0));
        List<Specification> specifications = specService.selectList(new EntityWrapper<Specification>().eq("del", 0));

        Map<String, Object> map = new HashMap<>();
        map.put("brands", brands);
        map.put("specifications", specifications);

        return map;
    }

    /*增加商品来模板*/
    @RequestMapping("/addTemplates")
    @ResponseBody
    public ResultMsg addTemplates(TypeTemplate typeTemplate, Integer[] brandIds, Integer[] specIds, String[] customAttributeItems) {

        List<Json2Object> brandList = new ArrayList<>();
        for (Integer brandId : brandIds) {
            Brand brand = brandService.selectById(brandId);
            Json2Object json2Object = new Json2Object();
            json2Object.setId(brandId);
            json2Object.setText(brand.getName());
            brandList.add(json2Object);
        }

        List<Json2Object> specList = new ArrayList<>();
        for (Integer specId : specIds) {
            Specification specification = specService.selectById(specId);
            Json2Object json2Object = new Json2Object();
            json2Object.setId(specId);
            json2Object.setText(specification.getSpecName());
            specList.add(json2Object);
        }

        List<CustomAttributeItemObject> attrItemsList = new ArrayList<>();
        for (String item : customAttributeItems) {
            CustomAttributeItemObject itemObject = new CustomAttributeItemObject();
            itemObject.setText(item);
            attrItemsList.add(itemObject);
        }

        typeTemplate.setDel(0);
        typeTemplate.setBrandIds(Json2ObjectUtils.object2Json(brandList));
        typeTemplate.setSpecIds(Json2ObjectUtils.object2Json(specList));
        typeTemplate.setCustomAttributeItems(JSON.toJSONString(attrItemsList));

        boolean i = service.insert(typeTemplate);
        ResultMsg result = null;
        if (i) {
            result = new ResultMsg(true, "更新成功");
        } else {
            result = new ResultMsg(false, "更新失敗");
        }
        return result;
    }

    /*更新前查询*/
    @RequestMapping("/goUpdateTemplate")
    @ResponseBody
    public Map<String, Object> goUpdateTemplate(Integer templateId) {

        HashMap<String, Object> map = new HashMap<>();

        List<Brand> brands = brandService.selectList(new EntityWrapper<Brand>().eq("del", 0));
        List<Specification> specifications = specService.selectList(new EntityWrapper<Specification>().eq("del", 0));
        TypeTemplate typeTemplate = service.selectById(templateId);

        List<Json2Object> brandList = JSON.parseArray(typeTemplate.getBrandIds(), Json2Object.class);
        List<Json2Object> specList = JSON.parseArray(typeTemplate.getSpecIds(), Json2Object.class);
        List<CustomAttributeItemObject> itemsList = JSON.parseArray(typeTemplate.getCustomAttributeItems(), CustomAttributeItemObject.class);

        for (Json2Object b : brandList) {
            for (Brand b1 : brands) {
                if (b.getId().equals(b1.getId().intValue())) {
                    b1.setFlag(true);
                    break;
                }
            }
        }

        for (Json2Object s : specList) {
            for (Specification s1 : specifications) {
                if (s.getId().equals(s1.getId().intValue())) {
                    s1.setFlag(true);
                    break;
                }
            }
        }

        map.put("typeTemplate", typeTemplate);
        map.put("brandList", brands);
        map.put("specList", specifications);
        map.put("itemsList", itemsList);

        return map;
    }

    /*更新*/
    @RequestMapping("/updateTemplate")
    @ResponseBody
    public ResultMsg updateTemplate(TypeTemplate typeTemplate, Integer[] brandIds, Integer[] specIds, String[] customAttributeItems) {

        //把数组转为数据库中JSON那种格式？

        List<Json2Object> brands = new ArrayList<Json2Object>();
        for (Integer id : brandIds) {
            Json2Object o = new Json2Object();
            o.setId(id);
            o.setText(brandService.selectById(id).getName());
            brands.add(o);
        }
        String brand_json = JSON.toJSONString(brands); //转换为数据库需要的格式

        List<Json2Object> specs = new ArrayList<Json2Object>();
        for (Integer id : specIds) {
            Json2Object o = new Json2Object();
            o.setId(id);
            o.setText(specService.selectById(id).getSpecName());
            specs.add(o);
        }
        String spec_json = JSON.toJSONString(specs);


        List<CustomAttributeItemObject> customAttributeItemObjects = new ArrayList<CustomAttributeItemObject>();
        for (String name : customAttributeItems) {
            CustomAttributeItemObject o = new CustomAttributeItemObject();
            o.setText(name);
            customAttributeItemObjects.add(o);
        }
        String customAttributeItems_json = JSON.toJSONString(customAttributeItemObjects);

        typeTemplate.setBrandIds(brand_json);
        typeTemplate.setSpecIds(spec_json);
        typeTemplate.setCustomAttributeItems(customAttributeItems_json);

        Boolean b;
        ResultMsg result = null;
        try {
            b = service.updateById(typeTemplate);
            if (b) {
                result = new ResultMsg(true, "更新成功");
            } else {
                result = new ResultMsg(false, "更新失败");
            }
        } catch (Exception e) {
            result = new ResultMsg(false, "更新失败");
        }
        return result;
    }
}

