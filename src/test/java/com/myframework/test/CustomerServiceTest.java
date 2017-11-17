package com.myframework.test;

import com.myframework.model.Customer;
import com.myframework.service.CustomerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
    public void init() {
        //初始化数据库

    }

    @Test
    public void getCustomerListTest() throws Exception{
        List<Customer> customers = customerService.listCustomers();
        Assert.assertEquals(2, customers.size());
    }
}
