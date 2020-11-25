package org.hibernate.performance.search.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class Manager extends Employee {

	@OneToMany(mappedBy = "manager")
	private List<Employee> employees = new ArrayList<>();

}
