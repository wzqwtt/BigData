package com.wzq.generic_exer;

import org.junit.Test;

/**
 * @author wzq
 * @create 2022-08-06 17:16
 */
public class DAOTest {

    @Test
    public void test1() {
        CustomerDAO dao1 = new CustomerDAO();
        dao1.add(new Customer());

        StudentDAO dao2 = new StudentDAO();
        dao2.add(new Student());
    }

}
