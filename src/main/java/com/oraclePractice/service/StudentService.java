package com.oraclePractice.service;




public interface StudentService {
   void createStudent( );
   Object getAllEmployee(Long studentId);
   Object getAllEmployeeByPackageProcedureCall();
   Object getAllEmployeeByIDUsingPackageProcedureCall(Long id);

   void createXmlBasedStudent();
}
