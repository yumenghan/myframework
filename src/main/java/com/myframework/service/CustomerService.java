package com.myframework.service;

import com.myframework.helper.DatabaseHelper;
import com.myframework.model.Customer;

import java.util.List;
import java.util.Map;

/**
 * @author Menghan Yu
 * @description customer 服务
 * @date 11/16/17 6:35 PM
 */
public class CustomerService {

    /**
     * 查询所有客户信息
     * @return
     */
    public List<Customer> listCustomers() {
        String sql = "SELECT * FROM customer";
        return DatabaseHelper.queryEntityList(Customer.class, sql);
    }

    /**
     * 添加客户
     * @param fieldMap
     * @return
     */
    public boolean createCustomer(Map<String, Object> fieldMap) {
        return DatabaseHelper.insertEntity(Customer.class, fieldMap);
    }

    /**
     * 更新客户信息
     * @param id
     * @param fieldMap
     * @return
     */
    public boolean updateCustomer(long id, Map<String, Object> fieldMap) {
        return DatabaseHelper.updateEntity(Customer.class, id, fieldMap);
    }

}
