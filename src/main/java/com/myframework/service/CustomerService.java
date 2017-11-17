package com.myframework.service;

import com.myframework.helper.DatabaseHelper;
import com.myframework.model.Customer;

import java.util.List;

/**
 * @author Menghan Yu
 * @description customer 服务
 * @date 11/16/17 6:35 PM
 */
public class CustomerService {

    public List<Customer> listCustomers() {
        String sql = "SELECT * FROM customer";
        return DatabaseHelper.queryEntityList(Customer.class, sql);
    }
}
