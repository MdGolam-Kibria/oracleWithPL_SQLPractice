package com.oraclePractice.service;

import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

@Service("studentService")
public class StudentServiceImpl implements StudentService {
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public void createStudent() {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("addstudent");
        query.registerStoredProcedureParameter(1, Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(4, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(5, String.class, ParameterMode.IN);
        //set value
        query.setParameter(1, Long.parseLong("302"));
        query.setParameter(2, "spring boot");
        query.setParameter(3, "spring@gmail.com");
        query.setParameter(4, "01771598949");
        query.setParameter(5, "413152");
        //now execute the query

        query.execute();



        // PL/SQL procedure below
        String procedure = """
                create PROCEDURE addstudent (
                    id   IN    student_test.id%TYPE,
                    name  IN   student_test.name%TYPE,
                    email IN  student_test.email%TYPE,
                    phone IN   student_test.phone%TYPE,
                    password VARCHAR2
                ) IS
                BEGIN
                    INSERT INTO student_test (
                        id,
                        name,
                        email,
                        password,
                        phone
                    ) VALUES (
                        id,
                        name,
                        email,
                        password,
                        phone
                    );
                                
                    dbms_output.put_line('Added student' || name);
                EXCEPTION
                    WHEN OTHERS THEN
                        dbms_output.put_line('ERROR ON Student Add : ' || name);
                END;
                /
                """;
    }


    @Override
    public Object getAllEmployee(Long studentId) {
         // PL/SQL procedure below
        String procedure = """
                create procedure getEmployeeAll(
                    id IN EMPLOYEE.ID%type,
                    e_disp OUT SYS_REFCURSOR
                )
                    is begin
                    open e_disp for select *  from EMPLOYEE where EMPLOYEE.ID = id;
                end;
                /
                """;
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getEmployeeAll");
        query.registerStoredProcedureParameter(1, Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, Object.class, ParameterMode.REF_CURSOR);
        //set value
        query.setParameter(1, Long.parseLong("5"));
        //now execute the query
        query.execute();

        //Get output parameters
        return query.getResultList();

    }


    @Override
    public void createXmlBasedStudent() {
        String trx_xml = "<statement>";
        trx_xml = trx_xml + "<rowrecord>";
        trx_xml = trx_xml + "<STUDENT_ID>" + 5 + "</STUDENT_ID>";
        trx_xml = trx_xml + "<STUDENT_NAME>" + "Kibria" + "</STUDENT_NAME>";
        trx_xml = trx_xml + "<STUDENT_EMAIL>" + "Kibria@gmail.com" + "</STUDENT_EMAIL>";
        trx_xml = trx_xml + "</rowrecord>";
        trx_xml = trx_xml + "</statement>";

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("saveEmployeeex");
        query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, Integer.class, ParameterMode.OUT);

        //set value
        query.setParameter(1, trx_xml);
        //now execute the query
        query.execute();

        int output = (int) query.getOutputParameterValue(2);
        System.out.println("" + output);

        // PL/SQL procedure below
        String procedure = """
                create procedure saveEmployeeex(
                        stmt IN CLOB,
                        output OUT number
                    ) AS
                        id_T    EMPLOYEE.ID%TYPE;
                        name_T  EMPLOYEE.NAME%TYPE;
                        email_T EMPLOYEE.EMAIL%TYPE;
                        CURSOR field_cursor
                            IS
                            SELECT XMLTYPE.EXTRACT(VALUE(a),
                                                   '/rowrecord/STUDENT_ID/text()').getStringVal(),
                                   XMLTYPE.EXTRACT(VALUE(a),
                                                   '/rowrecord/STUDENT_NAME/text()').getStringVal(),
                                   XMLTYPE.EXTRACT(VALUE(a), '/rowrecord/STUDENT_EMAIL/text()').getStringVal()
                                
                            FROM TABLE (
                                     XMLSEQUENCE(
                                             XMLTYPE(stmt).EXTRACT('/statement/rowrecord'))) a;
                    begin
                                
                        output := 0;
                                
                        open field_cursor;
                                
                        LOOP
                            FETCH field_cursor
                                INTO id_T,name_T,email_T;
                            exit when field_cursor%NOTFOUND;
                        END LOOP;
                        insert into EMPLOYEE(id, name, email) VALUES (id_T, name_T, email_T);
                        commit;
                        output := 1;
                    END saveEmployeeex;
                /
                """;
    }
}
