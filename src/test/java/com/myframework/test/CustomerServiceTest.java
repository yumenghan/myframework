package com.myframework.test;

import com.myframework.helper.DatabaseHelper;
import com.myframework.model.Customer;
import com.myframework.service.CustomerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author Menghan Yu
 * @description customer 测试
 * @date 11/16/17 6:38 PM
 */
public class CustomerServiceTest {
    private final CustomerService customerService;
    public CustomerServiceTest(){
        customerService = new CustomerService();
    }

    @Before
    public void init() throws IOException {
        //初始化数据库
        String file = "sql/customer_init.sql";
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String sql = null;
        while((sql = reader.readLine()) != null) {
            DatabaseHelper.executeUpdate(sql);
        }
    }

    @Test
    public void getCustomerListTest() throws Exception{
        List<Customer> customers = customerService.listCustomers();
        Assert.assertEquals(2, customers.size());
    }
}
