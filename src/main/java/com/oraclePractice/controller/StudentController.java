package com.oraclePractice.controller;

import com.oraclePractice.model.Student;
import com.oraclePractice.repository.StudentRepository;
import com.oraclePractice.service.StudentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class StudentController {
    private final StudentRepository studentRepository;
    private final StudentService studentService;


    public StudentController(StudentRepository studentRepository, StudentService studentService) {
        this.studentRepository = studentRepository;
        this.studentService = studentService;
    }

    //call store procedure here
    @GetMapping("/create")
    public String createStudent() {
        studentService.createStudent();
        return "ok";
    }

    @GetMapping("/getAllEmployee")
    public Object getAllEmployee() {
        return studentService.getAllEmployee(Long.parseLong(String.valueOf("5")));
    }

    @GetMapping("/getAllEmployeeByPackageProcedureCall")
    public Object getAllEmployeeByPackageProcedureCall() {
        return studentService.getAllEmployeeByPackageProcedureCall();
    }

    @GetMapping("/getAllEmployeeByIDUsingPackageProcedureCall/{id}")
    public Object getAllEmployeeByIDUsingPackageProcedureCall(@PathVariable("id") Long id) {
        return studentService.getAllEmployeeByIDUsingPackageProcedureCall(id);
    }
//    Call using view and Sequences
    @GetMapping("/getPersonByCallingViewAndSequences/{id}")
    public Object getPersonByCallingViewAndSequences(@PathVariable("id")Long id){
        return studentService.getPersonByCallingViewAndSequences(id);
    }
    @GetMapping("saveXmlProcedure")
    public String saveXmlBasedProcedure() {
        studentService.createXmlBasedStudent();
        return "ok";
    }

    @GetMapping("/getEmployeeAllInsideFunctionCall/{id}")
    public Object getEmployeeAllInsideFunctionCallPL_SQL(@PathVariable("id") Long id) {
        return studentService.getEmployeeAllInsideFunctionCall(id);

    }


    @GetMapping("/getAll")
    public Object getAllStudent() {
        return studentRepository.getAllStudents();
    }

    @GetMapping("/")
    public void insertStudent() {


        Student student = new Student();
        student.setName("kibria");
        student.setEmail("email");
        student.setPhone("01771598949");
        student.setPhone("0177");

        studentRepository.save(student);
    }
}
