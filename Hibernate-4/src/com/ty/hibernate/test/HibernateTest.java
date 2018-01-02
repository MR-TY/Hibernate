package com.ty.hibernate.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ty.hibernate.entity.Department;
import com.ty.hibernate.entity.Employee;

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
	//1.普通查询与模糊查询
	//基于位置查询
	@Test
	public void Test1(){
		//1.建立query
		String hql = "FROM Employee e WHERE e.salary>? AND e.email LIKE ? ";
		Query query = session.createQuery(hql);
		
		//2.绑定参数
		query.setFloat(0, 1).setString(1, "%q%");
		
		//3.查询结果
		List<Employee> employees = query.list();
		System.out.println(employees.size());
	}
	//基于命名查询
	@Test
	public void Test2(){
		//1.建立query
		String hql = "FROM Employee e WHERE e.salary>:salary AND e.email LIKE :email ";
		Query query = session.createQuery(hql);
		
		//2.绑定参数
		query.setFloat("salary", 13000).setString("email", "%@%");
		
		//3.查询结果
		List<Employee> employees = query.list();
		System.out.println(employees.size());
	}
	
	//分页查询
	@Test
	public void testPageQuery(){
		String hql = "FROM Employee";
		Query query = session.createQuery(hql);
		int page = 2;
		int pageSize = 3;
		//设置一个开始的页，这个开始的页是通过计算从第几个对象开始所以为(page-1)*pageSize，再设置每页显示多少条对象
		List<Employee> employees =  query.setFirstResult((page-1)*pageSize).setMaxResults(pageSize).list();
		System.out.println(employees);
	}
	
	//命名查询
	@Test
	public void testNameQuery(){
		Query query = session.getNamedQuery("salaryEmps");
		List<Employee> employees = query.setFloat("minSalary", 1200).setFloat("maxSalary", 10000).list();
		System.out.println(employees);
	}
	
	//投影查询
	@Test
	public void testApart(){
		String hql = "SELECT e.salary, e.email FROM Employee e WHERE e.dept = :dept";
		Query query = session.createQuery(hql);
		Department department = new Department();
		department.setId(2);
		List<Object[]> emList = query.setEntity("dept", department).list();
		for (Object[] objects : emList) {
			System.out.println(Arrays.asList(objects));
		}
	}
	
	//投影查询，把所查询的参数放在一个对象里面,所以需要在实体类里面添加构造器
		@Test
		public void testApart1(){
			String hql = "SELECT NEW Employee (e.salary, e.email) FROM Employee e WHERE e.dept = :dept";
			Query query = session.createQuery(hql);
			Department department = new Department();
			department.setId(2);
			List<Employee> emList = query.setEntity("dept", department).list();
			for (Employee employee : emList) {
				System.out.println(employee);
			}
		}
		
	//报表查询
		@Test
		public void testGroupAndHaving(){
			String hql = "SELECT MIN(e.salary),MAX(e.salary) FROM Employee e GROUP BY e.dept HAVING MIN(e.salary)>:minSalary";
			List<Object[]> objects = session.createQuery(hql).setFloat("minSalary",1000).list();
			for (Object[] objects2 : objects) {
				System.out.println(Arrays.asList(objects2));
			}
		}
	//迫切左外连接
		@Test
		public void testLeftJoinFecth(){
			String hql = " FROM Department d LEFT JOIN FETCH d.emps";
			Query query = session.createQuery(hql);
			List<Department> departments = query.list();
			//departments = new ArrayList<>(new LinkedHashSet<>(departments));
			for (Department department : departments) {
				System.out.println(department.getName());
			}
		}
		//左外连接
		@Test
		public void testLeftJoin(){
			String hql = " FROM Department d LEFT JOIN  d.emps";
			Query query = session.createQuery(hql);
			List<Department> departments = query.list();
//			System.out.println(departments);
//			employees = new ArrayList<>(new LinkedHashSet<>(employees));
//			for (Department department : departments) {
//				System.out.println(departments);
//			}
		}
}
