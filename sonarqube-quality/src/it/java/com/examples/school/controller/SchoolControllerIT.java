package com.examples.school.controller;

import static org.mockito.Mockito.*;
import static java.util.Arrays.asList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.examples.school.model.Student;
import com.examples.school.repository.StudentRepository;
import com.examples.school.repository.mongo.StudentMongoRepository;
import com.examples.school.view.StudentView;
import com.mongodb.MongoClient;

public class SchoolControllerIT {

	@Mock
	private StudentView studentView;
	
	private StudentRepository studentRepository;

	private SchoolController schoolController;
	
	private AutoCloseable closeable;
	
	@Before
	public void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		studentRepository = new StudentMongoRepository(new MongoClient("localhost"));
		// explicit empty the database through the repository
		for (Student student : studentRepository.findAll()) {
			studentRepository.delete(student.getId());
		}
		schoolController = new SchoolController(studentView, studentRepository);
	}
	
	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}
	
	@Test
	public void testAllStudents() {
		Student student = new Student("1", "test");
		studentRepository.save(student);
		schoolController.allStudents();
		verify(studentView).showAllStudents(asList(student));
	}
	
	@Test
	public void testNewStudent() {
		Student student = new Student("1", "test");
		schoolController.newStudent(student);
		verify(studentView).studentAdded(student);
	}

	@Test
	public void testDeleteStudent() {
		Student studentToDelete = new Student("1", "test");
		studentRepository.save(studentToDelete);
		schoolController.deleteStudent(studentToDelete);
		verify(studentView).studentRemoved(studentToDelete);
	}
}
