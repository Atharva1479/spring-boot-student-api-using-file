package com.student.api.file.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.student.api.file.model.Student;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.io.File;
import java.util.List;

@Service
public class StudentService {

    private static final String FILE_PATH = "students.json";
    private List<Student> students = new ArrayList<>();
    private ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        try {
            File file = new File(FILE_PATH);
            if (file.exists()) {
                students = objectMapper.readValue(file, new TypeReference<List<Student>>() {});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Student saveStudent(Student student) {
        student.setId(generateId());
        students.add(student);
        saveToFile();
        return student;
    }

    public List<Student> getAllStudents() {
        return students;
    }

    public Student getStudentById(Long id) {
        return students.stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
    }

    public Student updateStudent(Long id, Student updatedStudent) {
        Student existingStudent = getStudentById(id);
        if (existingStudent != null) {
            existingStudent.setName(updatedStudent.getName());
            existingStudent.setAge(updatedStudent.getAge());
            existingStudent.setCourse(updatedStudent.getCourse());
            existingStudent.setEmail(updatedStudent.getEmail());
            saveToFile();
        }
        return existingStudent;
    }

    public void deleteStudent(Long id) {
        students.removeIf(s -> s.getId().equals(id));
        saveToFile();
    }

    private Long generateId() {
        return students.isEmpty() ? 1L : students.get(students.size() - 1).getId() + 1;
    }

    private void saveToFile() {
        try {
            objectMapper.writeValue(new File(FILE_PATH), students);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
