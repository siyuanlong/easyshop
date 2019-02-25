package com;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PageUtil<T> implements Serializable {

    private int pageSize;
    private int pageIndex;
    private int totalPage;
    private int totalCount;
    private Object bean;
    private List<T> list;
    private int startPage;
    private int endPage;

    private  List<Integer> numbers = new ArrayList<>();

    public PageUtil(int pageSize, int pageIndex, int totalCount, Object bean, List<T> list) {
        this.pageSize = pageSize;
        this.pageIndex = pageIndex;
        this.totalPage = totalCount % pageSize == 0 ? (totalCount / pageSize) : (totalCount / pageSize + 1);
        this.totalCount = totalCount;
        this.bean = bean;
        this.list = list;

        if (totalPage <= 10) {
            this.startPage = 1;
            this.endPage = totalPage;
        } else {
            this.startPage = pageIndex - 4;
            this.endPage = pageIndex + 5;
            if (startPage < 1) {
                this.startPage = 1;
                this.endPage = 10;
            } else if (endPage > totalPage) {
                this.endPage = totalPage;
                this.startPage = totalPage - 9;
            }
        }

        for (int i = startPage; i <= endPage; i++) {
            numbers.add(i);
        }
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

    public List<Integer> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<Integer> numbers) {
        this.numbers = numbers;
    }
}
