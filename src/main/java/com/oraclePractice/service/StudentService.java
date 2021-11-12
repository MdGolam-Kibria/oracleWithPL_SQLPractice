package com.oraclePractice.service;




public interface StudentService {
   void createStudent( );
   Object getAllEmployee(Long studentId);
   Object getAllEmployeeByPackageProcedureCall();
   Object getAllEmployeeByIDUsingPackageProcedureCall(Long id);
   Object getEmployeeAllInsideFunctionCall(Long id);
   void createXmlBasedStudent();
}
