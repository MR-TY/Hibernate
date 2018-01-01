package com.atguigu.hibernate.entities.n21;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HibernateTest {

	private SessionFactory sessionFactory;
	private Session session;
	private Transaction transaction;
	
	@Before
	public void init(){
		Configuration configuration = new Configuration().configure();
		ServiceRegistry serviceRegistry = 
				new ServiceRegistryBuilder().applySettings(configuration.getProperties())
				                            .buildServiceRegistry();
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		
		session = sessionFactory.openSession();
		transaction = session.beginTransaction();
	}
	
	@After
	public void destroy(){
		transaction.commit();
		session.close();
		sessionFactory.close();
	}
	@Test
	public void TestMany2OneSave(){
		Customer customer = new Customer();
		customer.setCustomerId(1);
		customer.setCustomerName("唐宇");
		
		Order order1 = new Order();
		order1.setOrderName("av1");
		
		Order order2 = new Order();
		order2.setCustomer(customer);
		
		order1.setCustomer(customer);
		order2.setOrderName("av2");
		
		customer.getOrders().add(order1);
		customer.getOrders().add(order2);
		
		session.save(customer);
		session.save(order1);
		session.save(order2);
		
	}
	
	@Test
	public void Many2OneQuery(){
		Customer customer = (Customer) session.get(Customer.class,1);
		System.out.println(customer.getCustomerName());
	}
	
	@Test
	public void Many2OneUpdate(){
		Customer customer = (Customer) session.get(Customer.class, 1);
		customer.getOrders().iterator().next().setOrderName("你是猪吗？");
	}
	
	@Test 
	public void May2OneDelete(){
		//当关系被删除之后，因为customer没有被关系所以就能进行删除
		Order order = (Order) session.get(Order.class, 3);
		session.delete(order);
		
		/*//在不设置级联关系的情况下，custorm被关联之后是不能删除的
		Customer customer = (Customer) session.get(Customer.class, 1);
		session.delete(customer);*/
	}
}
