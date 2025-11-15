package com.example.utmentor.services;

import com.example.utmentor.infrastructures.repository.Interface.ConnectionRepository;
import com.example.utmentor.infrastructures.repository.Interface.UserRepository;
import com.example.utmentor.models.docEntities.Connection.Connection;
import com.example.utmentor.models.docEntities.Connection.StatusRequest;
import com.example.utmentor.models.webModels.connections.CreateConnectionRequest;
import com.example.utmentor.models.webModels.connections.CreateConnectionResponse;
import com.example.utmentor.util.Errors;
import com.example.utmentor.util.ValidatorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
public class ConnectionService {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private UserRepository UserRepository;

    @Autowired
    private MongoTemplate mongoTemplate;




    public CreateConnectionResponse ConnectionService(CreateConnectionRequest request) {
        String tutorId = request.tutorId();
        String studentId = request.studentId();
        String message = request.message();

        if (!UserRepository.existsById(tutorId) ) {
            ValidatorException vex = new ValidatorException("Không tìm thấy gia sư.");
            vex.add(Errors.TUTOR_NOT_FOUND);
            vex.setHttpCode(HttpStatus.NOT_FOUND);
            throw vex;
        }
        if (!UserRepository.existsById(studentId) ) {
            ValidatorException vex = new ValidatorException("Không tìm thấy học sinh.");
            vex.add(Errors.STUDENT_NOT_FOUND);
            vex.setHttpCode(HttpStatus.NOT_FOUND);
            throw vex;
        }

        Connection connection = connectionRepository.findFirstByTutorIdAndStudentId(tutorId, studentId);

        if (connection != null ) {
            if (connection.getStatus() != StatusRequest.REJECTED) {
                ValidatorException vex = new ValidatorException("Kết nối đã tồn tại.");
                vex.add(Errors.EXIST_CONNECTION);
                vex.setHttpCode(HttpStatus.CONFLICT);
                throw vex;
            }
            else {
                boolean isUpdated = connectionRepository.updateConnection(connection, message);
                if (isUpdated) {
                    return new CreateConnectionResponse("Kết nối được tái tạo thành công.");
                } else {
                    ValidatorException vex = new ValidatorException("Gặp lỗi khi tạo kết nối.");
                    vex.add(Errors.FAILED_CREATE_CONNECTION);
                    vex.setHttpCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    throw vex;
                }
            }
        }
        Connection newConnection = connectionRepository.createConnection(tutorId, studentId, message);
        if (newConnection != null) {
            return new CreateConnectionResponse("Kết nối được tạo thành công.");
        } else {
            ValidatorException vex = new ValidatorException("Gặp lỗi khi tạo kết nối.");
            vex.add(Errors.FAILED_CREATE_CONNECTION);
            vex.setHttpCode(HttpStatus.INTERNAL_SERVER_ERROR);
            throw vex;
        }
    }
}
