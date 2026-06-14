package com.university.controller;

import com.university.dao.StudentDAO;
import com.university.model.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.*;

@WebServlet("/student")
public class StudentController extends HttpServlet {

        private StudentDAO studentDAO;

        @Override
        public void init() {
                // Initialize DAO once when servlet is created
                studentDAO = new StudentDAO();
        }

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                        throws ServletException, IOException {
                request.setCharacterEncoding("UTF-8"); // enable vietnamese search

                String action = request.getParameter("action");
                if (action == null) action = "list";

                try {
                        switch (action) {
                                case "view" -> viewStudent(request, response);
                                case "new" -> showNewForm(request, response);
                                case "edit" -> showEditForm(request, response);
                                case "search" -> doSearch(request, response);
                                case "confirmDelete" -> showDeleteConfirmation(request, response);
                                case "list" -> listStudents(request, response);
                                case "export" -> exportToCSV(request, response);
                                case "statistic" -> showStatistic(request, response);
                                default -> listStudents(request, response);
                        }
                } catch (SQLException e) {
                        throw new ServletException("Database error", e);
                }
        }

        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response)
                        throws ServletException, IOException {
                request.setCharacterEncoding("UTF-8"); // enable vietnamese search

                String action = request.getParameter("action");
                if (action == null) action = "";

                try {
                        switch (action) {
                                case "insert" -> insertStudent(request, response);
                                case "update" -> updateStudent(request, response);
                                case "delete" -> deleteStudent(request, response);
                                case "bulkDeleteConfirm" -> showBulkDeleteConfirm(request, response);
                                case "executeBulkDelete" -> executeBulkDelete(request, response);
                                        
                                default -> response.sendRedirect("student?action=list");
                        }
                } catch (SQLException e) {
                        throw new ServletException("Database error", e);
                }
        }

        // VIEW single student
        private void viewStudent(HttpServletRequest request, HttpServletResponse response)
                        throws SQLException, ServletException, IOException {
                int id = Integer.parseInt(request.getParameter("id"));
                Student student = studentDAO.getStudentById(id);
                request.setAttribute("student", student);
                request.getRequestDispatcher("/WEB-INF/views/student-detail.jsp").forward(request, response);
        }

        // Show NEW student form
        private void showNewForm(HttpServletRequest request, HttpServletResponse response)
                        throws ServletException, IOException {
                request.getRequestDispatcher("/WEB-INF/views/student-form.jsp").forward(request, response);
        }

        // Show EDIT student form
        private void showEditForm(HttpServletRequest request, HttpServletResponse response)
                        throws SQLException, ServletException, IOException {
                int id = Integer.parseInt(request.getParameter("id"));
                Student student = studentDAO.getStudentById(id);
                request.setAttribute("student", student);
                request.getRequestDispatcher("/WEB-INF/views/student-form.jsp").forward(request, response);
        }

        // INSERT new student
        private void insertStudent(HttpServletRequest request, HttpServletResponse response)
                        throws SQLException, ServletException, IOException {
                String studentCode = request.getParameter("studentCode");
                String fullName = request.getParameter("fullName");
                String email = request.getParameter("email");
                String major = request.getParameter("major");
                // map include errors to push back to form
                Map<String, String> errors = new HashMap<>();

                // validate student code format: [A-Z]{2,6}IU\d{5}
                if (studentCode == null || !studentCode.matches("[A-Z]{2,6}IU\\d{5}")) {
                        errors.put("studentCode", "Format must be [A-Z]{2,6}IU\\d{5} (e.g., ITITIU21001)");
                }

                // validate email format
                if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                        errors.put("email", "Invalid email format");
                }

                // check for duplicate student code
                if (studentDAO.isStudentCodeExists(studentCode)) {
                        errors.put("studentCode", "Student code already exists");
                }

                // check for duplicate email
                if (studentDAO.isEmailExists(email)) {
                        errors.put("email", "Email already exists");
                }

                // IF ERRORS -> push back to form
                if (!errors.isEmpty()) {
                        request.setAttribute("errors", errors);
                        // push back data for user to not have to retype
                        Student student = new Student(studentCode, fullName, email, major);
                        request.setAttribute("student", student);
                        request.getRequestDispatcher("/WEB-INF/views/student-form.jsp").forward(request, response);
                        return; // stop here, no insert
                }

                // if satisfy the format -> insert student
                Student student = new Student(studentCode, fullName, email, major);
                studentDAO.insertStudent(student);
                // insert (POST) success -> redirect to list (GET)
                response.sendRedirect("student?action=list");
        }

        // UPDATE existing student
        private void updateStudent(HttpServletRequest request, HttpServletResponse response)
                        throws SQLException, IOException {
                int id = Integer.parseInt(request.getParameter("id"));
                String studentCode = request.getParameter("studentCode");
                String fullName = request.getParameter("fullName");
                String email = request.getParameter("email");
                String major = request.getParameter("major");

                Student student = new Student(studentCode, fullName, email, major);
                student.setId(id);
                studentDAO.updateStudent(student);

                response.sendRedirect("student?action=list");
        }

        // DELETE student
        private void deleteStudent(HttpServletRequest request, HttpServletResponse response)
                        throws SQLException, IOException {
                int id = Integer.parseInt(request.getParameter("id"));
                studentDAO.deleteStudent(id);

                response.sendRedirect("student?action=list");
        }

        // DELETE confirmation page
        private void showDeleteConfirmation(HttpServletRequest request, HttpServletResponse response)
                        throws SQLException, ServletException, IOException {
                int id = Integer.parseInt(request.getParameter("id"));
                Student student = studentDAO.getStudentById(id);
                request.setAttribute("student", student);
                request.getRequestDispatcher("/WEB-INF/views/student-delete.jsp").forward(request, response);
        }

        // BULK DELETE
        private void executeBulkDelete(HttpServletRequest request, HttpServletResponse response)
                        throws SQLException, IOException {
                String[] ids = request.getParameterValues("studentIds");
                if (ids != null && ids.length > 0) {
                        studentDAO.bulkDeleteStudents(ids);
                }
                response.sendRedirect("student?action=list");
        }
        // BULK DELETE confirmation page
        private void showBulkDeleteConfirm(HttpServletRequest request, HttpServletResponse response)
                        throws SQLException, ServletException, IOException {
                String[] ids = request.getParameterValues("studentIds");
                if (ids == null || ids.length == 0) {
                        response.sendRedirect("student?action=list");
                        return;
                }

                List<Student> studentsToDelete = studentDAO.getStudentsByIds(ids);
                request.setAttribute("studentsToDelete", studentsToDelete);
                request.getRequestDispatcher("/WEB-INF/views/student-bulk-delete.jsp").forward(request, response);
        }

        // SEARCH students
        private void doSearch(HttpServletRequest request, HttpServletResponse response)
                        throws SQLException, ServletException, IOException {
                String keyword = request.getParameter("keyword");
                List<Student> students = studentDAO.searchStudentsById(keyword);
                request.setAttribute("students", students);
                request.getRequestDispatcher("/WEB-INF/views/student-list.jsp").forward(request, response);
        }

        // LISTING, PAGINATION, SORTING
        private void listStudents(HttpServletRequest request, HttpServletResponse response)
                        throws SQLException, ServletException, IOException {
                
                String keyword = request.getParameter("keyword");
                String filterBy = request.getParameter("filterBy");
                int page = 1;

                String pageParam = request.getParameter("page");
                if (pageParam != null && !pageParam.isEmpty()) {
                        try {
                                page = Integer.parseInt(pageParam);
                        } catch (NumberFormatException e) {
                                page = 1; // vd: ?page=abc => ép về trang 1
                        }
                }
                // Lấy thông tin Sort từ URL
                String sort = request.getParameter("sort");
                if(sort == null || sort.isEmpty()) sort = "id";

                String dir = request.getParameter("dir");
                if (dir == null || dir.isEmpty()) dir = "ASC";
                        
                int pageSize = 5; // num of students per page
                // Tính tổng sv, trang
                int totalStudents = studentDAO.getTotalStudents(keyword, filterBy);
                int totalPages = (int) Math.ceil((double) totalStudents / pageSize);
                if (totalPages == 0) totalPages = 1;
                // control edge case
                if (page < 1) page = 1; // Nếu page < 1 => ép về trang 1
                else if (page > totalPages) page = totalPages; // Nếu page > totalPages => ép về trang cuối


                // call DAO
                List<Student> students = studentDAO.getStudentsByPage(page, pageSize, sort, dir, keyword, filterBy);
                // push data to JSP
                request.setAttribute("students", students);
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", totalPages);
                request.setAttribute("currentSort", sort);
                request.setAttribute("currentDir", dir);
                request.setAttribute("keyword", keyword);
                request.setAttribute("filterBy", filterBy);
                // forward to JSP
                request.getRequestDispatcher("/WEB-INF/views/student-list.jsp").forward(request, response);
        }

        // Other helper methods
        private void exportToCSV(HttpServletRequest request, HttpServletResponse response)
                        throws SQLException, IOException {
                String keyword = request.getParameter("keyword");
                String filterBy = request.getParameter("filterBy");
                String sort = request.getParameter("sort");
                if (sort == null) sort = "id";
                String dir = request.getParameter("dir");
                if (dir == null) dir = "ASC";
                // Dùng Integer.MAX_VALUE để gom all sv vào 1 trang
                List<Student> students = studentDAO.getStudentsByPage(1, Integer.MAX_VALUE, sort, dir, keyword, filterBy);
                // set HTTP headers
                response.setContentType("text/csv; charset=UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=\"students.csv\"");
                // write CSV
                try (PrintWriter out = response.getWriter()) {
                        // ghi BOM cho UTF-8
                        out.print("\uFEFF");
                        // header
                        out.println("student_code,full_name,email,major");
                        // data rows
                        for (Student s : students) {
                                out.printf("\"%s\",\"%s\",\"%s\",\"%s\"\n",
                                        s.getStudentCode(),
                                        s.getFullName(),
                                        s.getEmail(),
                                        s.getMajor());
                        }
                }
        }

        private void showStatistic(HttpServletRequest request, HttpServletResponse response)
                        throws SQLException, ServletException, IOException {
                // 1. lấy tổng sv
                int totalStudents = studentDAO.getTotalStudents(null, null);

                // 2. lấy dữ liệu biểu đồ
                Map<String, Integer> majorStats = studentDAO.getStudentCountByMajor();
                Map<String, Integer> growthStats = studentDAO.getStudentGrowthLast6Months();

                // 3. push data to JSP
                request.setAttribute("totalStudents", totalStudents);
                request.setAttribute("majorStats", majorStats);
                request.setAttribute("growthStats", growthStats);

                // 4. forward to statistic.jsp
                request.getRequestDispatcher("/WEB-INF/views/statistic.jsp").forward(request, response);
        }
}