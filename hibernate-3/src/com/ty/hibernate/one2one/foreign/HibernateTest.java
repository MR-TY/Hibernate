package com.ty.hibernate.one2one.foreign;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.hibernate.Query;
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
	public void TestOne2OneSave(){
		Department department = new Department();
		department.setDeptName("DEPT_AA");
		
		Manager manager = new Manager();
		manager.setMgrName("MGR_TY1");
		
		department.setMgr(manager);
		manager.setDept(department);
		
		session.save(manager);
		session.save(department);
	}
	
	@Test
	public void TestOne2OneQuery(){
		
		Department department = (Department) session.get(Department.class, 1);
		System.out.println(department.getDeptName());
		
		transaction.commit();
		session.close();
		session = sessionFactory.openSession();
		
		transaction = session.beginTransaction();
		Department department1 = (Department) session.get(Department.class, 1);
		System.out.println(department1.getDeptName());
	
	}
	@Test
	public void TestQuery(){
		Query query = session.createQuery("FROM Manager");
		query.setCacheable(true);
		List<Manager> managers = query.list();
		System.out.println(managers.size());
	}
}
